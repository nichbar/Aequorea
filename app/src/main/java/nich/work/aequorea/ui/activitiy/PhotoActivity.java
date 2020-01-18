package nich.work.aequorea.ui.activitiy;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import android.view.View;
import android.view.WindowManager;

import com.zzhoujay.richtext.ext.MD5;
import com.zzhoujay.richtext.ig.BitmapPool;
import com.zzhoujay.richtext.ig.BitmapWrapper;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import nich.work.aequorea.R;
import nich.work.aequorea.common.Constants;
import nich.work.aequorea.common.ui.activity.BaseActivity;
import nich.work.aequorea.common.ui.widget.DragPhotoView;
import nich.work.aequorea.common.utils.PermissionUtils;
import nich.work.aequorea.common.utils.ToastUtils;
import nich.work.aequorea.ui.fragment.SaveImageDialogFragment;

public class PhotoActivity extends BaseActivity {
    private String mUrl;
    private DragPhotoView mDragPhotoView;
    private static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 0;
    private static final String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    
    @Override
    protected int getContentViewId() {
        return R.layout.activity_photo;
    }
    
    @Override
    protected void initModel() {
        Single.create(new SingleOnSubscribe<Bitmap>() {
            @Override
            public void subscribe(SingleEmitter<Bitmap> e) throws Exception {
                Bitmap bitmap = loadBitmap();
                if (bitmap != null) {
                    e.onSuccess(bitmap);
                }
            }
        })
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(new Consumer<Bitmap>() {
                @Override
                public void accept(Bitmap bitmap) throws Exception {
                    updatePhoto(bitmap);
                }
            });
    }
    
    private Bitmap loadBitmap() {
        mUrl = getIntent().getStringExtra(Constants.PHOTO);
        String key = MD5.generate(mUrl);
        
        BitmapPool pool = BitmapPool.getPool();
        BitmapWrapper bitmapWrapper = pool.get(key, true, true);
        return bitmapWrapper.getBitmap();
    }
    
    @Override
    protected void initView() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        mDragPhotoView = findViewById(R.id.photo_view);
    }
    
    @Override
    protected void initPresenter() {
    
    }
    
    @Override
    protected void initTheme() {
        // does not need any theme
    }
    
    private boolean hasStoragePermission(int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !PermissionUtils.hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, requestCode);
            return false;
        }
        return true;
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    startSaveImageDialogFragment();
                } else {
                    ToastUtils.showShortToast(getString(R.string.require_write_stroage_permission));
                }
                break;
            default:
                break;
        }
    }
    
    private void updatePhoto(Bitmap bitmap) {
        mDragPhotoView.setImageBitmap(bitmap);
        mDragPhotoView.setOnExitListener(new DragPhotoView.OnExitListener() {
            @Override
            public void onExit(DragPhotoView view, float translateX, float translateY, float w, float h) {
                onBackPressed();
            }
        });
        mDragPhotoView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (hasStoragePermission(PERMISSION_WRITE_EXTERNAL_STORAGE)) {
                    startSaveImageDialogFragment();
                    return true;
                }
                return false;
            }
        });
    }
    
    private void startSaveImageDialogFragment() {
        SaveImageDialogFragment dialog = new SaveImageDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.ARG_URL, mUrl);
        dialog.setArguments(bundle);
    
        dialog.show(getSupportFragmentManager(), null);
    }
}

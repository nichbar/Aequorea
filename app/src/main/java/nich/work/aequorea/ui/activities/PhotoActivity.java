package nich.work.aequorea.ui.activities;

import android.graphics.Bitmap;
import android.view.WindowManager;

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
import nich.work.aequorea.common.ui.activities.BaseActivity;
import nich.work.aequorea.common.ui.widget.DragPhotoView;

public class PhotoActivity extends BaseActivity {
    private DragPhotoView mDragPhotoView;
    
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
        String key = getIntent().getStringExtra(Constants.PHOTO);
    
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
    
    private void updatePhoto(Bitmap bitmap) {
        mDragPhotoView.setImageBitmap(bitmap);
        mDragPhotoView.setOnExitListener(new DragPhotoView.OnExitListener() {
            @Override
            public void onExit(DragPhotoView view, float translateX, float translateY, float w, float h) {
                onBackPressed();
            }
        });
    }
}

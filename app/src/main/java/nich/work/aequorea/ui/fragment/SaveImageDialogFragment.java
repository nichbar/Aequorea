package nich.work.aequorea.ui.fragment;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import android.view.View;

import com.zzhoujay.richtext.ext.MD5;
import com.zzhoujay.richtext.ig.BitmapPool;
import com.zzhoujay.richtext.ig.BitmapWrapper;

import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import nich.work.aequorea.Aequorea;
import nich.work.aequorea.R;
import nich.work.aequorea.common.Constants;
import nich.work.aequorea.common.utils.IOUtils;
import nich.work.aequorea.common.utils.IntentUtils;
import nich.work.aequorea.common.utils.ToastUtils;

public class SaveImageDialogFragment extends DialogFragment {
    
    private String mUrl;
    private Disposable mDisposable;
    
    protected @OnClick(R.id.tv_save_image)
    void saveImage() {
        BitmapPool pool = BitmapPool.getPool();
        final String fileName = MD5.generate(mUrl);
        BitmapWrapper bitmapWrapper = pool.get(fileName, true, true);
        
        final Bitmap bitmap = bitmapWrapper.getBitmap();
        
        mDisposable = Single.create(new SingleOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<String> e) throws Exception {
                String address = IOUtils.saveBitmapToExternalStorage(bitmap, fileName, 100);
                e.onSuccess(address);
            }
        })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Consumer<String>() {
                @Override
                public void accept(String s) throws Exception {
                    ToastUtils.showShortToast(getString(R.string.image_saved) + IOUtils.PIC_DIR);
                    MediaScannerConnection.scanFile(Aequorea.Companion.getApp()
                        .getApplicationContext(), new String[]{Environment.getExternalStorageDirectory()
                        .getPath().concat(IOUtils.PIC_DIR)}, null, new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(final String path, Uri uri) {
                            // do nothing.
                        }
                    });
                    
                    dismiss();
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    ToastUtils.showShortToast(getString(R.string.save_failed) + throwable.getMessage());
                    dismiss();
                }
            });
    }
    
    protected @OnClick(R.id.tv_open_in_browser)
    void openImageInNewWebPage() {
        IntentUtils.openInBrowser(getContext(), mUrl);
        dismiss();
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mUrl = getArguments().getString(Constants.ARG_URL);
    }
    
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = View.inflate(getContext(), R.layout.dialog_save_image, null);
        
        ButterKnife.bind(this, view);
        
        return new AlertDialog.Builder(getActivity()).setView(view).create();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }
}

package nich.work.aequorea.ui.fragment;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import nich.work.aequorea.R;
import nich.work.aequorea.common.utils.FontHelper;
import nich.work.aequorea.common.utils.ToastUtils;
import nich.work.aequorea.ui.activitiy.ArticleActivity;

public class FontDialogFragment extends DialogFragment {
    private ArticleActivity mView;
    
    @BindView(R.id.tv_font_family)
    protected TextView mFontFamilyTv;
    
    @OnClick(R.id.iv_increase_font_size)
    protected void increaseFontSize() {
        mView.setContentFontSize(FontHelper.increaseFontSize());
    }
    
    @OnClick(R.id.iv_decrease_font_size)
    protected void decreaseFontSize() {
        mView.setContentFontSize(FontHelper.decreaseFontSize());
    }
    
    @OnClick(R.id.iv_increase_font_spacing)
    protected void increaseFontSpacing() {
        mView.setContentLineSpacing(FontHelper.expandSpacing());
    }
    
    @OnClick(R.id.iv_decrease_font_spacing)
    protected void decreaseFontSpacing() {
        mView.setContentLineSpacing(FontHelper.condenseSpacing());
    }
    
    @OnLongClick(R.id.iv_font_size)
    protected boolean useDefaultFontSize() {
        mView.setContentFontSize(FontHelper.setDefaultFontSize());
        ToastUtils.showShortToast(getString(R.string.use_default_font_size));
        return true;
    }
    
    @OnLongClick(R.id.iv_font_spacing)
    protected boolean useDefaultFontSpacing() {
        mView.setContentLineSpacing(FontHelper.setDefaultSpacing());
        ToastUtils.showShortToast(getString(R.string.use_default_line_spacing));
        return true;
    }
    
    @OnClick(R.id.tv_font_family)
    protected void showFontFamilyPopup() {
        PopupMenu menu = new PopupMenu(mView, mFontFamilyTv);
        menu.getMenuInflater().inflate(R.menu.font_menu, menu.getMenu());
        
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_change_to_monospace:
                        FontHelper.setFontFamily(FontHelper.MONOSPACE);
                        mView.setContentFontFamily(FontHelper.MONOSPACE);
                        mFontFamilyTv.setText(convertFontName(FontHelper.MONOSPACE));
                        break;
                    case R.id.action_change_to_serif:
                        FontHelper.setFontFamily(FontHelper.SERIF);
                        mView.setContentFontFamily(FontHelper.SERIF);
                        mFontFamilyTv.setText(convertFontName(FontHelper.SERIF));
                        break;
                    case R.id.action_change_to_sans_serif:
                        FontHelper.setFontFamily(FontHelper.SANS_SERIF);
                        mView.setContentFontFamily(FontHelper.SANS_SERIF);
                        mFontFamilyTv.setText(convertFontName(FontHelper.SANS_SERIF));
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        menu.show();
    }
    
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mView = (ArticleActivity) getActivity();
        
        View view = View.inflate(getContext(), R.layout.dialog_font, null);
        ButterKnife.bind(this, view);
        
        mFontFamilyTv.setText(convertFontName(FontHelper.getFontFamily()));
    
        return new AlertDialog.Builder(getActivity()).setView(view).create();
    }
    
    private String convertFontName(String name) {
        switch (name) {
            case FontHelper.SANS_SERIF:
                return mView.getString(R.string.sans_serif);
            case FontHelper.MONOSPACE:
                return mView.getString(R.string.monospace);
            case FontHelper.SERIF:
                return mView.getString(R.string.serif);
            default:
                return mView.getString(R.string.sans_serif);
        }
    }
}

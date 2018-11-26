package nich.work.aequorea.common.ui.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckBox;
import nich.work.aequorea.R;
import nich.work.aequorea.common.Constants;
import nich.work.aequorea.common.utils.SPUtils;

public class ACheckBox extends FrameLayout implements View.OnClickListener {
    private AppCompatCheckBox mCheckBox;
    private TextView mTitle;
    private TextView mSummary;
    private String mKey;
    private OnClickCallback mClickCallback;

    public ACheckBox(@NonNull Context context) {
        this(context, null);
    }

    public ACheckBox(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ACheckBox(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        View.inflate(context, R.layout.layout_custom_checkbox, this);
        init(context, attrs);
        setOnClickListener(this);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ACheckBox);
        String title = typedArray.getString(R.styleable.ACheckBox_title);
        String summary = typedArray.getString(R.styleable.ACheckBox_summary);

        mCheckBox = findViewById(R.id.cb);
        mTitle = findViewById(R.id.tv_title);
        mSummary = findViewById(R.id.tv_summary);

        mTitle.setText(title);
        mSummary.setText(summary);

        typedArray.recycle();
    }

    @Override
    public void onClick(View view) {
        if (mKey != null) {
            boolean checked = !mCheckBox.isChecked();
            mCheckBox.setChecked(checked);
            if (!mKey.equals(Constants.THEME)) {
                SPUtils.setBoolean(mKey, checked);
            }
            if (mClickCallback != null) {
                mClickCallback.click(checked);
            }
        }
    }

    public void setColorStateList(ColorStateList stateList) {
        mCheckBox.setButtonTintList(stateList);
    }

    public void setTextColor(int titleColor, int summaryColor) {
        mTitle.setTextColor(titleColor);
        mSummary.setTextColor(summaryColor);
    }

    public void initStatus(String key, boolean status) {
        mKey = key;
        mCheckBox.setChecked(status);
    }

    public void setOnClickCallback(OnClickCallback callback) {
        mClickCallback = callback;
    }

    public interface OnClickCallback {
        void click(boolean status);
    }
}

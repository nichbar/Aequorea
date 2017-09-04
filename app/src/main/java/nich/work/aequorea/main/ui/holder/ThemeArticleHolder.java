package nich.work.aequorea.main.ui.holder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import nich.work.aequorea.R;
import nich.work.aequorea.main.model.mainpage.Datum;
import nich.work.aequorea.main.ui.adapters.MainArticleAdapter;

public class ThemeArticleHolder extends MainArticleAdapter.ViewHolder {

    @BindView(R.id.iv_author) ImageView authorImg;

    public ThemeArticleHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void onBindView(Context context, Datum data) {
        
    }
}

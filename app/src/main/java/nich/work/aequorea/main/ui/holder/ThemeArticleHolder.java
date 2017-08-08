package nich.work.aequorea.main.ui.holder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import nich.work.aequorea.R;
import nich.work.aequorea.common.ui.widget.glide.CircleTransformation;
import nich.work.aequorea.main.model.mainpage.Datum;
import nich.work.aequorea.main.ui.adapters.MainArticleAdapter;

public class ThemeArticleHolder extends MainArticleAdapter.ViewHolder {

    @BindView(R.id.iv_author) ImageView authorImg;

    public ThemeArticleHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        Context context = itemView.getContext();
    }

    @Override
    public void onBindView(Context context, Datum data) {
        Glide.with(context)
                .load(R.drawable.icon_author)
                .transform(new CircleTransformation(context))
                .into(authorImg);
    }
}

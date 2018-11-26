package nich.work.aequorea.view.author

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import butterknife.BindView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import nich.work.aequorea.R
import nich.work.aequorea.common.Constants
import nich.work.aequorea.common.arch.paging.ListAdapter
import nich.work.aequorea.common.arch.paging.ListFragment
import nich.work.aequorea.common.arch.paging.ListViewModel
import nich.work.aequorea.common.di.Injectable
import nich.work.aequorea.common.utils.DisplayUtils
import nich.work.aequorea.common.utils.ImageHelper
import nich.work.aequorea.common.utils.observeNonNull
import nich.work.aequorea.common.utils.viewModelProvider
import nich.work.aequorea.data.entity.Author
import nich.work.aequorea.data.entity.Datum
import nich.work.aequorea.view.shared.SimpleArticleListAdapter

class AuthorFragment : ListFragment<Datum, Datum>(), Injectable {

    @BindView(R.id.toolbar)
    lateinit var toolbar: Toolbar
    @BindView(R.id.tv_introduction)
    lateinit var introductionTv: TextView
    @BindView(R.id.tv_article_count)
    lateinit var articleCountTv: TextView
    @BindView(R.id.iv_author)
    lateinit var authorIv: ImageView
    @BindView(R.id.main_content)
    lateinit var coordinatorLayout: CoordinatorLayout
    @BindView(R.id.container_collapsing_toolbar)
    lateinit var collapsingToolbarLayout: CollapsingToolbarLayout
    @BindView(R.id.appbar)
    lateinit var appBar: AppBarLayout

    private lateinit var mViewModel: AuthorViewModel

    private var mIsAuthorDetailShowing = false

    override fun provideViewModel(): ListViewModel<Datum, Datum> {
        mViewModel = viewModelProvider(factory)
        mViewModel.authorId = arguments?.getLong(Constants.AUTHOR_ID)!!
        return mViewModel
    }

    override fun provideAdapter(): ListAdapter<Datum> {
        return SimpleArticleListAdapter()
    }

    override fun provideContentView(): View {
        return inflate(R.layout.fragment_author)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mViewModel.author = arguments?.get(Constants.AUTHOR) as Author

        toolbar.setNavigationIcon(R.drawable.icon_ab_back_material)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        collapsingToolbarLayout.title = " "
        coordinatorLayout.setPadding(0, DisplayUtils.getStatusBarHeight(resources), 0, 0)

        appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
            if (verticalOffset >= -15) {
                showAuthorDetail()
            } else {
                hideAuthorDetail()
            }
        })

        mViewModel.author?.let { updateAuthorImg(it) }
        mViewModel.authorInfo.observeNonNull(this) { updateAuthorInfo(it) }
    }

    private fun updateAuthorImg(author: Author) {
        collapsingToolbarLayout.title = author.name
        ImageHelper.loadImage(context, author.avatar, authorIv, true)
    }

    private fun updateAuthorInfo(author: Author) {
        updateAuthorImg(author)

        var intro = author.introduction
        if (!TextUtils.isEmpty(intro) && " " != intro) {
            if (intro.contains("。")) {
                val position = intro.indexOf("。")
                intro = intro.substring(0, position)
            }
            introductionTv.text = intro
        } else {
            introductionTv.setText(R.string.default_introduction)
        }

        articleCountTv.text = String.format(getString(R.string.article_count), author.articleCount)
    }

    private fun showAuthorDetail() {
        if (!mIsAuthorDetailShowing) {
            animateAuthorInfo(ANIMATE_SHOW)
            mIsAuthorDetailShowing = true
        }
    }

    private fun hideAuthorDetail() {
        if (mIsAuthorDetailShowing) {
            animateAuthorInfo(ANIMATE_HIDE)
            mIsAuthorDetailShowing = false
        }
    }

    private fun animateAuthorInfo(style: Int) {
        authorIv.animate()
                .alpha(style.toFloat())
                .scaleX(style.toFloat())
                .scaleY(style.toFloat())
                .setInterpolator(FastOutSlowInInterpolator())
                .start()

        introductionTv.animate().alpha(style.toFloat()).setDuration(100).start()
        articleCountTv.animate().alpha(style.toFloat()).setDuration(100).start()
    }

    companion object {
        private const val ANIMATE_SHOW = 1
        private const val ANIMATE_HIDE = 0
    }
}
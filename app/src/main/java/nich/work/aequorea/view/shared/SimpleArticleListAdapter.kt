package nich.work.aequorea.view.shared

import android.app.Activity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import nich.work.aequorea.R
import nich.work.aequorea.common.arch.paging.ListAdapter
import nich.work.aequorea.data.entity.Datum
import nich.work.aequorea.databinding.ItemArticleLiteBinding

class SimpleArticleListAdapter : ListAdapter<Datum>() {

    override fun onCreateListViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ArticleHolder(DataBindingUtil.inflate((parent.context as Activity).layoutInflater, R.layout.item_article_lite, parent, false))
    }

    override fun onBindListViewHolder(holder: RecyclerView.ViewHolder, item: Datum, position: Int) {
        when (holder) {
            is ArticleHolder -> {
                holder.binding.data = item
            }
        }
    }

    override fun areItemsTheSame(oldItem: Datum, newItem: Datum): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Datum, newItem: Datum): Boolean {
        return oldItem.likeTimes == oldItem.likeTimes
    }

    class ArticleHolder(var binding: ItemArticleLiteBinding) : RecyclerView.ViewHolder(binding.root)

}
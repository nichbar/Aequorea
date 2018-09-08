package nich.work.aequorea.view.shared

import android.app.Activity
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
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
                holder.binding.data = item.data[0]
            }
        }
    }

    override fun areItemsTheSame(oldItem: Datum, newItem: Datum): Boolean {
        return oldItem.data[0].id == newItem.data[0].id
    }

    override fun areContentsTheSame(oldItem: Datum, newItem: Datum): Boolean {
        return oldItem.data[0].likeTimes == oldItem.data[0].likeTimes
    }

    class ArticleHolder(var binding: ItemArticleLiteBinding) : RecyclerView.ViewHolder(binding.root)

}
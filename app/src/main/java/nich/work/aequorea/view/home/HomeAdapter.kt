package nich.work.aequorea.view.home

import android.app.Activity
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import nich.work.aequorea.R
import nich.work.aequorea.common.arch.paging.ListAdapter
import nich.work.aequorea.data.entity.Datum
import nich.work.aequorea.databinding.ItemArticleBinding

class HomeAdapter : ListAdapter<Datum>() {

    override fun onCreateListViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ArticleHolder(DataBindingUtil.inflate((parent.context as Activity).layoutInflater, R.layout.item_article, parent, false))
    }

    override fun onBindListViewHolder(holder: RecyclerView.ViewHolder, item: Datum, position: Int) {
        when (holder) {
            is ArticleHolder -> {
                holder.binding.data = item.data[0]
                holder.binding.executePendingBindings()
            }
        }
    }

    internal class ArticleHolder(var binding: ItemArticleBinding) : RecyclerView.ViewHolder(binding.root)

}
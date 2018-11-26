package nich.work.aequorea.view.home

import android.app.Activity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
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

    override fun areItemsTheSame(oldItem: Datum, newItem: Datum): Boolean {
        return oldItem.data[0].id == newItem.data[0].id
    }

    override fun areContentsTheSame(oldItem: Datum, newItem: Datum): Boolean {
        return oldItem.data[0].likeTimes == oldItem.data[0].likeTimes
    }

    class ArticleHolder(var binding: ItemArticleBinding) : RecyclerView.ViewHolder(binding.root)

}
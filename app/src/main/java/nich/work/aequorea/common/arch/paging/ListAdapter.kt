package nich.work.aequorea.common.arch.paging

import android.annotation.SuppressLint
import android.app.Activity
import android.databinding.DataBindingUtil
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import io.reactivex.Single
import io.reactivex.SingleOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import nich.work.aequorea.R
import nich.work.aequorea.databinding.ItemFooterBinding

abstract class ListAdapter<T> : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_FOOTER = 999
    }

    var dataList = listOf<T>()

    var footerStatus: FooterStatus = FooterStatus.LOADING

    var retryCallback: RetryCallback? = null


    protected fun getItem(position: Int): T {
        return dataList[position]
    }

    @SuppressLint("CheckResult")
    open fun submitList(list: List<T>) {

        if (list.isEmpty()) {
            dataList = arrayListOf()
            notifyDataSetChanged()
            return
        }

        if (list.size < dataList.size) {
            dataList = ArrayList(list)
            notifyDataSetChanged()
            return
        }

        // Calculate difference.
        Single.create(SingleOnSubscribe<DiffUtil.DiffResult> {
            val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return dataList[oldItemPosition] == list[newItemPosition]
                }

                override fun getOldListSize(): Int {
                    return dataList.size
                }

                override fun getNewListSize(): Int {
                    return list.size
                }

                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return areContentsTheSame(dataList[oldItemPosition], list[newItemPosition])
                }
            })
            it.onSuccess(result)
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
            dataList = ArrayList(list)
            it.dispatchUpdatesTo(this)
        }, {
            it.printStackTrace()
        })
    }

    open fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }

    override fun getItemCount(): Int {
        return if (containsFooter() && dataList.isNotEmpty()) {
            dataList.size + 1
        } else {
            dataList.size
        }
    }

    abstract fun onCreateListViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_FOOTER -> FooterHolder(DataBindingUtil.inflate((parent.context as Activity).layoutInflater, R.layout.item_footer, parent, false))
            else -> onCreateListViewHolder(parent, viewType)
        }
    }

    abstract fun onBindListViewHolder(holder: RecyclerView.ViewHolder, item: T, position: Int)

    final override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is FooterHolder -> {
                when (footerStatus) {
                    FooterStatus.REACH_THE_END -> {
                        holder.binding.pgLoadingMore.visibility = View.GONE
                        holder.binding.tvFooter.text = holder.binding.root.context.getString(R.string.footer_hint_reach_the_end)
                        holder.binding.root.setOnClickListener(null)
                    }

                    FooterStatus.NETWORK_ERROR -> {
                        holder.binding.pgLoadingMore.visibility = View.GONE
                        holder.binding.tvFooter.text = holder.binding.root.context.getString(R.string.footer_hint_network_error)
                        holder.binding.root.setOnClickListener { retryCallback?.retry() }
                    }

                    FooterStatus.LOADING -> {
                        holder.binding.pgLoadingMore.visibility = View.VISIBLE
                        holder.binding.tvFooter.text = ""
                        holder.binding.root.setOnClickListener(null)
                    }
                }
            }
            else -> onBindListViewHolder(holder, getItem(position), position)
        }
    }

    open fun getItemViewType(item: T): Int {
        return 0
    }

    final override fun getItemViewType(position: Int): Int {
        return when {
            containsFooter() && position == itemCount - 1 -> TYPE_FOOTER

            else -> getItemViewType(getItem(position))
        }
    }

    open fun containsFooter(): Boolean {
        return true
    }

    fun updateFooterStatus(status: FooterStatus) {
        footerStatus = status
        if (containsFooter()) {
            notifyItemChanged(itemCount - 1)
        }
    }


    internal class FooterHolder(var binding: ItemFooterBinding) : RecyclerView.ViewHolder(binding.root)

    interface RetryCallback {
        fun retry()
    }

    enum class FooterStatus {
        LOADING,

        NETWORK_ERROR,

        REACH_THE_END
    }

}
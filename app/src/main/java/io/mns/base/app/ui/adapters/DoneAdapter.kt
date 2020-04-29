package io.mns.base.app.ui.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.mns.base.app.data.DoneItem
import io.mns.base.app.databinding.ItemDoneBinding

class DoneAdapter() :
    RecyclerView.Adapter<DoneAdapter.DoneViewHolder>() {
    private var data: List<DoneItem> = mutableListOf()

    fun setData(nextData: List<DoneItem>) {
        if (data.isEmpty()) {
            data = nextData
            notifyItemRangeInserted(0, data.size)
        } else {
            val callback = object : DiffUtil.Callback() {
                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                    data[oldItemPosition].id == nextData[newItemPosition].id

                override fun getOldListSize() = data.size

                override fun getNewListSize() = nextData.size

                override fun areContentsTheSame(
                    oldItemPosition: Int,
                    newItemPosition: Int
                ) = data[oldItemPosition] == nextData[newItemPosition]
            }

            val result = DiffUtil.calculateDiff(callback)
            data = nextData
            result.dispatchUpdatesTo(this)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoneViewHolder {
        return DoneViewHolder.of(LayoutInflater.from(parent.context), parent)
    }

    override fun onBindViewHolder(holder: DoneViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemId(position: Int): Long {
        return data[position].id.hashCode().toLong()
    }

    override fun getItemCount(): Int {
        return data.size
    }


    class DoneViewHolder(
        private val binding: ItemDoneBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DoneItem) {
            binding.item = item
            binding.title.apply {
                paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }
            binding.executePendingBindings()
        }

        companion object {
            fun of(
                inflater: LayoutInflater,
                parent: ViewGroup
            ): DoneViewHolder {
                return DoneViewHolder(
                    ItemDoneBinding.inflate(
                        inflater,
                        parent,
                        false
                    )
                )
            }
        }
    }
}
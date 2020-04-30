package io.mns.base.app.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.mns.base.app.data.TodoItem
import io.mns.base.app.databinding.ItemTodoBinding
import io.mns.base.app.ui.adapters.callbacks.TodoClickCallBack

class TodoAdapter(private val clickCallBack: TodoClickCallBack) :
    RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {
    private var data: List<TodoItem> = mutableListOf()

    fun setData(nextData: List<TodoItem>) {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        return TodoViewHolder.of(LayoutInflater.from(parent.context), parent, clickCallBack)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemId(position: Int): Long {
        return data[position].id.hashCode().toLong()
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class TodoViewHolder(
        private val binding: ItemTodoBinding,
        private val clickCallBack: TodoClickCallBack
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: TodoItem) {
            binding.item = item
            binding.done.isChecked = false
            binding.done.setOnCheckedChangeListener { _, _ ->
                clickCallBack.todoDone(item)
            }
            binding.executePendingBindings()
        }

        companion object {
            fun of(
                inflater: LayoutInflater,
                parent: ViewGroup,
                clickCallBack: TodoClickCallBack
            ): TodoViewHolder {
                return TodoViewHolder(
                    ItemTodoBinding.inflate(
                        inflater,
                        parent,
                        false
                    ),
                    clickCallBack
                )
            }
        }
    }
}

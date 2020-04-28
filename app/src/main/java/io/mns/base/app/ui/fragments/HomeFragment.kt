package io.mns.base.app.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import io.mns.base.app.R
import io.mns.base.app.data.TodoItem
import io.mns.base.app.databinding.FragmentHomeBinding
import io.mns.base.app.ui.adapters.TodoAdapter
import io.mns.base.app.ui.adapters.callbacks.TodoClickCallBack
import io.mns.base.app.ui.viewmodels.HomeViewModel


class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home), TodoClickCallBack {
    private val viewModel by viewModels<HomeViewModel>()
    private lateinit var adapter: TodoAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadData()
        binding.add.setOnClickListener {
            viewModel.insert()
        }
    }

    private fun loadData() {
        adapter = TodoAdapter(this)
        binding.list.setHasFixedSize(true)
        adapter.setHasStableIds(true)
        binding.list.adapter = adapter

        viewModel.load().observe(viewLifecycleOwner, Observer {
            if (it != null) {
                adapter.setData(it)
            }
        })
    }

    override fun todoChanged(todo: TodoItem, checked: Boolean) {
        todo.done = checked
        viewModel.update(todo)
    }
}

package io.mns.base.app.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import io.mns.base.app.ADD_FRAGMENT_TAG
import io.mns.base.app.R
import io.mns.base.app.data.TodoItem
import io.mns.base.app.databinding.FragmentHomeBinding
import io.mns.base.app.ui.MainActivity
import io.mns.base.app.ui.adapters.TodoAdapter
import io.mns.base.app.ui.adapters.callbacks.TodoClickCallBack
import io.mns.base.app.ui.viewmodels.HomeViewModel

class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home), TodoClickCallBack {
    private val viewModel by viewModels<HomeViewModel>()
    private lateinit var adapter: TodoAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        binding.vm = viewModel
        initiateList()
        loadData()
        observeAdd()
        observeSetting()
    }

    private fun observeSetting() {
        (requireActivity() as MainActivity).showBottomNav()
        viewModel.settingClicked.observe(
            viewLifecycleOwner,
            Observer {
                if (it) {
                    viewModel.settingHandled()
                    Navigation.findNavController(requireView())
                        .navigate(R.id.action_homeFragment_to_settingFragment)
                    (requireActivity() as MainActivity).hideBottomNav()
                }
            }
        )
    }

    private fun observeAdd() {
        viewModel.addClicked.observe(
            viewLifecycleOwner,
            Observer {
                if (it) {
                    viewModel.addHandled()
                    AddFragment(::addTodo).show(parentFragmentManager, ADD_FRAGMENT_TAG)
                }
            }
        )
    }

    private fun addTodo(title: String) {
        viewModel.insertItem(title)
        Handler().post {
            binding.list.layoutManager?.scrollToPosition(0)
        }
    }

    private fun loadData() {
        viewModel.loadItems().observe(
            viewLifecycleOwner,
            Observer {
                binding.emptyList = it.isEmpty()
                adapter.setData(it)
            }
        )
    }

    private fun initiateList() {
        adapter = TodoAdapter(this)
        binding.list.setHasFixedSize(true)
        adapter.setHasStableIds(true)
        binding.list.adapter = adapter
    }

    override fun todoDone(todo: TodoItem) {
        Handler().postDelayed(
            {
                viewModel.done(todo)
            },
            800
        )
    }
}

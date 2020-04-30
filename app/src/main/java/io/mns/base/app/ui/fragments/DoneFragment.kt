package io.mns.base.app.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import io.mns.base.app.R
import io.mns.base.app.databinding.FragmentDoneBinding
import io.mns.base.app.ui.MainActivity
import io.mns.base.app.ui.adapters.DoneAdapter
import io.mns.base.app.ui.viewmodels.DoneViewModel

class DoneFragment : BaseFragment<FragmentDoneBinding>(R.layout.fragment_done) {
    private val viewModel by viewModels<DoneViewModel>()
    private lateinit var adapter: DoneAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.vm = viewModel
        initList()
        loadData()
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
                        .navigate(R.id.action_doneFragment_to_settingFragment)
                    (requireActivity() as MainActivity).hideBottomNav()
                }
            }
        )
    }

    private fun initList() {
        adapter = DoneAdapter()
        binding.list.setHasFixedSize(true)
        adapter.setHasStableIds(true)
        binding.list.adapter = adapter
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
}

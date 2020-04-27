package io.mns.base.app.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import io.mns.base.app.R
import io.mns.base.app.databinding.FragmentHomeBinding
import io.mns.base.app.ui.viewmodels.HomeViewModel


class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {
    private val viewModel by viewModels<HomeViewModel>()

    init {
        loadData()
    }

    private fun loadData() {

    }
}

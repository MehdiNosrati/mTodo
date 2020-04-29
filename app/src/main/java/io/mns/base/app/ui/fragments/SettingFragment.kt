package io.mns.base.app.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import io.mns.androidlib.toggleTheme
import io.mns.base.app.R
import io.mns.base.app.databinding.FragmentSettingBinding
import io.mns.base.app.ui.viewmodels.SettingViewModel

class SettingFragment : BaseFragment<FragmentSettingBinding>(R.layout.fragment_setting) {
    private val viewModel by viewModels<SettingViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.vm = viewModel
        observeBack()
        handleTheme()
    }

    private fun handleTheme() {
        viewModel.toggleTheme.observe(viewLifecycleOwner, Observer {
            if (it) {
                viewModel.themeToggled()
                resources.toggleTheme()
            }
        })

    }

    private fun observeBack() {
        viewModel.backClicked.observe(viewLifecycleOwner, Observer {
            if (it) {
                viewModel.backHandled()
                Navigation.findNavController(requireView()).navigateUp()
            }
        })
    }

}

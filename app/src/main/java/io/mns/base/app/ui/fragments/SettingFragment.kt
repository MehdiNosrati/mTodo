package io.mns.base.app.ui.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.core.content.edit
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import io.mns.androidlib.isDark
import io.mns.base.app.IS_DARK
import io.mns.base.app.R
import io.mns.base.app.THEME_PREFS_NAME
import io.mns.base.app.databinding.FragmentSettingBinding
import io.mns.base.app.ui.MainActivity
import io.mns.base.app.ui.viewmodels.SettingViewModel

class SettingFragment : BaseFragment<FragmentSettingBinding>(R.layout.fragment_setting) {
    private val viewModel by viewModels<SettingViewModel>()
    private lateinit var sharedPreferences: SharedPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.vm = viewModel
        sharedPreferences =
            requireContext().applicationContext.getSharedPreferences(THEME_PREFS_NAME, Context.MODE_PRIVATE)
        observeBack()
        handleThemeChange()
    }

    private fun handleThemeChange() {
        viewModel.toggleTheme.observe(
            viewLifecycleOwner,
            Observer {
                if (it) {
                    viewModel.themeToggled()
                    sharedPreferences.edit {
                        putBoolean(IS_DARK, !resources.isDark())
                    }
                    Handler().postDelayed(
                        {
                            if (activity != null) {
                                (activity as MainActivity).toggleTheme()
                            }
                        },
                        200
                    )
                }
            }
        )
    }

    private fun observeBack() {
        viewModel.backClicked.observe(
            viewLifecycleOwner,
            Observer {
                if (it) {
                    viewModel.backHandled()
                    Navigation.findNavController(requireView()).navigateUp()
                }
            }
        )
    }
}

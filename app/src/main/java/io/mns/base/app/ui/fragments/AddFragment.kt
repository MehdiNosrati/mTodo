package io.mns.base.app.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.mns.base.app.databinding.FragmentAddBinding
import io.mns.base.app.ui.viewmodels.AddViewModel

typealias OnInsert = (title: String) -> Unit

class AddFragment private constructor(private val insert: OnInsert) : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentAddBinding
    private val viewModel by viewModels<AddViewModel>()

    companion object {
        fun getInstance(insert: OnInsert): AddFragment {
            return AddFragment(insert)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.vm = viewModel
        observeAdd()
        observeBack()
    }

    private fun observeBack() {
        viewModel.backClicked.observe(
            viewLifecycleOwner,
            Observer {
                if (it) {
                    viewModel.backHandled()
                    requireActivity().runOnUiThread {
                        dismiss()
                    }
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
                    val title = binding.title
                    if (title != null && title.isNotEmpty()) {
                        binding.add.setOnClickListener(null)
                        requireActivity().runOnUiThread {
                            insert(title)
                            dismiss()
                        }
                    }
                }
            }
        )
    }
}

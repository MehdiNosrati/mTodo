package io.mns.base.app.utils

import androidx.databinding.BindingAdapter
import com.github.lguipeng.library.animcheckbox.AnimCheckBox

@BindingAdapter("onCheckedChange")
fun onCheckedChange(v: AnimCheckBox, action: () -> Unit) {
    v.setOnCheckedChangeListener{_,_ ->
        action()
    }
}
package io.mns.base.app.utils

import android.view.View
import androidx.databinding.BindingAdapter
import com.github.lguipeng.library.animcheckbox.AnimCheckBox

@BindingAdapter("onCheckedChange")
fun onCheckedChange(v: AnimCheckBox, action: () -> Unit) {
    v.setOnCheckedChangeListener { _, _ ->
        action()
    }
}

@BindingAdapter("isVisible")
fun isVisible(v: View, visible: Boolean) {
    v.visibility = if (visible) View.VISIBLE else View.GONE
}

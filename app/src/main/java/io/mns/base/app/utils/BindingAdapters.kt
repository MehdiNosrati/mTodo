package io.mns.base.app.utils

import android.view.View
import androidx.databinding.BindingAdapter

@BindingAdapter("isVisible")
fun isVisible(v: View, visible: Boolean) {
    v.visibility = if (visible) View.VISIBLE else View.GONE
}

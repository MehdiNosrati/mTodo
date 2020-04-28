package io.mns.base.app.ui.adapters.callbacks

import io.mns.base.app.data.TodoItem

interface TodoClickCallBack {
    fun todoChanged(todo: TodoItem, checked: Boolean)
}
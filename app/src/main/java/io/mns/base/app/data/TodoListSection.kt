package io.mns.base.app.data

sealed class TodoListSection {
    data class Header(
        val hourStartMs: Long,
        val label: String,
        val key: String = "h_${hourStartMs}_${label.hashCode()}"
    ) : TodoListSection()
    data class Item(val todo: TodoItem) : TodoListSection()
}

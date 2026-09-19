package io.mns.base.app.data

sealed class TodoListSection {
    data class Header(val hourStartMs: Long, val label: String) : TodoListSection()
    data class Item(val todo: TodoItem) : TodoListSection()
}

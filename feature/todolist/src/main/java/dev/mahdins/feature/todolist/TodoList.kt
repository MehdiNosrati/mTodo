package dev.mahdins.feature.todolist

import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun TodoList(todoViewModel: TodoViewModel) {
    Text(todoViewModel.text())
}
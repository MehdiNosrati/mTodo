package dev.mahdins.feature.donelist

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable


@Composable
fun DoneList(doneViewModel: DoneViewModel) {
    Text(text = doneViewModel.text())
}
package io.mns.base.app.data

import io.mns.base.app.util.generateRandomUuid
import kotlinx.serialization.Serializable

@Serializable
data class Subtask(
    val id: String = generateRandomUuid(),
    val title: String,
    val isDone: Boolean = false
)

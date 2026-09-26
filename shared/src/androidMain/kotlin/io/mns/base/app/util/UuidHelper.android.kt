package io.mns.base.app.util

import java.util.UUID

actual fun generateRandomUuid(): String = UUID.randomUUID().toString()

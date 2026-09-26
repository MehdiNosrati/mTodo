package io.mns.base.app.util

import platform.Foundation.NSUUID

actual fun generateRandomUuid(): String = NSUUID().UUIDString

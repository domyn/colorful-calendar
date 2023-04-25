package com.github.domyn.colorfulcalendar.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

@Suppress("DeferredIsResult")
internal suspend fun <T> io(block: suspend CoroutineScope.() -> T) = coroutineScope { async(Dispatchers.IO, block = block) }

package com.hyunju.weatherwear.util.event

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object UpdateEventBus {
    private val _events = MutableSharedFlow<UpdateEvent>()
    private val events = _events.asSharedFlow()

    suspend fun invokeEvent(event: UpdateEvent) = _events.emit(event)

    suspend fun subscribeEvent(onEvent: (UpdateEvent) -> Unit) {
        events.collect {
            onEvent(it)
        }
    }
}

enum class UpdateEvent {
    Updated,
    UnUpdated
}
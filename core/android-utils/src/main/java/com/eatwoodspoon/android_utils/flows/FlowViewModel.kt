package com.eatwoodspoon.android_utils.flows

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

open class FlowViewModel<State, Events>(initialState: State) : ViewModel() {

    protected val _state = MutableStateFlow(initialState)
    val state = _state.toImmutable()

    protected val _events = MutableSharedFlow<Events>()
    val events = _events.toImmutable()

}

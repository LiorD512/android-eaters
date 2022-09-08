package com.eatwoodspoon.android_utils.flows

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

fun <T> MutableStateFlow<T>.toImmutable(): StateFlow<T> = this

fun <T> MutableSharedFlow<T>.toImmutable(): SharedFlow<T> = this

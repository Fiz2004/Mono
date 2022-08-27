package com.fiz.mono.base.android.utils

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

/**
 * Launches a new coroutine and repeats `block` every time the Fragment's viewLifecycleOwner
 * is in and out of `minActiveState` lifecycle state.
 */
inline fun Fragment.launchAndRepeatWithViewLifecycle(
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    crossinline block: suspend CoroutineScope.() -> Unit
) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.lifecycle.repeatOnLifecycle(minActiveState) {
            block()
        }
    }
}

inline fun <ViewEffect> Fragment.collectUiEffect(
    viewEffect: MutableSharedFlow<ViewEffect>,
    crossinline reactTo: (viewEffect: ViewEffect) -> Unit,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED
) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.lifecycle.repeatOnLifecycle(minActiveState) {
            viewEffect.collect {
                reactTo.invoke(it)
            }
        }
    }
}

inline fun <ViewState> Fragment.collectUiState(
    viewState: MutableSharedFlow<ViewState>,
    crossinline updateScreenState: (viewState: ViewState) -> Unit,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED
) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.lifecycle.repeatOnLifecycle(minActiveState) {
            viewState.collect {
                updateScreenState.invoke(it)
            }
        }
    }
}
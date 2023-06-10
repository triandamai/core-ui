/*
 * Copyright (c) 2023 trian.app.
 */

package app.trian.core.ui

import androidx.compose.runtime.Composable

@Composable
inline fun <State, Event> UIWrapper(
    invoker: UIListener<State, Event>,
    content: @Composable UIListener<State, Event>.() -> Unit
) { content(invoker) }

@Composable
inline fun <State, Data, Event> UiWrapperData(
    invoker: UIListenerData<State, Data, Event>,
    content: @Composable UIListenerData<State, Data, Event>.() -> Unit
) { content(invoker) }

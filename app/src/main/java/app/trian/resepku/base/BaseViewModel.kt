/*
 * Copyright (c) 2023 trian.app.
 */

package app.trian.resepku.base

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.trian.resepku.base.extensions.backPressedAndClose
import app.trian.resepku.base.extensions.getString
import app.trian.resepku.base.extensions.hideBottomSheet
import app.trian.resepku.base.extensions.navigate
import app.trian.resepku.base.extensions.navigateAndReplace
import app.trian.resepku.base.extensions.navigateAndReplaceAll
import app.trian.resepku.base.extensions.navigateSingleTop
import app.trian.resepku.base.extensions.navigateUp
import app.trian.resepku.base.extensions.runSuspend
import app.trian.resepku.base.extensions.showBottomSheet
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class BaseViewModel<State : Parcelable, Action>(
    private val initialState: State,
) : ViewModel() {
    companion object {
        val dispatcher: CoroutineDispatcher = Dispatchers.Default
    }

    private val _uiState: MutableStateFlow<State> = MutableStateFlow(initialState)
    val uiState get() = _uiState.asStateFlow()

    private val action = Channel<Action>(Channel.UNLIMITED)

    private lateinit var _app: ApplicationState
    protected fun onEvent(
        block: suspend (Action) -> Unit
    ) {
        async {
            action
                .consumeAsFlow()
                .collect {
                    block(it)
                }
        }
    }


    protected inline fun async(crossinline block: suspend () -> Unit) = with(viewModelScope) {
        launch { block() }
    }

    protected inline fun asyncWithState(crossinline block: suspend State.() -> Unit) =
        with(viewModelScope) {
            launch { block(uiState.value) }
        }

    protected suspend inline fun <T> await(crossinline block: suspend () -> T): T =
        withContext(dispatcher) { block() }

    protected inline fun asyncFlow(crossinline block: suspend () -> Unit) = async {
        withContext(dispatcher) {
            block()
        }
    }

    fun runSuspend(cb: suspend CoroutineScope. () -> Unit) = _app.runSuspend(block = cb)

    protected abstract fun handleActions()
    fun commit(state: State) {
        _uiState.tryEmit(state)
    }

    fun commit(state: State.() -> State) {
        _uiState.tryEmit(state(uiState.value))
    }

    protected infix fun BaseViewModel<State, Action>.commit(s: State.() -> State) {
        commit(s(uiState.value))
    }

    fun resetState() {
        commit(initialState)
    }

    fun dispatch(e: Action) = async { action.send(e) }

    fun setAppState(appState: ApplicationState) {
        _app = appState
    }

    //region snakcbar

    //

    //region navigation
    fun backAndClose() = _app.backPressedAndClose()
    fun navigateUp() = _app.navigateUp()
    fun navigate(routeName: String, vararg args: String) = _app.navigate(routeName, *args)
    fun navigateSingleTop(routeName: String, vararg args: String) =
        _app.navigateSingleTop(routeName, *args)

    fun navigateSingleTop(routeName: String) = _app.navigateSingleTop(routeName)
    fun navigateAndReplace(routeName: String, vararg args: String) =
        _app.navigateAndReplace(routeName, *args)

    fun navigateAndReplaceAll(routeName: String, vararg args: String) =
        _app.navigateAndReplaceAll(routeName, *args)

    fun navigateAndReplaceAll(routeName: String) = _app.navigateAndReplaceAll(routeName)

    //end region
    //region string
    fun getString(res: Int) = _app.getString(res)
    fun getString(res: Int, vararg params: String) = _app.getString(res, *params)
    //end region

    //region bottom sheet
    fun showBottomSheet() = _app.showBottomSheet()
    fun hideBottomSheet() = _app.hideBottomSheet()
    //end region
    override fun onCleared() {
        super.onCleared()

        action.cancel()
        _uiState.tryEmit(initialState)
        async { currentCoroutineContext().cancel() }
    }
}

abstract class BaseViewModelData<State : Parcelable, DataState : Parcelable, Action>(
    initialState: State,
    private val initialData: DataState
) : BaseViewModel<State, Action>(initialState) {
    private val _uiDataState: MutableStateFlow<DataState> = MutableStateFlow(initialData)
    val uiDataState get() = _uiDataState.asStateFlow()

    protected inline fun asyncWithData(crossinline block: suspend DataState.() -> Unit) =
        with(viewModelScope) {
            launch { block(uiDataState.value) }
        }

    fun commitData(dataState: DataState) {
        _uiDataState.tryEmit(dataState)
    }

    fun commitData(dataState: DataState.() -> DataState) {
        commitData(dataState(uiDataState.value))
    }

    fun resetDataState() {
        commitData(initialData)
    }


    override fun onCleared() {
        super.onCleared()
        _uiDataState.tryEmit(initialData)
    }
}

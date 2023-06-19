/*
 * Copyright (c) 2023 trian.app.
 */

package app.trian.mvi.ui.internal


import android.content.Context
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue.Hidden
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import app.trian.mvi.ui.internal.listener.BaseEventListener
import app.trian.mvi.ui.internal.listener.EventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class UIController(
    private val router: NavHostController,
    private val bottomSheetState: ModalBottomSheetState,
    private val scope: CoroutineScope,
    private val event: BaseEventListener = EventListener(),
    private val context: Context
) {

    var snackbarHostState by mutableStateOf(SnackbarHostState())

    val navigator get() = Navigator(router, event)

    val bottomSheet
        get() = BottomSheet(
            bottomSheetState,
            scope
        )
    val keyboard get() = Keyboard(context)

    val snackBar
        get() = Snackbar(
            snackbarHostState,
            context,
            scope
        )

    val toast
        get() = ToastMvi(
            context
        )


    //region event

    //region string
    fun getString(res: Int): String =
        context.getString(res)

    fun getString(res: Int, vararg params: String): String =
        context.getString(res, *params)
    //end region
}

@Composable
fun rememberUIController(
    event: BaseEventListener = EventListener(),
    router: NavHostController = rememberNavController(),
    scope: CoroutineScope = rememberCoroutineScope(),
    allowHideBottomSheet: Boolean = false,
    context: Context = LocalContext.current
): UIController {
    val state = rememberModalBottomSheetState(
        initialValue = Hidden,
        confirmValueChange = {
            if (allowHideBottomSheet) true
            else event.changeBottomSheet(it)
        }
    )
    return remember {
        UIController(
            router,
            state,
            scope,
            event,
            context
        )
    }
}
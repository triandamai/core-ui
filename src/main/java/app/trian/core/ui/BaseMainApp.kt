/*
 * Copyright (c) 2023 trian.app.
 */

package app.trian.core.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bluehabit.core.ui.theme.BudgetKuTheme

@Composable
fun BaseMainApp(
    controller: UIController = rememberUIController(),
    content: @Composable (appState: UIController) -> Unit = { }
) {
    BudgetKuTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.surface
        ) {
            content.invoke(controller)
        }
    }
}
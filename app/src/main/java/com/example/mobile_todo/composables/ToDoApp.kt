package com.example.mobile_todo.composables

import android.app.Activity
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobile_todo.viewmodel.TaskViewModel
import com.example.mobile_todo.utils.isTablet
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.LaunchedEffect
import com.example.mobile_todo.database.TaskWithAttachemnts
import com.example.mobile_todo.database.Task


@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun ToDoApp() {

    val context = LocalContext.current
    val taskViewModel: TaskViewModel = viewModel()
    val isTablet = isTablet(context)
    val activity = context as? Activity
    val windowSizeClass = activity?.let { calculateWindowSizeClass(it) }

    LaunchedEffect(Unit) {
        taskViewModel.init(context)
    }

    val useTabletLayout = isTablet &&
            (windowSizeClass?.widthSizeClass == WindowWidthSizeClass.Expanded ||
                    windowSizeClass?.widthSizeClass == WindowWidthSizeClass.Medium)

    if (useTabletLayout) {
        TabletLayout()
    } else {
        PhoneLayout(taskViewModel)
    }

}



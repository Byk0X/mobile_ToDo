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


@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun ToDoApp(viewModel: TaskViewModel = viewModel()) {

    val context = LocalContext.current


    val isTablet = isTablet(context)


    val activity = context as? Activity
    val windowSizeClass = activity?.let { calculateWindowSizeClass(it) }


    val useTabletLayout = isTablet &&
            (windowSizeClass?.widthSizeClass == WindowWidthSizeClass.Expanded ||
                    windowSizeClass?.widthSizeClass == WindowWidthSizeClass.Medium)

    if (useTabletLayout) {
        TabletLayout()
    } else {
        PhoneLayout()
    }

}



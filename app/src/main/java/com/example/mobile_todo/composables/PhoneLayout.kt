package com.example.mobile_todo.composables

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import com.example.mobile_todo.utils.TabItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.util.fastForEachIndexed
import com.example.mobile_todo.viewmodel.TaskViewModel
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PhoneLayout(viewModel: TaskViewModel, taskId: Int? = null){

    val tabItems = listOf(
        TabItem.IconTab(Icons.Default.Home, "Lista"),
        TabItem.IconTab(Icons.Rounded.Settings, "Ustawienia")

    )

    val pagerState = rememberPagerState(initialPage = 0, pageCount = {tabItems.size})
    val coroutineScope = rememberCoroutineScope()

    Column {
        CenterAlignedTopAppBar(
            title = {
                Text(text = tabItems[pagerState.currentPage].label)
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        )

        PrimaryTabRow(
            selectedTabIndex = pagerState.currentPage
        ) {
            tabItems.fastForEachIndexed { index, tab ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch{
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = {
                        when (tab) {
                            else -> Text(tab.label)
                        }

                    }
                )
            }
        }
        Box(modifier = Modifier.weight(1f)) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 ->  TaskList(viewModel)
                    1 ->  Settings(viewModel, onBack = {})

                }
            }
        }
    }

}
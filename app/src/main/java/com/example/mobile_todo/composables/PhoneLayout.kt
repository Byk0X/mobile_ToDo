package com.example.mobile_todo.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import com.example.mobile_todo.utils.TabItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PhoneLayout(){

    val tabItems = listOf(
        TabItem.IconTab(Icons.Default.Home, "Podstawowe"),
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
                    0 -> LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            TaskList()
                        }
                    }
                }
            }
        }
    }

}
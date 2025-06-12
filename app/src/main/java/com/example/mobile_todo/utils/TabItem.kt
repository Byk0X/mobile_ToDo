package com.example.mobile_todo.utils

import androidx.compose.ui.graphics.vector.ImageVector

sealed class TabItem (val label: String) {
    class TextTab (label : String) : TabItem(label)
    class IconTab(val icon: ImageVector, label: String) : TabItem(label)
}
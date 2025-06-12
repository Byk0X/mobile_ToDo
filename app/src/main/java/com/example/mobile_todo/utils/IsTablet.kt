package com.example.mobile_todo.utils

import android.content.Context
import kotlin.math.pow
import kotlin.math.sqrt

fun isTablet(context: Context): Boolean{

    val displayMetrics = context.resources.displayMetrics
    val widthInches = displayMetrics.widthPixels / displayMetrics.xdpi
    val heightInches = displayMetrics.heightPixels / displayMetrics.ydpi
    val diagonalInches = sqrt(widthInches.toDouble().pow(2.0) + heightInches.toDouble().pow(2.0))

    return diagonalInches >= 7.0
}
package com.example.mobile_todo.utils

import android.content.Context
import android.net.Uri
import java.io.File

fun deleteAttachmentFile(context: Context, uri: Uri) {
    try {
        val file = File(uri.path ?: return)
        if (file.exists()) {
            file.delete()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
package com.example.mobile_todo.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID

fun copyUriToInternalStorage(context: Context, uri: Uri): String? {
    return try {
        val contentResolver = context.contentResolver

        val mimeType = contentResolver.getType(uri)
        val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType) ?: "tmp"

        val inputStream = contentResolver.openInputStream(uri) ?: return null
        val fileName = "${UUID.randomUUID()}.$extension"

        val attachmentsDir = File(context.filesDir, "attachments")
        if (!attachmentsDir.exists()) attachmentsDir.mkdirs()

        val file = File(attachmentsDir, fileName)

        FileOutputStream(file).use { output ->
            inputStream.copyTo(output)
        }

        fileName
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}





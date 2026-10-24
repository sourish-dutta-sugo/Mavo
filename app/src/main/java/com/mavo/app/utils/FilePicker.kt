package com.mavo.app.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

fun copyUriToInternalStorage(
    context: Context,
    uri: Uri,
    fileName: String
): String? {
    return try {
        val dir = File(context.filesDir, "Mavo")
        dir.mkdirs()
        val destFile = File(dir, fileName)
        context.contentResolver
            .openInputStream(uri)?.use { input ->
            FileOutputStream(destFile).use { output ->
                input.copyTo(output)
            }
        }
        destFile.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
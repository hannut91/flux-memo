package com.hyejineee.fluxmemo.services

import android.content.Context
import android.net.Uri
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

@Throws(IOException::class)
fun createImageFile(context: Context): File? {
    val timeStemp = SimpleDateFormat("yyyyMMss_HHmmssSSS").format(Date())
    val directory =
        File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "")
            .apply { if (!exists()) mkdir() }

    return File.createTempFile(
        "memo_${timeStemp}",
        ".jpg",
        directory
    )
}

@Throws(IOException::class)
fun findCopyImagePath(context: Context, contentUri: Uri): String {
    val resolver = context.contentResolver
    val inputStream = resolver.openInputStream(contentUri) ?: throw IOException()
    val buffer = ByteArray(inputStream.available())
    inputStream.read(buffer)

    val targetFile = createImageFile(context)?.also {
        FileOutputStream(it).write(buffer)
    } ?: throw IOException()
    return targetFile.absolutePath
}

@Throws(IOException::class)
fun deleteImageFile(path: String) {
    File(path)?.let {
        if (it.exists()) {
            it.delete()
        }
    }
}

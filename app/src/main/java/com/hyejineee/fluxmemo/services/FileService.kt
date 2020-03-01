package com.hyejineee.fluxmemo.services

import android.content.Context
import android.net.Uri
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

const val prefixString = "memo_"

@Throws(IOException::class)
fun createImageFile(directory: File): File =
    File.createTempFile(
        prefix(),
        "jpg",
        directory
    )

fun prefix(): String {
    val timeStamp = SimpleDateFormat("yyyyMMdd_hhmmssSSS").format(Date())
    return "${prefixString}${timeStamp}"
}

fun getRootDirectory(context: Context) =
    File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "")
        .apply {
            if (!exists()) {
                mkdir()
            }
        }

fun createCopyImagePath(imageInputStream: InputStream, targetFile: File): String {
    val buffer = ByteArray(imageInputStream.available())
    imageInputStream.read(buffer)
    FileOutputStream(targetFile).write(buffer)

    return targetFile.absolutePath
}

@Throws(IOException::class)
fun findGalleryImage(context: Context, contentUri: Uri) =
    context.contentResolver.openInputStream(contentUri) ?: throw IOException()

@Throws(IOException::class)
fun deleteImageFile(path: String) {
    File(path).let {
        if (it.exists()) {
            it.delete()
        }
    }
}

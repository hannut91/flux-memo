package com.hyejineee.fluxmemo.services

import android.content.Context
import android.content.DialogInterface
import com.google.android.material.dialog.MaterialAlertDialogBuilder

fun promptShow(
    context: Context,
    title: String = "",
    content: String = "",
    positiveBtnText: String = "예",
    positiveCallback: (DialogInterface, Int) -> Unit = { dialog, _ -> dialog.dismiss() },
    negativeBtnText: String = "아니오",
    negativeCallback: (DialogInterface, Int) -> Unit = { dialog, _ -> dialog.dismiss() }
) {
    MaterialAlertDialogBuilder(context)
        .setTitle(title)
        .setMessage(content)
        .setNegativeButton(negativeBtnText, negativeCallback)
        .setPositiveButton(positiveBtnText, positiveCallback)
        .show()
}

fun dialogShow(
    context: Context,
    title: String = "안내",
    content: String = "",
    items: Array<String> = emptyArray(),
    callback: (DialogInterface, Int) -> Unit = { dialog, _ -> dialog.dismiss() }
) {
    val dialog = MaterialAlertDialogBuilder(context)
        .setTitle(title)
        .setItems(items, callback)

    if (content.isNotEmpty()) {
        dialog.setMessage(content)
    }

    dialog.show()
}

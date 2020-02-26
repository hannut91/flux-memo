package com.hyejineee.fluxmemo.services

import android.view.ViewGroup
import com.google.android.material.snackbar.Snackbar

fun snackBarShort(viewGroup: ViewGroup, text: String) {
    Snackbar.make(viewGroup, text, Snackbar.LENGTH_SHORT).show()
}

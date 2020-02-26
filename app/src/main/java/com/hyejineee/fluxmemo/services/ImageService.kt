package com.hyejineee.fluxmemo.services

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import java.io.File

@BindingAdapter("loadThumbnailImage")
fun ImageView.loadThumbnailImage(imagePath: String) {
    Glide.with(this.context).let {
        when {
            imagePath.startsWith("http") -> it.load(imagePath)
            else -> it.load(File(imagePath))
        }
    }
        .thumbnail(0.3f)
        .centerCrop()
        .into(this)
}

@BindingAdapter("loadOriginImage")
fun ImageView.loadOriginImage(imagePath: String) {
    Glide.with(this.context).let {
        when {
            imagePath.startsWith("http") -> it.load(imagePath)
            else -> it.load(File(imagePath))
        }
    }
        .into(this)
}

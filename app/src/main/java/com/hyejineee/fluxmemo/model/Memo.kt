package com.hyejineee.fluxmemo.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Memo(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var title: String = "",
    var content: String = ""
) {
    fun isEmpty() =
        (title.isBlank() || title.isEmpty()) && (content.isBlank() || content.isEmpty())
}

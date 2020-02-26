package com.hyejineee.fluxmemo.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = arrayOf(
        ForeignKey(
            entity = Memo::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("memoId"),
            onDelete = ForeignKey.CASCADE
        )
    )
)
data class ImagePath(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val memoId: Long = -1,
    val path: String = ""
)

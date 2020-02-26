package com.hyejineee.fluxmemo.model

import androidx.room.Embedded
import androidx.room.Relation

data class MemoWithImages(
    @Embedded
    var memo: Memo = Memo(),

    @Relation(parentColumn = "id", entityColumn = "memoId")
    var images: List<ImagePath> = listOf()
)

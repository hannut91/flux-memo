package com.hyejineee.fluxmemo.viewmodels

import androidx.lifecycle.ViewModel
import com.hyejineee.fluxmemo.datasources.MemoDataSource
import com.hyejineee.fluxmemo.model.Memo
import com.hyejineee.fluxmemo.model.MemoWithImages

class MemoViewModel(private val memoDataSource: MemoDataSource) : ViewModel() {

    var currentMemo: MemoWithImages? = null
    var isEditMode = false

    fun createMemoWithImages(memo: Memo, images: List<String>) = memoDataSource.save(memo, images)

    fun getAllMemos() = memoDataSource.findAllWithImages()

    fun getMemo(id: Long) = memoDataSource.findByIdWithImages(id)

    fun updateMemo(memo: Memo, images: List<String>) = memoDataSource.update(memo, images)

    fun deleteMemo(id: Long) = memoDataSource.delete(id)
}

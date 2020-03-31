package com.hyejineee.fluxmemo

import com.hyejineee.fluxmemo.model.Memo
import com.hyejineee.fluxmemo.model.MemoWithImages

class RxEvent {
    data class MemoClick(val memoId: Long)

    data class ImageClick(val path: String)
    data class ImageLongClick(val path: String, val position: Int)

    data class MemosChange(val memos: List<MemoWithImages>)
    data class MemoChange(val memo: MemoWithImages)
    class SubscribeMemos
    data class SubscribeMemo(val memoId: Long)
    data class CreateMemo(val memo: Memo, val images: List<String>)
    data class UpdateMemo(val memo: Memo, val images: List<String>)
    data class DeleteMemo(val memoId: Long)
    class MemoDeleted
    class MemoUpdated
    class MemoCreated

    data class UrlValidationSuccess(val url: String)
}

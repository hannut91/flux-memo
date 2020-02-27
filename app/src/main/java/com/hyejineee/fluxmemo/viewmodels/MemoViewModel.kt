package com.hyejineee.fluxmemo.viewmodels

import androidx.lifecycle.ViewModel
import com.hyejineee.fluxmemo.ActionType
import com.hyejineee.fluxmemo.Dispatcher
import com.hyejineee.fluxmemo.datasources.MemoDataSource
import com.hyejineee.fluxmemo.model.Memo
import com.hyejineee.fluxmemo.model.MemoWithImages
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject

class MemoViewModel(private val memoDataSource: MemoDataSource) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    var memos: List<MemoWithImages> = emptyList()
        set(value) {
            field = value
            onMemosChange.onNext(field)
        }
    var onMemosChange: Subject<List<MemoWithImages>> = BehaviorSubject.createDefault(memos)

    var memo: MemoWithImages = MemoWithImages()
        set(value) {
            field = value
            onMemoChange.onNext(field)
        }
    var onMemoChange: Subject<MemoWithImages> = BehaviorSubject.createDefault(memo)


    init {
        setSubscribeDispatcher()
    }

    private fun setSubscribeDispatcher() {
        Dispatcher.onAction.subscribe { action ->
            when (action.type) {
                ActionType.GET_MEMOS -> {
                    getAllMemos()
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { memos = it }
                }
                ActionType.GET_MEMO -> {
                    if (action.data[0] is Long) {
                        getMemo(action.data[0] as Long)
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe { memo = it }
                    }
                }
                ActionType.UPDATE_MEMO -> {
                    val memo = action.data[0] as Memo
                    val images = action.data[1] as List<String>

                    updateMemo(memo, images)
                        .subscribeOn(Schedulers.newThread())
                        .subscribe()
                }
                ActionType.DELETE_MEMO ->{
                    val memoId = action.data[0] as Long
                    deleteMemo(memoId)
                        .subscribeOn(Schedulers.newThread())
                        .subscribe()
                }
            }
        }.addTo(compositeDisposable)
    }

    var isEditMode = false

    fun createMemoWithImages(memo: Memo, images: List<String>) = memoDataSource.save(memo, images)

    fun getAllMemos() = memoDataSource.findAllWithImages()

    fun getMemo(id: Long) = memoDataSource.findByIdWithImages(id)

    fun updateMemo(memo: Memo, images: List<String>) = memoDataSource.update(memo, images)

    fun deleteMemo(id: Long) = memoDataSource.delete(id)
}

package com.hyejineee.fluxmemo.viewmodels

import android.annotation.SuppressLint
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
    var memos:List<MemoWithImages> = emptyList()
    set(value) {
        field = value
        onMemosChange.onNext(field)
    }
    var onMemosChange:Subject<List<MemoWithImages>>  = BehaviorSubject.createDefault(memos)

    var memo:MemoWithImages = MemoWithImages()
    set(value){
        field = value
        onMemoChange.onNext(field)
    }
    var onMemoChange:Subject<MemoWithImages> = BehaviorSubject.createDefault(memo)


    init {
        setSubscribeDispatcher()
    }

    private fun setSubscribeDispatcher(){
        Dispatcher.onAction.subscribe { action ->
            when(action.type){
                ActionType.GET_MEMOS->{
                    getAllMemos()
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { memos = it }
                }
                ActionType.GET_MEMO ->{
                    if(action.data is Long){
                        getMemo(action.data)
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe { memo = it }
                    }

                }
            }
        }.addTo(compositeDisposable)
    }


    var currentMemo: MemoWithImages? = null
    var isEditMode = false

    fun createMemoWithImages(memo: Memo, images: List<String>) = memoDataSource.save(memo, images)

    fun getAllMemos() = memoDataSource.findAllWithImages()

    fun getMemo(id: Long) = memoDataSource.findByIdWithImages(id)

    fun updateMemo(memo: Memo, images: List<String>) = memoDataSource.update(memo, images)

    fun deleteMemo(id: Long) = memoDataSource.delete(id)
}

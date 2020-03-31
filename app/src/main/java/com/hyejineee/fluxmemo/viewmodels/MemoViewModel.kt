package com.hyejineee.fluxmemo.viewmodels

import androidx.lifecycle.ViewModel
import com.hyejineee.fluxmemo.RxBus
import com.hyejineee.fluxmemo.RxEvent
import com.hyejineee.fluxmemo.datasources.MemoDataSource
import com.hyejineee.fluxmemo.model.MemoWithImages
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers

class MemoViewModel(private val memoDataSource: MemoDataSource) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    var memos: List<MemoWithImages> = emptyList()
        set(value) {
            field = value
            RxBus.publish(RxEvent.MemosChange(value))
        }

    var memo: MemoWithImages = MemoWithImages()
        set(value) {
            field = value
            RxBus.publish(RxEvent.MemoChange(value))
        }

    init {
        RxBus.listen(RxEvent.SubscribeMemos::class.java)
            .flatMap { memoDataSource.findAllWithImages() }
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { memos = it }
            .addTo(compositeDisposable)

        RxBus.listen(RxEvent.SubscribeMemo::class.java)
            .flatMap { (memoId) -> memoDataSource.findByIdWithImages(memoId) }
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { memo = it }
            .addTo(compositeDisposable)

        RxBus.listen(RxEvent.CreateMemo::class.java)
            .subscribe { (memo, images) ->
                memoDataSource.save(memo, images)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { RxBus.publish(RxEvent.MemoCreated()) }
            }
            .addTo(compositeDisposable)

        RxBus.listen(RxEvent.UpdateMemo::class.java)
            .subscribe { (memo, images) ->
                memoDataSource.update(memo, images)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { RxBus.publish(RxEvent.MemoUpdated()) }
            }
            .addTo(compositeDisposable)

        RxBus.listen(RxEvent.DeleteMemo::class.java)
            .subscribe { (memoId) ->
                memoDataSource.delete(memoId)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { RxBus.publish(RxEvent.MemoDeleted()) }
            }
            .addTo(compositeDisposable)
    }
}

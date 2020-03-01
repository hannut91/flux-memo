package com.hyejineee.fluxmemo.datasources

import com.hyejineee.fluxmemo.model.ImagePath
import com.hyejineee.fluxmemo.model.Memo
import com.hyejineee.fluxmemo.room.MemoAppDatabase
import io.reactivex.Completable

class MemoLocalDataSource(private val db: MemoAppDatabase) : MemoDataSource {

    override fun findAllWithImages() = db.memoDao().findAllWithImages()

    override fun findByIdWithImages(id: Long) = db.memoDao().findByIdWithImages(id)

    override fun save(memo: Memo, images: List<String>) = Completable.create {
        db.runInTransaction {
            val id = db.memoDao().insert(memo)
            db.imageDao().insert(
                *images.map {
                    ImagePath(memoId = id, path = it)
                }.toTypedArray()
            )
        }
        it.onComplete()
    }

    override fun update(memo: Memo, images: List<String>) = Completable.create {
        db.runInTransaction {
            db.memoDao().insert(memo)
            db.imageDao().deleteByMemoId(memo.id)
            images.forEach {
                db.imageDao().insert(
                    ImagePath(
                        memoId = memo.id,
                        path = it
                    )
                )
            }
        }
        it.onComplete()
    }

    override fun delete(id: Long) = Completable.create {
        db.memoDao().delete(id)
        it.onComplete()
    }

    override fun deleteImage(vararg images: ImagePath) = Completable.create {
        images.forEach {
            db.imageDao().delete(it)
        }
        it.onComplete()
    }
}

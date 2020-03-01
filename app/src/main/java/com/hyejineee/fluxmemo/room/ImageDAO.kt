package com.hyejineee.fluxmemo.room

import androidx.room.*
import com.hyejineee.fluxmemo.model.ImagePath
import io.reactivex.Observable

@Dao
interface ImageDAO {

    @Query("SELECT * FROM imagepath WHERE memoId = :memoId")
    fun findAllByMemoId(memoId: Long): Observable<List<ImagePath>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(path: ImagePath)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg path: ImagePath)

    @Delete
    fun delete(imagePath: ImagePath)

    @Query("DELETE FROM imagepath WHERE memoId = :memoId")
    fun deleteByMemoId(memoId: Long)
}

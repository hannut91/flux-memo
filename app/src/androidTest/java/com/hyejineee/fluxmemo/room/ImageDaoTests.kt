package com.hyejineee.fluxmemo.room

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hyejineee.fluxmemo.model.ImagePath
import com.hyejineee.fluxmemo.model.Memo
import org.hamcrest.CoreMatchers.equalTo
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class ImageDaoTests {

    private lateinit var db: MemoAppDatabase
    private lateinit var dao: ImageDAO

    private var memoId: Long = 0

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, MemoAppDatabase::class.java).build()
        dao = db.imageDao()

        val memoDao = db.memoDao()
        memoId = memoDao.insert(Memo(title = "Memo title", content = "Memo content"))
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun insert() {
        val originImagePaths = dao.findAllByMemoId(memoId).blockingFirst()

        dao.insert(ImagePath(memoId = memoId, path = "path"))

        val imagePaths = dao.findAllByMemoId(memoId).blockingFirst()

        assertThat(imagePaths.size - originImagePaths.size, equalTo(1))
    }

    @Test
    fun findAllByMemoId() {
        val path = "path"
        val imagePath = ImagePath(memoId = memoId, path = path)
        dao.insert(imagePath)

        val imagePaths = dao.findAllByMemoId(memoId).blockingFirst()

        assertThat(imagePaths[0].path, equalTo(path))
        assertThat(imagePaths[0].memoId, equalTo(memoId))
    }

    @Test
    fun delete() {
        listOf(
            ImagePath(memoId = memoId, path = "path1"),
            ImagePath(memoId = memoId, path = "path2")
        ).forEach { dao.insert(it) }

        val originImagePaths = dao.findAllByMemoId(memoId).blockingFirst()

        dao.delete(originImagePaths[0])

        val imagePaths = dao.findAllByMemoId(memoId).blockingFirst()

        assertThat(originImagePaths.size - imagePaths.size, equalTo(1))
    }

    @Test
    fun deleteByMemoId() {
        dao.insert(ImagePath(memoId = memoId, path = "path"))

        val originImagePaths = dao.findAllByMemoId(memoId).blockingFirst()

        dao.deleteByMemoId(memoId)

        val imagePaths = dao.findAllByMemoId(memoId).blockingFirst()

        assertThat(originImagePaths.size - imagePaths.size, equalTo(1))
    }
}

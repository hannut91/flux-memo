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
internal class MemoDaoTests {
    private lateinit var dao: MemoDAO
    private lateinit var db: MemoAppDatabase

    private val title = "Memo title"
    private val content = "Memo content"
    private val images = listOf(
        "https://images.unsplash.com/photo-1576158113840-43db9ff3ef09?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=800&q=60",
        "https://images.unsplash.com/photo-1454021108581-20ec63d13d55?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=800&q=60"
    )

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, MemoAppDatabase::class.java
        ).build()

        dao = db.memoDao()
        val imageDAO = db.imageDao()

        val memo = Memo(title = title, content = content)

        db.runInTransaction {
            val id = dao.insert(memo)
            images.forEach {
                imageDAO.insert(ImagePath(memoId = id, path = it))
            }
        }
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun insert() {
        val memo = Memo(title = title, content = content)
        val id = dao.insert(memo)

        assertThat(id, equalTo(2L))
    }

    @Test
    fun delete() {
        dao.delete(1)

        val size = dao.findAllWithImages().blockingFirst().size

        assertThat(size, equalTo(0))
    }

    @Test
    fun findAllWithImages() {
        val data = dao.findAllWithImages().blockingFirst()

        assertThat(data.size, equalTo(1))
        assertThat(data[0].memo?.id, equalTo(1L))
        assertThat(data[0].memo?.title, equalTo(title))
        assertThat(data[0].memo?.content, equalTo(content))

        val dbImages = data[0].images.map { it.path }
        assertThat(dbImages, equalTo(images))
    }

    @Test
    fun findByIdWithImages() {
        val data = dao.findByIdWithImages(1L).blockingFirst()

        assertThat(data.memo?.id, equalTo(1L))
        assertThat(data.memo?.title, equalTo(title))
        assertThat(data.memo?.content, equalTo(content))

        val dbImages = data.images.map { it.path }
        assertThat(dbImages, equalTo(images))
    }

    @Test
    fun update() {
        val newTitle = "new title"
        val newContent = "new Content"

        val data = dao.findAllWithImages().blockingFirst()
        val originMemo = data[0].memo!!

        originMemo.title = newTitle
        originMemo.content = newContent

        dao.update(originMemo)
        val updated = dao.findByIdWithImages(originMemo.id).blockingFirst()
        val memo = updated.memo!!

        assertThat(memo.title, equalTo(newTitle))
        assertThat(memo.content, equalTo(newContent))
    }
}

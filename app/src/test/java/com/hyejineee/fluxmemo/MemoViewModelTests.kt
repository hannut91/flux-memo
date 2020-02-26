package com.hyejineee.fluxmemo

import com.hyejineee.fluxmemo.datasources.MemoDataSource
import com.hyejineee.fluxmemo.model.ImagePath
import com.hyejineee.fluxmemo.model.Memo
import com.hyejineee.fluxmemo.model.MemoWithImages
import com.hyejineee.fluxmemo.viewmodels.MemoViewModel
import io.reactivex.Observable
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

internal class MemoViewModelTests {

    private lateinit var memoViewModel: MemoViewModel

    @Mock
    private lateinit var memoDataSource: MemoDataSource

    val mockMemo = Memo(id = 1, title = "메모 제목", content = "메모 내용")
    val mockMemos = listOf(mockMemo)
    val mockImages = listOf(
        "https://images.unsplash.com/photo-1576158113840-43db9ff3ef09?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=800&q=60",
        "https://images.unsplash.com/photo-1454021108581-20ec63d13d55?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=800&q=60"
    )

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        memoViewModel = MemoViewModel(memoDataSource)

        val imagePaths = mockImages
            .map { ImagePath(memoId = mockMemo.id, path = it) }
        val mockMemosWithImages = mockMemos
            .map { MemoWithImages(memo = it, images = imagePaths) }

        given(memoDataSource.findAllWithImages())
            .willReturn(Observable.just(mockMemosWithImages))

        given(memoDataSource.findByIdWithImages(mockMemo.id))
            .willReturn(Observable.just(mockMemosWithImages[0]))
    }

    @Test
    fun createMemoWithImages() {
        memoViewModel.createMemoWithImages(mockMemo, mockImages)

        Mockito.verify(memoDataSource).save(mockMemo, mockImages)
    }

    @Test
    fun getAllMemos() {
        val memosWithImages = memoViewModel.getAllMemos().blockingFirst()

        memosWithImages.forEachIndexed { index, (memo, images) ->
            assertThat(memo).isEqualTo(mockMemos[index])

            images.forEachIndexed { i, imagePath ->
                assertThat(imagePath.path).isEqualTo(mockImages[i])
            }
        }
    }

    @Test
    fun getMemo() {
        val (memo, images) = memoViewModel.getMemo(mockMemo.id).blockingFirst()

        assertThat(memo).isEqualTo(mockMemo)

        images.forEachIndexed { index, imagePath ->
            assertThat(imagePath.path).isEqualTo(mockImages[index])
        }
    }

    @Test
    fun updateMemo() {
        memoViewModel.updateMemo(mockMemo, mockImages)

        Mockito.verify(memoDataSource).update(mockMemo, mockImages)
    }

    @Test
    fun deleteMemo() {
        memoViewModel.deleteMemo(mockMemo.id)

        Mockito.verify(memoDataSource).delete(mockMemo.id)
    }
}

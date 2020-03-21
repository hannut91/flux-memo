package com.hyejineee.fluxmemo.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil.setContentView
import androidx.recyclerview.widget.LinearLayoutManager
import com.hyejineee.fluxmemo.R
import com.hyejineee.fluxmemo.RxBus
import com.hyejineee.fluxmemo.RxEvent
import com.hyejineee.fluxmemo.databinding.ActivityWriteMemoBinding
import com.hyejineee.fluxmemo.model.Memo
import com.hyejineee.fluxmemo.services.*
import com.hyejineee.fluxmemo.view.adapter.ImageAdapter
import com.hyejineee.fluxmemo.view.dialog.OriginImageDialog
import com.hyejineee.fluxmemo.view.dialog.WriteImageUrlDialog
import com.hyejineee.fluxmemo.viewmodels.MemoViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.activity_write_memo.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.IOException

private const val FILE_PROVIDER = "com.hyejineee.fluxmemo.fileprovider"
private const val TAKE_PICTURE = 1
private const val GET_GALLERY = 2
private const val GET_URL = 3
private const val IMAGE_MAX_COUNT = 5

class WriteMemoActivity : AppCompatActivity() {

    private val compositeDisposable = CompositeDisposable()

    private val memoViewModel: MemoViewModel by viewModel()
    private val imageAdapter = ImageAdapter()
    private var editMode = false

    private lateinit var imageFilePath: String
    private lateinit var viewDataBinding: ActivityWriteMemoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val memoId = intent.getLongExtra("memoId", -1)
        editMode = memoId > -1

        setView()
        setEvents()

        if (editMode) {
            RxBus.publish(RxEvent.SubscribeMemo(memoId))
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        compositeDisposable.clear()
    }

    override fun onBackPressed() {
        super.onBackPressed()

        updateOrCreate()
    }

    fun back(view: View) {
        updateOrCreate()
    }

    fun deleteMemo(view: View) {
        promptShow(this, title = "주의", content = "삭제하시겠습니까?",
            positiveCallback = { _, _ ->
                val id = memoViewModel.memo.memo.id
                RxBus.publish(RxEvent.DeleteMemo(id))
            }
        )
    }

    fun addImage(view: View) {
        if (imageAdapter.itemCount >= IMAGE_MAX_COUNT) {
            snackBarShort(this.write_memo_activity, getString(R.string.image_max_count_caution))
            return
        }

        dialogShow(
            context = this, title = "이미지 선택", items = arrayOf(
                getString(R.string.camera),
                getString(R.string.gallery),
                getString(R.string.link)
            )
        ) { _, i ->
            when (i) {
                0 -> takePicture()
                1 -> pickFromGallery()
                2 -> showWriteImageUrlDialog()
            }
        }
    }

    private fun takePicture() {
        lateinit var imagePath: Uri
        try {
            imagePath = createImagePath()
        } catch (e: IOException) {
            snackBarShort(viewDataBinding.writeMemoActivity, "파일 생성에 실패했습니다.")
            return
        }

        val i = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            .putExtra(MediaStore.EXTRA_OUTPUT, imagePath)
        startActivityForResult(i, TAKE_PICTURE)
    }

    private fun pickFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply { type = "image/*" }
        startActivityForResult(intent, GET_GALLERY)
    }

    private fun showWriteImageUrlDialog() {
        WriteImageUrlDialog().show(supportFragmentManager, "")
    }

    private fun createImagePath(): Uri {
        val rootDir = getRootDirectory(this)
        val newImage = createImageFile(rootDir)
        imageFilePath = newImage.absolutePath
        return FileProvider.getUriForFile(this, FILE_PROVIDER, newImage)
    }

    private fun setView() {
        viewDataBinding = setContentView(this, R.layout.activity_write_memo)
        viewDataBinding.lifecycleOwner = this
        viewDataBinding.imageList.layoutManager = LinearLayoutManager(
            this@WriteMemoActivity,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        viewDataBinding.imageList.adapter = imageAdapter

        if (!editMode) {
            viewDataBinding.deleteButton.visibility = View.GONE
        }
    }

    private fun setEvents() {
        RxBus.listen(RxEvent.ImageClick::class.java).subscribe { (path) ->
            OriginImageDialog(path).show(supportFragmentManager, "")
        }.addTo(compositeDisposable)

        RxBus.listen(RxEvent.ImageLongClick::class.java).subscribe { (path, position) ->
            promptShow(
                this,
                title = "주의",
                content = "삭제하시겠습니까?",
                positiveCallback = { _, _ -> deleteImage(path, position) }
            )
        }.addTo(compositeDisposable)

        RxBus.listen(RxEvent.UrlValidationSuccess::class.java)
            .subscribe { (url) -> imageAdapter.appendImage(url) }
            .addTo(compositeDisposable)

        RxBus.listen(RxEvent.MemoChange::class.java)
            .subscribe { (memo) ->
                viewDataBinding.memo = memo.memo
                imageAdapter.images = memo.images.map { imagePath -> imagePath.path }
            }.addTo(compositeDisposable)

        RxBus.listen(RxEvent.MemoCreated::class.java)
            .subscribe { finish() }
            .addTo(compositeDisposable)

        RxBus.listen(RxEvent.MemoUpdated::class.java)
            .subscribe { finish() }
            .addTo(compositeDisposable)

        RxBus.listen(RxEvent.MemoDeleted::class.java)
            .subscribe { finish() }
            .addTo(compositeDisposable)
    }

    private fun deleteImage(path: String, position: Int) {
        try {
            deleteImageFile(path)
            imageAdapter.deleteImage(position)
        } catch (e: IOException) {
            snackBarShort(viewDataBinding.writeMemoActivity, "이미지 삭제에 실패했습니다.")
        }
    }

    private fun updateOrCreate() {
        if (editMode) {
            updateMemo()
            return
        }

        saveMemo()
    }

    private fun saveMemo() {
        val title = viewDataBinding.memoTitleEditText.text.toString().trim()
        val content = viewDataBinding.memoContentEditText.text.toString().trim()

        val memo = Memo(title = title, content = content)
        if (memo.isEmpty()) {
            finish()
            return
        }

        RxBus.publish(RxEvent.CreateMemo(memo, imageAdapter.images))
    }

    private fun updateMemo() {
        val memoId = memoViewModel.memo.memo.id
        val title = viewDataBinding.memoTitleEditText.text.toString().trim()
        val content = viewDataBinding.memoContentEditText.text.toString().trim()

        val memo = Memo(id = memoId, title = title, content = content)
        if (memo.isEmpty()) {
            finish()
            return
        }

        RxBus.publish(RxEvent.UpdateMemo(memo, imageAdapter.images))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_CANCELED || data == null) {
            return
        }

        when (requestCode) {
            TAKE_PICTURE -> imageAdapter.appendImage(imageFilePath)
            GET_GALLERY -> {
                try {
                    val rootDir = getRootDirectory(this)
                    val newImage = createImageFile(rootDir)

                    createCopyImagePath(
                        findGalleryImage(this, data.data!!),
                        newImage
                    ).let { imageAdapter.appendImage(it) }
                } catch (e: IOException) {
                    snackBarShort(viewDataBinding.writeMemoActivity, "이미지를 추가할 수 없습니다.")
                }
            }
        }
    }
}

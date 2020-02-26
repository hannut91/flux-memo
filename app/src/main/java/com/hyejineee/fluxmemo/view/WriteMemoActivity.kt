package com.hyejineee.fluxmemo.view

import android.app.Activity
import android.content.Intent
import android.drm.DrmStore
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil.setContentView
import androidx.recyclerview.widget.LinearLayoutManager
import com.hyejineee.fluxmemo.ActionType
import com.hyejineee.fluxmemo.Actions
import com.hyejineee.fluxmemo.Dispatcher
import com.hyejineee.fluxmemo.R
import com.hyejineee.fluxmemo.databinding.ActivityWriteMemoBinding
import com.hyejineee.fluxmemo.model.Memo
import com.hyejineee.fluxmemo.services.*
import com.hyejineee.fluxmemo.view.adapter.ImageAdapter
import com.hyejineee.fluxmemo.view.dialog.OriginImageDialog
import com.hyejineee.fluxmemo.view.dialog.WriteImageUrlDialog
import com.hyejineee.fluxmemo.viewmodels.MemoViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_write_memo.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.IOException

private const val FILE_PROVIDER = "com.hyejineee.linechallenge.fileprovider"
private const val TAKE_PICTURE = 1
private const val GET_GALLERY = 2
private const val GET_URL = 3
private const val IMAGE_MAX_COUNT = 5

class WriteMemoActivity : AppCompatActivity() {

    private val compositeDisposable = CompositeDisposable()

    private val memoViewModel: MemoViewModel by viewModel()
    private val imageAdapter = ImageAdapter()

    private lateinit var imageFilePath: String
    private lateinit var viewDataBinding: ActivityWriteMemoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)




        val memoId = intent.getLongExtra("memoId", -1)
        memoViewModel.isEditMode = memoId > 0
        if (memoViewModel.isEditMode) {
            Dispatcher.dispatch(Actions(ActionType.GET_MEMO, memoId))
        }

        setView()

        memoViewModel.onMemoChange
            .subscribe {
                viewDataBinding.memo = it.memo
                imageAdapter.images = it.images.map { imagePath -> imagePath.path }
            }.addTo(compositeDisposable)
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
            positiveCallback = { _, _ -> deleteMemo(memoViewModel.currentMemo?.memo!!.id) }
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
                2 ->
                    WriteImageUrlDialog { url ->
                        onActivityResult(
                            GET_URL, Activity.RESULT_OK, Intent().putExtra("url", url)
                        )
                    }.show(supportFragmentManager, "")
            }
        }
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
        imageAdapter.clickListener = {
            OriginImageDialog(it).show(supportFragmentManager, "")
        }
        imageAdapter.longClickListener = { index, path ->
            promptShow(this, title = "주의", content = "삭제하시겠습니까?",
                positiveCallback = { _, _ ->
                    try {
                        deleteImageFile(path)
                        imageAdapter.deleteImage(index)
                    } catch (e: IOException) {
                        snackBarShort(viewDataBinding.writeMemoActivity, "이미지 삭제에 실패했습니다.")
                    }
                }
            )
        }

        if (!memoViewModel.isEditMode) {
            viewDataBinding.deleteButton.visibility = View.GONE
        }
    }

    private fun deleteMemo(id: Long) {
        memoViewModel.deleteMemo(id)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { finish() }
            .addTo(compositeDisposable)
    }

    private fun updateOrCreate() {
        if (memoViewModel.isEditMode) {
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

        memoViewModel.createMemoWithImages(memo, imageAdapter.images)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { finish() }
            .addTo(compositeDisposable)
    }

    private fun updateMemo() {
        val title = viewDataBinding.memoTitleEditText.text.toString().trim()
        val content = viewDataBinding.memoContentEditText.text.toString().trim()

        val memoId = memoViewModel.memo.memo.id
        val memo = Memo(id = memoId, title = title, content = content)
        if (memo.isEmpty()) {
            finish()
            return
        }

        memoViewModel.updateMemo(memo, imageAdapter.images)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { finish() }
            .addTo(compositeDisposable)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_CANCELED || data == null) {
            return
        }

        when (requestCode) {
            TAKE_PICTURE -> {
                if (imageFilePath.isEmpty()) {
                    return
                }

                imageAdapter.appendImage(imageFilePath)
            }
            GET_GALLERY -> {
                try {
                    findCopyImagePath(this, data.data!!).let {
                        imageAdapter.appendImage(it)
                    }
                } catch (e: IOException) {
                    snackBarShort(viewDataBinding.writeMemoActivity, "이미지 추가에 실패했습니다.")
                }
            }
            GET_URL -> {
                imageAdapter.appendImage(data.getStringExtra("url")!!)
            }
        }
    }

    private fun takePicture() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                val newImage: File? = try {
                    createImageFile(this)
                } catch (ex: IOException) {
                    null
                }

                newImage?.also {
                    imageFilePath = it.absolutePath
                    val imagePath = FileProvider.getUriForFile(
                        this,
                        FILE_PROVIDER,
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imagePath)
                    startActivityForResult(takePictureIntent, TAKE_PICTURE)
                }
            }
        }
    }

    private fun pickFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply { type = "image/*" }
        intent.resolveActivity(packageManager)?.let { startActivityForResult(intent, GET_GALLERY) }
    }
}

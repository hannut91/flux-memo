package com.hyejineee.fluxmemo.view.dialog

import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil.inflate
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hyejineee.fluxmemo.R
import com.hyejineee.fluxmemo.databinding.DialogOriginImageBinding

class OriginImageDialog(private val imagePath: String) : BottomSheetDialogFragment() {

    private lateinit var viewDataBinding: DialogOriginImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setView()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?) =
        super.onCreateDialog(savedInstanceState).apply {
            setContentView(viewDataBinding.root)

            BottomSheetBehavior.from(viewDataBinding.root.parent as View).state =
                BottomSheetBehavior.STATE_EXPANDED

            viewDataBinding.originImageDialog.layoutParams.height =
                Resources.getSystem().displayMetrics.heightPixels
        }

    fun close() {
        dismiss()
    }

    private fun setView() {
        viewDataBinding = inflate(
            LayoutInflater.from(context),
            R.layout.dialog_origin_image,
            null,
            false
        )
        viewDataBinding.path = imagePath
        viewDataBinding.dialog = this
    }
}

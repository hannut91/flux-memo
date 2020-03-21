package com.hyejineee.fluxmemo.view.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.databinding.DataBindingUtil.inflate
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hyejineee.fluxmemo.R
import com.hyejineee.fluxmemo.RxBus
import com.hyejineee.fluxmemo.RxEvent
import com.hyejineee.fluxmemo.databinding.DialogEnterUrlBinding
import com.hyejineee.fluxmemo.services.isValidURL
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.widget.editorActionEvents
import io.reactivex.Observable
import io.reactivex.Observable.merge
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo

class WriteImageUrlDialog : BottomSheetDialogFragment() {

    private val compositeDisposable = CompositeDisposable()

    private lateinit var viewDataBinding: DialogEnterUrlBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setView()
        setEvents()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?) =
        super.onCreateDialog(savedInstanceState).apply {
            setContentView(viewDataBinding.root)

            BottomSheetBehavior.from(viewDataBinding.root.parent as View).state =
                BottomSheetBehavior.STATE_EXPANDED
        }

    override fun onDestroy() {
        super.onDestroy()

        compositeDisposable.clear()
    }

    private fun setView() {
        viewDataBinding = inflate(
            LayoutInflater.from(context),
            R.layout.dialog_enter_url,
            null,
            false
        )
    }

    private fun setEvents() {
        merge(
            viewDataBinding.okButton.clicks(),
            viewDataBinding.urlEditText.editorActionEvents()
                .filter { it.actionId == EditorInfo.IME_ACTION_GO }
        )
            .map { viewDataBinding.urlEditText.text.toString() }
            .flatMap {
                isValidURL(context!!, it)
                    .doOnError { exception ->
                        viewDataBinding.urlTextInputLayout.error = exception.message
                    }
                    .onErrorResumeNext(Observable.empty())
            }
            .subscribe {
                viewDataBinding.urlTextInputLayout.error = null
                RxBus.publish(RxEvent.UrlValidationSuccess(it))
                dismiss()
            }
            .addTo(compositeDisposable)
    }
}

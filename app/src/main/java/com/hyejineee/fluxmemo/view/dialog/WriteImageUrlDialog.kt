package com.hyejineee.fluxmemo.view.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.databinding.DataBindingUtil.inflate
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hyejineee.fluxmemo.R
import com.hyejineee.fluxmemo.databinding.DialogEnterUrlBinding
import com.jakewharton.rxbinding3.widget.editorActionEvents
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo

class WriteImageUrlDialog(val dataCallback: (String) -> Unit) : BottomSheetDialogFragment() {

    private val compositeDisables = CompositeDisposable()
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

        compositeDisables.clear()
    }

    fun confirm() {
        val url = viewDataBinding.urlEditText.text.toString()

        val queue = Volley.newRequestQueue(activity)
        val stringRequest = StringRequest(
            Request.Method.GET,
            url,
            Response.Listener<String> {
                viewDataBinding.urlTextInputLayout.error = null
                dataCallback(url)
                dismiss()
            },
            Response.ErrorListener {
                viewDataBinding.urlTextInputLayout.error = "올바른 url이 아닙니다."
            }
        )
        queue.add(stringRequest)
    }

    private fun setView() {
        viewDataBinding = inflate(
            LayoutInflater.from(context),
            R.layout.dialog_enter_url,
            null,
            false
        )
        viewDataBinding.dialog = this
    }

    private fun setEvents() {
        viewDataBinding.run {
            urlEditText.editorActionEvents()
                .filter { it.actionId == EditorInfo.IME_ACTION_GO }
                .subscribe { confirm() }
                .addTo(compositeDisables)
        }
    }
}
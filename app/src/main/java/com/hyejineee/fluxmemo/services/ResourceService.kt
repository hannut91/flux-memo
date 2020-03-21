package com.hyejineee.fluxmemo.services

import android.content.Context
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import io.reactivex.Observable

fun isValidURL(context: Context, url: String) = Observable.create<String> { observer ->
    val queue = Volley.newRequestQueue(context)
    val stringRequest = StringRequest(
        Request.Method.GET,
        url,
        Response.Listener<String> {
            observer.onNext(url)
            observer.onComplete()
        },
        Response.ErrorListener {
            observer.onError(Exception("올바른 URL이 아닙니다."))
        }
    )
    queue.add(stringRequest)
}

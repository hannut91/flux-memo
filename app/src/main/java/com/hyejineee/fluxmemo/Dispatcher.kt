package com.hyejineee.fluxmemo

import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

object Dispatcher {
    val onAction:Subject<Actions> = PublishSubject.create()

    fun dispatch(action:Actions){
        onAction.onNext(action)
    }

}
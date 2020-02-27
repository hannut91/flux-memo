package com.hyejineee.fluxmemo

import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

object Dispatcher {
    val onAction:Subject<Action> = PublishSubject.create()

    fun dispatch(action:Action){
        onAction.onNext(action)
    }
}
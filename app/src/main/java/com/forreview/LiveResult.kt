package com.forreview

import com.forreview.model.Result
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class LiveResult<R: Result> {

    private val successSubject = BehaviorSubject.create<R>()

    private val loadingSubject = BehaviorSubject.create<R>()

    private val errorSubject = BehaviorSubject.create<R>()

   // private val loadingSubject = PublishSubject.create<R>()

  //  private val errorSubject = PublishSubject.create<R>()

    val resultObservable: Observable<R> = Observable.merge(
        successSubject,
        loadingSubject,
        errorSubject
    )

    fun applySuccess(data: R) {
        successSubject.onNext(data)
    }

    fun applyLoading(isLoading: R) {
        loadingSubject.onNext(isLoading)
    }

    fun applyError(throwable: R) {
        errorSubject.onNext(throwable)
    }

    companion object {
        fun <T : Result>create(): LiveResult<T> {
            return LiveResult()
        }
    }
}
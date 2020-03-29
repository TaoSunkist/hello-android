package com.zhimeng.battery.utilities

import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.exceptions.OnErrorNotImplementedException
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.Subject

val MainThread: Scheduler = AndroidSchedulers.mainThread()

/* 成功时候会发生 */
private val onNextStub: (Any) -> Unit = {}

/* 失败发生 */
private val onErrorStub: (Throwable) -> Unit =
    {
        /*TODO 处理所有失败请求*/
        RxJavaPlugins.onError(OnErrorNotImplementedException(it))
    }

/* 无论成功失败都会发生 */
private var onTerminateStub: () -> Unit = {}

fun <T : Any> Single<T>.observeOnMainThread(
    onSuccess: (T) -> Unit = onNextStub,
    onError: (Throwable) -> Unit = onErrorStub,
    onTerminate: () -> Unit = onTerminateStub,
    subscribeOnBackground: Boolean = true
): Disposable {
    return observeOn(MainThread).let {
        if (subscribeOnBackground) it.subscribeOn(Schedulers.io()) else it
    }.doAfterTerminate(onTerminate).subscribe(onSuccess, onError)
}

fun <T : Any> Subject<T>.observeOnMainThread(
    onNext: (T) -> Unit = onNextStub,
    onError: (Throwable) -> Unit = onErrorStub,
    onTerminate: () -> Unit = onTerminateStub,
    subscribeOnBackground: Boolean = true
): Disposable {
    return observeOn(MainThread).let {
        if (subscribeOnBackground) it.subscribeOn(Schedulers.io()) else it
    }.subscribe(onNext, onError, onTerminate)
}

fun <T> Single<T>.with(disposables: CompositeDisposable): Single<T> =
    doOnSubscribe { disposable -> disposables.add(disposable) }
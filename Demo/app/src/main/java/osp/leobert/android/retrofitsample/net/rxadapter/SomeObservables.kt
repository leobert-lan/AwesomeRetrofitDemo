package osp.leobert.android.retrofitsample.net.rxadapter

import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.exceptions.CompositeException
import io.reactivex.exceptions.Exceptions
import io.reactivex.plugins.RxJavaPlugins
import osp.leobert.android.retrofitsample.log.L
import osp.leobert.android.retrofitsample.net.BaseData
import osp.leobert.android.retrofitsample.net.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response


/**
 * Created by leobert on 2020/6/8.
 */
internal class ResultObservable<T>(private val upstream: Observable<Response<T>?>) : Observable<Result<T>>() {
    override fun subscribeActual(observer: Observer<in Result<T>?>) {
        upstream.subscribe(ResultObserver(observer))
    }

    private class ResultObserver<R> internal constructor(private val observer: Observer<in Result<R>?>) : Observer<Response<R>?> {
        override fun onSubscribe(disposable: Disposable) {
            observer.onSubscribe(disposable)
        }

        override fun onNext(response: Response<R>) {
            observer.onNext(Result.response(response))
        }

        override fun onError(throwable: Throwable) {
            try {
                observer.onNext(Result.error(throwable))
            } catch (t: Throwable) {
                try {
                    observer.onError(t)
                } catch (inner: Throwable) {
                    Exceptions.throwIfFatal(inner)
                    RxJavaPlugins.onError(CompositeException(t, inner))
                }
                return
            }
            observer.onComplete()
        }

        override fun onComplete() {
            observer.onComplete()
        }
    }
}

internal class BodyObservable<T>(private val upstream: Observable<Response<T>?>) : Observable<T>() {
    override fun subscribeActual(observer: Observer<in T>) {
        upstream.subscribe(BodyObserver<T>(observer))
    }

    private class BodyObserver<R> internal constructor(private val observer: Observer<in R>) : Observer<Response<R>?> {
        private var terminated = false
        override fun onSubscribe(disposable: Disposable) {
            observer.onSubscribe(disposable)
        }

        override fun onNext(response: Response<R>) {
            if (response.isSuccessful) {
                response.body()?.let {
                    observer.onNext(it)
                }
            } else {
                terminated = true
                val t: Throwable = HttpException(response)
                try {
                    observer.onError(t)
                } catch (inner: Throwable) {
                    Exceptions.throwIfFatal(inner)
                    RxJavaPlugins.onError(CompositeException(t, inner))
                }
            }
        }

        override fun onComplete() {
            if (!terminated) {
                observer.onComplete()
            }
        }

        override fun onError(throwable: Throwable) {
            if (!terminated) {
                observer.onError(throwable)
            } else {
                // This should never happen! onNext handles and forwards errors automatically.
                val broken: Throwable = AssertionError(
                        "This should never happen! Report as a bug with the full stacktrace.")
                broken.initCause(throwable)
                RxJavaPlugins.onError(broken)
            }
        }
    }
}

internal class CallEnqueueObservable<T>(private val originalCall: Call<T>) : Observable<Response<T>?>() {
    override fun subscribeActual(observer: Observer<in Response<T>?>) {
        // Since Call is a one-shot type, clone it for each new observer.
        val call = originalCall.clone()
        val callback = CallCallback(call, observer)
        observer.onSubscribe(callback)
        if (!callback.isDisposed) {
            call.enqueue(callback)
        }
    }

    private class CallCallback<T> internal constructor(private val call: Call<*>, private val observer: Observer<in Response<T>?>)
        : Disposable, Callback<T> {

        @Volatile
        private var disposed = false
        var terminated = false
        override fun onResponse(call: Call<T>, response: Response<T>) {
            if (disposed) return
            try {
                val body = response.body()
                if (body is BaseData) {
                    body.callHolder = call
                }

                observer.onNext(response)
                if (!disposed) {
                    terminated = true
                    observer.onComplete()
                }
            } catch (t: Throwable) {
                if (terminated) {
                    RxJavaPlugins.onError(t)
                } else if (!disposed) {
                    try {
                        observer.onError(t)
                    } catch (inner: Throwable) {
                        Exceptions.throwIfFatal(inner)
                        RxJavaPlugins.onError(CompositeException(t, inner))
                    }
                }
            }

            try {
                RetrofitClient.netTracker?.apiRequestInfoTrack(call.request())
            } catch (e: Exception) {
                L.e(e)
            }
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            if (call.isCanceled) return
            try {
                observer.onError(t)
            } catch (inner: Throwable) {
                Exceptions.throwIfFatal(inner)
                RxJavaPlugins.onError(CompositeException(t, inner))
            }
        }

        override fun dispose() {
            disposed = true
            call.cancel()
        }

        override fun isDisposed(): Boolean {
            return disposed
        }
    }

}

internal class CallExecuteObservable<T>(private val originalCall: Call<T>) : Observable<Response<T>?>() {
    override fun subscribeActual(observer: Observer<in Response<T>?>) {
        // Since Call is a one-shot type, clone it for each new observer.
        val call = originalCall.clone()
        val disposable = CallDisposable(call)
        observer.onSubscribe(disposable)
        if (disposable.isDisposed) {
            return
        }
        var terminated = false
        try {
            val response = call.execute()
            if (!disposable.isDisposed) {
                val body = response.body()
                if (body is BaseData) {
                    body.callHolder = originalCall //这个originalCall在没有使用时内部的call和request都不会创建，
                                                   // 为了避免重新测原有业务，不做修改了，但是如果要使用它的request和call信息时注意其时效性
                }
                observer.onNext(response)
            }
            if (!disposable.isDisposed) {
                terminated = true
                observer.onComplete()
            }
        } catch (t: Throwable) {
            Exceptions.throwIfFatal(t)
            if (terminated) {
                RxJavaPlugins.onError(t)
            } else if (!disposable.isDisposed) {
                try {
                    observer.onError(t)
                } catch (inner: Throwable) {
                    Exceptions.throwIfFatal(inner)
                    RxJavaPlugins.onError(CompositeException(t, inner))
                }
            }
        }

        try {
            RetrofitClient.netTracker?.apiRequestInfoTrack(call.request())
        } catch (e: Exception) {
            L.e(e)
        }
    }

    private class CallDisposable internal constructor(private val call: Call<*>) : Disposable {

        @Volatile
        private var disposed = false
        override fun dispose() {
            disposed = true
            call.cancel()
        }

        override fun isDisposed(): Boolean {
            return disposed
        }
    }
}

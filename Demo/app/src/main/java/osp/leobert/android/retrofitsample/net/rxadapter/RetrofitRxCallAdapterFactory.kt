package osp.leobert.android.retrofitsample.net.rxadapter

import io.reactivex.*
import retrofit2.CallAdapter
import retrofit2.Response
import retrofit2.Retrofit
//import retrofit2.adapter.rxjava2.Result
//import retrofit2.adapter.rxjava2.RxJava2CallAdapter
//import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Created by leobert on 2020/6/8.
 */
class RetrofitRxCallAdapterFactory(private val scheduler: Scheduler? = null, private val isAsync: Boolean = false)
    : CallAdapter.Factory() {

    companion object {
        /**
         * Returns an instance which creates synchronous observables that do not operate on any scheduler
         * by default.
         */
        fun create(): RetrofitRxCallAdapterFactory {
            return RetrofitRxCallAdapterFactory(null, false)
        }

        /**
         * Returns an instance which creates asynchronous observables. Applying
         * [Observable.subscribeOn] has no effect on stream types created by this factory.
         */
        fun createAsync(): RetrofitRxCallAdapterFactory {
            return RetrofitRxCallAdapterFactory(null, true)
        }

        /**
         * Returns an instance which creates synchronous observables that
         * [subscribe on][Observable.subscribeOn] `scheduler` by default.
         */
        // Guarding public API nullability.
        fun createWithScheduler(scheduler: Scheduler?): RetrofitRxCallAdapterFactory? {
            if (scheduler == null) throw NullPointerException("scheduler == null")
            return RetrofitRxCallAdapterFactory(scheduler, false)
        }
    }



    override fun get(returnType: Type?, annotations: Array<Annotation?>?, retrofit: Retrofit?): CallAdapter<*, *>? {
        val rawType = getRawType(returnType)
        if (rawType == Completable::class.java) {
            // Completable is not parameterized (which is what the rest of this method deals with) so it
            // can only be created with a single configuration.
            return RetrofitRxCallAdapter<Any?>(Void::class.java, scheduler, isAsync, isResult = false, isBody = true,
                    isFlowable = false, isSingle = false,
                    isMaybe = false, isCompletable = true)
        }
        val isFlowable = rawType == Flowable::class.java
        val isSingle = rawType == Single::class.java
        val isMaybe = rawType == Maybe::class.java
        if (rawType != Observable::class.java && !isFlowable && !isSingle && !isMaybe) {
            return null
        }
        var isResult = false
        var isBody = false
        val responseType: Type
        if (returnType !is ParameterizedType) {
            val name = if (isFlowable) "Flowable" else if (isSingle) "Single" else if (isMaybe) "Maybe" else "Observable"
            throw IllegalStateException(name + " return type must be parameterized"
                    + " as " + name + "<Foo> or " + name + "<? extends Foo>")
        }
        val observableType = getParameterUpperBound(0, returnType)
        when (getRawType(observableType)) {
            Response::class.java -> {
                check(observableType is ParameterizedType) {
                    ("Response must be parameterized"
                            + " as Response<Foo> or Response<? extends Foo>")
                }
                responseType = getParameterUpperBound(0, observableType)
            }
            Result::class.java -> {
                check(observableType is ParameterizedType) {
                    ("Result must be parameterized"
                            + " as Result<Foo> or Result<? extends Foo>")
                }
                responseType = getParameterUpperBound(0, observableType)
                isResult = true
            }
            else -> {
                responseType = observableType
                isBody = true
            }
        }
        return RetrofitRxCallAdapter<Any?>(responseType, scheduler, isAsync, isResult, isBody, isFlowable,
                isSingle, isMaybe, false)
    }
}
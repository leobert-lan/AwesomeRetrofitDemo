package osp.leobert.android.retrofitsample.net.rxadapter

//import retrofit2.adapter.rxjava2.BodyObservable
//import retrofit2.adapter.rxjava2.CallEnqueueObservable
//import retrofit2.adapter.rxjava2.CallExecuteObservable
//import retrofit2.adapter.rxjava2.ResultObservable
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.plugins.RxJavaPlugins
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Response
import java.lang.reflect.Type

/**
 * Created by leobert on 2020/6/8.
 */
class RetrofitRxCallAdapter<R>(
        private val responseType: Type? = null,
        private val scheduler: Scheduler? = null,
        private val isAsync: Boolean = false,
        private val isResult: Boolean = false,
        private val isBody: Boolean = false,
        private val isFlowable: Boolean = false,
        private val isSingle: Boolean = false,
        private val isMaybe: Boolean = false,
        private val isCompletable: Boolean = false
) : CallAdapter<R, Any> {

    override fun responseType(): Type? {
        return responseType
    }

    override fun adapt(call: Call<R>?): Any? {
        if (call == null) return null

        val responseObservable: Observable<Response<R>?> = if (isAsync) CallEnqueueObservable(call) else CallExecuteObservable(call)
        var observable: Observable<*>
        observable = when {
            isResult -> {
                ResultObservable<R>(responseObservable)
            }
            isBody -> {
                BodyObservable<R>(responseObservable)
            }
            else -> {
                responseObservable
            }
        }

        if (scheduler != null) {
            observable = observable.subscribeOn(scheduler)
        }
        if (isFlowable) {
            return observable.toFlowable(BackpressureStrategy.LATEST)
        }
        if (isSingle) {
            return observable.singleOrError()
        }
        if (isMaybe) {
            return observable.singleElement()
        }
        return if (isCompletable) {
            observable.ignoreElements()
        } else RxJavaPlugins.onAssembly(observable)
    }
}
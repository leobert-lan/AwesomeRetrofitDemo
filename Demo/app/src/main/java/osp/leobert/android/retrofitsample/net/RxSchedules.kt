package osp.leobert.android.retrofitsample.net

import io.reactivex.Flowable
import io.reactivex.FlowableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.reactivestreams.Publisher

/**
 * <p><b>Package:</b> osp.leobert.android.retrofitsample.net </p>
 * <p><b>Project:</b> RetrofitSample </p>
 * <p><b>Classname:</b> RxSchedules </p>
 * Created by leobert on 2020/9/1.
 */
val ioFlowableTransformer: FlowableTransformer<*, *>? =
    FlowableTransformer<Any?, Any?> { upstream ->
        upstream.subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

fun <T> applyFlowableIo(): FlowableTransformer<Result<T>?, Result<T>?>? {
    return ioFlowableTransformer as FlowableTransformer<Result<T>?, Result<T>?>?
}
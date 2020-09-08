package osp.leobert.android.retrofitsample.net.subscriber

import osp.leobert.android.retrofitsample.net.RetrofitException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import osp.leobert.android.retrofitsample.net.Result

/**
 * <p><b>Package:</b> com.calvin.android.http </p>
 * <p><b>Classname:</b> ReComposeRetrofitCallBack </p>
 * Created by leobert on 2020/6/8.
 */
internal class ReComposeRetrofitCallBack<T>(private val subscriber: RetrofitSubscriber<T>) : Callback<Result<T>> {

    override fun onFailure(call: Call<Result<T>>, t: Throwable) {
        subscriber.onFailure(RetrofitException(RetrofitException.UNKNOWN, "未知错误", t))
    }

    override fun onResponse(call: Call<Result<T>>, response: Response<Result<T>>) {
        try {
            subscriber.onNext(response.body())
        } catch (e: Exception) {
            subscriber.onFailure(RetrofitException(RetrofitException.UNKNOWN, "未知错误", e))
        } finally {
            try {
                subscriber.onComplete()
            } catch (e2: Exception) {

            }
        }
    }
}
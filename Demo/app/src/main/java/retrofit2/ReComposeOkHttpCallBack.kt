package retrofit2

import android.os.Handler
import android.os.Looper
import osp.leobert.android.retrofitsample.net.Result
import osp.leobert.android.retrofitsample.net.RetrofitException
import osp.leobert.android.retrofitsample.net.subscriber.RetrofitSubscriber
import java.io.IOException

/**
 * <p><b>Package:</b> retrofit2 </p>
 * <p><b>Classname:</b> ReComposeOkHttpCallBack </p>
 * Created by leobert on 2020/6/8.
 */
class ReComposeOkHttpCallBack<T>(
    private val originalCall: Call<Result<T>>,
    private val subscriber: RetrofitSubscriber<T>
) : okhttp3.Callback {
    companion object {
        val mainHandler: Handler = Handler(Looper.getMainLooper())
        fun canHandle(originalCall: Call<*>): Boolean {
            return originalCall is OkHttpCall
        }
    }

    override fun onFailure(call: okhttp3.Call, e: IOException) {
        mainHandler.post {
            subscriber.onFailure(
                RetrofitException(
                    RetrofitException.NETWORK,
                    RetrofitException.ERROR_NONE_NETWORK,
                    e
                )
            )
        }
    }

    override fun onResponse(call: okhttp3.Call, rawResponse: okhttp3.Response) {
        val response: Response<Result<T>>
        try {
            if (originalCall is OkHttpCall) {
                response = originalCall.parseResponse(rawResponse)

                mainHandler.post {
                    try {
                        subscriber.onNext(response.body())
                    } catch (e: Exception) {
                        subscriber.onFailure(
                            RetrofitException(
                                RetrofitException.UNKNOWN,
                                "未知错误",
                                e
                            )
                        )
                    } finally {
                        try {
                            subscriber.onComplete()
                        } catch (e2: Exception) {

                        }
                    }
                }
            }
        } catch (e: Throwable) {
//            Utils.throwIfFatal(e)
            mainHandler.post {
                subscriber.onFailure(RetrofitException(RetrofitException.UNKNOWN, "内部错误", e))
            }
        }
    }


//        try {
//            subscriber.onNext(response.body())
//        } catch (e: Exception) {
//            subscriber.onFailure(RetrofitException(RetrofitException.UNKNOWN, "未知错误", e))
//        } finally {
//            try {
//                subscriber.onComplete()
//            } catch (e2: Exception) {
//
//            }
//        }
}
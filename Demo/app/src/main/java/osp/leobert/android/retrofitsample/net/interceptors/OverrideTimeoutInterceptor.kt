package osp.leobert.android.retrofitsample.net.interceptors

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * <p><b>Package:</b> com.calvin.android.http.interceptor </p>
 * <p><b>Classname:</b> OverrideTimeoutInterceptor </p>
 * Created by leobert on 2019-10-17.
 */
class OverrideTimeoutInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        var newChain = chain

        originalRequest.header(Consts.OVERRIDE_CONNECT_TIMEOUT)?.toIntOrNull()?.let {
            if (it > 0)
                newChain = newChain.withConnectTimeout(it, TimeUnit.MILLISECONDS)
        }

        originalRequest.header(Consts.OVERRIDE_WRITE_TIMEOUT)?.toIntOrNull()?.let {
            if (it > 0)
                newChain = newChain.withWriteTimeout(it, TimeUnit.MILLISECONDS)
        }

        originalRequest.header(Consts.OVERRIDE_READ_TIMEOUT)?.toIntOrNull()?.let {
            if (it > 0)
                newChain = newChain.withReadTimeout(it, TimeUnit.MILLISECONDS)
        }

        return newChain.proceed(originalRequest)
    }
}
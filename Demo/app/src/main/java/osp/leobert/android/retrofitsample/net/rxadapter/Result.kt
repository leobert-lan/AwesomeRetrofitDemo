package osp.leobert.android.retrofitsample.net.rxadapter

import retrofit2.Response
import java.io.IOException

/**
 * Created by leobert on 2020/6/8.
 */
class Result<T> private constructor(private val response: Response<T>? = null,
                                    private val error: Throwable? = null) {

    companion object {
        // Guarding public API nullability.
        fun <T> error(error: Throwable?): Result<T>? {
            if (error == null) throw NullPointerException("error == null")
            return Result(null, error)
        }

        // Guarding public API nullability.
        fun <T> response(response: Response<T>?): Result<T>? {
            if (response == null) throw NullPointerException("response == null")
            return Result(response, null)
        }
    }

    /**
     * The response received from executing an HTTP request. Only present when [.isError] is
     * false, null otherwise.
     */
    fun response(): Response<T>? {
        return response
    }

    /**
     * The error experienced while attempting to execute an HTTP request. Only present when [ ][.isError] is true, null otherwise.
     *
     *
     * If the error is an [IOException] then there was a problem with the transport to the
     * remote server. Any other exception type indicates an unexpected failure and should be
     * considered fatal (configuration error, programming error, etc.).
     */
    fun error(): Throwable? {
        return error
    }

    /** `true` if the request resulted in an error. See [.error] for the cause.  */
    fun isError(): Boolean {
        return error != null
    }

}
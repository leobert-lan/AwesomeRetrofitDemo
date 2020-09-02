package osp.leobert.android.mockapi.dto

/**
 * <p><b>Package:</b> osp.leobert.android.mockapi.dto </p>
 * <p><b>Classname:</b> Resp </p>
 * Created by leobert on 2020/8/28.
 */
open class Resp(val code: Int, val msg: String?) {

    companion object {
        fun newSuccess(): Resp = Resp(0, "success")

        fun newSuccess(msg: String?): Resp = Resp(0, msg)

        fun newFailure(code: Int, msg: String?): Resp = Resp(code, msg)
    }
}

open class ValueResp<T>(code: Int, msg: String?) : Resp(code, msg) {
    var value: T? = null

    companion object {
        fun <T> newVSuccess(value: T): ValueResp<T> {
            val ret = ValueResp<T>(0, "success")
            ret.value = value
            return ret
        }

        fun <T> newVSuccess(msg: String, value: T): ValueResp<T> {
            val ret = ValueResp<T>(0, msg)
            ret.value = value
            return ret
        }
    }
}
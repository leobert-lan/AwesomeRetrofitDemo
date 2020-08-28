package osp.leobert.android.mockapi

import com.google.gson.Gson
import java.lang.StringBuilder
import javax.servlet.http.HttpServletRequest

/**
 * <p><b>Package:</b> osp.leobert.android.mockapi </p>
 * <p><b>Classname:</b> Utils </p>
 * Created by leobert on 2020/8/28.
 */
fun HttpServletRequest.debugHeaders(): String {
    val info = StringBuilder().append("Header:[")
    this.headerNames.run {
        var key = this.nextElement()
        info.append(key).append("=").append(this@debugHeaders.getHeader(key))

        while (this.hasMoreElements()) {
            key = this.nextElement()
            info.append(", ").append(key).append("=").append(this@debugHeaders.getHeader(key))
        }
    }
    info.append("]")
    return info.toString()
}

fun HttpServletRequest.debugFields(): String {
    val info = StringBuilder().append("Parameters:")
    info.append(Gson().toJson(this.parameterMap))
    return info.toString()
}
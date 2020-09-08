package osp.leobert.android.retrofitsample.net.track

import android.util.Log
import okhttp3.*
import osp.leobert.android.retrofitsample.log.L
import retrofit2.Invocation
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Proxy
import java.util.concurrent.TimeUnit

/**
 * <p><b>Classname:</b> NetLoggingEventListener </p>
 * Created by leobert on 2020/5/25.
 */
class NetLoggingEventListener private constructor(request: Request) : EventListener() {


    class RetrofitRequest(val timeMillis: Long) {
        var logInfo: StringBuilder? = null

        override fun toString(): String {
            return "RetrofitRequest(timeMillis=$timeMillis)"
        }
    }

    private var startNs: Long = 0
    private val RETURN = "\r\n"

//    init {
//        L.e("retrofit-monitor", "request debug:" + request.hashCode() + "   ;" + request)
//    }


    private val logBuilder: StringBuilder = StringBuilder()
        .append("Request{method=")
        .append(request.method)
        .append(", url=")
        .append(request.url)
        .append(", tags=")
        .append(request.tag(Invocation::class.java).tagString()).append('}')
        .append(RETURN)
        .apply {
            //其实这个时间差衡量没有什么意义，无论是同步处理的call还是放到线程池处理的异步call，都会立即调用到callStart
            //我们这里计算出来的时间差，只能代表Retrofit和OKhttp内部对call进行了一堆处理所耗费的事件
            val tag = request.tag(RetrofitRequest::class.java)
            append("retrofit request at: ${tag?.timeMillis}")
            if (tag != null)
                append("; waited [${System.currentTimeMillis() - tag.timeMillis} MS]")

            append(RETURN)
        }
        .append("listener created at ${System.currentTimeMillis()}").append(RETURN)


    override fun callStart(call: Call) {
        call.request().tag(NetLoggingEventListener.RetrofitRequest::class.java)?.logInfo = logBuilder

        startNs = System.nanoTime()

        logWithTime(call, "callStart", false)
    }

    override fun proxySelectStart(call: Call, url: HttpUrl) {
        logWithTime(call, "proxySelectStart: $url", false)
    }

    override fun proxySelectEnd(call: Call, url: HttpUrl, proxies: List<Proxy>) {
        logWithTime(call, "proxySelectEnd: $proxies", false)
    }

    override fun dnsStart(call: Call, domainName: String) {
        logWithTime(call, "dnsStart: $domainName", false)
    }

    override fun dnsEnd(call: Call, domainName: String, inetAddressList: List<InetAddress>) {
        logWithTime(call, "dnsEnd: $inetAddressList", false)
    }

    override fun connectStart(call: Call, inetSocketAddress: InetSocketAddress, proxy: Proxy) {
        logWithTime(call, "connectStart: $inetSocketAddress $proxy", false)
    }

    override fun secureConnectStart(call: Call) {
        logWithTime(call, "secureConnectStart", false)
    }

    override fun secureConnectEnd(call: Call, handshake: Handshake?) {
        logWithTime(call, "secureConnectEnd: $handshake", false)
    }

    override fun connectEnd(
        call: Call,
        inetSocketAddress: InetSocketAddress,
        proxy: Proxy,
        protocol: Protocol?
    ) {
        logWithTime(call, "connectEnd: $protocol", false)
    }

    override fun connectFailed(
        call: Call,
        inetSocketAddress: InetSocketAddress,
        proxy: Proxy,
        protocol: Protocol?,
        ioe: IOException
    ) {
        logWithTime(call, "connectFailed: $protocol $ioe", false)
    }

    override fun connectionAcquired(call: Call, connection: Connection) {
        logWithTime(call, "connectionAcquired: $connection", false)
    }

    override fun connectionReleased(call: Call, connection: Connection) {
        logWithTime(call, "connectionReleased", false)
    }

    override fun requestHeadersStart(call: Call) {
        logWithTime(call, "requestHeadersStart", false)
    }

    override fun requestHeadersEnd(call: Call, request: Request) {
        logWithTime(call, "requestHeadersEnd", false)
    }

    override fun requestBodyStart(call: Call) {
        logWithTime(call, "requestBodyStart", false)
    }

    override fun requestBodyEnd(call: Call, byteCount: Long) {
        logWithTime(call, "requestBodyEnd: byteCount=$byteCount", false)
    }

    override fun requestFailed(call: Call, ioe: IOException) {
        logWithTime(call, "requestFailed: $ioe", false)
    }

    override fun responseHeadersStart(call: Call) {
        logWithTime(call, "responseHeadersStart", false)
    }

    override fun responseHeadersEnd(call: Call, response: Response) {
        logWithTime(call, "responseHeadersEnd: $response", false)
    }

    override fun responseBodyStart(call: Call) {
        logWithTime(call, "responseBodyStart", false)
    }

    override fun responseBodyEnd(call: Call, byteCount: Long) {
        logWithTime(call, "responseBodyEnd: byteCount=$byteCount", false)
    }

    override fun responseFailed(call: Call, ioe: IOException) {
        logWithTime(call, "responseFailed: $ioe", true, Log.ERROR)
    }

    override fun callEnd(call: Call) {
        logWithTime(call, "callEnd", true)
    }

    override fun callFailed(call: Call, ioe: IOException) {
        logWithTime(call, "callFailed: $ioe", true, Log.ERROR)
    }

    private fun logWithTime(call: Call, message: String, print: Boolean, level: Int = Log.DEBUG) {
        val timeMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)
        logBuilder.append("[$timeMs ms] ").append(message).append(RETURN)
        if (print) {
            Log.println(level, "retrofit-monitor", logBuilder.toString())
            L.d("retrofit-monitor", " ")
            L.d("retrofit-monitor", " ")
        }
    }

    open class Factory : EventListener.Factory {

        override fun create(call: Call): EventListener = NetLoggingEventListener(call.request())
    }
}

private fun Invocation?.tagString(): String {
    return if (this == null)
        "null"
    else
        String.format(
            "%s.%s() ignore param",
            this.method().declaringClass.name, this.method().name
        )
}

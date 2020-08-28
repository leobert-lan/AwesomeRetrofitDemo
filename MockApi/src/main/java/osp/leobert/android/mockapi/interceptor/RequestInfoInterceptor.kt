package osp.leobert.android.mockapi.interceptor

import org.apache.log4j.Logger
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView
import osp.leobert.android.mockapi.debugFields
import osp.leobert.android.mockapi.debugHeaders
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * <p><b>Package:</b> osp.leobert.android.mockapi.interceptor </p>
 * <p><b>Classname:</b> RequestInfoInterceptor </p>
 * Created by leobert on 2020/8/28.
 */
class RequestInfoInterceptor : HandlerInterceptor {

    val logger: Logger = Logger.getLogger(RequestInfoInterceptor::class.java)

    override fun preHandle(request: HttpServletRequest?, response: HttpServletResponse?,
                           handler: Any?): Boolean {

        request?.let {
            logger.info("${request.method} ${request.requestURL} \n ${request.debugHeaders()} \n ${request.debugFields()}")
        }
        return true
    }

    override fun postHandle(request: HttpServletRequest?, response: HttpServletResponse?, handler: Any?, modelAndView: ModelAndView?) {
    }

    override fun afterCompletion(request: HttpServletRequest?, response: HttpServletResponse?, handler: Any?, ex: Exception?) {
    }

}
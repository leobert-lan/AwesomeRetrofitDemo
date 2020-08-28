package osp.leobert.android.mockapi.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter
import osp.leobert.android.mockapi.interceptor.RequestInfoInterceptor

/**
 * <p><b>Package:</b> osp.leobert.android.mockapi.config </p>
 * <p><b>Classname:</b> InterceptorConfig </p>
 * Created by leobert on 2020/8/28.
 */
@Configuration
open class InterceptorConfig: WebMvcConfigurerAdapter() {

    override fun addInterceptors(registry: InterceptorRegistry?) {
        super.addInterceptors(registry)
        registry?.addInterceptor(RequestInfoInterceptor())
    }

}
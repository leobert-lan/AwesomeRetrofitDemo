package osp.leobert.android.mockapi

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.embedded.MultipartConfigFactory
import org.springframework.context.annotation.Bean
import javax.servlet.MultipartConfigElement

/**
 * <p><b>Package:</b>  </p>
 * <p><b>Classname:</b> osp.leobert.android.mockapi.Application </p>
 * Created by leobert on 2020/8/28.
 */
@SpringBootApplication
open class Application {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(Application::class.java, *args)
        }
    }

    @Bean
    open fun multipartConfigElement(): MultipartConfigElement? {
        val factory = MultipartConfigFactory()
        factory.setMaxFileSize("1000MB")
        factory.setMaxRequestSize("1000MB")
        return factory.createMultipartConfig()
    }
}
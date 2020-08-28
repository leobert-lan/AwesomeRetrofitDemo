package osp.leobert.android.mockapi.web

import org.apache.log4j.Logger
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import osp.leobert.android.mockapi.dto.Resp
import osp.leobert.android.mockapi.dto.ValueResp

/**
 * <p><b>Package:</b> osp.leobert.android.mockapi.web </p>
 * <p><b>Classname:</b> MockController </p>
 * Created by leobert on 2020/8/28.
 */
@RestController
@RequestMapping(value = ["/mock"])
class MockController {
    val logger: Logger = Logger.getLogger(MockController::class.java)

    @RequestMapping(value = ["/helloworld"], method = [RequestMethod.GET])
    fun helloworld(): Resp {
        return ValueResp.newVSuccess("hello world")
    }

}
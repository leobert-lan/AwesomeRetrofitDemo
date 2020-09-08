package osp.leobert.android.mockapi.web

import org.apache.log4j.Logger
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import osp.leobert.android.mockapi.MissingParamException
import osp.leobert.android.mockapi.dto.Resp
import osp.leobert.android.mockapi.dto.ValueResp
import osp.leobert.android.mockapi.requireNotNull

/**
 * <p><b>Package:</b> osp.leobert.android.mockapi.web </p>
 * <p><b>Classname:</b> MockController </p>
 * Created by leobert on 2020/8/28.
 */
@RestController
@RequestMapping(value = "/mock")
class MockController {
    val logger: Logger = Logger.getLogger(MockController::class.java)

    @RequestMapping(value = "/helloworld", method = arrayOf(RequestMethod.GET))
    fun helloworld(): Resp {
        return ValueResp.newVSuccess("hello world")
    }

    @RequestMapping(value = "/follow", method = arrayOf(RequestMethod.POST))
    fun follow(@RequestParam("uid", required = false) uid: Int,
               @RequestParam("scene", required = false) scene: String?,
               @RequestParam("sig", required = false) sig: String?,
               @RequestParam("nc_token", required = false) nc_token: String?,
               @RequestParam("csessionid", required = false) csessionid: String?): Resp {
        try {
            uid.requireNotNull("uid")
        } catch (e: MissingParamException) {
            return Resp.newFailure(-1, e.message)
        }

        try {
            scene.requireNotNull("scene")
            sig.requireNotNull("sig")
            nc_token.requireNotNull("nc_token")
            csessionid.requireNotNull("csessionid")
        } catch (e: MissingParamException) {
            return Resp.newFailure(66666, e.message)
        }

        return ValueResp.newVSuccess(1)

    }

}
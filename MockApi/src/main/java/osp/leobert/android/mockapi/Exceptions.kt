package osp.leobert.android.mockapi

/**
 * <p><b>Package:</b> osp.leobert.android.mockapi </p>
 * <p><b>Project:</b> MockApi </p>
 * <p><b>Classname:</b> Exceptions </p>
 * Created by leobert on 2020/9/2.
 */
@Throws(MissingParamException::class)
fun Any?.requireNotNull(paramName: String) {
    this ?: throw MissingParamException(paramName)
}

class MissingParamException(paramName: String) : RuntimeException("missing param $paramName")
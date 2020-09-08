package osp.leobert.android.retrofitsample.net;

public class RetrofitException extends RuntimeException {
    public int code;
    public String msg;

    public static final int NETWORK = 999;//network not connected or something
    public static final int UNKNOWN = 1000;
    public static final int PARSE_ERROR = 1001;

    public static final String ERROR_NONE_NETWORK = "网络连接超时，请稍后重试";
    public static final String ERROR_UNKNOWN = "未知错误，请稍后重试";
    public static final String ERROR_PARSE_ERROR = "数据异常，请稍后再试";

    public RetrofitException(int code) {
        this.code = code;
        switch (code) {
            case NETWORK:
                msg = ERROR_NONE_NETWORK;
                break;
            case PARSE_ERROR:
                msg = ERROR_PARSE_ERROR;
                break;
            case UNKNOWN:
            default:
                msg = ERROR_UNKNOWN;
                break;
        }
    }

    public RetrofitException(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public RetrofitException(int code, String msg, Throwable e) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "code = " + code + "\n cause = " + msg + "\n" + super.toString();
    }
}

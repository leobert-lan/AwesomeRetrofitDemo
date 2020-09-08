package osp.leobert.android.retrofitsample.net.subscriber;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import org.json.JSONException;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.UnknownFormatConversionException;

import io.reactivex.subscribers.DisposableSubscriber;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import okhttp3.Call;
import okhttp3.Request;
import osp.leobert.android.retrofitsample.CenterToast;
import osp.leobert.android.retrofitsample.log.L;
import osp.leobert.android.retrofitsample.net.Result;
import osp.leobert.android.retrofitsample.net.RetrofitClient;
import osp.leobert.android.retrofitsample.net.RetrofitException;
import osp.leobert.android.retrofitsample.net.humancheck.HumanCheckParam;
import osp.leobert.android.retrofitsample.net.humancheck.HumanChecker;
import retrofit2.HttpException;
import retrofit2.ReComposeOkHttpCallBack;

public abstract class RetrofitSubscriber<T> extends DisposableSubscriber<Result<T>> {


    public static final int CODE_HUMAN_CHECK = 66666;

    public static final String TAG = "RetrofitResult";

    public RetrofitSubscriber() {
    }

    @Override
    public void onComplete() {
        L.d("request onComplete");
    }

    @Override
    public void onError(Throwable e) {
        RetrofitException resultException;

        if (e instanceof HttpException) {//non-200 error
            HttpException httpError = (HttpException) e;
            resultException = new RetrofitException(RetrofitException.NETWORK);
            //new RetrofitException(httpError.code());
            L.e(TAG, "Retrofit onError: Http-Code error," + e.toString(), e);
            onFailure(resultException);
        }
        //json parse error
        else if (e instanceof UnknownFormatConversionException
                || e instanceof JSONException
                || e instanceof JsonParseException /* JsonIOException || JsonSyntaxException sub-clz of JsonParseException*/) {
            resultException = new RetrofitException(RetrofitException.PARSE_ERROR);
            L.e(TAG, "Retrofit onError: Json Parse Error," + e.toString(), e);
            onFailure(resultException);
        } else if (e instanceof IOException/*sub : UnknownHostException ||  SocketTimeoutException */
                || String.valueOf(e.getMessage()).toLowerCase().contains("timeout".toLowerCase())
                || String.valueOf(e.getMessage()).toLowerCase().contains("hostname".toLowerCase())
                || isRxFlowRetrofitNetworkException(e)) {//network error
            resultException = new RetrofitException(RetrofitException.NETWORK);
            L.e(TAG, "Retrofit onError: Network Error," + e.toString(), e);
            onFailure(resultException);
        }

        //unknown error
        else {
            L.e(TAG, "Retrofit onError: 业务异常？" + e.toString(), e);
            returnUnKnow(e);
        }
    }

    /**
     * @return true if the previous aspect has handle the exception then transform
     * it to a {@link RetrofitException} with code {@link RetrofitException#NETWORK}
     */
    private boolean isRxFlowRetrofitNetworkException(Throwable e) {
        if (e instanceof RetrofitException) {
            return ((RetrofitException) e).code == RetrofitException.NETWORK;
        }
        return false;
    }

    private void returnUnKnow(Throwable e) {
        RetrofitException resultException = new RetrofitException(RetrofitException.UNKNOWN);
        L.e(TAG, "Retrofit unknown Error," + e.getClass() + "msg:" + e.getMessage());
        onFailure(resultException);
    }

    public void onReqSuccess(Result<T> result) {
        onSuccess(result.value, result.ext);
    }

    public void onSuccess(T data, JsonElement ext) {
        onSuccess(data);
    }

    @Override
    public void onNext(Result<T> result) {
        if (result == null) return;
        if (result.code == null || result.code == 0) {
            try {
                onReqSuccess(result);
            } catch (Exception e) {
                int code = -1;
                if (result.code != null) {
                    code = result.code;
                }
                L.e(TAG, "Retrofit; handle success but got exception", e);
                onError(new RetrofitException(code, result.msg));
            }
        } else {
            int code = result.code;
            internalOnFailureCode(code, result);
        }
    }

    private void internalOnFailureCode(int code, Result<T> result) {
        if (code == CODE_HUMAN_CHECK) {
            if (result.callHolder != null) {
                onHumanCheck(result.callHolder, code, result);
                return;
            }
        }

//       无关细节已移除

        onFailureCode(code, result);
    }

    private WeakReference<Function1<HumanCheckParam, Unit>> humanCheckRef;

    protected void onHumanCheck(@NonNull final retrofit2.Call oldCall, final int code, final Result<T> result) {
        humanCheckRef = new WeakReference<Function1<HumanCheckParam, Unit>>(new Function1<HumanCheckParam, Unit>() {
            @Override
            public Unit invoke(HumanCheckParam humanCheckParam) {
                if (isDisposed())
                    return null;

                if (humanCheckParam == null) {
                    onFailureCode(code, result);
                    return null;
                }

                try {
                    Request request = oldCall.request();

                    Call call = RetrofitClient.getRetrofit().callFactory()
                            .newCall(request.newBuilder()
                                            .url(request.url().newBuilder()
                                                    .setQueryParameter("scene", null2Empty(humanCheckParam.getScene()))
                                                    .setQueryParameter("sig", null2Empty(humanCheckParam.getSig()))
                                                    .setQueryParameter("nc_token", null2Empty(humanCheckParam.getNcToken()))
                                                    .setQueryParameter("csessionid", null2Empty(humanCheckParam.getSessionid()))
                                                    .build())
                                            //header存在长度限制，还是使用queryString
//                                    .addHeader("scene", null2Empty(humanCheckParam.getScene()))
//                                    .addHeader("sig", null2Empty(humanCheckParam.getSig()))
//                                    .addHeader("ncToken", null2Empty(humanCheckParam.getNcToken()))
//                                    .addHeader("sessionId", null2Empty(hu
//                                    manCheckParam.getSessionid()))
                                            .build()
                            );
                    ReComposeOkHttpCallBack.Companion.getMainHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                onStart();
                            } catch (Exception ignore) {
                            }
                        }
                    });
                    if (call instanceof retrofit2.Call) {// 目前是走不进来的
                        ((retrofit2.Call) call).enqueue(new ReComposeRetrofitCallBack<T>(RetrofitSubscriber.this));
                    } else if (ReComposeOkHttpCallBack.Companion.canHandle(oldCall)) {
                        call.enqueue(new ReComposeOkHttpCallBack<T>(oldCall, RetrofitSubscriber.this));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }

            private String null2Empty(@Nullable String nullable) {
                if (nullable == null)
                    return "";
                return nullable;
            }

        });
        HumanChecker.INSTANCE.requestCheck(null, humanCheckRef);
    }


    /**
     * call this when getting non SUCCESS code.
     * <p>
     * <em> Caution : the message contains in the response
     * has been displayed via toast</em>
     */
    protected void onFailureCode(int resultCode, Result<T> result) {
        onFailure(new RetrofitException(resultCode, result.msg));
    }

    protected boolean needInterceptFailureMsg(int resultCode) {
        return false;
    }

    protected void showFailureMsg(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            CenterToast.showToast(msg);
        }
    }

    /**
     * error like: not-200 series, io-exception, json-parse-exception, time out .etc
     */
    public void onFailure(RetrofitException e) {
        if (!needInterceptFailureMsg(e.code)) {
            showFailureMsg(e.msg);
        }
        L.e(TAG, "Retrofit onFailure: " + e.toString(), e);
    }

    /**
     * only 200-series and ret-code is zero and parse success!
     */
    public abstract void onSuccess(T data);
}

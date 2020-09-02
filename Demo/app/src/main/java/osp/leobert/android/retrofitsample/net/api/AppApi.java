package osp.leobert.android.retrofitsample.net.api;

import io.reactivex.Flowable;
import osp.leobert.android.retrofitsample.net.Result;
import osp.leobert.android.retrofitsample.net.interceptors.Consts;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;


/**
 * <p><b>Package:</b> osp.leobert.android.retrofitsample.net.api </p>
 * <p><b>Project:</b> RetrofitSample </p>
 * <p><b>Classname:</b> AppApi </p>
 * Created by leobert on 2020/9/1.
 */
public interface AppApi {
    // TODO: 2020/9/1 修改为部署服务的机器局域网ip
    String BASE_URL = "http://10.33.74.253:8889";

    //极端一点，直接改成1ms
    @GET("mock/helloworld")
    @Headers(
            value = {Consts.OVERRIDE_CONNECT_TIMEOUT + ":1",
                    Consts.OVERRIDE_WRITE_TIMEOUT + ":1",
                    Consts.OVERRIDE_READ_TIMEOUT + ":1"}
    )
    Flowable<Result<String>> helloWorld1ms();

    @GET("mock/helloworld")
    Flowable<Result<String>> helloWorld();

    @POST("mock/follow")
    @FormUrlEncoded
    Flowable<Result<String>> follow(@Field("uid") int uid);
}

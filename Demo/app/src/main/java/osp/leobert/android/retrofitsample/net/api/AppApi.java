package osp.leobert.android.retrofitsample.net.api;

import io.reactivex.Flowable;
import osp.leobert.android.retrofitsample.net.Result;
import retrofit2.http.GET;


/**
 * <p><b>Package:</b> osp.leobert.android.retrofitsample.net.api </p>
 * <p><b>Project:</b> RetrofitSample </p>
 * <p><b>Classname:</b> AppApi </p>
 * Created by leobert on 2020/9/1.
 */
public interface AppApi {
    // TODO: 2020/9/1 修改为部署服务的机器局域网ip
    String BASE_URL = "http://192.168.0.5:8889";

    @GET("mock/helloworld")
    Flowable<Result<String>> helloWorld();
}

package osp.leobert.android.retrofitsample.net;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import osp.leobert.android.retrofitsample.net.interceptors.OverrideTimeoutInterceptor;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import osp.leobert.android.retrofitsample.log.L;
import osp.leobert.android.retrofitsample.net.api.AppApi;
import osp.leobert.android.retrofitsample.net.converter.MyGsonConverterFactory;
import osp.leobert.android.retrofitsample.net.converter.StringConverterFactory;
import osp.leobert.android.retrofitsample.net.track.INetTracker;
import osp.leobert.android.retrofitsample.net.track.NetLoggingEventListener;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;

public class RetrofitClient {
    @Nullable
    public static INetTracker netTracker;

    static class OkHttpClient2 extends OkHttpClient {

        public OkHttpClient2(@NotNull Builder builder) {
            super(builder);
        }

        @NotNull
        @Override
        public Call newCall(@NotNull Request request) {
            // 看了下代码没有享元模式， 这样改没啥风险，但是也没啥用，
            // 底层基本都是retrofit创建的Proxy模式的Call，真正使用时才通过RequestFactory创建request、继而创建ok的call，
            // 即取出来的tag应该都是null
            NetLoggingEventListener.RetrofitRequest tag = null;
            try {
                tag = request.tag(NetLoggingEventListener.RetrofitRequest.class);
            } catch (Exception e) {
                L.e(e);
            }

            if (tag == null)
                request = request.newBuilder().tag(NetLoggingEventListener.RetrofitRequest.class,
                        new NetLoggingEventListener.RetrofitRequest(System.currentTimeMillis())).build();
            return super.newCall(request);
        }

        static OkHttpClient2 reBuilder(@NonNull OkHttpClient client) {
            return new OkHttpClient2(client.newBuilder());
        }
    }

    private static OkHttpClient okHttpsClient = new OkHttpClient();

    private static final int TIMEOUT_SECONDS = 20;

    private static CallAdapter.Factory callAdapter;

    private static GsonBuilder gsonBuilder = new GsonBuilder()
            .setPrettyPrinting()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .serializeNulls();

    public static void setCallAdapter(@NonNull CallAdapter.Factory call) {
        callAdapter = call;
    }

    public static void buildOkHttpClient(Interceptor... interceptors) {
        OkHttpClient.Builder httpsBuilder = new OkHttpClient().newBuilder()
                .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .eventListenerFactory(new NetLoggingEventListener.Factory())
                .retryOnConnectionFailure(true);

        if (interceptors != null && interceptors.length > 0) {
            for (Interceptor interceptor : interceptors) {
                httpsBuilder.addInterceptor(interceptor);
            }
        }

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpsBuilder.addInterceptor(loggingInterceptor);


        httpsBuilder.addInterceptor(new OverrideTimeoutInterceptor());

        okHttpsClient = new OkHttpClient2(httpsBuilder);
    }

    private static volatile Retrofit retrofit = null;


    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            synchronized (RetrofitClient.class) {
                if (retrofit == null)
                    retrofit = new Retrofit.Builder()
                            .baseUrl(AppApi.BASE_URL)
                            .client(okHttpsClient)
                            .addConverterFactory(new StringConverterFactory())
                            .addConverterFactory(MyGsonConverterFactory.create(gsonBuilder.create()))
                            .addCallAdapterFactory(callAdapter)
                            .build();
            }
        }
        return retrofit;
    }

    public static <T> T createApi(Class<T> clazz) {
        return getRetrofit().create(clazz);
    }

}

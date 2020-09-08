package osp.leobert.android.retrofitsample

import android.app.Application
import osp.leobert.android.retrofitsample.net.interceptors.OverrideTimeoutInterceptor
import osp.leobert.android.retrofitsample.net.RetrofitClient
import osp.leobert.android.retrofitsample.net.rxadapter.RetrofitRxCallAdapterFactory

/**
 * <p><b>Package:</b> osp.leobert.android.retrofitsample </p>
 * <p><b>Project:</b> RetrofitSample </p>
 * <p><b>Classname:</b> DemoApp </p>
 * Created by leobert on 2020/9/1.
 */
class DemoApp : Application() {
    companion object {
        var instance: Application? = null
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        RetrofitClient.setCallAdapter(RetrofitRxCallAdapterFactory.create())
        RetrofitClient.buildOkHttpClient(OverrideTimeoutInterceptor())
    }
}
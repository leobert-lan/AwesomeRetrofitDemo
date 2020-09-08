package osp.leobert.android.retrofitsample.net.api

import osp.leobert.android.retrofitsample.net.RetrofitClient

/**
 * <p><b>Package:</b> osp.leobert.android.retrofitsample.net.api </p>
 * <p><b>Project:</b> RetrofitSample </p>
 * <p><b>Classname:</b> Manager </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2020/9/1.
 */
val appApi: AppApi by lazy {
    RetrofitClient.createApi(AppApi::class.java)
}
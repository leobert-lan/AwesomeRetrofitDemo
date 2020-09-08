package osp.leobert.android.retrofitsample.net.subscriber;


import osp.leobert.android.retrofitsample.log.L;

public class CommonRetrofitSubscriber<T> extends RetrofitSubscriber<T> {

    //无关细节已移除

    @Override
    public void onSuccess(T data) {
        L.d("onSuccess");
    }

}

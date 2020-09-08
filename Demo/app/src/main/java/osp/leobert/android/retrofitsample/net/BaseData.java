package osp.leobert.android.retrofitsample.net;

import androidx.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

import retrofit2.Call;

public class BaseData {

    /**
     * 因为配合RxJava使用，需要修改的底层过多，我们留一个引用来存储原来的Call
     */
    @Nullable
    public transient Call<?> callHolder;

    @SerializedName("code")
    public Integer code;

    @SerializedName(value = "msg")
    public String msg;

    @Nullable
    @SerializedName("ext")
    public JsonElement ext;
}

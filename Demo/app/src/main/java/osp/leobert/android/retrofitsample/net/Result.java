package osp.leobert.android.retrofitsample.net;

import com.google.gson.annotations.SerializedName;

public class Result<M> extends BaseData {

    @SerializedName(value = "data", alternate = {"value"})
    public M value;

}

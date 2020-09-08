package osp.leobert.android.retrofitsample.net.humancheck

import com.google.gson.annotations.SerializedName

data class HumanCheckParam(

        @field:SerializedName("scene")
        val scene: String? = null,

        @field:SerializedName("sig")
        val sig: String? = null,

        @field:SerializedName("nc_token")
        val ncToken: String? = null,

        @field:SerializedName("csessionid")
        val sessionid: String? = null
)
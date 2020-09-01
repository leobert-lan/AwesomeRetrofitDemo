package osp.leobert.android.retrofitsample

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.disposables.Disposable
import osp.leobert.android.retrofitsample.net.RetrofitException
import osp.leobert.android.retrofitsample.net.api.appApi
import osp.leobert.android.retrofitsample.net.applyFlowableIo
import osp.leobert.android.retrofitsample.net.subscriber.CommonRetrofitSubscriber

class MainActivity : AppCompatActivity() {

    val tvInfo: TextView by lazy { findViewById<TextView>(R.id.info) }
    val btnCase1: Button by lazy { findViewById<Button>(R.id.case1) }
    val btnCase2: Button by lazy { findViewById<Button>(R.id.case2) }
    val btnCase3: Button by lazy { findViewById<Button>(R.id.case3) }

    var onRequesting: Boolean = false
    var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnCase1.setOnClickListener { case1() }
        btnCase2.setOnClickListener { case2() }
        btnCase3.setOnClickListener { case3() }
    }

    //    动态设置请求超时时间
    fun case1() {

    }

    // 网络优化的判断依据：时长统计工具
    fun case2() {
        if (onRequesting) {
            tvInfo.append("心急吃不了热豆腐")
            return
        }
        disposable = appApi.helloWorld()
            .compose(applyFlowableIo())
            .subscribeWith(object : CommonRetrofitSubscriber<String?>() {
                override fun onStart() {
                    super.onStart()
                    onRequesting = true
                    tvInfo.text = "时长统计工具... 看日志，tag：retrofit-monitor"
                }

                override fun onSuccess(data: String?) {
                    super.onSuccess(data)
                    tvInfo.text = data?:""
                    onRequesting = false
                }

                override fun onFailure(e: RetrofitException?) {
                    super.onFailure(e)
                    tvInfo.text = e.toString()
                    onRequesting = false
                }
            })
    }

    //    全局支持人机校验
    fun case3() {

    }

    override fun onDestroy() {
        disposable?.dispose()
        super.onDestroy()
    }
}
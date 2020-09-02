package osp.leobert.android.retrofitsample

import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.disposables.Disposable
import okhttp3.Request
import osp.leobert.android.retrofitsample.net.RetrofitClient
import osp.leobert.android.retrofitsample.net.RetrofitException
import osp.leobert.android.retrofitsample.net.api.AppApi
import osp.leobert.android.retrofitsample.net.api.appApi
import osp.leobert.android.retrofitsample.net.applyFlowableIo
import osp.leobert.android.retrofitsample.net.humancheck.HumanCheckParam
import osp.leobert.android.retrofitsample.net.humancheck.HumanChecker
import osp.leobert.android.retrofitsample.net.subscriber.CommonRetrofitSubscriber
import osp.leobert.android.retrofitsample.net.track.INetTracker
import osp.leobert.android.retrofitsample.net.track.NetLoggingEventListener

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

        RetrofitClient.netTracker = object : INetTracker {
            override fun apiRequestInfoTrack(request: Request?) {
                request ?: return
                val currentTimeMillis = System.currentTimeMillis()

                val url = request.url.toString()
                if (TextUtils.isEmpty(url) || !url.contains(AppApi.BASE_URL)) {
                    return
                }

                val tag = request.tag(NetLoggingEventListener.RetrofitRequest::class.java) ?: return

                val waitTime = currentTimeMillis - tag.timeMillis

                tvInfo.post {
                    tvInfo.append("\n$url ; create:[${tag.timeMillis}]  whole:[$waitTime ms]\n${tag.logInfo}}")
                }

            }

        }
    }

    //    动态设置请求超时时间
    private fun case1() {
        if (onRequesting) {
            tvInfo.append("心急吃不了热豆腐")
            return
        }
        disposable = appApi.helloWorld1ms()
            .compose(applyFlowableIo())
            .subscribeWith(object : CommonRetrofitSubscriber<String?>() {
                override fun onStart() {
                    super.onStart()
                    onRequesting = true
                    tvInfo.text = "自定义超时"
                }

                override fun onSuccess(data: String?) {
                    super.onSuccess(data)
                    tvInfo.text = data ?: ""
                    onRequesting = false
                }

                override fun onFailure(e: RetrofitException?) {
                    super.onFailure(e)
                    tvInfo.text = e.toString()
                    onRequesting = false
                }
            })
    }

    // 网络优化的判断依据：时长统计工具
    private fun case2() {
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
                    tvInfo.text = data ?: ""
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
    private fun case3() {
        if (onRequesting) {
            tvInfo.append("心急吃不了热豆腐")
            return
        }
        disposable = appApi.follow(1)
            .compose(applyFlowableIo())
            .subscribeWith(object : CommonRetrofitSubscriber<String?>() {
                override fun onStart() {
                    super.onStart()
                    onRequesting = true
                    tvInfo.text = "人机校验"
                }

                override fun onSuccess(data: String?) {
                    super.onSuccess(data)
                    tvInfo.text = data ?: ""
                    onRequesting = false
                }

                override fun onFailure(e: RetrofitException?) {
                    super.onFailure(e)
                    tvInfo.text = e.toString()
                    onRequesting = false
                }
            })
    }

    var checkerWindow: HumanChecker.ICheckerWindow? = null

    override fun onResume() {
        super.onResume()
        checkerWindow = object : HumanChecker.ICheckerWindow {
            override fun show(scene: String?): Boolean {
                CenterToast.showToast("我们直接设一个参数说明流程能通")

                tvInfo.postDelayed({
                    HumanChecker.startHandle(
                        HumanCheckParam(
                            scene = "mock_scene",
                            sig = "mock_sig",
                            ncToken = "mock_nc_token",
                            sessionid = "mock_session_id"
                        )
                    )
                }, 3000)

                return true
            }
        }
        HumanChecker.iCheckerWindow = checkerWindow
    }

    override fun onPause() {
        if (HumanChecker.iCheckerWindow == checkerWindow)
            HumanChecker.iCheckerWindow = null
        checkerWindow = null

        super.onPause()
    }

    override fun onDestroy() {
        disposable?.dispose()
        super.onDestroy()
    }
}
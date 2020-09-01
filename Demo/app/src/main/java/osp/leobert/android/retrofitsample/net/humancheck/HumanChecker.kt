package osp.leobert.android.retrofitsample.net.humancheck

import osp.leobert.android.retrofitsample.log.L
import java.lang.ref.WeakReference

typealias CheckRunnable = Function1<HumanCheckParam?, Unit>

object HumanChecker {

    interface ICheckerWindow {
        fun show(scene: String?): Boolean
    }

    var iCheckerWindow: ICheckerWindow? = null

    private val runnableList: MutableList<CheckRunnable> = arrayListOf()

    //无关细节已移除

    fun requestCheck(scene: String?, checkRunnableRef: WeakReference<CheckRunnable>) {
        if (iCheckerWindow == null) {
            checkRunnableRef.get()?.invoke(null)
            return
        }
        //目前不做场景归约设计，认为都是一个场景
        synchronized(this) {
            checkRunnableRef.get()?.let {
                runnableList.add(it)
            }
        }
        iCheckerWindow?.show(scene).takeIf { it == false }?.let {
            startHandle(null)
        }
    }

    fun startHandle(param: HumanCheckParam?) {
        synchronized(this) {
            runnableList.forEach {
                try {
                    it.invoke(param)
                } catch (e: Exception) {
                    L.e("HumanChecker", "exception", e)
                }
            }
            runnableList.clear()
        }
    }
}
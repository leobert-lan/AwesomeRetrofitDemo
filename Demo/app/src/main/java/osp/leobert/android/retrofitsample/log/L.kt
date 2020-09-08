package osp.leobert.android.retrofitsample.log

import android.util.Log

/**
 * <p><b>Package:</b> osp.leobert.android.retrofitsample.log </p>
 * <p><b>Project:</b> RetrofitSample </p>
 * <p><b>Classname:</b> L </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2020/9/1.
 */
class L {

    companion object {
        private var TAG: String = "DEMO"
        private val DEBUG: Boolean = true

        @JvmStatic
        fun tag(tag: String) {
            if (DEBUG) {
                TAG = tag
            }
        }

        @JvmStatic
        fun v(msg: String) {
            if (DEBUG) {
                Log.v(TAG, msg)
            }
        }

        @JvmStatic
        fun v(tag: String?, msg: String) {
            if (DEBUG) {
                Log.v(tag, msg)
            }
        }

        @JvmStatic
        fun d(msg: String) {
            if (DEBUG) {
                Log.d(TAG, msg)
            }
        }

        @JvmStatic
        fun d(tag: String?, msg: String) {
            if (DEBUG) {
                Log.d(tag, msg)
            }
        }

        @JvmStatic
        fun i(msg: String) {
            if (DEBUG) {
                Log.i(TAG, msg)
            }
        }

        @JvmStatic
        fun i(tag: String?, msg: String) {
            if (DEBUG) {
                Log.i(tag, msg)
            }
        }

        @JvmStatic
        fun w(msg: String) {
            if (DEBUG) {
                Log.w(TAG, msg)
            }
        }

        @JvmStatic
        fun w(tag: String?, msg: String) {
            if (DEBUG) {
                Log.w(tag, msg)
            }
        }

        @JvmStatic
        fun e(msg: String) {
            if (DEBUG) {
                Log.e(TAG, msg)
            }
        }

        @JvmStatic
        fun e(tag: String?, msg: String) {
            if (DEBUG) {
                Log.e(tag, msg)
            }
        }

        @JvmStatic
        fun e(tag: String?, msg: String, e: Throwable?) {
            if (DEBUG) {
                Log.e(tag, msg, e)
            }
        }

        @JvmStatic
        fun e(throwable: Throwable?) {
            if (DEBUG) {
                Log.e("MOTOR", "log throwable", throwable)
            }
        }
    }
}
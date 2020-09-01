package osp.leobert.android.retrofitsample;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.StringRes;

public class CenterToast {

    public static void showToast(@StringRes int stringId) {
        Context context = DemoApp.Companion.getInstance();
        if (context != null)
            Toast.makeText(context, context.getString(stringId), Toast.LENGTH_SHORT).show();
    }

    public static void showToast(String info) {
        Context context = DemoApp.Companion.getInstance();
        if (context != null)
            Toast.makeText(context, info, Toast.LENGTH_SHORT).show();
    }
//无关细节移除


}

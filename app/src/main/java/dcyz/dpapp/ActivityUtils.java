package dcyz.dpapp;

import android.app.AlertDialog;
import android.content.Context;

public class ActivityUtils {
    public static final int REQUEST_PERMISSION_NETWORK = 100;
    public static final int REQUEST_PERMISSION_LOCATION = 200;

    /**
     * 弹出对话框（AlertDialog）
     *
     * @param title 对话框标题
     * @param msg   对话框内容
     */
    public static void setDialog(Context context, String title, String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(title);
        dialog.setMessage(msg);
        dialog.show();
    }
}

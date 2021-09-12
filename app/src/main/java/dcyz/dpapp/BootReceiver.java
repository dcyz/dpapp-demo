package dcyz.dpapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO BootReceiver用于开机自启动服务，如需开启则取消BootReceiver和Manifest的注释
//        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
//            Intent serviceIntent = new Intent(context, QueryService.class);
//            context.startService(serviceIntent);
//        }
    }
}
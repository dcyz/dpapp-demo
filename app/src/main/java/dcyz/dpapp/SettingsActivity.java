package dcyz.dpapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;

import util.ActivityUtils;

public class SettingsActivity extends AppCompatActivity {

    private ActivityUtils.MyReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // 接收器，在跳转到下一个activity后接受广播信息，关闭此activity
        receiver = new ActivityUtils.MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.CLOSE_SETTINGS");
        filter.addAction("android.intent.action.CLOSE_ALL");
        registerReceiver(receiver, filter);
    }

    public void onUpload(View view) {

    }
}
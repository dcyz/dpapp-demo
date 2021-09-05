package dcyz.dpapp;

import static dcyz.dpapp.ActivityUtils.getEncryptedSharedPreferences;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ActivityUtils.MyReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 广播intent，关闭SignInActivity和WelcomeActivity
        Intent intent1 = new Intent();
        intent1.setAction("android.intent.action.CLOSE_SIGNIN");
        sendBroadcast(intent1);
        Intent intent2 = new Intent();
        intent2.setAction("android.intent.action.CLOSE_WELCOME");
        sendBroadcast(intent2);

        // 接收器，在跳转到下一个activity后接受广播信息，关闭此activity
        receiver = new ActivityUtils.MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.CLOSE_MAIN");
        filter.addAction("android.intent.action.CLOSE_ALL");
        registerReceiver(receiver, filter);

        // 设置DrawerLayout及其toolbar，使导航按钮能够监听点击动作并展开滑动窗口
        mDrawerLayout = (DrawerLayout) findViewById(R.id.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            mDrawerLayout.openDrawer(GravityCompat.START);
            ((TextView) findViewById(R.id.textNavName)).setText(getIntent().getStringExtra("user"));
        });
    }

    public void onClickSettings(MenuItem menuItem) {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    public void onClickSignOut(MenuItem menuItem) {
        SharedPreferences sharedPreferences = getEncryptedSharedPreferences(MainActivity.this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("status", false);
        editor.apply();
        startActivity(new Intent(MainActivity.this, SignInActivity.class));
        finish();
    }
}
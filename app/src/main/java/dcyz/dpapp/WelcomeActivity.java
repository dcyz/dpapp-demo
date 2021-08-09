package dcyz.dpapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import models.RspModel;
import models.structs.User;
import network.HttpsManager;
import network.MyCallback;
import retrofit2.Call;
import retrofit2.Retrofit;
import services.PostRequest;

public class WelcomeActivity extends AppCompatActivity {

    private MyReceiver receiver;
    private Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //取消显示标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_welcome);

        // 延时2秒跳转下一个activity
        handler.sendEmptyMessageDelayed(0, 2000);

        // 接收器，在跳转到下一个activity后接受广播信息，关闭此activity
        receiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.CLOSE_WELCOME");
        registerReceiver(receiver, filter);

        // 设置Okhttp和Retrofit库允许自签名证书
        retrofit = HttpsManager.getRetrofit(WelcomeActivity.this);
    }

    // 内部类，广播信息的接收器
    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 注销监听器
        unregisterReceiver(receiver);
    }

    // handler延时提交任务
    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            autoSignIn();
        }
    };

    /**
     * 自动登录
     * 如果sharedPreferences中存储了user和passwd，则自动登录获取token
     */
    private void autoSignIn() {
        // 从sharedPreferences中获取user和passwd
        SharedPreferences sharedPreferences = getSharedPreferences("dp-app", MODE_PRIVATE);
        String user = sharedPreferences.getString("user", "");
        String passwd = sharedPreferences.getString("passwd", "");

        // 如果sharedPreferences中没有存储user和passwd，则跳转到SignInActivity
        if (user.equals("") || passwd.equals("")) {
            Intent intent = new Intent(WelcomeActivity.this, SignInActivity.class);
            startActivity(intent);
        } else {
            // 如果有user和passwd则尝试自动登录
            PostRequest postRequest = retrofit.create(PostRequest.class);
            Call<RspModel<User>> resp = postRequest.signIn(new User(user, passwd));
            resp.enqueue(new MyCallback<User>() {
                @Override
                protected void success(String msg, User data) {
                    if (data != null) {
                        // 将user和passwd写入sharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences("dp-app", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("user", user);
                        editor.putString("passwd", passwd);
                        editor.apply();
                        // 设置token
                        HttpsManager.setToken("Bearer " + data.getToken());
                        // 获取token后跳转到下一个Activity
                        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                        Log.d("WelcomeActivity", "Token: " + HttpsManager.getToken());
                    } else {
                        startActivity(new Intent(WelcomeActivity.this, SignInActivity.class));
                        Log.d("WelcomeActivity", msg);
                    }
                }

                @Override
                protected void failed(int type, String msg) {
                    startActivity(new Intent(WelcomeActivity.this, SignInActivity.class));
                    Log.d("WelcomeActivity", msg);
                }
            });
        }
    }
}
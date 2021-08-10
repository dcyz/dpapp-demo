package dcyz.dpapp;

import static dcyz.dpapp.ActivityUtils.setDialog;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import models.RspModel;
import models.structs.User;
import network.HttpsManager;
import network.MyCallback;
import retrofit2.Call;
import retrofit2.Retrofit;
import services.PostRequest;

public class SignInActivity extends AppCompatActivity {

    private MyReceiver receiver;
    private Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        // 发送广播，关闭WelcomeActivity
        Intent intent = new Intent();
        intent.setAction("android.intent.action.CLOSE_WELCOME");
        sendBroadcast(intent);

        // 设置Okhttp和Retrofit库允许自签名证书
        retrofit = HttpsManager.getRetrofit(SignInActivity.this);

        receiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.CLOSE_SIGNIN");
        registerReceiver(receiver, filter);

        // 自动填充用户名和密码
        SharedPreferences sharedPreferences = getSharedPreferences("dp-app", MODE_PRIVATE);
        String user = sharedPreferences.getString("user", "");
        String passwd = sharedPreferences.getString("passwd", "");
        ((EditText) findViewById(R.id.editName)).setText(user);
        ((EditText) findViewById(R.id.editPasswd)).setText(passwd);
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //注销监听器
        unregisterReceiver(receiver);
    }

    /**
     * 注册按钮的监听事件
     *
     * @param view ...
     */
    public void onClickSignUp(View view) {
        EditText editName = findViewById(R.id.editName);
        EditText editPasswd = findViewById(R.id.editPasswd);
        String user = editName.getText().toString(), passwd = editPasswd.getText().toString();
        if (user.equals("") || passwd.equals("")) {
            setDialog(SignInActivity.this, "出错啦 QAQ", "用户名和密码不能为空");
            return;
        }
        // 通过Retrofit构建请求
        PostRequest postRequest = retrofit.create(PostRequest.class);
        Call<RspModel<User>> resp = postRequest.signUp(new User(user, passwd));
        resp.enqueue(new MyCallback<User>() {
            @Override
            protected void success(String msg, User data) {
                if (data != null) {
                    // TODO user和passwd均以明文形式存放，可以考虑加密后存储
                    // 将user和passwd写入sharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("dp-app", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("user", user);
                    editor.putString("passwd", passwd);
                    editor.putBoolean("status", true);
                    editor.apply();
                    // 设置token
                    HttpsManager.setToken("Bearer " + data.getToken());
                    // 获取token后跳转到下一个Activity
                    Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                    intent.putExtra("user", user);
                    startActivity(intent);
                    ActivityUtils.setDialog(SignInActivity.this, "注册成功 =ω=", "");
                    Log.d("signup", "Token: " + HttpsManager.getToken());
                } else {
                    Log.d("signup", msg);
                    ActivityUtils.setDialog(SignInActivity.this, "数据弄丢啦 QAQ", msg);
                }
            }

            @Override
            protected void failed(int type, String msg) {
                ActivityUtils.setDialog(SignInActivity.this, "似乎出了点问题 QAQ", msg);
                Log.d("signup", msg);
            }
        });
    }

    /**
     * 登录按钮的监听事件
     *
     * @param view ...
     */
    public void onClickSignIn(View view) {
        EditText editName = findViewById(R.id.editName);
        EditText editPasswd = findViewById(R.id.editPasswd);
        String user = editName.getText().toString(), passwd = editPasswd.getText().toString();
        if (user.equals("") || passwd.equals("")) {
            setDialog(SignInActivity.this, "出错啦 QAQ", "用户名和密码不能为空");
            return;
        }
        // 通过Retrofit构建请求
        PostRequest postRequest = retrofit.create(PostRequest.class);
        Call<RspModel<User>> resp = postRequest.signIn(new User(user, passwd));
        resp.enqueue(new MyCallback<User>() {
            @Override
            protected void success(String msg, User data) {
                if (data != null) {
                    // TODO user和passwd均以明文形式存放，可以考虑加密后存储
                    // 将user和passwd写入sharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("dp-app", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("user", user);
                    editor.putString("passwd", passwd);
                    editor.putBoolean("status", true);
                    editor.apply();
                    // 设置token
                    HttpsManager.setToken("Bearer " + data.getToken());
                    // 获取token后跳转到下一个Activity
                    Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                    intent.putExtra("user", user);
                    startActivity(intent);
                    ActivityUtils.setDialog(SignInActivity.this, "登录成功 =ω=", "");
                    Log.d("signin", "Token: " + HttpsManager.getToken());
                } else {
                    ActivityUtils.setDialog(SignInActivity.this, "数据弄丢啦 QAQ", msg);
                    Log.d("signin", msg);
                }
            }

            @Override
            protected void failed(int type, String msg) {
                ActivityUtils.setDialog(SignInActivity.this, "似乎出了点问题 QAQ", msg);
                Log.d("signin", msg);
            }
        });
    }
}
package dcyz.dpapp;

import static dcyz.dpapp.ActivityUtils.REQUEST_PERMISSION_NETWORK;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.util.Arrays;

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

        Intent intent = new Intent();
        intent.setAction("android.intent.action.CLOSE_WELCOME");
        sendBroadcast(intent);

        // 请求网络权限
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SignInActivity.this, new String[]{Manifest.permission.INTERNET, Manifest.permission.ACCESS_WIFI_STATE}, REQUEST_PERMISSION_NETWORK);
        }
        // 设置Okhttp和Retrofit库允许自签名证书
        retrofit = HttpsManager.getRetrofit(SignInActivity.this);

        receiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.CLOSE_SIGNIN");
        registerReceiver(receiver, filter);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_NETWORK) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.d("onRequestPermissionResult", "成功申请权限：" + Arrays.toString(permissions));
            } else {
                Log.d("onRequestPermissionResult", "申请权限失败：" + Arrays.toString(permissions));
                ActivityUtils.setDialog(SignInActivity.this, "出错啦 ~", "网络权限申请失败");
            }
        }
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
        // 通过Retrofit构建请求
        PostRequest postRequest = retrofit.create(PostRequest.class);
        Call<RspModel<User>> resp = postRequest.signUp(new User(user, passwd));
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
                    Intent intent = new Intent(SignInActivity.this, FirstActivity.class);
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
        // 通过Retrofit构建请求
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
                    Intent intent = new Intent(SignInActivity.this, FirstActivity.class);
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
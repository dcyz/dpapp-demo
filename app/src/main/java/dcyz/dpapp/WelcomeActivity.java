package dcyz.dpapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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
import services.PostRequest;

public class WelcomeActivity extends AppCompatActivity {

    private final int REQUEST_PERMISSION_NETWORK = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        // 请求网络权限
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(WelcomeActivity.this, new String[]{Manifest.permission.INTERNET, Manifest.permission.ACCESS_WIFI_STATE}, REQUEST_PERMISSION_NETWORK);
        }
        // 设置Okhttp和Retrofit库允许自签名证书
        HttpsManager.setHttps(WelcomeActivity.this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_NETWORK) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.d("onRequestPermissionResult", "成功申请权限：" + Arrays.toString(permissions));
            } else {
                Log.d("onRequestPermissionResult", "申请权限失败：" + Arrays.toString(permissions));
                ActivityUtils.setDialog(WelcomeActivity.this, "出错啦 ~", "网络权限申请失败");
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
        PostRequest postRequest = HttpsManager.getRetrofit().create(PostRequest.class);
        Call<RspModel<User>> resp = postRequest.signUp(new User(user, passwd));
        resp.enqueue(new MyCallback<User>() {
            @Override
            protected void success(String msg, User data) {
                if (data != null) {
                    ActivityUtils.setDialog(WelcomeActivity.this, "注册成功 =ω=", "");
                    // 设置token
                    HttpsManager.setToken("Bearer " + data.getToken());
                    // 获取token后跳转到下一个Activity
                    Intent intent = new Intent(WelcomeActivity.this, FirstActivity.class);
                    startActivity(intent);
                    Log.d("signup", "Token: " + HttpsManager.getToken());
                } else {
                    Log.d("signup", msg);
                    ActivityUtils.setDialog(WelcomeActivity.this, "么得数据哇 QAQ", msg);
                }
            }

            @Override
            protected void failed(int type, String msg) {
                ActivityUtils.setDialog(WelcomeActivity.this, "似乎出了点问题 QAQ", msg);
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
        PostRequest postRequest = HttpsManager.getRetrofit().create(PostRequest.class);
        Call<RspModel<User>> resp = postRequest.signIn(new User(user, passwd));
        resp.enqueue(new MyCallback<User>() {
            @Override
            protected void success(String msg, User data) {
                if (data != null) {
                    ActivityUtils.setDialog(WelcomeActivity.this, "登录成功 =ω=", "");
                    // 设置token
                    HttpsManager.setToken("Bearer " + data.getToken());
                    // 获取token后跳转到下一个Activity
                    Intent intent = new Intent(WelcomeActivity.this, FirstActivity.class);
                    startActivity(intent);
                    Log.d("signin", "Token: " + HttpsManager.getToken());
                } else {
                    Log.d("signin", msg);
                    ActivityUtils.setDialog(WelcomeActivity.this, "么得数据哇 QAQ", msg);
                }
            }

            @Override
            protected void failed(int type, String msg) {
                ActivityUtils.setDialog(WelcomeActivity.this, "似乎出了点问题 QAQ", msg);
                Log.d("signin", msg);
            }
        });
    }
}
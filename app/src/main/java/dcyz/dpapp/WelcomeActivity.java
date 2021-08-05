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

import models.RspModel;
import models.structs.User;
import network.HttpsManager;
import network.MyCallback;
import retrofit2.Call;
import services.PostRequest;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        // 请求网络权限
        RequestPermissions(WelcomeActivity.this, Manifest.permission.INTERNET);
        RequestPermissions(WelcomeActivity.this, Manifest.permission.ACCESS_WIFI_STATE);
        // 设置Okhttp和Retrofit库允许自签名证书
        HttpsManager.setHttps(WelcomeActivity.this);
    }

    /**
     * 获取必要的权限
     *
     * @param context    上下文信息
     * @param permission 申请的权限
     */
    private void RequestPermissions(@NonNull Context context, @NonNull String permission) {
        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            Log.i("RequestPermissions", ": [ " + permission + " ]没有授权，申请权限");
            ActivityCompat.requestPermissions((Activity) context, new String[]{permission}, 100);
        } else {
            Log.i("RequestPermissions", ": [ " + permission + " ]有权限");
        }
    }

    /**
     * 注册按钮的监听事件
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
                    setDialog("注册成功 =ω=", "");
                    // 设置token
                    HttpsManager.setToken("Bearer " + data.getToken());
                    // 获取token后跳转到下一个Activity
                    Intent intent = new Intent(WelcomeActivity.this, FirstActivity.class);
                    startActivity(intent);
                    Log.d("signup", "Token: " + HttpsManager.getToken());
                } else {
                    Log.d("signup", msg);
                    setDialog("么得数据哇 QAQ", msg);
                }
            }

            @Override
            protected void failed(int type, String msg) {
                setDialog("似乎出了点问题 QAQ", msg);
                Log.d("signup", msg);
            }
        });
    }

    /**
     * 登录按钮的监听事件
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
                    setDialog("登录成功 =ω=", "");
                    // 设置token
                    HttpsManager.setToken("Bearer " + data.getToken());
                    // 获取token后跳转到下一个Activity
                    Intent intent = new Intent(WelcomeActivity.this, FirstActivity.class);
                    startActivity(intent);
                    Log.d("signin", "Token: " + HttpsManager.getToken());
                } else {
                    Log.d("signin", msg);
                    setDialog("么得数据哇 QAQ", msg);
                }
            }

            @Override
            protected void failed(int type, String msg) {
                setDialog("似乎出了点问题 QAQ", msg);
                Log.d("signin", msg);
            }
        });
    }


    /**
     * 弹出对话框（AlertDialog）
     * @param title 对话框标题
     * @param msg   对话框内容
     */
    public void setDialog(String title, String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(WelcomeActivity.this);
        dialog.setTitle(title);
        dialog.setMessage(msg);
        dialog.show();
    }
}
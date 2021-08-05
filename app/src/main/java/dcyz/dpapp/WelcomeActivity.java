package dcyz.dpapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import network.HttpsManager;
import services.Requests;

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

    public void onClickSignUp(View view) {
        EditText editName = findViewById(R.id.editName);
        EditText editPasswd = findViewById(R.id.editPasswd);
        Requests.signup(WelcomeActivity.this, editName.getText().toString(), editPasswd.getText().toString());
    }

    public void onClickSignIn(View view) {
        EditText editName = findViewById(R.id.editName);
        EditText editPasswd = findViewById(R.id.editPasswd);
        Requests.signin(WelcomeActivity.this, editName.getText().toString(), editPasswd.getText().toString());
    }

    public void onClickUpload(View view) {
    }

    public void onClickDownload(View view) {
    }
}
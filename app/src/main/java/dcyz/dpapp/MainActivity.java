package dcyz.dpapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;

import https.Requests;
import https.OkhttpManager;
import https.Types;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private Retrofit retrofit = null;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 请求网络权限
        RequestPermissions(MainActivity.this, Manifest.permission.INTERNET);
        RequestPermissions(MainActivity.this, Manifest.permission.ACCESS_WIFI_STATE);
        // 设置Okhttp和Retrofit库允许自签名证书
        try {
            OkhttpManager.getInstance().setTrustrCertificates(getAssets().open("certificate"));
            OkHttpClient mOkhttpClient = OkhttpManager.getInstance().build();
            // 实例化Retrofit对象
            retrofit = new Retrofit.Builder()
                    .client(mOkhttpClient)
                    .baseUrl("https://39.107.92.179/")
                    // 使用Gson进行（反）序列化
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public void signup(String user, String passwd) {
        Requests.PostRequest postRequest = retrofit.create(Requests.PostRequest.class);
        Call<Types.Result> resp = postRequest.signIn(new Types.User(user, passwd), "");
        resp.enqueue(new Callback<Types.Result>() {
            @Override
            public void onResponse(@NotNull Call<Types.Result> call, @NotNull Response<Types.Result> response) {
                assert response.body() != null;
                int status = response.body().getStatus();
                if (status != 0) {
                    String msg = response.body().getMsg();
                    hinter("signup", msg);
                } else {
                    token = (String) (response.body().getData().get("Token"));
                    hinter("signup", "Token: " + token);
                }
            }

            @Override
            public void onFailure(@NotNull Call<Types.Result> call, @NotNull Throwable t) {
                hinter("signup", "Get Token Failed");
                t.printStackTrace();
            }
        });
    }

    public void signin(String user, String passwd) {
        Requests.PostRequest postRequest = retrofit.create(Requests.PostRequest.class);
        Call<Types.Result> resp = postRequest.signIn(new Types.User(user, passwd), "");
        resp.enqueue(new Callback<Types.Result>() {
            @Override
            public void onResponse(@NotNull Call<Types.Result> call, @NotNull Response<Types.Result> response) {
                assert response.body() != null;
                int status = response.body().getStatus();
                if (status != 0) {
                    String msg = response.body().getMsg();
                    hinter("signin", msg);
                } else {
                    token = (String) (response.body().getData().get("Token"));
                    hinter("signin", "Token: " + token);
                }
            }

            @Override
            public void onFailure(@NotNull Call<Types.Result> call, @NotNull Throwable t) {
                hinter("signin", "Get Token Failed");
                t.printStackTrace();
            }
        });
    }

    public void upload(View view) {

    }

    public void download() {
        Requests.GetRequest getRequest = retrofit.create(Requests.GetRequest.class);
        Call<ResponseBody> resp = getRequest.download(new HashMap<>(), "Bearer " + token);
        resp.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                String contentType = response.headers().get("Content-Type");
                assert contentType != null;
                if (contentType.contains("json")) {
                    try {
                        assert response.body() != null;
                        Gson gson = new Gson();
                        Types.Result result = gson.fromJson(response.body().string(), Types.Result.class);
                        hinter("DOWNLOAD", result.getMsg());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    hinter("DOWNLOAD", contentType);
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                Toast.makeText(MainActivity.this, "Get Token Failed", Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }


    private void hinter(String tag, String msg) {
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
        Log.d(tag, msg);
    }

    public void onClickSignUp(View view) {
        signup("234", "234");
    }

    public void onClickSignIn(View view) {
        signin("234", "234");
    }

    public void onClickUpload(View view) {
    }

    public void onClickDownload(View view) {
        download();
    }
}
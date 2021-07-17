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

import https.GetRequest;
import https.OkhttpManager;
import https.PostRequest;
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
            retrofit = new Retrofit.Builder()
                    .client(mOkhttpClient)
                    .baseUrl("https://39.107.92.179/")
                    // 使用Gson进行序列化
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void RequestPermissions(@NonNull Context context, @NonNull String permission) {
        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            Log.i("RequestPermissions", ": [ " + permission + " ]没有授权，申请权限");
            ActivityCompat.requestPermissions((Activity) context, new String[]{permission}, 100);
        } else {
            Log.i("RequestPermissions", ": [ " + permission + " ]有权限");
        }
    }

    public void signup(View view) {

    }

    public void signin(View view) {
        PostRequest postRequest = retrofit.create(PostRequest.class);
        Types.User user = new Types.User("234", "234");
        Call<Types.Result> resp = postRequest.signIn("signin", user, "");
        resp.enqueue(new Callback<Types.Result>() {
            @Override
            public void onResponse(@NotNull Call<Types.Result> call, @NotNull Response<Types.Result> response) {
                assert response.body() != null;
                int status = response.body().getStatus();
                if (status != 0) {
                    String msg = response.body().getMsg();
                    hinter("SIGNIN", msg);
                } else {
                    token = (String) (response.body().getData().get("Token"));
                    hinter("SIGNIN", "Token: " + token);
                }
            }

            @Override
            public void onFailure(@NotNull Call<Types.Result> call, @NotNull Throwable t) {
                hinter("SIGNIN", "Get Token Failed");
                t.printStackTrace();
            }
        });
    }

    public void upload(View view) {

    }

    public void download(View view) {
        GetRequest getRequest = retrofit.create(GetRequest.class);
        Call<ResponseBody> resp = getRequest.getResult("download", new HashMap<>(), "Bearer " + token);
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
}
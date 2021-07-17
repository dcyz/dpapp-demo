package https;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;

import dcyz.dpapp.MainActivity;
import gson.Result;
import gson.User;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Requests {

    public static String token = "";
    private static Retrofit retrofit;

    public static void setHttps(Context context) {
        try {
            OkhttpManager.getInstance().setTrustrCertificates(context.getAssets().open("certificate"));
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

    public static String signup(String user, String passwd) {
        PostRequest postRequest = retrofit.create(PostRequest.class);
        Call<Result> resp = postRequest.signUp(new User(user, passwd), "");
        final String[] msg = new String[1];
        resp.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(@NotNull Call<Result> call, @NotNull Response<Result> response) {
                assert response.body() != null;
                int status = response.body().getStatus();
                if (status != 0) {
                    msg[0] = response.body().getMsg();
                    Log.d("signup", msg[0]);
                } else {
                    msg[0] = response.body().getMsg();
                    Log.d("signup", msg[0]);
                }
            }

            @Override
            public void onFailure(@NotNull Call<Result> call, @NotNull Throwable t) {
                Log.d("signup", "Get Token Failed");
                t.printStackTrace();
            }
        });
        return msg[0];
    }

    public static String signin(String user, String passwd) {
        PostRequest postRequest = retrofit.create(PostRequest.class);
        Call<Result> resp = postRequest.signIn(new User(user, passwd), "");
        final String[] msg = new String[1];
        resp.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(@NotNull Call<Result> call, @NotNull Response<Result> response) {
                assert response.body() != null;
                int status = response.body().getStatus();
                if (status != 0) {
                    msg[0] = response.body().getMsg();
                    Log.d("signin", msg[0]);
                } else {
                    msg[0] = response.body().getMsg();
                    Requests.token = "Bearer " + response.body().getData().get("Token");
                    Log.d("signin", "Token: " + Requests.token);
                }
            }

            @Override
            public void onFailure(@NotNull Call<Result> call, @NotNull Throwable t) {
                Log.d("signin", "Get Token Failed");
                t.printStackTrace();
            }
        });
        return msg[0];
    }

//    public static String upload() {
//
//    }

    public static String download() {
        GetRequest getRequest = retrofit.create(GetRequest.class);
        Call<ResponseBody> resp = getRequest.download(new HashMap<>(), Requests.token);
        final String[] msg = new String[1];
        resp.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                String contentType = response.headers().get("Content-Type");
                assert contentType != null;
                if (contentType.contains("json")) {
                    try {
                        assert response.body() != null;
                        Gson gson = new Gson();
                        Result result = gson.fromJson(response.body().string(), Result.class);
                        msg[0] = result.getMsg();
                        Log.d("download", msg[0]);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    msg[0] = response.message();
                    Log.d("download", contentType);
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                Log.d("download", "Get Token Failed");
                t.printStackTrace();
            }
        });
        return msg[0];
    }
}




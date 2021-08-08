package network;

import android.content.Context;

import java.io.IOException;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpsManager {
    private static String token;

    public static void setToken(String token) {
        HttpsManager.token = token;
    }

    public static String getToken() {
        return token;
    }

    public static Retrofit getRetrofit(Context context) {
        Retrofit retrofit = null;
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
        return retrofit;
    }
}

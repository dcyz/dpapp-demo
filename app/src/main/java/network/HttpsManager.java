package network;

import android.content.Context;

import java.io.IOException;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpsManager {
    public static Retrofit retrofit;

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
}

package https;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface GetRequest {
    // 获取服务端计算结果
    @Headers({
            "User-Agent: dpapp"
    })
    @HTTP(method = "GET", path = "{path}")
    Call<ResponseBody> getResult(
            @Path("path") String path,
            @QueryMap Map<String, String> query,
            @Header("Authorization") String auth
    );
}

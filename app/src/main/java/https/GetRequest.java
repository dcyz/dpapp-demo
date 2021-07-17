package https;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.QueryMap;

/**
 * Get请求的接口
 */
public interface GetRequest {
    /**
     * 获取服务端计算结果
     *
     * @param query 查询Map
     * @param auth  Token字符串
     * @return ResponseBody的Call
     */
    @Headers({
            "User-Agent: dpapp"
    })
    @HTTP(method = "GET", path = "download")
    Call<ResponseBody> download(
            @QueryMap Map<String, String> query,
            @Header("Authorization") String auth
    );
}

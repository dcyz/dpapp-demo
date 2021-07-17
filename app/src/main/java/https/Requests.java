package https;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.QueryMap;

public class Requests {

    /**
     * Get请求的接口
     */
    public interface GetRequest {
        /**
         * 获取服务端计算结果
         * @param query 查询Map
         * @param auth Token字符串
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

    /**
     * Post请求的接口
     */
    public interface PostRequest {

        /**
         * 登录至服务器，body形式application/json
         * @param body Types.User对象
         * @param auth Token字符串
         * @return Types.Result的Call
         */
        @Headers({
                "Content-Type: application/json",
                "User-Agent: dpapp"
        })
        @HTTP(method = "POST", path = "signin", hasBody = true)
        Call<Types.Result> signIn(
                @Body Types.User body,
                @Header("Authorization") String auth
        );

        /**
         * 注册至服务器，body形式application/json
         * @param body Types.User对象
         * @param auth Token字符串
         * @return Types.Result的Call
         */
        @Headers({
                "Content-Type: application/json",
                "User-Agent: dpapp"
        })
        @HTTP(method = "POST", path = "signup", hasBody = true)
        Call<Types.Result> signUp(
                @Body Types.User body,
                @Header("Authorization") String auth
        );
    }


}

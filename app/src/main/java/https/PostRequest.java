package https;

import gson.Result;
import gson.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.Headers;

/**
 * Post请求的接口
 */
public interface PostRequest {

    /**
     * 登录至服务器，body形式application/json
     *
     * @param body Types.User对象
     * @param auth Token字符串
     * @return Types.Result的Call
     */
    @Headers({
            "Content-Type: application/json",
            "User-Agent: dpapp"
    })
    @HTTP(method = "POST", path = "signin", hasBody = true)
    Call<Result> signIn(
            @Body User body,
            @Header("Authorization") String auth
    );

    /**
     * 注册至服务器，body形式application/json
     *
     * @param body Types.User对象
     * @param auth Token字符串
     * @return Types.Result的Call
     */
    @Headers({
            "Content-Type: application/json",
            "User-Agent: dpapp"
    })
    @HTTP(method = "POST", path = "signup", hasBody = true)
    Call<Result> signUp(
            @Body User body,
            @Header("Authorization") String auth
    );
}
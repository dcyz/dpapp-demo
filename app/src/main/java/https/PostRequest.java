package https;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface PostRequest {
    // 登录至服务器
    @Headers({
            "Content-Type: application/json",
            "User-Agent: dpapp"
    })
    @HTTP(method = "POST", path = "{path}", hasBody = true)
    Call<Types.Result> signIn(
            @Path("path") String path,
            @Body Types.User body,
            @Header("Authorization") String auth
    );
}

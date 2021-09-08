package services;

import java.util.ArrayList;
import java.util.Queue;

import models.RspModel;
import models.structs.Query;
import retrofit2.Call;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.Headers;

public interface GetRequest {
    /**
     * 刷新AccessToken
     *
     * @return Types.Result的Call
     */
    @Headers({
            "Content-Type: application/json",
            "User-Agent: dpapp"
    })
    @HTTP(method = "GET", path = "/user/refresh")
    Call<RspModel<String>> refresh(
            @Header("Authorization") String refreshToken
    );

    /**
     * 获取调查区域
     *
     * @return Types.Result的Call
     */
    @Headers({
            "Content-Type: application/json",
            "User-Agent: dpapp"
    })
    @HTTP(method = "GET", path = "/user/query")
    Call<RspModel<Query>> query(
            @Header("Authorization") String accessToken
    );
}

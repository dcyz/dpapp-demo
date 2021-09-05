package network;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import dcyz.dpapp.ActivityUtils;
import dcyz.dpapp.SignInActivity;
import models.RspModel;
import models.structs.RespStatus;
import retrofit2.Call;
import services.GetRequest;

public class Errors {

    private static final int AccessTokenExpired = 1;
    private static final int RefreshTokenExpired = 2;

    public static void responseErrorCheck(Context context, RespStatus respStatus) {
        switch (respStatus.getCode()) {
            case 401:
                tokenCheck(context, respStatus);
            default:
        }
    }

    private static void tokenCheck(Context context, RespStatus respStatus) {
        int code = respStatus.getCode();
        int status = respStatus.getStatus();
        String msg = respStatus.getMsg();

        if (code == 401 && status == AccessTokenExpired) {
            GetRequest getRequest = HttpsManager.getRetrofit().create(GetRequest.class);
            Call<RspModel<String>> resp = getRequest.refresh(HttpsManager.getRefreshToken());
            resp.enqueue(new MyCallback<String>() {
                @Override
                protected void success(RespStatus innerRespStatus, String token) {
                    if (token != null) {
                        HttpsManager.setAccessToken("Bearer " + token);
                        Log.d("tokenCheck-1", "AccessToken: " + HttpsManager.getAccessToken());
                        Toast.makeText(context, "Token Refresh Successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d("tokenCheck-2", innerRespStatus.getMsg());
                    }
                }

                @Override
                protected void failed(RespStatus respStatus, Call<RspModel<String>> call) {
                    if (respStatus.getCode() == 401 && respStatus.getStatus() == RefreshTokenExpired) {
                        Intent intent = new Intent(context, SignInActivity.class);
                        context.startActivity(intent);
                    }
                    Log.d("tokenCheck-3", msg);
                }
            });
        } else if (code == 401) {
            Intent intent = new Intent(context, SignInActivity.class);
            context.startActivity(intent);
        } else {
            ActivityUtils.setDialog(context, "TokenCheck", msg);
        }
    }
}

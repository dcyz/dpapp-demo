package services;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import dcyz.dpapp.FirstActivity;
import network.HttpsManager;
import network.MyCallback;
import models.RspModel;
import models.structs.User;
import retrofit2.Call;
import services.interfaces.PostRequest;


public class Requests {

    private static String token;

    public static void signup(Context context, String user, String passwd) {
        PostRequest postRequest = HttpsManager.retrofit.create(PostRequest.class);
        Call<RspModel<User>> resp = postRequest.signUp(new User(user, passwd));
        resp.enqueue(new MyCallback<User>() {
            @Override
            protected void success(String msg, User data) {
                if (data != null) {
                    setDialog(context, "注册成功 =ω=", "");
                    token = "Bearer " + data.getToken();
                    Intent intent = new Intent(context, FirstActivity.class);
                    context.startActivity(intent);
                    Log.d("signup", "Token: " + token);
                } else {
                    Log.d("signup", msg);
                    setDialog(context, "么得数据哇~", msg);
                }
            }

            @Override
            protected void failed(int type, String msg) {
                setDialog(context, "似乎出了点问题~", msg);
                Log.d("signup", msg);
            }
        });
    }

    public static void signin(Context context, String user, String passwd) {
        PostRequest postRequest = HttpsManager.retrofit.create(PostRequest.class);
        Call<RspModel<User>> resp = postRequest.signIn(new User(user, passwd));
        resp.enqueue(new MyCallback<User>() {
            @Override
            protected void success(String msg, User data) {
                if (data != null) {
                    setDialog(context, "登录成功 =ω=", "");
                    token = "Bearer " + data.getToken();
                    Intent intent = new Intent(context, FirstActivity.class);
                    context.startActivity(intent);
                    Log.d("signin", "Token: " + token);
                } else {
                    Log.d("signin", msg);
                    setDialog(context, "么得数据哇~", msg);
                }
            }

            @Override
            protected void failed(int type, String msg) {
                setDialog(context, "似乎出了点问题~", msg);
                Log.d("signin", msg);
            }
        });
    }

    public static void setDialog(Context context, String title, String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(title);
        dialog.setMessage(msg);
        dialog.show();
    }

}




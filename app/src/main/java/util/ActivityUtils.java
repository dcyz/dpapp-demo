package util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;

import dcyz.dpapp.SignInActivity;
import models.RspModel;
import models.structs.RespStatus;
import network.HttpsManager;
import network.MyCallback;
import retrofit2.Call;
import services.GetRequest;

public class ActivityUtils extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }

    /**
     * 弹出对话框（AlertDialog）
     *
     * @param title 对话框标题
     * @param msg   对话框内容
     */
    public static void setDialog(Context context, String title, String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(title);
        dialog.setMessage(msg);
        dialog.show();
    }

    public static SharedPreferences getEncryptedSharedPreferences(Context context) {
        SharedPreferences sharedPreferences = null;
        try {
            MasterKey.Builder builder = new MasterKey.Builder(context);
            MasterKey masterKey = builder.setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build();
            sharedPreferences = EncryptedSharedPreferences.create(
                    context,
                    "dp-app",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
        return sharedPreferences;
    }

    public static class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ((Activity) context).finish();
        }
    }

    public static int getGradientColor(double weight, float alpha) {
        if (weight < 0.25) {
            return Color.argb(alpha, 0, (float) (weight * 4), 1);
        } else if (weight < 0.5) {
            return Color.argb(alpha, 0, 1, (float) ((0.5 - weight) * 4));
        } else if (weight < 0.75) {
            return Color.argb(alpha, (float) ((weight - 0.5) * 4), 1, 0);
        } else {
            return Color.argb(alpha, 1, (float) ((1 - weight) * 4), 0);
        }
    }

}

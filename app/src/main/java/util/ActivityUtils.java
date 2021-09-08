package util;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.amap.api.maps.model.LatLng;

import java.io.IOException;
import java.security.GeneralSecurityException;

import dcyz.dpapp.SignInActivity;
import models.RspModel;
import models.structs.RespStatus;
import network.HttpsManager;
import network.MyCallback;
import retrofit2.Call;
import services.GetRequest;

public class ActivityUtils {

    private static Handler tokenHandler;
    private static LocationManager locationManager;
    private static String providerName;

    public static void setTokenHandler(Context context) {
        tokenHandler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Runnable runnable1 = this;
                GetRequest getRequest = HttpsManager.getRetrofit(context).create(GetRequest.class);
                Call<RspModel<String>> resp = getRequest.refresh(HttpsManager.getRefreshToken());
                resp.enqueue(new MyCallback<String>() {
                    @Override
                    protected void success(RespStatus respStatus, String token) {
                        if (token != null) {
                            HttpsManager.setAccessToken("Bearer " + token);
                            Log.d("setTokenHandler-1", "AccessToken: " + HttpsManager.getAccessToken());
                        } else {
                            Log.d("setTokenHandler-2", respStatus.getMsg());
                        }
                    }

                    @Override
                    protected void failed(RespStatus respStatus, Call<RspModel<String>> call) {
                        Intent intent = new Intent(context, SignInActivity.class);
                        context.startActivity(intent);
                        tokenHandler.removeCallbacks(runnable1);
                        Log.d("setTokenHandler-3", respStatus.getMsg());
                    }
                });
                tokenHandler.postDelayed(this, 1000 * 60);
            }
        };
        tokenHandler.post(runnable);
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

    public static boolean isInArea(LatLng point, LatLng center, double width) {
        double pointLat = point.latitude;
        double pointLng = point.longitude;

        double latMin = center.latitude - width / 2;
        double latMax = center.latitude + width / 2;
        double lngMin = center.longitude - width / 2;
        double lngMax = center.longitude + width / 2;

        return (pointLat >= latMin) && (pointLat <= latMax) && (pointLng >= lngMin) && (pointLng <= lngMax);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static void setLocationService(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ((Activity) context).requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
            }, 100);
            return;
        }

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            return;
        }

        final LocationListener listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 10, 100, listener);

    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static LatLng getLocation(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ((Activity) context).requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
            }, 100);
            return null;
        }
        if (locationManager == null) {
            setLocationService(context);
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            Toast.makeText(context, "[ " + location.getLatitude() + ", " + location.getLongitude() + " ]", Toast.LENGTH_SHORT).show();
            return new LatLng(location.getLatitude(), location.getLongitude());
        } else {
            Toast.makeText(context, "获取位置失败", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

}

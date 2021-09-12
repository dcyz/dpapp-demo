package util;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import static util.ActivityUtils.setDialog;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.amap.api.maps.model.LatLng;

public class LocationUtils {
    private static LocationManager locationManager;

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
    public static boolean checkLocationPermission(Context context) {
        return (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static void getLocationPermission(Context context) {
        ((Activity) context).requestPermissions(
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                }, 100
        );
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static void setLocationService(Context context) {
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

    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static LatLng getLocation(Context context) {
        if (locationManager == null) {
            setLocationService(context);
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            //Toast.makeText(context, "[ " + location.getLatitude() + ", " + location.getLongitude() + " ]", Toast.LENGTH_SHORT).show();
            return new LatLng(location.getLatitude(), location.getLongitude());
        } else {
            Toast.makeText(context, "获取位置失败", Toast.LENGTH_SHORT).show();
            return null;
        }
    }
}

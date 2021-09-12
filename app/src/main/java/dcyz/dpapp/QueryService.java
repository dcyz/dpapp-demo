package dcyz.dpapp;

import static util.LocationUtils.checkLocationPermission;
import static util.LocationUtils.getLocation;
import static util.LocationUtils.isInArea;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.amap.api.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;

import models.RspModel;
import models.structs.Query;
import models.structs.RespStatus;
import network.HttpsManager;
import network.MyCallback;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import rappor.Rappor;
import retrofit2.Call;
import services.GetRequest;
import services.PostRequest;

public class QueryService extends Service {
    private Handler queryHandler = null;
    private Runnable runnable;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        queryHandler.removeCallbacks(runnable);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("ServiceStart", "Service Starts");
        if (!checkLocationPermission(QueryService.this)) {
            Log.d("ServiceStart", "Service Stops");
            Toast.makeText(QueryService.this, "服务中止（无位置权限）", Toast.LENGTH_SHORT).show();
            stopSelf();
            return;
        }
        query();
    }

    private void query() {
        queryHandler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                GetRequest getRequest = HttpsManager.getRetrofit(QueryService.this).create(GetRequest.class);
                Call<RspModel<Query>> resp = getRequest.query(HttpsManager.getAccessToken());
                resp.enqueue(new MyCallback<Query>() {
                    @RequiresApi(api = Build.VERSION_CODES.Q)
                    @Override
                    protected void success(RespStatus respStatus, Query query) {
                        checkAreas(query);
                    }

                    @Override
                    protected void failed(RespStatus respStatus, Call<RspModel<Query>> call) {
                    }
                });
                queryHandler.postDelayed(this, 1000 * 10);
            }
        };
        queryHandler.post(runnable);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void checkAreas(Query query) {
        ArrayList<ArrayList<Double>> areas = query.getAreas();
        HashMap<String, Double> args = query.getArgs();
        int scale = query.getScale();
        int bitLen = scale * areas.size();

        Double f = args.get("f"), p = args.get("p"), q = args.get("q");
        if (f == null || p == null || q == null) {
            Log.d("checkAreas", "args are null");
            return;
        }
        Rappor rappor = new Rappor(bitLen);
        rappor.setParams(f, p, q);

        LatLng location = getLocation(QueryService.this);
        if (location == null) {
            Toast.makeText(QueryService.this, "无法获取当前位置", Toast.LENGTH_SHORT).show();
            Log.d("checkAreas", "args are null");
            return;
        }

        for (int i = 0; i < areas.size(); i++) {
            double lng = areas.get(i).get(0), lat = areas.get(i).get(1), width = areas.get(i).get(2);
            if (!isInArea(location, new LatLng(lat, lng), width)) {
                continue;
            }
            for (int j = 0; j < scale; j++) {
                rappor.setBit(i + areas.size() * j);
            }
        }
        rappor.rr();
        PostRequest postRequest = HttpsManager.getRetrofit(QueryService.this).create(PostRequest.class);
        RequestBody body = RequestBody.create(MediaType.parse("application/octet-stream"), rappor.getData());
        Call<RspModel<String>> resp = postRequest.upload(body, HttpsManager.getAccessToken());
        resp.enqueue(new MyCallback<String>() {
            @Override
            protected void success(RespStatus respStatus, String query) {
                Toast.makeText(QueryService.this, "上传成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void failed(RespStatus respStatus, Call<RspModel<String>> call) {
                Toast.makeText(QueryService.this, respStatus.getMsg(), Toast.LENGTH_SHORT).show();
            }
        });
        Log.d("rappor", rappor.getDataBinaryString());
    }
}
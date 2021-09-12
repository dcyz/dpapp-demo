package dcyz.dpapp;

import static com.amap.api.maps.AMapOptions.ZOOM_POSITION_RIGHT_CENTER;
import static util.ActivityUtils.getEncryptedSharedPreferences;
import static util.ActivityUtils.getGradientColor;
import static util.ActivityUtils.setDialog;
import static util.LocationUtils.checkLocationPermission;
import static util.LocationUtils.getLocationPermission;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.Gradient;
import com.amap.api.maps.model.HeatmapTileProvider;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.api.maps.model.TileOverlayOptions;
import com.amap.api.maps.model.WeightedLatLng;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.RspModel;
import models.structs.AreaStat;
import models.structs.Query;
import models.structs.RespStatus;
import models.structs.Token;
import network.HttpsManager;
import network.MyCallback;
import rappor.Rappor;
import retrofit2.Call;
import services.GetRequest;
import util.ActivityUtils;

public class MainActivity extends AppCompatActivity implements Inputtips.InputtipsListener {

    private DrawerLayout mDrawerLayout;
    private final ArrayList<Map<String, String>> resultList = new ArrayList<>();
    private SimpleAdapter resultAdapter;
    private MapView mMapView;
    private final Handler mHandler = new Handler();
    private AMap aMap = null;
    private BitmapDescriptor bitmapDescriptor;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 广播intent，关闭SignInActivity和WelcomeActivity
        Intent intent1 = new Intent();
        intent1.setAction("android.intent.action.CLOSE_SIGNIN");
        sendBroadcast(intent1);
        Intent intent2 = new Intent();
        intent2.setAction("android.intent.action.CLOSE_WELCOME");
        sendBroadcast(intent2);

        // 接收器，在跳转到下一个activity后接受广播信息，关闭此activity
        ActivityUtils.MyReceiver receiver = new ActivityUtils.MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.CLOSE_MAIN");
        filter.addAction("android.intent.action.CLOSE_ALL");
        registerReceiver(receiver, filter);

        setDrawLayout();
        setListView();
        setMapView(savedInstanceState);

        Intent tokenService = new Intent(MainActivity.this, TokenService.class);
        startService(tokenService);

        if (!checkLocationPermission(this)) {
            setDialog(this, "位置权限", "应用程序需要位置权限以提供服务");
            getLocationPermission(this);
        } else {
            Intent serviceIntent = new Intent(MainActivity.this, QueryService.class);
            startService(serviceIntent);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 100) {
            ArrayList<String> failed = new ArrayList<>();
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    failed.add(permissions[i]);
                }
            }
            if (failed.size() == 0) {
                Intent serviceIntent = new Intent(MainActivity.this, QueryService.class);
                startService(serviceIntent);
            } else {
                getLocationPermission(this);
            }
        }
    }

    private void setDrawLayout() {
        // 设置DrawerLayout及其toolbar，使导航按钮能够监听点击动作并展开滑动窗口
        mDrawerLayout = (DrawerLayout) findViewById(R.id.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            mDrawerLayout.openDrawer(GravityCompat.START);
            ((TextView) findViewById(R.id.textNavName)).setText(getIntent().getStringExtra("user"));
        });
    }

    private void setListView() {
        ListView resultListView = (ListView) findViewById(R.id.poiList);
        resultAdapter = new SimpleAdapter(
                MainActivity.this,
                resultList,
                R.layout.listview_main,
                new String[]{"name", "desc"},
                new int[]{R.id.poiName, R.id.poiDesc}
        );
        resultListView.setAdapter(resultAdapter);
        resultListView.setBackgroundColor(getResources().getColor(R.color.white));

        resultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, String> hashMap = (HashMap<String, String>) resultList.get(position);
                if (hashMap.get("lat") != null && hashMap.get("lng") != null) {
                    double lat = Double.parseDouble(hashMap.get("lat"));
                    double lng = Double.parseDouble(hashMap.get("lng"));
                    LatLng latLng = new LatLng(lat, lng);
                    Marker marker = aMap.addMarker(
                            new MarkerOptions()
                                    .position(latLng)
                                    .title(hashMap.get("name"))
                                    .snippet(hashMap.get("desc"))
                                    .icon(bitmapDescriptor)
                    );
                    marker.showInfoWindow();
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(lat, lng)).zoom(15).build();
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                    aMap.animateCamera(cameraUpdate);
                }
            }
        });
    }

    private void setMapView(Bundle savedInstanceState) {
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.mapView);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        //初始化地图控制器对象
        if (aMap == null) {
            aMap = mMapView.getMap();
        }
        UiSettings mUiSettings;//定义一个UiSettings对象
        mUiSettings = aMap.getUiSettings();//实例化UiSettings类对象
        mUiSettings.setScaleControlsEnabled(true);
        mUiSettings.setZoomPosition(ZOOM_POSITION_RIGHT_CENTER);

        bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
    }

    @Override
    public void onGetInputtips(List<Tip> list, int i) {
        if (i != 1000) {
            resultList.clear();
            resultAdapter.notifyDataSetChanged();
            Toast.makeText(MainActivity.this, "获取查询信息失败", Toast.LENGTH_SHORT).show();
            return;
        }
        resultList.clear();
        for (int index = 0; index < list.size(); index++) {
            HashMap<String, String> hashMap = new HashMap<>();
            Tip tip = list.get(index);
            hashMap.put("name", tip.getName());
            hashMap.put("desc", tip.getAddress());
            if (tip.getPoint() != null) {
                hashMap.put("lat", String.valueOf(tip.getPoint().getLatitude()));
                hashMap.put("lng", String.valueOf(tip.getPoint().getLongitude()));
            }
            resultList.add(hashMap);
        }
        resultAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        resultList.clear();
        resultAdapter.notifyDataSetChanged();

        getMenuInflater().inflate(R.menu.menu_main_search, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView mSearchView = (SearchView) MenuItemCompat.getActionView(item);
        mSearchView.setQueryHint("输入查询地点");
//        mSearchView.setSubmitButtonEnabled(true);
//        mSearchView.setIconifiedByDefault(false);

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mHandler.removeCallbacksAndMessages(null);
                resultList.clear();
                resultAdapter.notifyDataSetChanged();

                if (newText.equals("")) {
                    return true;
                }
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        InputtipsQuery inputquery = new InputtipsQuery(newText, null);
                        Inputtips inputTips = new Inputtips(MainActivity.this, inputquery);
                        inputTips.setInputtipsListener(MainActivity.this);
                        inputTips.requestInputtipsAsyn();
                    }
                }, 300);
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }


    public void onClickSettings(MenuItem menuItem) {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    public void onClickSignOut(MenuItem menuItem) {
        SharedPreferences sharedPreferences = getEncryptedSharedPreferences(MainActivity.this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("status", false);
        editor.apply();
        startActivity(new Intent(MainActivity.this, SignInActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        Log.d("MainActivity", "onDestroy");
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
        Intent tokenService = new Intent(MainActivity.this, TokenService.class);
        stopService(tokenService);
        Intent queryService = new Intent(MainActivity.this, QueryService.class);
        stopService(queryService);
    }

    @Override
    protected void onResume() {
        Log.d("MainActivity", "onResume");
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        Log.d("MainActivity", "onPause");
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            moveTaskToBack(true);
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    public void onClickSearch(View view) {
        Context context = ActivityUtils.getContext();
        GetRequest getRequest = HttpsManager.getRetrofit(context).create(GetRequest.class);
        Call<RspModel<ArrayList<AreaStat>>> resp = getRequest.search(HttpsManager.getAccessToken());
        resp.enqueue(new MyCallback<ArrayList<AreaStat>>() {
            @Override
            protected void success(RespStatus respStatus, ArrayList<AreaStat> stats) {
                aMap.clear();
                int max = 0;
                for (AreaStat stat : stats) {
                    if (stat.getCount() > max) {
                        max = stat.getCount();
                    }
                }
                for (AreaStat stat : stats) {
                    aMap.addPolygon(
                            new PolygonOptions().addAll(getSquareArea(stat.getLng(), stat.getLat(), stat.getWidth()))
                                    .fillColor(getGradientColor(stat.getCount() * 1.0 / max, 0.5f))
                                    .strokeColor(Color.argb(0, 0, 0, 0))
                                    .strokeWidth(0)
                    );
                }

            }

            @Override
            protected void failed(RespStatus respStatus, Call<RspModel<ArrayList<AreaStat>>> call) {
                Toast.makeText(MainActivity.this, respStatus.getMsg(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private ArrayList<LatLng> getSquareArea(double lng, double lat, double width) {
        ArrayList<LatLng> points = new ArrayList<>();
        points.add(new LatLng(lat - width / 2, lng - width / 2));
        points.add(new LatLng(lat + width / 2, lng - width / 2));
        points.add(new LatLng(lat + width / 2, lng + width / 2));
        points.add(new LatLng(lat - width / 2, lng + width / 2));
        return points;
    }

}
package dcyz.dpapp;

import static dcyz.dpapp.ActivityUtils.setTokenHandler;

import androidx.appcompat.app.AppCompatActivity;

import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import models.RspModel;
import models.structs.RespStatus;
import network.HttpsManager;
import network.MyCallback;
import retrofit2.Call;
import services.PostRequest;

public class SettingsActivity extends AppCompatActivity {

    private ActivityUtils.MyReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // 接收器，在跳转到下一个activity后接受广播信息，关闭此activity
        receiver = new ActivityUtils.MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.CLOSE_SETTINGS");
        filter.addAction("android.intent.action.CLOSE_ALL");
        registerReceiver(receiver, filter);

        setTokenHandler(SettingsActivity.this);
    }

    public void onUpload(View view) {
        PostRequest postRequest = HttpsManager.getRetrofit().create(PostRequest.class);
        Call<RspModel<String>> resp = postRequest.upload("Kacsas", HttpsManager.getAccessToken());
        resp.enqueue(new MyCallback<String>() {
            @Override
            protected void success(RespStatus respStatus, String data) {
                if (data != null) {
                    Log.d("upload", data);
                    Toast.makeText(SettingsActivity.this, data, Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("upload", respStatus.getMsg());
                    Toast.makeText(SettingsActivity.this, respStatus.getMsg(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            protected void failed(RespStatus respStatus, Call<RspModel<String>> call) {
                Log.d("upload", "[ " + respStatus.getCode() + " | " + respStatus.getStatus() + " ]" + respStatus.getMsg());
            }
        });
    }
}
package network;

import android.util.Log;

import androidx.annotation.NonNull;

import models.RspModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class MyCallback<T> implements Callback<RspModel<T>> {
    private int status;
    private String msg;

    public String getMsg() {
        return msg;
    }

    public int getStatus() {
        return status;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public void onResponse(@NonNull Call<RspModel<T>> call, Response<RspModel<T>> response) {
        RspModel<T> rspModel = response.body();
        if (rspModel != null) {
            int status = rspModel.getStatus();
            String msg = rspModel.getMsg();
            Log.d("MyCallback", status + " -> " + msg);
            if (rspModel.getStatus() == 0) {
                success(msg, rspModel.getData());
            } else {
                failed(rspModel.getStatus(), msg);
            }
        } else {
            failed(0, null);
        }
    }

    @Override
    public void onFailure(@NonNull Call<RspModel<T>> call, Throwable t) {
        failed(-1, t.getMessage());
    }

    protected abstract void success(String msg, T data);

    protected abstract void failed(int type, String Msg);
}
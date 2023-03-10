package com.rokid.rkglassdemokotlin.network;

import android.util.Log;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class ResultCallback<T extends Result> implements Callback<T> {
    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (response.raw().code() == 200) {
            try {
                int errorCode = response.body().getCode();
                if (errorCode == 200) {
                    onSuccess(response);
                } else {
                    onFail(response.body().getMessage());
                }
            } catch (Exception e) {
                onFail("解析错误");
            }
        } else {
            try {
                onFailure(call, new RuntimeException(response.raw().message()));
            } catch (Exception e) {
                Log.e("TAG", "catch_else");
            }
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable e) {
        String msgStr = "";
        if (e instanceof ConnectException || e instanceof SocketException) {
            msgStr = "网络连接超时，请检查您的网络状态！";
        } else if (e instanceof SocketTimeoutException) {
            msgStr = "服务器响应超时";
        } else if (e instanceof RuntimeException) {
            msgStr = "数据请求错误";
        } else {
            msgStr = "数据异常";
        }
        onFail(msgStr);
    }

    public abstract void onSuccess(Response<T> response);

    public abstract void onFail(String message);
}

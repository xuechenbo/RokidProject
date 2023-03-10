package com.rokid.rkglassdemokotlin.network;

import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public class ResponseInterceptor implements Interceptor {
    private final String TAG = "ResponseInterceptor ";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        if(response!= null){
            if ( response.header("token") != null ){
                //获取请求头中的token 并保存在sp中
                final String token = response.header("token");
                Log.e(TAG,"保存在本地的token为:"+token);
            }
        }
        return response;
    }
}

package com.rokid.rkglassdemokotlin.network;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpUtil {


    public void TestHttp() {
        String url = "http://www.baidu.com";
        //生成OkHttpClient实例对象'
        OkHttpClient okHttpClient = new OkHttpClient();
//        OkHttpClient.Builder builder = okHttpClient.newBuilder();
//        builder.connectTimeout(15, TimeUnit.SECONDS);//连接超时时间
//        builder.readTimeout(10, TimeUnit.SECONDS);//读操作超时时间
//        builder.writeTimeout(10, TimeUnit.SECONDS);
        FormBody mFormBody = new FormBody.Builder()
                .add("age", "211")
                .add("sex", "男")
                .build();
        //生成Request对象'
        Request request = new Request.Builder()
                .url(url)
                .post(mFormBody)
                .build();
        //生成Call对象'
        Call call = okHttpClient.newCall(request);
        //异步请求：enqueue  ,同execute
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String requestContent = response.body().toString();
            }
        });
    }

}

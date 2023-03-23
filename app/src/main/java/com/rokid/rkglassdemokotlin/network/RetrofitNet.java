package com.rokid.rkglassdemokotlin.network;

import android.util.Log;

import com.rokid.rkglassdemokotlin.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

public class RetrofitNet {
//    private static final String BASE_URL = "https://www.wanandroid.com/";
    private static final String BASE_URL = "http://39.105.6.180:8888/";
//    private static final String BASE_URL = "http://39.105.6.180:8888/";
    private static final int DEFAULT_TIME_OUT = 20;
    private static final int DEFAULT_READ_TIME_OUT = 25;
    private final Retrofit build;
    private static RetrofitNet retrofitServiceManager;

    public static RetrofitNet getInstance() {
        if (retrofitServiceManager == null) {
            synchronized (RetrofitNet.class) {
                if (retrofitServiceManager == null) {
                    retrofitServiceManager = new RetrofitNet();
                }
            }
        }
        return retrofitServiceManager;
    }


    public void clearRetrofit() {
        retrofitServiceManager = null;
    }

    public RetrofitNet() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.retryOnConnectionFailure(true);//默认重试一次
        builder.connectTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS);//连接超时时间
        builder.readTimeout(DEFAULT_READ_TIME_OUT, TimeUnit.SECONDS);//读操作超时时间
        builder.writeTimeout(DEFAULT_READ_TIME_OUT, TimeUnit.SECONDS);
        //最大连接数
//        builder.connectionPool(new ConnectionPool(100, 8, TimeUnit.MINUTES));
        if (BuildConfig.DEBUG) {
            // 日志显示级别
            HttpLoggingInterceptor.Level level = HttpLoggingInterceptor.Level.BODY;
            //新建log拦截器
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(String message) {
                    Log.e("HTTP", "OkHttp====Message:" + message);
                }
            });
            loggingInterceptor.setLevel(level);
            builder.addInterceptor(loggingInterceptor);
        }

        //创建Retrofit
        build = new Retrofit.Builder().client(builder.build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(new Retrofit2ConverterFactory())
                .baseUrl(BASE_URL)
                .build();
    }

    public AppService getApi() {
        return build.create(AppService.class);
    }
}

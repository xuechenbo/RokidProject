package com.rokid.rkglassdemokotlin.test;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.rokid.rkglassdemokotlin.R;
import com.rokid.rkglassdemokotlin.camera.FaceBean;
import com.rokid.rkglassdemokotlin.network.BitmapUtil;
import com.rokid.rkglassdemokotlin.network.ListBean;
import com.rokid.rkglassdemokotlin.network.ProjectBean;
import com.rokid.rkglassdemokotlin.network.Result;
import com.rokid.rkglassdemokotlin.network.ResultCallback;
import com.rokid.rkglassdemokotlin.network.RetrofitNet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;


public class ImageActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        findViewById(R.id.bt_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getInfo();
            }
        });
        findViewById(R.id.bt_upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //bitmap 转 png
                try {
                    upLoadImage(BitmapUtil.saveImage(ImageActivity.this, BitmapUtil.drawableToBmp(ImageActivity.this, R.drawable.liu)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    //上传图片
    private void upLoadImage(String path) {
//        File file = new File(path);
//        Log.e("TAG", "uploadHeadImage: File=====" + file.getAbsolutePath());
//        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
//        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
//        RetrofitNet.getInstance().getApi().updateSignStr(body).enqueue(new ResultCallback<Result<FaceBean>>() {
//            @Override
//            public void onSuccess(Response<Result<FaceBean>> response) {
////                Log.e("HTTP====", response.body().getData().getKey());
//            }
//
//            @Override
//            public void onFail(String message) {
//                Log.e("HTTP====", message);
//            }
//        });
    }

    //测试嘞
    private void getInfo() {
        //TODO success
        RetrofitNet.getInstance().getApi().getProject().enqueue(new ResultCallback<ProjectBean>() {
            @Override
            public void onSuccess(Response<ProjectBean> response) {
                Log.e("HTTP", response.body().getOffset() + "uuuuuuuuuuuuuuu");
            }

            @Override
            public void onFail(String message) {
                Log.e("HTTP", message);
            }
        });
        //TODO success
//        Call<Result<String>> json = RetrofitNet.getInstance().getApi().getJSON();
//        json.enqueue(new ResultCallback<Result<String>>() {
//            @Override
//            public void onSuccess(Response<Result<String>> response) {
//                Log.e("HTTP", response.body().getData());
//            }
//
//            @Override
//            public void onFail(String message) {
//                Log.e("onFail", message");
//            }
//        });

        //TODO success
//        RetrofitNet.getInstance().getApi().getJSONBean().enqueue(new ResultCallback<ListBean>() {
//            @Override
//            public void onSuccess(Response<ListBean> response) {
//                Log.e("HTTP",response.body().getData().getTotal()+"uuuuuuuuuuuuuuu");
//            }
//
//            @Override
//            public void onFail(String message) {
//                Log.e("onFail", message);
//            }
//        });


    }


}

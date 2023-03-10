package com.rokid.rkglassdemokotlin.test;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Sample {
    public static final String API_KEY = "t7CUDjzzRf33pmzEXN5h5nhd";
    public static final String SECRET_KEY = "NBIX0lB0xEpcjasPPfGkd7bP1abzEURi";

    static final OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder().build();

    public static String getImageString(String strBase64) throws IOException, JSONException {
        MediaType mediaType = MediaType.parse("application/json");
        ImageBean imageBean = new ImageBean();
        imageBean.setImage(strBase64);
        imageBean.setImage_type("BASE64");
        String strContent = new Gson().toJson(imageBean);
        RequestBody body = RequestBody.create(mediaType, strContent);
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/rest/2.0/face/v3/detect?access_token=" + getAccessToken())
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        return response.body().string();
    }


    public static String getImageString111(String strBase64) throws IOException, JSONException {
        MediaType mediaType = MediaType.parse("application/json");
        ImageBean imageBean = new ImageBean();
        imageBean.setImage(strBase64);
        imageBean.setImage_type("BASE64");
        String strContent = new Gson().toJson(imageBean);
        RequestBody body = RequestBody.create(mediaType, strContent);
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/rest/2.0/face/v3/detect?access_token=" + getAccessToken())
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        return response.body().string();
    }

    /**
     * 获取文件base64编码
     *
     * @param path 文件路径
     * @return base64编码信息，不带文件头
     * @throws IOException IO异常
     */
    static String getFileContentAsBase64(String path) throws IOException {
        byte[] b = Files.readAllBytes(Paths.get(path));
        return Base64.getEncoder().encodeToString(b);
    }


    /**
     * 从用户的AK，SK生成鉴权签名（Access Token）
     *
     * @return 鉴权签名（Access Token）
     * @throws IOException IO异常
     */
    static String getAccessToken() throws IOException, JSONException {
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "grant_type=client_credentials&client_id=" + API_KEY
                + "&client_secret=" + SECRET_KEY);
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/oauth/2.0/token")
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        return new JSONObject(response.body().string()).getString("access_token");
    }

}

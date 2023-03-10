package com.rokid.rkglassdemokotlin.network;

import com.rokid.rkglassdemokotlin.camera.FaceBean;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface AppService {

    @FormUrlEncoded
    @POST("users/visitorMode")
    Call<Result<String>> Login(@Field("nickname") String name, @Field("gender") int gender);


    @GET("article/list/0/json")
    Call<Result<String>> getJSON();

    @FormUrlEncoded
    @POST("user/register")
    Call<Result<String>> register(@Field("username") String name, @Field("password") String password, @Field("repassword") String repassword);


    @GET("article/list/0/json")
    Call<ListBean> getJSONBean();


    @GET("article/list/0/json")
    Call<ProjectBean> getProject();


    //上传tp
    @Multipart
    @POST("face/face_search")
    Call<FaceBean> updateSign(@Part MultipartBody.Part file);

    @Multipart
    @POST("face/face_search")
    Call<Result<FaceBean>> updateSignStr(@Part MultipartBody.Part file);
}

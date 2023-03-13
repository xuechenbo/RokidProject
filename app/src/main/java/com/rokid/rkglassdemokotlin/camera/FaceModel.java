package com.rokid.rkglassdemokotlin.camera;

import android.graphics.Bitmap;
import android.graphics.Rect;

public class FaceModel {
    public FaceModel(int faceId, Rect rect, Bitmap bitmap, float imageQualitySimilar) {
        this.faceId = faceId;
        this.rect = rect;
        this.bitmap = bitmap;
        this.imageQualitySimilar = imageQualitySimilar;
    }

    private int faceId;
    private Rect rect;
    private Bitmap bitmap;
    private float imageQualitySimilar;

    private String name = "";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getImageQualitySimilar() {
        return imageQualitySimilar;
    }

    public void setImageQualitySimilar(float imageQualitySimilar) {
        this.imageQualitySimilar = imageQualitySimilar;
    }

    public int getFaceId() {
        return faceId;
    }

    public void setFaceId(int faceId) {
        this.faceId = faceId;
    }

    public Rect getRect() {
        return rect;
    }

    public void setRect(Rect rect) {
        this.rect = rect;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    public String toString() {
        return
                "faceId=" + faceId +
                        ", imageQualitySimilar=" + imageQualitySimilar +
                        ", name=" + name;
    }


    //    public int getFaceIdTotal() {
//        return
//    }
}

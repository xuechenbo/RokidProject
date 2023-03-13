package com.rokid.rkglassdemokotlin.camera;

import com.rokid.rkglassdemokotlin.network.Result;

public class FaceBean extends Result {

    private String name;
    private String faceId;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFaceId() {
        return faceId;
    }

    public void setFaceId(String faceId) {
        this.faceId = faceId;
    }

    @Override
    public String toString() {
        return "FaceBean{" +
                "name='" + name + '\'' +
                ", faceId='" + faceId + '\'' +
                '}';
    }
}

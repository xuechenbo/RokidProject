package com.rokid.rkglassdemokotlin.camera;

import com.rokid.rkglassdemokotlin.network.Result;

public class FaceBean extends Result {

    private String key;
    private String similar;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSimilar() {
        return similar;
    }

    public void setSimilar(String similar) {
        this.similar = similar;
    }

    @Override
    public String toString() {
        return key;
    }
}

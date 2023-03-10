package com.rokid.rkglassdemokotlin.test;

import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;

public class ImageBean implements Serializable {
    private String image;
    private String image_type;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage_type() {
        return image_type;
    }

    public void setImage_type(String image_type) {
        this.image_type = image_type;
    }


    @Override
    public String toString() {

        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                //要推迟执行的方法
            }
        };
        timer.schedule(task,1000,500);
        return "{" +
                "\"image_type\":\"" + image_type + '\"' +
                ",\"image\":\"" + image + '\"' +
                '}';
    }




}

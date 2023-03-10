package com.rokid.rkglassdemokotlin.network;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;

public class MatFactBitmap {
    public static Bitmap getFaceBitmap(Bitmap source, Rect faceRect) {
        //调整人脸框
        Rect newRect = new Rect(faceRect);
        //确保人脸框在图像内
        if (newRect.left < 0) {
            newRect.left = 0;
        }
        if (newRect.top < 0) {
            newRect.top = 0;
        }
        if (newRect.right > source.getWidth()) {
            newRect.right = source.getWidth();
        }
        if (newRect.bottom > source.getHeight()) {
            newRect.bottom = source.getHeight();
        }
        int offsetX = Math.min(Math.min(faceRect.width() / 2, newRect.left), source.getWidth() - newRect.right);
        int offsetY = Math.min(Math.min(faceRect.height() / 2, newRect.top), source.getHeight() - newRect.bottom);
        newRect.inset(-offsetX, -offsetY);
        //创建Bitmap，假设需要顺时针旋转90°
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap bitmap = Bitmap.createBitmap(source, newRect.left, newRect.top, newRect.width(), newRect.height(), matrix, true);
        return bitmap;
    }
}

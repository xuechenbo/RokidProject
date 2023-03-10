package com.rokid.rkglassdemokotlin.network;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapUtil {
    public static Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);  ///把流转化为Bitmap图片

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap drawableToBmp(Context context, int drawableId) {
        Drawable d = context.getResources().getDrawable(drawableId);
        return drawableToBmp(d);
    }

    public static Bitmap drawableToBmp(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static String saveImage(Context context, Bitmap cachebBitmap) throws IOException {
        String storePath;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            storePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath();
            Log.e("TAG", "11路径====" + storePath);
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
            File file = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            Log.d("沙盒文件路径", "openCamera: " + Environment.DIRECTORY_PICTURES);
            storePath = file.getAbsolutePath();
        } else {
            storePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "onName";
            Log.e("TAG", "其他路径====" + storePath);
        }

        File appDir = new File(storePath);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".png";
        Log.e("TAG", "保存路径: " + storePath + "/" + fileName);
        File file = new File(appDir, fileName);
        Bitmap bitmap = cachebBitmap;
        Bitmap outB = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(outB);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, 0, 0, null);

        try {
            FileOutputStream fos = new FileOutputStream(file);
            //通过io流的方式来压缩保存图片
//            Bitmap.CompressFormat.
            outB.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);
            Uri uri = Uri.fromFile(file);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            Log.e("保存成功", "保存成功");
            return storePath + "/" + fileName;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return storePath + "/" + fileName;
    }
}

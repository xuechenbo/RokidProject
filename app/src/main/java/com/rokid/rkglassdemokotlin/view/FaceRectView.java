package com.rokid.rkglassdemokotlin.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;

import com.arcsoft.face.FaceInfo;
import com.rokid.rkglassdemokotlin.R;
import com.rokid.rkglassdemokotlin.camera.FaceModel;

import java.util.ArrayList;
import java.util.List;

public class FaceRectView extends View {
    private Rect rect;
    private String name;

    private int screenWidth;

    private int screenHeight;
    private Paint mPaint;
    private Paint mPaintText;
    private List<FaceModel> faceInfoList = new ArrayList<>();

    public FaceRectView(Context context) {
        this(context, null);
    }

    public FaceRectView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public FaceRectView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        screenWidth = wm.getDefaultDisplay().getWidth();

        screenHeight = wm.getDefaultDisplay().getHeight();

        initPaint(context);

    }

    private void initPaint(Context context) {
        mPaintText = new Paint();
        mPaintText.setAntiAlias(true);
        mPaintText.setStrokeWidth(6);
        mPaintText.setColor(context.getResources().getColor(R.color.blue_a700));
        mPaintText.setTextSize(25);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(6);
        mPaint.setColor(context.getResources().getColor(R.color.red_a700));
    }

    /**
     * 开始画矩形框
     *
     * @param rect1
     */

    public void drawFaceRect(Rect rect1, String name) {
        this.rect = rect1;
        this.name = name;
        //将屏幕人脸框转换为视频区域的人脸框
//        rect.left = rect.left * getWidth() / screenHeight + 20;
//        rect.right = rect.right * getWidth() / screenHeight + 40;
//        rect.bottom = rect.bottom * getHeight() / screenHeight + 75;
        //在主线程发起绘制请求
        postInvalidate();

    }

    public void drawFaceRect(List<FaceModel> list, String name) {
        faceInfoList = list;
        this.name = name;
        postInvalidate();
    }

    public void clearRect() {
        rect = null;
        faceInfoList.clear();
        postInvalidate();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        if (rect != null) {
////            canvas.drawRect(rect,mPaint);
//            //左上角的竖线
//            canvas.drawLine(rect.left, rect.top, rect.left, rect.top + 20, mPaint);
//            //左上角的横线
//            canvas.drawLine(rect.left, rect.top, rect.left + 20, rect.top, mPaint);
//            //右上角的竖线
//            canvas.drawLine(rect.right, rect.top, rect.right - 20, rect.top, mPaint);
//            //右上角的横线
//            canvas.drawLine(rect.right, rect.top, rect.right, rect.top + 20, mPaint);
//            //左下角的竖线
//            canvas.drawLine(rect.left, rect.bottom, rect.left, rect.bottom - 20, mPaint);
//            // 左下角的横线
//            canvas.drawLine(rect.left, rect.bottom, rect.left + 20, rect.bottom, mPaint);
//            //右下角的竖线
//            canvas.drawLine(rect.right, rect.bottom, rect.right, rect.bottom - 20, mPaint);
//            //右下角的横线
//            canvas.drawLine(rect.right, rect.bottom, rect.right - 20, rect.bottom, mPaint);
//            canvas.drawText(name, rect.left - 20, rect.top - 20, mPaintText);
//        }

        if (faceInfoList.size() != 0) {
            for (int i = 0; i < faceInfoList.size(); i++) {
                if (faceInfoList.get(i).getRect() != null) {
                    FaceModel faceInfo = faceInfoList.get(i);
                    Rect rect = faceInfo.getRect();
                    int faceId = faceInfo.getFaceId();
                    canvas.drawLine(rect.left, rect.top, rect.left, rect.top + 20, mPaint);
                    //左上角的横线
                    canvas.drawLine(rect.left, rect.top, rect.left + 20, rect.top, mPaint);
                    //右上角的竖线
                    canvas.drawLine(rect.right, rect.top, rect.right - 20, rect.top, mPaint);
                    //右上角的横线
                    canvas.drawLine(rect.right, rect.top, rect.right, rect.top + 20, mPaint);
                    //左下角的竖线
                    canvas.drawLine(rect.left, rect.bottom, rect.left, rect.bottom - 20, mPaint);
                    // 左下角的横线
                    canvas.drawLine(rect.left, rect.bottom, rect.left + 20, rect.bottom, mPaint);
                    //右下角的竖线
                    canvas.drawLine(rect.right, rect.bottom, rect.right, rect.bottom - 20, mPaint);
                    //右下角的横线
                    canvas.drawLine(rect.right, rect.bottom, rect.right - 20, rect.bottom, mPaint);
//                    canvas.drawText(name, rect.left - 20, rect.top - 20, mPaintText);
                    canvas.drawText(faceInfo.getName(), rect.left - 20, rect.top - 20, mPaintText);
                }
            }
        }


    }


}

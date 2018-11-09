package com.ayuan.paintboard;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;


import static com.ayuan.paintboard.R.id.btn_b;
import static com.ayuan.paintboard.R.id.btn_rad;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private String TAG = "MainActivity";
    private Bitmap bitmap;
    private Bitmap srcBitmap;
    private Paint paint;
    private Canvas canvas;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //用来显示我们化的内容
        imageView = (ImageView) findViewById(R.id.iv_drawingboard);
        Button redButton = (Button) findViewById(btn_rad);
        Button bButton = (Button) findViewById(btn_b);
        Button saveButton = (Button) findViewById(R.id.btn_save);
        redButton.setOnClickListener(this);
        bButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);
        //1.获取bg原图
        srcBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.bg);

        //2.获取原图的副本
        bitmap = Bitmap.createBitmap(srcBitmap.getWidth(), srcBitmap.getHeight(), srcBitmap.getConfig());
        //创建一个Panit对象（画笔）
        paint = new Paint();
        //创建一个Canvas对象（画布）
        canvas = new Canvas(bitmap);
        //开始创建副本
        canvas.drawBitmap(srcBitmap, new Matrix(), paint);
        //开始作画
        canvas.drawLine(20, 20, 60, 60, paint);
        imageView.setImageBitmap(bitmap);
        //给imageView设置一个触摸事件
            imageView.setOnTouchListener(new View.OnTouchListener() {

            private float startY = 0;
            private float startX = 0;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //获取当前事件的类型
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN://按下
                        //获取划线的开始位置
                        startX = event.getX();
                        startY = event.getY();
                        Log.i(TAG, "按下");
                        break;
                    case MotionEvent.ACTION_MOVE://移动:
                        //获取结束位置
                        float stopX = event.getX();
                        float stopY = event.getY();
                        //不停的画线
                        canvas.drawLine(startX, startY, stopX, stopY, paint);
                        //再次显示到控件上
                        imageView.setImageBitmap(bitmap);
                        //更新开始坐标
                        startX = stopX;
                        startY = stopY;
                        Log.i(TAG, "移动");
                        break;
                    case MotionEvent.ACTION_UP://抬起:
                        Log.i(TAG, "抬起");
                        break;
                }
                //如果侦听器已使用该事件，则为True，否则为false。
                return true;//true:监听器处理完事件了   false:
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_b://加粗的点击事件
                bPaint();
                break;
            case R.id.btn_rad://改变颜色的点击事件:
                changeColor();
                break;
            case R.id.btn_save://保存的点击事件
                saveImage();
                break;
        }
    }

    //加粗的方法
    private void bPaint() {
        paint.setStrokeWidth(15f);
    }

    //改变颜色的方法
    private void changeColor() {
        paint.setColor(Color.RED);
    }

    //保存的方法
    private void saveImage() {
        File file = new File("/sdcard/DCIM/Camera", SystemClock.uptimeMillis() + ".png");
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            /**
             * format:图片的保存格式--》PNG，JPEG，WEBP
             * quality:保存图片的质量  int：提示压缩器，0-100。 0表示压缩小尺寸，100表示​​压缩以获得最高质量。某些格式，如无损的PNG，将忽略质量设置
             * stream:
             */
            FileOutputStream finalFileOutputStream = fileOutputStream;
            //开始保存
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, finalFileOutputStream);
            //发送一条广播 欺骗系统相册的应用
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_MEDIA_MOUNTED);
            intent.setAction(Intent.ACTION_MEDIA_UNMOUNTED);
            intent.setData(Uri.fromFile(Environment.getExternalStorageDirectory()));
            //发送一条广播
            sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

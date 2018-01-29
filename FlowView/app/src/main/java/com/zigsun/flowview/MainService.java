package com.zigsun.flowview;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Seal on 2018/1/26.
 */

public class MainService extends Service {

    private static final String TAG = "MainService";
    private WindowManager.LayoutParams mLayoutParams;
    private WindowManager mWindowManager;
    private RelativeLayout mInflate;
    private TextView mTextTv;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createToucher();
    }


    private void createToucher() {
        mLayoutParams = new WindowManager.LayoutParams();
        mWindowManager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);

        //设置type.系统提示型窗口，一般都在应用程序窗口之上.
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        //设置效果为背景透明.
        mLayoutParams.format = PixelFormat.RGBA_8888;
        //设置flags.不可聚焦及不可使用按钮对悬浮窗进行操控.
//        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        //设置窗口初始停靠位置.
//        mLayoutParams.gravity = Gravity.CENTER;
        mLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        mLayoutParams.x = 0;
        mLayoutParams.y = 0;

        //设置悬浮窗口长宽数据.
        mLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        LayoutInflater inflater = LayoutInflater.from(getApplication());
        //获取浮动窗口视图所在布局.
        mInflate = (RelativeLayout) inflater.inflate(R.layout.main_layout, null);
        mWindowManager.addView(mInflate, mLayoutParams);

        //主动计算出当前View的宽高信息.
//        mInflate.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        //用于检测状态栏高度.
//        int identifier = getResources().getIdentifier("status_bar_height", "dimen", "android");

        //浮动窗按钮
        mTextTv = (TextView) mInflate.findViewById(R.id.test_tv);

        mTextTv.setOnClickListener(new View.OnClickListener() {
            long[] hints = new long[2];

            @Override
            public void onClick(View v) {
                Log.i(TAG, "点击了……");
                System.arraycopy(hints, 1, hints, 0, hints.length - 1);
                hints[hints.length - 1] = SystemClock.uptimeMillis();
                if (SystemClock.uptimeMillis() - hints[0] >= 100) {
                    Log.i(TAG, "要执行");
                    Toast.makeText(MainService.this, "连续点击2次退出……", Toast.LENGTH_SHORT).show();
                } else {
                    Log.i(TAG, "即将关闭……");
                    stopSelf();
                }
            }
        });


        mTextTv.setOnTouchListener(new View.OnTouchListener() {
            int startX  ;
            int startY  ;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //获取相对于屏幕的X、Y坐标
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //获取新的相对于屏幕的X、Y坐标
                        int newX = (int) event.getRawX();
                        int newY = (int) event.getRawY();
                        //获取偏差
                        int dx = newX - startX;
                        int dy = newY - startY;

                        mLayoutParams.x +=dx;
                        mLayoutParams.y +=dy;

                        if(mLayoutParams.x<0){
                            mLayoutParams.x = 0;
                        }
                        if(mLayoutParams.y<0){
                            mLayoutParams.y = 0;
                        }
                        if(mLayoutParams.x>(mWindowManager.getDefaultDisplay().getWidth()-mInflate.getWidth())){
                            mLayoutParams.x=(mWindowManager.getDefaultDisplay().getWidth()-mInflate.getWidth());
                        }
                        if(mLayoutParams.y>(mWindowManager.getDefaultDisplay().getHeight()-mInflate.getHeight())){
                            mLayoutParams.y=(mWindowManager.getDefaultDisplay().getHeight()-mInflate.getHeight());
                        }
                        mWindowManager.updateViewLayout(mInflate, mLayoutParams);
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public void onDestroy() {
        if (mTextTv != null) {
            mWindowManager.removeViewImmediate(mInflate);
        }
        super.onDestroy();
    }
}

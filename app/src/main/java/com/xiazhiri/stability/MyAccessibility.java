package com.xiazhiri.stability;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.util.Date;
import java.util.Random;

public class MyAccessibility extends AccessibilityService {

    private static final String TAG = "MyAccessibility";
    private static final int MSG_CONTINUOUS_CLICK = 9527;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_CONTINUOUS_CLICK) {
                continuousClick();
            }
        }
    };
    private Date startTime;

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP && event.getAction() == KeyEvent.ACTION_UP) {
            if (!mHandler.hasMessages(MSG_CONTINUOUS_CLICK)) {
                Toast.makeText(getApplicationContext(), "开始测试", Toast.LENGTH_SHORT).show();
                startTime = new Date();
                continuousClick();
            }
        }
        if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN && event.getAction() == KeyEvent.ACTION_UP) {
            mHandler.removeCallbacksAndMessages(null);
            Toast.makeText(getApplicationContext(), "测试结束", Toast.LENGTH_SHORT).show();
        }
        return super.onKeyEvent(event);
    }

    private void continuousClick() {
        int x = 650 + new Random().nextInt(50);
        int y = 1000 + new Random().nextInt(400);
        click(x, y);
        int sleep = new Random().nextInt(10) + 5;
        Log.d(TAG, "continuousClick: sleep " + sleep);
        mHandler.sendEmptyMessageDelayed(MSG_CONTINUOUS_CLICK, sleep * 1000);
        Toast.makeText(getApplicationContext(),
                "稳定性测试中\n"
                        + "已执行" + (new Date().getTime() - startTime.getTime()) / 1000 / 60 + "分钟\n"
                        + sleep + "秒后触发" +
                        "\n按\"音量-\"停止测试", Toast.LENGTH_LONG).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void click(int x, int y) {
        Log.d(TAG, "click: " + x + ", " + y);
        Path path = new Path();
        path.moveTo(x, y);
        boolean click = dispatchGesture(
                new GestureDescription
                        .Builder()
                        .addStroke(new GestureDescription.StrokeDescription(path, 0, 100)).build(), null, null);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.d(TAG, "onAccessibilityEvent: ");
    }

    @Override
    public void onInterrupt() {
        Log.d(TAG, "onInterrupt: ");
    }
}


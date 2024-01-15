package com.emmanuel_yegon.messageicon;

import static java.security.AccessController.getContext;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.emmanuel_yegon.messageicon.utility.ETUtility;

import java.util.Calendar;

public class WidgetService extends Service {

    private int LAYOUT_FLAG;
    private View mfloatingView;
    private WindowManager windowManager;
    private ImageView imageClose;
    private TextView tvWidget;
    private float height, width;
    private WindowManager.LayoutParams layoutParams;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }

        // Inflate widget layout
        mfloatingView = LayoutInflater.from(this).inflate(R.layout.layout_widget, null);

        layoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        layoutParams.gravity = Gravity.TOP | Gravity.RIGHT;
        layoutParams.x = 0;
        layoutParams.y = 100;

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        // Set up the close button
        imageClose = new ImageView(this);
        imageClose.setImageResource(R.drawable.baseline_clear_white);
        imageClose.setVisibility(View.INVISIBLE);

        WindowManager.LayoutParams imageParams = new WindowManager.LayoutParams(
                140,
                140,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        imageParams.gravity = Gravity.BOTTOM | Gravity.CENTER;
        imageParams.y = 100;

        // Add the close button and the widget layout to the WindowManager
        windowManager.addView(imageClose, imageParams);
        windowManager.addView(mfloatingView, layoutParams);
        mfloatingView.setVisibility(View.VISIBLE);

        height = windowManager.getDefaultDisplay().getHeight();
        width = windowManager.getDefaultDisplay().getWidth();

        tvWidget = mfloatingView.findViewById(R.id.text_widget);

        // Set text on the widget

        tvWidget.setOnTouchListener(new View.OnTouchListener() {
            int initialX, initialY;
            float initialTouchX, initialTouchY;
            long startClickTime;

            int MAX_CLICK_DURATION = 200;
            String txt = "AAA";


            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {

                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        startClickTime = Calendar.getInstance().getTimeInMillis();
                        imageClose.setVisibility(View.VISIBLE);

                        initialX = layoutParams.x;
                        initialY = layoutParams.y;

                        initialTouchX = motionEvent.getRawX();
                        initialTouchY = motionEvent.getRawY();

                        return true;
                    case MotionEvent.ACTION_UP:
                        long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                        imageClose.setVisibility(View.VISIBLE);

                        layoutParams.x = initialX + (int) (initialTouchX - motionEvent.getRawX());
                        layoutParams.y = initialY + (int) (motionEvent.getRawY() - initialTouchY);

                        if (clickDuration < MAX_CLICK_DURATION) {
                            pasteTextFromClipboard();
                            Toast.makeText(WidgetService.this, "AAA Copied to clipboard", Toast.LENGTH_SHORT).show();
                        } else {
                            if (layoutParams.y > (height * 0.6)) {
                                stopSelf();
                            }
                        }

                        return true;
                    case MotionEvent.ACTION_MOVE:
                        layoutParams.x = initialX + (int) (initialTouchX - motionEvent.getRawX());
                        layoutParams.y = initialY + (int) (motionEvent.getRawY() - initialTouchY);

                        windowManager.updateViewLayout(mfloatingView, layoutParams);

                        if (layoutParams.y > (height * 0.6)) {
                            imageClose.setImageResource(R.drawable.baseline_clear_24);
                        } else {
                            imageClose.setImageResource(R.drawable.baseline_clear_white);
                        }

                        return true;
                }
                return false;
            }
        });


        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mfloatingView != null) {
            windowManager.removeView(mfloatingView);
        }

        if (imageClose != null) {
            windowManager.removeView(imageClose);
        }
    }



    private void pasteTextFromClipboard() {
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        // Create a ClipData object with the text to be copied
        ClipData clipData = ClipData.newPlainText("label", "AAA");

        // Set the data to the clipboard
        if (clipboardManager != null) {
            clipboardManager.setPrimaryClip(clipData);
        }

        // Now, the text "AAA" is set to the clipboard
    }



}



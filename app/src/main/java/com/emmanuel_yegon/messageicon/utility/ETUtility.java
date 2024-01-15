package com.emmanuel_yegon.messageicon.utility;

import android.content.Context;
import android.os.Build;

public class ETUtility {

    public static void setClipboard(Context context, String text){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB){
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        }else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);
        }
    }

}

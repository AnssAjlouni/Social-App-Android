package com.rovas.forgram.fogram.Utils.helper;


import android.content.Context;
import android.support.annotation.StringRes;
import android.widget.Toast;

import com.rovas.forgram.fogram.Utils.StringUtils;


/**
 * Created by Mohamed El Sayed
 */
public class ToastHelper {

    public static void show(Context context, String text) {
        if(!StringUtils.isValid(text)) {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
        }
    }

    public static void show(Context context, @StringRes int resourceId){
        show(context, context.getString(resourceId));
    }

}

package com.muy.uisample.utils;

import android.app.Activity;
import android.util.DisplayMetrics;

/**
 * Created by James on 2019-11-30.
 * Desc:
 */
public class UIUtils {

    private static int sScreenHeight = -1;
    private static int sScreenWidth = -1;

    private static void measureScreen(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        sScreenHeight = displayMetrics.heightPixels;
        sScreenWidth = displayMetrics.widthPixels;
    }

    public static int getScreenHeight(Activity activity) {
        if (sScreenHeight == -1) {
            measureScreen(activity);
        }
        return sScreenHeight;
    }

    public static int getScreenWidth(Activity activity) {
        if (sScreenWidth == -1) {
            measureScreen(activity);
        }
        return sScreenWidth;
    }
}

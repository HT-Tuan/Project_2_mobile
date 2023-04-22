package com.project.musicapplication.util;

import android.content.Context;
import android.content.Intent;

public class ActivityUtil {
    public static void openActivity(Context context, Class<?> activityClass) {
        Intent intent = new Intent(context, activityClass);
        context.startActivity(intent);
    }
}

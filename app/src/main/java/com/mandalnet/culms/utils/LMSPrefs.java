package com.mandalnet.culms.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class LMSPrefs {
    private static final String PREF_NAME = "LMS_DATA";
    private static final String KEY_COOKIE = "session_cookie";

    public static void saveCookie(Context context, String cookie) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit().putString(KEY_COOKIE, cookie).apply();
    }

    public static String getCookie(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getString(KEY_COOKIE, "");
    }
}

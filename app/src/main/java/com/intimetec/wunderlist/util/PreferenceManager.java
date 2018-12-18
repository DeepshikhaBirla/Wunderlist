package com.intimetec.wunderlist.util;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {
    private static SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String isUserLogin = "isUserLogin";

    public static boolean isUserLogin(Context context) {
        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        return sharedpreferences.getBoolean(isUserLogin, false);
    }

    public static void setUserLogin(Context context, boolean isLogin) {
        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.putBoolean(isUserLogin, isLogin);
        editor.commit();
    }
}

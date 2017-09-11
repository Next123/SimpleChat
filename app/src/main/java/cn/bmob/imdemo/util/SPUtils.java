package cn.bmob.imdemo.util;

import android.content.Context;
import android.content.SharedPreferences;

import cn.bmob.imdemo.BmobIMApplication;

/**
 * Created by JK on 2017/7/28.
 * <p>
 * sharedpreferences工具
 */

public class SPUtils {
    private static Context context = BmobIMApplication.INSTANCE();
    private static SharedPreferences sp = context.getSharedPreferences("SimpleChat", Context.MODE_PRIVATE);
    private static SharedPreferences.Editor edit = sp.edit();

    public static void putBoolean(String key, boolean value) {
        edit.putBoolean(key, value);
        edit.commit();
    }

    public static boolean getBoolean(String key, boolean defValue) {
        return sp.getBoolean(key, defValue);
    }

    public static void putInt(String key, int value) {
        edit.putInt(key, value);
        edit.commit();
    }

    public static int getInt(String key, int defValue) {
        return sp.getInt(key, defValue);
    }

    public static void putString(String key, String value) {
        edit.putString(key, value);
        edit.commit();
    }

    public static String getString(String key, String defValue) {
        return sp.getString(key, defValue);
    }
}

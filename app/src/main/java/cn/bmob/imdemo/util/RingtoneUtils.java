package cn.bmob.imdemo.util;

import android.content.Context;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.imdemo.BmobIMApplication;

/**
 * Created by JK on 2017/8/1.
 *
 * 铃声工具类
 */

public class RingtoneUtils {

    private static Context mContext = BmobIMApplication.INSTANCE();
    //获取系统默认通知音
    public static Ringtone getDefaultRingtone(int type) {

        return RingtoneManager.getRingtone(mContext, RingtoneManager.getActualDefaultRingtoneUri(mContext, type));

    }

    //获取默认通知URI
    public static Uri getDefaultRingtoneUri(int type) {

        return RingtoneManager.getActualDefaultRingtoneUri(mContext, type);

    }

    //获取系统通知集合
    public static List<Ringtone> getRingtoneList(int type) {

        List<Ringtone> resArr = new ArrayList<Ringtone>();

        RingtoneManager manager = new RingtoneManager(mContext);

        manager.setType(type);

        Cursor cursor = manager.getCursor();

        int count = cursor.getCount();

        for (int i = 0; i < count; i++) {

            resArr.add(manager.getRingtone(i));

        }

        return resArr;

    }

    //获取某一个系统铃声
    public static Ringtone getRingtone(int type, int pos) {

        RingtoneManager manager = new RingtoneManager(mContext);

        manager.setType(type);
        Cursor cursor = manager.getCursor();

        return manager.getRingtone(pos);

    }

    //获取铃声标题
    public static ArrayList<String> getRingtoneTitleList(int type) {

        ArrayList<String> resArr = new ArrayList<String>();

        RingtoneManager manager = new RingtoneManager(mContext);

        manager.setType(type);

        Cursor cursor = manager.getCursor();

        if (cursor.moveToFirst()) {

            do {
                resArr.add(cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX));

            } while (cursor.moveToNext());

        }

        return resArr;

    }


    //获取铃声Uri路径
    public static Uri getRingtoneUriPath(int type, int pos) {

        RingtoneManager manager = new RingtoneManager(mContext);

        manager.setType(type);

        Uri uri = manager.getRingtoneUri(pos);

        return uri == null ? RingtoneManager.getDefaultUri(type) : uri;

    }


    //通过path获取铃声
    public Ringtone getRingtoneByUriPath(int type, String uriPath) {

        RingtoneManager manager = new RingtoneManager(mContext);

        manager.setType(type);

        Uri uri = Uri.parse(uriPath);

        return RingtoneManager.getRingtone(mContext, uri);

    }
}

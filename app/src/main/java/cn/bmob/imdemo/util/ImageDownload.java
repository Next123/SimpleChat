package cn.bmob.imdemo.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

import cn.bmob.imdemo.callback.BitmapCallback;

/**
 * 图片下载类
 */
public class ImageDownload {
    // 传递进接口参数，这样其他类引用的时候就能调用，这个方法在运行的时候又会回调MainActivity的方法
    public Bitmap getBitmap(final String path, final BitmapCallback callback) {

        final Handler handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                Bitmap bitmap = (Bitmap) msg.obj;
                callback.getDownloadBitmap(bitmap);
            }
        };
        // 图片下载进程

        new Thread(new Runnable() {
            @Override
            public void run() {
                URL imageurl = null;
                try {
                    imageurl = new URL(path);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                try {
                    HttpURLConnection conn = (HttpURLConnection) imageurl.openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    Message message = Message.obtain();
                    message.obj = bitmap;
                    handler.sendMessage(message);
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        return null;
    }
}

package com.example.yangwensing.myapplication.main;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by nameless on 2018/4/23.
 */

public class Common {

    //常用字串
    private static final String TAG = "Common";
    public final static String PREF_FILE = "preference";
    public final static String URL = "http://10.0.2.2:8080/SchoolConnectionBook";
    public final static String URLForMingTa = "http://10.0.2.2:8080/iContact";
    public static final int PERMISSION_READ_EXTERNAL_STORAGE = 0;


    //檢查網路
    public static boolean networkConnected(Activity activity) {
        ConnectivityManager conManager =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    //Toast功能
    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(Context context, int messageResId) {
        Toast.makeText(context, messageResId, Toast.LENGTH_SHORT).show();
    }

    //取pref的整數資訊功能
    public static int getDataFromPref(Activity activity, String key, int defValue) {
        SharedPreferences preferences = activity.getSharedPreferences(Common.PREF_FILE, Context.MODE_PRIVATE);
        return preferences.getInt(key, defValue);


    }

    //縮小照片
    public static Bitmap downSize(Bitmap srcBitmap, int newSize) {
        if (newSize <= 1) {
            newSize = 128;
        }
        int srcWidth = srcBitmap.getWidth();
        int srcHeight = srcBitmap.getHeight();
        String text = "source image size = " + srcWidth + "x" + srcHeight;
        Log.d(TAG, "downSize: " + text);
        int longer = Math.max(srcWidth, srcHeight);

        if (longer > newSize) {

            double scale = longer / (double) newSize;
            int dstWidth = (int) (srcWidth / scale);
            int dstHeight = (int) (srcHeight / scale);
            srcBitmap = Bitmap.createScaledBitmap(srcBitmap, dstWidth, dstHeight, false);
            System.gc();
            text = "\nscale = " +
                    scale +
                    "\nscaled image size = " +
                    srcBitmap.getWidth() +
                    "x" +
                    srcBitmap.getHeight();
            Log.d(TAG, "downSize: " + text);


        }

        return srcBitmap;
    }

    //取得permission
    public static void askPermissions(Activity activity, String[] permissions, int requestCode) {
        Set<String> permissionRequest = new HashSet<>();
        for (String permission : permissions) {

            int result = ContextCompat.checkSelfPermission(activity, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                permissionRequest.add(permission);

            }


        }

        if (!permissionRequest.isEmpty())
            ActivityCompat.requestPermissions(activity, permissionRequest.toArray(new String[permissionRequest.size()]), requestCode);

    }


}

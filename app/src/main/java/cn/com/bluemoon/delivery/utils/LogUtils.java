package cn.com.bluemoon.delivery.utils;

import android.util.Log;

import cn.com.bluemoon.delivery.BuildConfig;


public class LogUtils {
	
    private static final String TAG = "test";
    
    
    public static void e(String msg)
    {
        if (!BuildConfig.RELEASE&&msg!=null)
        {
            Log.e(TAG, msg);
        }
    }
    
    public static void e(String tag, String msg)
    {
        if (!BuildConfig.RELEASE&&msg!=null)
        {
            Log.e(tag, msg);
        }
    }
    
    public static void e(String tag, String msg, Throwable throwable)
    {
        if (!BuildConfig.RELEASE&&msg!=null)
        {
            Log.e(tag, msg, throwable);
        }
    }
    
    public static void i(String msg)
    {
        if (!BuildConfig.RELEASE&&msg!=null)
        {
            Log.i(TAG, msg);
        }
    }
    
    public static void i(String tag, String msg)
    {
        if (!BuildConfig.RELEASE&&msg!=null)
        {
            Log.i(tag, msg);
        }
    }
    
    public static void i(String tag, String msg, Throwable throwable)
    {
        if (!BuildConfig.RELEASE&&msg!=null)
        {
            Log.i(tag, msg, throwable);
        }
    }
    
    public static void d(String tag, String msg)
    {
        if (!BuildConfig.RELEASE&&msg!=null)
        {
            Log.d(tag, msg );
        }
    }
    
    public static void d(String msg)
    {
        if (!BuildConfig.RELEASE&&msg!=null)
        {
            Log.d(TAG, msg);
        }
    }
    
    public static void v(String msg)
    {
        if (!BuildConfig.RELEASE&&msg!=null)
            Log.v(TAG, msg);
    }
    
    public static void v(String tag, String msg)
    {
        if (!BuildConfig.RELEASE&&msg!=null)
        {
            Log.v(tag, msg);
        }
    }
    
    public static void w(String tag, String msg)
    {
        if (!BuildConfig.RELEASE&&msg!=null)
        {
            Log.e(tag, msg);
        }
    }
    
    public static void w(Throwable throwable)
    {
        if (!BuildConfig.RELEASE)
        {
            Log.w(TAG, throwable);
        }
    }
    
    public static void w(String tag, Throwable throwable)
    {
        if (!BuildConfig.RELEASE)
        {
            Log.w(tag, throwable);
        }
    }
}

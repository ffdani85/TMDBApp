package com.danifernandez.tmdbapp.utils;

import android.util.Log;

public final class MyLog {

    private final static boolean debug = false;

    public static void v(String tag, String msg) {
        if(debug)   Log.v(tag,msg);
    }

    public static void d(String tag, String msg) {
        if(debug)   Log.d(tag,msg);
    }

    public static void i(String tag, String msg) {
        if(debug)   Log.v(tag,msg);
    }

    public static void w(String tag, String msg) {
        if(debug)   Log.w(tag,msg);
    }

    public static void e(String tag, String msg) {
        if(debug)   Log.e(tag,msg);
    }

    public static void e(String tag, String msg, Throwable tr) {
        if(debug)   Log.e(tag,msg,tr);
    }

    public static void wtf(String tag, String msg) {
        if(debug)   Log.wtf(tag,msg);
    }

}

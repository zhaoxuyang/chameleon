package com.zxy.skin.sdk;

import android.util.Log;

/**
 *
 * @Description: 打印日志使用
 * @author: zhaoxuyang
 * @Date: 2019/1/31
 */
public class Logger {

    private static boolean debug = true;

    public static void setDebug(boolean d) {
        debug = d;
    }

    public static void d(String tag, String msg) {
        if (!debug) {
            return;
        }
        Log.d(tag, msg);
    }

    public static void d(String tag, String msg, Throwable thr) {
        if (!debug) {
            return;
        }
        Log.d(tag, msg, thr);
    }
}

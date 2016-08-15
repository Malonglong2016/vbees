package com.yanjushop.m.utils;

import com.orhanobut.logger.Logger;

import cn.vbees.shop.BuildConfig;

public class L {

    public static void i(String msg, Object... args) {
        if (BuildConfig.LOG_DEBUG)
            Logger.i(msg, args);
    }

    public static void e(String msg, Object... args) {
        if (BuildConfig.LOG_DEBUG)
            Logger.e(msg, args);
    }

    public static void d(String msg, Object... args) {
        if (BuildConfig.LOG_DEBUG)
            Logger.d(msg, args);
    }

    public static void v(String msg, Object... args) {
        if (BuildConfig.LOG_DEBUG)
            Logger.v(msg, args);
    }

    public static void w(String msg, Object... args) {
        if (BuildConfig.LOG_DEBUG)
            Logger.w(msg, args);
    }

    public static void json(String json) {
        if (BuildConfig.LOG_DEBUG)
            Logger.json(json);
    }

    public static void xml(String xml) {
        if (BuildConfig.LOG_DEBUG)
            Logger.xml(xml);
    }

}

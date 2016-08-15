package com.yanjushop.m.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import java.util.Stack;

/**
 * Created by Administrator on 2015/11/20.
 */
public class ActivityManager {

    /**
     * TODO activity栈
     */
    private static Stack<Activity> activityStack;
    /**
     * TODO 单例
     */
    private static ActivityManager instance;

    /**
     * TODO 构造函数私有化
     *
     * @author LiShen
     * @date 2015-6-3 下午3:33:47
     */
    private ActivityManager() {

    }

    /**
     * TODO 单例模式
     *
     * @author LiShen
     * @date 2015-6-3 下午3:49:14
     * @see
     */
    public static ActivityManager getInstance() {

        if (instance == null) {
            synchronized(ActivityManager.class) {
                if(instance == null){
                    instance = new ActivityManager();
                }
            }
        }
        return instance;
    }

    /**
     * TODO 添加一个activity入栈
     *
     * @author LiShen
     * @date 2015-6-3 下午3:51:00
     * @see
     */
    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        activityStack.add(activity);
    }

    /**
     * TODO 获得当前activity
     */
    public Activity currentActivity() {
        Activity activity = null;
        if (!activityStack.empty()) {
            activity = activityStack.lastElement();
        }
        return activity;
    }

    /**
     * TODO 关闭当前activity
     */
    public void finishActivity() {
        Activity activity = activityStack.lastElement();
        finishActivity(activity);
    }

    /**
     * TODO 关闭指定activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    // 退出栈顶Activity

    public void popActivity(Activity activity) {

        if (activity != null) {

            activity.finish();

            activityStack.remove(activity);

            activity = null;

        }

    }

    /**
     * TODO 关闭指定activity.class
     */
    public void finishActivity(Class<?> cls) {
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }

    /**
     * TODO 关闭所有的activity
     */
    public void finishAllActivity() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }

    /**
     * TODO 退出app，关闭所有的activity
     */
    @SuppressWarnings("deprecation")
    public void exitApp(Context context) {
        try {
            finishAllActivity();
            int currentVersion = android.os.Build.VERSION.SDK_INT;
            if (currentVersion > android.os.Build.VERSION_CODES.ECLAIR_MR1) {
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(startMain);
                System.exit(0);
            } else {// android2.1
                android.app.ActivityManager am = (android.app.ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                am.restartPackage(context.getPackageName());
            }
        } catch (Exception e) {
        }
    }
}

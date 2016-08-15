package com.yanjushop.m.utils;

import android.content.Context;
import android.widget.Toast;

import cn.vbees.shop.R;

/**
 * 提示工具类
 * @ClassName ToastUtlis
 * @date 2015-4-9
 */
public class ToastUtlis {

	public static void makeTextShort(Context context,String text){
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}
	public static void makeTextLong(Context context,String text){
		Toast.makeText(context, text, Toast.LENGTH_LONG).show();
	}
	public static void makeText(Context context,String text, int duration){
		Toast.makeText(context, text, duration).show();
	}
	public static void makeTextShort(Context context,int text){
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}
	public static void makeTextLong(Context context,int text){
		Toast.makeText(context, text, Toast.LENGTH_LONG).show();
	}
	public static void makeText(Context context,int text, int duration){
		Toast.makeText(context, text, duration).show();
	}

	public static void requestFailToast(Context context,String msg){
		if (!DeviceInfo.isNetworkConnected(context)) {
			ToastUtlis.makeTextLong(context, R.string.no_connection);
		}else {
			ToastUtlis.makeTextShort(context, msg);
		}
	}
}

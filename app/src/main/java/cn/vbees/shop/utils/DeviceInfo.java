package cn.vbees.shop.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * 获取设备状态
 * @ClassName DeviceInfo
 * @Description TODO
 * @author malong
 * @date 2015-4-3
 */

public class DeviceInfo {


	public String getMacAddress(Context context) {
		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifi.getConnectionInfo();
		return wifiInfo.getMacAddress();
	}
	/**
	 * 国际移动用户识别码
	 * @return
	 */
	public String getIMEI(Context context) {
		return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE))
		                                .getDeviceId();
	}
	/**
	 * 是否有网络连接
	 * @return
	 */
	public static boolean isNetworkConnected(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
		                                .getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		return (networkInfo != null && networkInfo.isConnected());
	}

	public static String getMODEL() {
		return android.os.Build.MODEL;
	}
	
	public static String getManufacturer(){
		return android.os.Build.MANUFACTURER;
	}

	/**
	 * getAPN
	 * @return
	 */
	public String getAPN(Context context) {
		ConnectivityManager connMgr = (ConnectivityManager) context
		                                .getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo == null) {
			return null;
		}
		int apnType = networkInfo.getType();
		if (apnType == ConnectivityManager.TYPE_MOBILE) {
			return (networkInfo.getExtraInfo()).toUpperCase();
		}else if (apnType == ConnectivityManager.TYPE_WIFI) {
			return "WIFI";
		}		
		return null;
	}
	/**
	 * 获取android版本
	 * @return
	 */
	public static String getAndroidVersion(){
		return android.os.Build.VERSION.RELEASE;
	}
	/**
	 * 尺寸参数
	 * @return
	 */
	public String getDisplayParameters(Context context){
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		dm = new DisplayMetrics();  
		wm.getDefaultDisplay().getMetrics(dm);
		int height = dm.heightPixels;
		int width = dm.widthPixels;
		float dpi = dm.density;		
		return "resolution: " + Integer.toString(width) + " * " + Integer.toString(height) + " , dpi: " + Float.toString(dpi);
	}
	/**
	 * 获取屏幕的高(单位：px)
	 * @return
	 */
	public int getDisplayHeight(Context context){
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		dm = new DisplayMetrics();  
		wm.getDefaultDisplay().getMetrics(dm);
		return dm.heightPixels;
	}
	
	/**
	 * 获取屏幕的宽(单位：px)
	 * @return
	 */
	public int getDisplayWidth(Context context){
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		dm = new DisplayMetrics();  
		wm.getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels;
	}
	/**
	 * 获取屏幕的密度
	 * @return
	 */
	public float getDisplayDensity(Context context){
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		dm = new DisplayMetrics();  
		wm.getDefaultDisplay().getMetrics(dm);
		return dm.density;
	}
	
	/**
	 * 是否有内存卡
	 * @return
	 */
	public static boolean hasSDCard() {
		String status = Environment.getExternalStorageState();
		return status.equals(Environment.MEDIA_MOUNTED);
	}
	
	public static String getNativePhoneNumber(Context context){
		String number = "";
		if(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED){
			TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			number = telephonyManager.getLine1Number();
		}
		return number;
	}

}

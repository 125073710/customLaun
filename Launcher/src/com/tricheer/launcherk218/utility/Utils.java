package com.tricheer.launcherk218.utility;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.List;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

/**
 * 类说明：工具类
 * 
 */
public class Utils {

	private static final String TAG = "Utils";

	// -------------------------------------//
	public static String THEME_SETTING = "theme_setting";
	public static String FM_SETTING = "app_fm_state";
	public static String BT_MUSIC_SETTING = "app_bt_state";
	public static String MUSIC_SETTING = "app_music_state";

	/*
	 * 判断网络是否可用
	 */

	public static boolean isNetworkAvailable(Context context) {

		ConnectivityManager manager = (ConnectivityManager) context.getApplicationContext().getSystemService(
				Context.CONNECTIVITY_SERVICE);

		if (manager == null) {
			return false;
		}

		NetworkInfo networkinfo = manager.getActiveNetworkInfo();

		if (networkinfo == null || !networkinfo.isAvailable()) {
			return false;
		}

		return true;
	}
	
	/**
	 * 判断wifi 状态
	 * @param context
	 * @return
	 */
	public static boolean isWiFiActive(Context context) {    
		WifiManager wifimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);//得到wifi管理器对象
		Log.e(TAG,wifimanager.isWifiEnabled()+"");
	return wifimanager.isWifiEnabled();//返回wifi状态
	}
	/**
	 * 获取sd卡和U盘路径
	 * @return
	 */
	public  static List<String> getAllExterSdcardPath() {
		List<String> SdList = new ArrayList<String>();
		try {
			Runtime runtime = Runtime.getRuntime();
			// 运行mount命令，获取命令的输出，得到系统中挂载的所有目录
			Process proc = runtime.exec("mount");
			InputStream is = proc.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			String line;
			BufferedReader br = new BufferedReader(isr);
			while ((line = br.readLine()) != null) {
				Log.d("", line);
				// 将常见的linux分区过滤掉
				// SdList.add(line);
				if (line.contains("secure"))
					continue;
				if (line.contains("asec"))
					continue;
				// 下面这些分区是我们需要的
				if (line.contains("vfat") || line.contains("fuse")
						|| line.contains("fat") || (line.contains("ntfs"))) {
					// 将mount命令获取的列表分割，items[0]为设备名，items[1]为挂载路径
					String items[] = line.split(" ");
					if (items != null && items.length > 1) {
						String path = items[2].toLowerCase(Locale.getDefault());
						// 添加一些判断，确保是sd卡，如果是otg等挂载方式，可以具体分析并添加判断条件
						if (path != null && !SdList.contains(path)
								&& path.contains("media_rw")) {
							SdList.add(items[2]);
						}

					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return SdList;
	}
	
	public static boolean isHasSupperUDisk(Context context){
		Toast.makeText(context, "size="+getAllExterSdcardPath().size(), 0).show();
		return getAllExterSdcardPath().size()>0;
		
	}
	
}

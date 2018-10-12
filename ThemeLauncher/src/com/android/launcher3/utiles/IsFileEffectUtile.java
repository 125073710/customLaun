package com.android.launcher3.utiles;

import java.io.File;

import android.util.Log;

public class IsFileEffectUtile {
	// //////////////2017-4-7
	public static String TAG = "IsFileEffect";

	public static final String SDpath = "/storage/sdcard0/theme/";
	public static final String themepath = "/storage/sdcard0/theme_thumbs";

	/**
	 * 判断指定路径下文件是否存在
	 * 
	 * @param name  路径
	 * @return  Boolean 值
	 */
	public static boolean isFileEffect(String name) {
		File file = new File(name);
		Log.e(TAG, " file.exists()--->" + file.exists() + "");
		Log.e(TAG, "file.isDirectory()--->" + file.isDirectory() + "");
		if (file.exists() && file.isDirectory())
			return true;
		else
			return false;

	}
}

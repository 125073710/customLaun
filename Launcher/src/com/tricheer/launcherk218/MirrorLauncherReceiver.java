package com.tricheer.launcherk218;

import java.util.HashMap;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class MirrorLauncherReceiver extends BroadcastReceiver {

	private static final String TAG = "MirrorLauncherReceiver";
	
	public interface MirrorLauncherReceiverListener {

		/**
		 * Notify 收到开机广播
		 */
		public void onNotifyBootComplete();
		
		/**
		 * Notify 关机广播
		 */
		public void onNotifyShutDown();
		
		/**
		 * Notify 插入SD卡的广播
		 */
		public void onNotifySDCardMounted(int state);

		/**
		 * Notify 模拟测试的广播
		 */
		public void notifyTest(int which, int province);
	}

	protected static Map<String, MirrorLauncherReceiverListener> mMapNotifyObjects = new HashMap<String, MirrorLauncherReceiverListener>();
	
	public static void registerNotifyObject(String notifyKey, MirrorLauncherReceiverListener notifyObj) {
		if (!mMapNotifyObjects.containsKey(notifyKey)) {
			mMapNotifyObjects.remove(notifyKey);
			mMapNotifyObjects.put(notifyKey, notifyObj);
		}
	}

	public static void removeNotifyObject(String notifyKey) {
		if (mMapNotifyObjects.containsKey(notifyKey)) {
			mMapNotifyObjects.remove(notifyKey);
		}
	}

	/**
	 * 收到开机广播
	 */
	public void notifyBootComplete() {
		try {
			for (MirrorLauncherReceiverListener notifyObj : mMapNotifyObjects.values()) {
				notifyObj.onNotifyBootComplete();
			}
		} catch (Exception e) {
			Log.e(TAG, "notifyBootComplete() lyy crash!!!");
			e.printStackTrace();
		}
	}
	
	/**
	 * 收到关机广播
	 */
	public void notifyShutDown() {
		try {
			for (MirrorLauncherReceiverListener notifyObj : mMapNotifyObjects.values()) {
				notifyObj.onNotifyShutDown();
			}
		} catch (Exception e) {
			Log.e(TAG, "notifyShutDown() lyy crash!!!");
			e.printStackTrace();
		}
	}
	
	/**
	 * 插入SD卡的广播
	 */
	public void notifySDCardMounted(int state) {
		try {
			for (MirrorLauncherReceiverListener notifyObj : mMapNotifyObjects.values()) {
				notifyObj.onNotifySDCardMounted(state);
			}
		} catch (Exception e) {
			Log.e(TAG, "notifySDCardMounted() lyy crash!!!");
			e.printStackTrace();
		}
	}

	/**
	 * 模拟位置的广播
	 */
	public void notifyTest(int which, int type) {
		try {
			for (MirrorLauncherReceiverListener notifyObj : mMapNotifyObjects.values()) {
				notifyObj.notifyTest(which, type);
			}
		} catch (Exception e) {
			Log.e(TAG, "notifyTest() lyy crash!!!");
			e.printStackTrace();
		}
	}

	@Override
	public void onReceive(final Context context, Intent intent) {

		String strAction = intent.getAction();

		Log.e(TAG, "Receive Broadcast111: intent.getAction()" + strAction);
		
		Bundle localBundle = new Bundle();
		
		if (strAction.equals("android.intent.action.BOOT_COMPLETED")) {
			notifyBootComplete();
		} else if (strAction.equals("android.intent.action.ACTION_SHUTDOWN")) {
			notifyShutDown();
		} else if (strAction.equals("android.intent.action.MEDIA_MOUNTED")) {
			notifySDCardMounted(1);
		} else if(strAction.equals("android.intent.action.MEDIA_UNMOUNTED")){
			notifySDCardMounted(0);
		}else if (strAction.equals("com.mirrorlauncher.manual")) {
			// adb shell am broadcast -a com.mirrorlauncher.manual --ei which 0 --ei type 4
			// which: 0测试拷贝离线地图      type：省份对应的navi文件夹名
			// which: 1修改地图存储卡位置   type:0----/storage/sdcard1      1----/storage/usbotg
			
			localBundle = intent.getExtras();
			
			if (null == localBundle) {
			    Log.e(TAG, "receive action " + strAction + ", but localBundle is null!!!");
			    return;
			}
			
			int which = localBundle.getInt("which", 0);
			int type = localBundle.getInt("type", -1);
			
			Log.e(TAG, "Receive Broadcast: which="+which+", type=" + type);
			
			notifyTest(which, type);
		}
	}
}

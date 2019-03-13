package com.tricheer.launcherk218;

import java.util.HashMap;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.util.Log;

public class AppInstallReceiver extends BroadcastReceiver{

	static final String TAG = "AppInstallReceiver";
	
	public interface AppChangedReceiverListener {
		// install app
		public void onNotifyAppAdded(String packagename);
		// uninstall app
		public void onNotifyAppRemove(String packagename);
		// change app
		public void onNotifyAppReplace(String packagename);
		
		/*//u disk  attached
		public void onNotifyAppUDiskAttach();
		
		//u disk detach
		public void onNotifyAppUDiskDeattach();*/
	}
	
	protected static Map<String, AppChangedReceiverListener> mMapNotifyObjects = new HashMap<String, AppChangedReceiverListener>();
	
	public static void registerNotifyObject(String notifyKey, AppChangedReceiverListener notifyObj) {
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
	
	// install app
	public void onNotifyAppAdded(String packagename) {
		try {
			for (AppChangedReceiverListener notifyObj : mMapNotifyObjects.values()) {
				notifyObj.onNotifyAppAdded(packagename);
			}
		} catch (Exception e) {
			Log.e(TAG, "onNotifyAppAdded() exception is "+e.toString());
		}
	}
	// uninstall app
	public void onNotifyAppRemove(String packagename) {
		try {
			for (AppChangedReceiverListener notifyObj : mMapNotifyObjects.values()) {
				notifyObj.onNotifyAppRemove(packagename);
			}
		} catch (Exception e) {
			Log.e(TAG, "onNotifyAppRemove() exception is "+e.toString());
		}
	}
	// replace app
	public void onNotifyAppReplace(String packagename) {
		try {
			for (AppChangedReceiverListener notifyObj : mMapNotifyObjects.values()) {
				notifyObj.onNotifyAppReplace(packagename);
			}
		} catch (Exception e) {
			Log.e(TAG, "onNotifyAppReplace() exception is "+e.toString());
		}
	}
/*	//u disk 
	public void onNotifyAppUDiskAttach(){
		try {
			for (AppChangedReceiverListener notifyObj : mMapNotifyObjects.values()) {
				notifyObj.onNotifyAppUDiskAttach();
			}
		} catch (Exception e) {
			Log.e(TAG, "onNotifyAppUDiskAttach() exception is "+e.toString());
		}
	}
	
	public void onNotifyAppUDiskDeattach(){
		try {
			for (AppChangedReceiverListener notifyObj : mMapNotifyObjects.values()) {
				notifyObj.onNotifyAppUDiskDeattach();
			}
		} catch (Exception e) {
			Log.e(TAG, "onNotifyAppUDiskAttach() exception is "+e.toString());
		}
	}*/
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onReceive() action "+intent.getAction());
		if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
			String packageName = intent.getData().getSchemeSpecificPart();
			onNotifyAppAdded(packageName);
			Log.d(TAG, "onReceive() added successful is "+packageName);
			
		}else if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
			String packageName = intent.getData().getSchemeSpecificPart();
			onNotifyAppRemove(packageName);
			Log.d(TAG, "onReceive() uninstall successful is "+packageName);
		}else if (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {
			String packageName = intent.getData().getSchemeSpecificPart();
			onNotifyAppReplace(packageName);
			Log.d(TAG, "onReceive() replace successful is "+packageName);
		}else if (intent.getAction().equals(Intent.ACTION_REBOOT)) {
			
			Log.d(TAG, "onReceive() reboot ");
		}else if (intent.getAction().equals(Intent.ACTION_POWER_DISCONNECTED)) {
			
			Log.d(TAG, "onReceive() power disconnect ");
		}else if (intent.getAction().equals(Intent.ACTION_SHUTDOWN)) {
		}	
	/*		
		}else if(intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)){
			onNotifyAppUDiskAttach();
		}else if(intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_DETACHED)){
			onNotifyAppUDiskDeattach();
		}*/
	}

}

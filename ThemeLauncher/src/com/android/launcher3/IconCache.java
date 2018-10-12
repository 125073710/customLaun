/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.launcher3;

import android.app.ActivityManager;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.android.launcher3.utiles.IsFileEffectUtile;

/**
 * Cache of application icons. Icons can be made from any thread.
 */
public class IconCache {
	@SuppressWarnings("unused")
	private static final String TAG = "IconCache";

	private static final int INITIAL_ICON_CACHE_CAPACITY = 50;//这个哈希映射表的初始容量。
	private static boolean fileEffect;

	private static class CacheEntry {
		public Bitmap icon;
		public String title;
	}

	private final Bitmap mDefaultIcon;
	private final Context mContext;
	private final PackageManager mPackageManager;
	private final HashMap<ComponentName, CacheEntry> mCache = new HashMap<ComponentName, CacheEntry>(INITIAL_ICON_CACHE_CAPACITY);
	private int mIconDpi;

	SharedPreferences mSharedPrefs;

	public IconCache(Context context) {
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

		mContext = context;
		// ybf
		mSharedPrefs = context.getSharedPreferences(LauncherAppState.getSharedPreferencesKey(), Context.MODE_PRIVATE);
		mPackageManager = context.getPackageManager();
		mIconDpi = activityManager.getLauncherLargeIconDensity();
		// need to set mIconDpi before getting default icon
		fileEffect = IsFileEffectUtile.isFileEffect(IsFileEffectUtile.SDpath);
		mDefaultIcon = makeDefaultIcon();

	}

	public Drawable getFullResDefaultActivityIcon() {
		return getFullResIcon(Resources.getSystem(), android.R.drawable.sym_def_app_icon);
	}

	@SuppressWarnings("deprecation")
	public Drawable getFullResIcon(Resources resources, int iconId) {
		Drawable d;
		try {
			d = resources.getDrawableForDensity(iconId, mIconDpi);
			//Bitmap b = addThemeLogo(((BitmapDrawable) d).getBitmap());// ybf
			//d = new FastBitmapDrawable(b);// ybf
		} catch (Resources.NotFoundException e) {
			d = null;
		}

		return (d != null) ? d : getFullResDefaultActivityIcon();
	}

	// ybf  获取资源图片
	public Drawable getFullResIcon(String packageName, int iconId) {
		// add 
		String themeKeyname = mSharedPrefs.getString("theme_key", "default");
		// android.os.SystemProperties.get("persist.sys.theme.key", "default");
		if (!themeKeyname.equals("default")) {
			String iconpath = IsFileEffectUtile.SDpath + themeKeyname + "/" + themeKeyname + "_" + convertToIconResName(packageName) + ".png";
			Log.e(TAG, "convertToIconResName--package name>"+convertToIconResName(packageName) );//包名
			if (new File(iconpath).exists()) {
				return new FastBitmapDrawable(BitmapFactory.decodeFile(iconpath));
			}

		}
		// add by 
		return getFullResDefaultActivityIcon();// ybf

	}

	

	public Drawable getFullResIcon(ResolveInfo info) {
		return getFullResIcon(info.activityInfo);
	}


	/**
	* 获取原始应用icon
	 */
	public Drawable getFullResIcon(ActivityInfo info) {

		Resources resources;
		try {
			resources = mPackageManager.getResourcesForApplication(info.applicationInfo);
		} catch (PackageManager.NameNotFoundException e) {
			resources = null;
		}
		if (resources != null) {
			int iconId = info.getIconResource();
			if (iconId != 0) {
				return getFullResIcon(resources, iconId);
			}
		}
		return getFullResDefaultActivityIcon();
	}
	/**
	 *创建默认图标
	 */
	private Bitmap makeDefaultIcon() {
		Drawable d = getFullResDefaultActivityIcon();
		Bitmap b = Bitmap.createBitmap(Math.max(d.getIntrinsicWidth(), 1), Math.max(d.getIntrinsicHeight(), 1),
				Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(b);
		d.setBounds(0, 0, b.getWidth(), b.getHeight());
		d.draw(c);
		c.setBitmap(null);
		return b;
	}

	/**
	 * Remove any records for the supplied ComponentName.
	 * 删除任何纪录ComponentName提供。
	 */
	public void remove(ComponentName componentName) {
		synchronized (mCache) {
			mCache.remove(componentName);
		}
	}

	/**
	 * Empty out the cache.
	 * 清空缓存
	 */
	public void flush() {
		synchronized (mCache) {
			mCache.clear();
		}
	}

	/**
	 * Empty out the cache that aren't of the correct grid size
	 * 清空缓存不正确的网格尺寸
	 */
	public void flushInvalidIcons(DeviceProfile grid) {
		synchronized (mCache) {
			Iterator<Entry<ComponentName, CacheEntry>> it = mCache.entrySet().iterator();
			while (it.hasNext()) {
				final CacheEntry e = it.next().getValue();
				if (e.icon.getWidth() != grid.iconSizePx || e.icon.getHeight() != grid.iconSizePx) {
					it.remove();
				}
			}
		}
	}

	/**
	 * Fill in "application" with the icon and label for "info."
	 */
	public void getTitleAndIcon(AppInfo application, ResolveInfo info, HashMap<Object, CharSequence> labelCache) {
		synchronized (mCache) {
			CacheEntry entry = cacheLocked(application.componentName, info, labelCache);

			application.title = entry.title;
			application.iconBitmap = entry.icon;
		}
	}

	public Bitmap getIcon(Intent intent) {
		synchronized (mCache) {
			final ResolveInfo resolveInfo = mPackageManager.resolveActivity(intent, 0);
			ComponentName component = intent.getComponent();

			if (resolveInfo == null || component == null) {
				Log.e(TAG, "--> resolveInfo == null || component == null");
				return mDefaultIcon;
			}

			CacheEntry entry = cacheLocked(component, resolveInfo, null);
			Log.e(TAG, "--> entry" + entry.icon);
			return entry.icon;
		}
	}

	public Bitmap getIcon(ComponentName component, ResolveInfo resolveInfo, HashMap<Object, CharSequence> labelCache) {
		synchronized (mCache) {
			if (resolveInfo == null || component == null) {
				return null;
			}

			CacheEntry entry = cacheLocked(component, resolveInfo, labelCache);
			return entry.icon;
		}
	}

	public boolean isDefaultIcon(Bitmap icon) {
		return mDefaultIcon == icon;
	}


	// ybf
	private CacheEntry cacheLocked(ComponentName componentName, ResolveInfo info, HashMap<Object, CharSequence> labelCache) {
	
		CacheEntry entry = mCache.get(componentName);
		
		if (entry == null) {
			entry = new CacheEntry();
		}
		mCache.put(componentName, entry); //应用名字   应用图片  ComponentInfo{com.UCMobile/com.UCMobile.main.UCMobile}   com.android.launcher3.IconCache$CacheEntry@2cc25371
		Log.e(TAG, "componentName ---> " + componentName+"\n"+"entry--->"+entry);
		ComponentName key = LauncherModel.getComponentNameFromResolveInfo(info);
		Log.e(TAG, "ComponentName key ---> " + key);// ComponentInfo{com.UCMobile/com.UCMobile.main.UCMobile}
		if (labelCache != null && labelCache.containsKey(key)) {//判断是否包含指定键名
			entry.title = labelCache.get(key).toString();  
		} else {
			entry.title = info.loadLabel(mPackageManager).toString();
			if (labelCache != null) { 
				labelCache.put(key, entry.title);
			}
		}
		if (entry.title == null) {
			entry.title = info.activityInfo.name;
		}
		String themeKeyname;
		//判断SD卡路径是否存在
		if(fileEffect){
			 themeKeyname = mSharedPrefs.getString("theme_key", "default");
		}else{
			 themeKeyname = "default";
		}
		//String themeKeyname = mSharedPrefs.getString("theme_key", "default");//
		Log.e(TAG, "get key --- " + themeKeyname);
		
		String iconpath;
		if (!themeKeyname.equals("default")) {
			iconpath = IsFileEffectUtile.SDpath + themeKeyname + "/" + themeKeyname + "_" + convertToIconResName(info.activityInfo.name) + ".png";
			Log.e(TAG, "convertToIconResName--class name-->"+convertToIconResName(info.activityInfo.name) );//返回每个应用的activity名字
			Log.e(TAG, "应用图标名字---iconpath-1->" + iconpath);
			if (new File(iconpath).exists()) {//判断替换图片是否存在  back_com_autonavi_auto_remote_fill_usbfillactivity.png
				entry.icon = BitmapFactory.decodeFile(iconpath);
				Log.e(TAG, "entry.icon-2-使用替换的图片->" + entry.icon);
			} else {
				entry.icon = Utilities.createIconBitmap(getFullResIcon(info), mContext);
				Log.e(TAG, "entry.icon-3-使用默认-->" + entry.icon);
			}
		} else {
			Log.e(TAG, "entry.icon-4--使用默认---->" + entry.icon);
			entry.icon = Utilities.createIconBitmap(getFullResIcon(info), mContext);
			
		}
		
		return entry;// ybf

	}
	/**
	 * 将activity名字改分割符. 变成 下划线 _
	 */
	private String convertToIconResName(String input) {
		return input != null && !input.equals("") ? input.replace('.', '_').toLowerCase() : input;
	}
	
	//创建的图片会盖在icon上面,所以在Utilities里面设置背景Icon
	private Bitmap addThemeLogo(Bitmap srcBitmap) {
		String themeKeyname = mSharedPrefs.getString("theme_key", "default");
		// android.os.SystemProperties.get("persist.sys.theme.key", "default");

		Bitmap b2 = BitmapFactory.decodeFile(IsFileEffectUtile.SDpath + themeKeyname + "/" + themeKeyname + "_icon_bg.png");
		if (themeKeyname.equals("default") || b2 == null) {
			Log.e(TAG, "return srcBitmap");
			return srcBitmap;
		}
		Bitmap b3 = Bitmap.createBitmap(srcBitmap.getWidth() + 20, srcBitmap.getHeight() + 20, srcBitmap.getConfig());
		Canvas canvas = new Canvas(b3);
		canvas.drawBitmap(srcBitmap, 0, 0, new Paint(Paint.FILTER_BITMAP_FLAG));
		canvas.drawBitmap(b2, 0, Math.abs(srcBitmap.getHeight() - b2.getHeight()), new Paint(Paint.FILTER_BITMAP_FLAG));
		Log.e(TAG, "return b3");
		return b3;
	}

	public HashMap<ComponentName, Bitmap> getAllIcons() {
		synchronized (mCache) {
			HashMap<ComponentName, Bitmap> set = new HashMap<ComponentName, Bitmap>();
			for (ComponentName cn : mCache.keySet()) {
				final CacheEntry e = mCache.get(cn);
				set.put(cn, e.icon);
			}
			return set;
		}
	}
	
	
	
}

/*
 * Copyright (C) 2013 The Android Open Source Project
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

import java.util.Locale;

import android.app.Application;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.res.Configuration;


public class LauncherApplication extends Application {
	private static   String locale="zh_en";
	public static Context mcontext;
	public static WallpaperManager wallpaperManager;
	
    @Override
    public void onCreate() {
        super.onCreate();
        LauncherAppState.setApplicationContext(this);
        LauncherAppState.getInstance();
   	  
        mcontext=getApplicationContext();
       
        
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        LauncherAppState.getInstance().onTerminate();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    	// TODO Auto-generated method stub
    	super.onConfigurationChanged(newConfig);
    	//Log.e("ybf application",newConfig.locale.toString() );
    	 locale= newConfig.locale.toString();
    	
    }
    public static String getLanguage(){//ybf  监听语言改变
    	return locale;
    }
    public static String getNowlanguage(){//获取系统当前语言
    	String language=Locale.getDefault().getLanguage();
		return language;
    	
    }
    
  
    
    
}
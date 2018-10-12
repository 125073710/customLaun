package com.tricheer.launcherk218;

import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;


public class ApplicationInfo {

	ResolveInfo mresolveInfo;
	String mtitle;
	String mmainClassName;
	Drawable micon;
	int AppType;
	
	
	public int getAppType() {
		return AppType;
	}

	public void setAppType(int appType) {
		AppType = appType;
	}

	ApplicationInfo(){
		mresolveInfo = null;
		mtitle = "";
		mmainClassName = "";
		micon = null;
	}
	
	public void setResolveInfo(ResolveInfo rsInfo){
		mresolveInfo = rsInfo;
	}
	public ResolveInfo getResolveInfo(){
		return mresolveInfo;
	}
	public void setTitle(String strTitle){
		mtitle = strTitle;
	}
	public String getTitle(){
		return mtitle;
	}
	public void setMainClassName(String className){
		mmainClassName = className;
	}
	public String getMainClassName(){
		return mmainClassName;
	}
	public void setIcon(Drawable icon){
		micon = icon;
	}
	public Drawable getIcon(){
		return micon;
	}
}

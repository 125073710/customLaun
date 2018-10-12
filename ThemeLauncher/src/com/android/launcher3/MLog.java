package com.android.launcher3;

import android.util.Log;

public class MLog {
	 /**
     * 是否开启debug
     */
    public static boolean isDebug=true;
     
      public static void e(String st,String msg){
        if(isDebug){
            Log.e(st,msg+"");
        }
    }
}

package com.android.launcher3.utiles;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.util.Log;

/**
* @ClassName: FUtilities
* @Description: 处理aux  语音  DVD 隐藏与显示的读取系统配置文件工具类
* @author yangbofeng
* @date 2017-3-8
* @see FUtilities
*/
public class FUtilities {
    
    public static final String TAG = "FUtilities";
    public static final String dataPath = "/data";
    public static final String dataPath1 = "/persist";
    public static final String xmlName = "factory_setting";
    public static final String tempXmlName = "factory_setting_temp";
    
    public static String persistString = "/persist/factory_setting.xml";
    public static String userString = "/data/factory_setting.xml";
    public static String tempString = "/data/factory_setting_temp.xml";
    
    private static String syncString = "sync";
    
    public static String getStringValueSaved(Context mContext, String name, String key, String def) {
        try {  
            Field field;  
            // 获取ContextWrapper对象中的mBase变量。该变量保存了ContextImpl对象  
            field = ContextWrapper.class.getDeclaredField("mBase");  
            field.setAccessible(true);  
            // 获取mBase变量  
            Object obj = field.get(mContext);  
            // 获取ContextImpl。mPreferencesDir变量，该变量保存了数据文件的保存路径  
            field = obj.getClass().getDeclaredField("mPreferencesDir");  
            field.setAccessible(true);  
            // 创建自定义路径  
            File file = new File(dataPath1);  
            // 修改mPreferencesDir变量的值  
            field.set(obj, file);  
		    SharedPreferences mSharedPreferences = mContext.getSharedPreferences(name,
                mContext.MODE_WORLD_READABLE);
            return mSharedPreferences.getString(key, def);
        } catch (NoSuchFieldException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
            return null;
        } catch (IllegalArgumentException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
            return null;
        } catch (IllegalAccessException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
            return null;
        }
    }
    
    public static String getStringValueSaved(Context mContext, String key, String def) {
    	return getStringValueSaved(mContext, xmlName, key, def);
    }
    
    public static void saveStringValue(Context mContext, String key, String value) {
        try {  
            Field field;  
            // 获取ContextWrapper对象中的mBase变量。该变量保存了ContextImpl对象  
            field = ContextWrapper.class.getDeclaredField("mBase");  
            field.setAccessible(true);  
            // 获取mBase变量  
            Object obj = field.get(mContext);  
            // 获取ContextImpl。mPreferencesDir变量，该变量保存了数据文件的保存路径  
            field = obj.getClass().getDeclaredField("mPreferencesDir");  
            field.setAccessible(true);  
            // 创建自定义路径  
            File file = new File(dataPath);  
            // 修改mPreferencesDir变量的值  
            field.set(obj, file);  
            //   SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
	        //	mSharedPreferences.setSharedPreferencesMode(sharedPreferencesMode)
		    SharedPreferences mSharedPreferences = mContext.getSharedPreferences(tempXmlName,
                mContext.MODE_WORLD_WRITEABLE);
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString(key, value);
            editor.commit();
        } catch (NoSuchFieldException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        } catch (IllegalArgumentException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        } catch (IllegalAccessException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }          
    }
    
    public static void exec(final String command) {
		new Thread(){
			public void run() {
				try {
					Log.d(TAG, command);
					
					Process process;
					String[] commandStrings = command.split(",");
					process = Runtime.getRuntime().exec(commandStrings);
					process.waitFor();
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}.start();
	}
    
    public static void copyFile(String sourceString, String targetString) {
		File sourceFile = new File(sourceString);
		File targetFile = new File(targetString);
		
		if (!sourceFile.exists() || sourceFile.isDirectory()) {
			return ;
		}
		
		try {
			copyFile(sourceFile, targetFile);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		exec(syncString);
	}
    
    public static void copyFile(File sourceFile,File targetFile) throws IOException{  
    	// 新建文件输入流并对它进行缓冲   
    	FileInputStream input = new FileInputStream(sourceFile);  
    	BufferedInputStream inBuff=new BufferedInputStream(input);  
    		  
    	// 新建文件输出流并对它进行缓冲   
    	FileOutputStream output = new FileOutputStream(targetFile);  
    	BufferedOutputStream outBuff=new BufferedOutputStream(output);  
    		          
    	// 缓冲数组   
    	byte[] b = new byte[1024 * 5];  
    	int len;  
    	while ((len =inBuff.read(b)) != -1) {  
    		outBuff.write(b, 0, len);  
    	}  
    	// 刷新此缓冲的输出流   
    	outBuff.flush();  
    		          
    	//关闭流   
    	inBuff.close();  
    	outBuff.close();  
    	output.close();  
    	input.close();  
    }  
}

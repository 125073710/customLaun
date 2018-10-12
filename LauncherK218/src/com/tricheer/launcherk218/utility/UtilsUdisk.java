package com.tricheer.launcherk218.utility;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import android.hardware.usb.UsbManager;
public class UtilsUdisk {
	
	//UsbManager.ACTION_USB_DEVICE_ATTACHED;
	//UsbManager.ACTION_USB_DEVICE_DETACHED;
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

package com.android.launcher3.utiles;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;

public class CopyAssetsToSDUtile {

	

	/**
	 * 读取assets下文件拷贝到指定路径
	 * @param assetDir asset下文件
	 * @param dir  拷贝到目标路径
	 * 
	 */
	@SuppressWarnings("unused")
	public static void CopyAssets(Context mContext, String assetDir, String dir) {

		String[] files;
		try { // 获得Assets一共有几多文件
			files = mContext.getResources().getAssets().list(assetDir);
		} catch (IOException e1) {
			return;
		}
		File mWorkingPath = new File(dir);
		// 如果文件路径不存在
		// if this directory does not exists, make one.
		if (!mWorkingPath.exists()) {
			// 创建文件夹
			if (!mWorkingPath.mkdirs()) {
				// 文件夹创建不成功时调用
			}
		}

		for (int i = 0; i < files.length; i++) {
			try {// 获得每个文件的名字
				String fileName = files[i];
				// we make sure file name not contains '.' to be a folder.// 根据路径判断是文件夹还是文件
				if (!fileName.contains(".")) {
					if (0 == assetDir.length()) {
						CopyAssets(mContext, fileName, dir + "/" + fileName + "/");
					} else {
						CopyAssets(mContext, assetDir + "/" + fileName, dir + "/" + fileName + "/");
					}
					continue;
				}
				File outFile = new File(mWorkingPath, fileName);
				if (outFile.exists())
					outFile.delete();
				InputStream in = null;
				if (0 != assetDir.length())
					in = mContext.getAssets().open(assetDir + "/" + fileName);
				else
					in = mContext.getAssets().open(fileName);
				OutputStream out = new FileOutputStream(outFile);

				// Transfer bytes from in to out
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}

				in.close();
				out.close();

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	
	
	/**
	 * 判断文件夹是否存在
	 */
	public static boolean isExist(String path) {
		File file = new File(path);
		// 判断文件夹是否存在,如果不存在则创建文件夹
		if (!file.exists()) {
			return false;
		} else {
			return true;
		}

	}

}

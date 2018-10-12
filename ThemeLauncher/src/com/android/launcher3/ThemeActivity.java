package com.android.launcher3;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.android.launcher3.themeAdapter.ImageAdapter;
import com.android.launcher3.utiles.IsFileEffectUtile;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

/**
 * 
 * @ClassName: ThemeActivity
 * @Description: TODO
 * @author yangbofeng
 * @date 2017-3-20
 * @see ThemeActivity
 */
public class ThemeActivity extends Activity {
	private String TAG = "ThemeActivity";
	private SharedPreferences sp;
	private List<String> mThemesNames = new ArrayList<String>();
	// private List<Bitmap> mThemesBitmaps = null;
	private Map<String, Bitmap> mMapImgs = new HashMap<String, Bitmap>();
	private ImageAdapter mImageAdapter;
	private static boolean fileEffect;
	GridView gridview;
	int mCounter;
	ArrayList<String> namepkg = new ArrayList<String>();;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.theme_picker);
		sp = getSharedPreferences(LauncherAppState.getSharedPreferencesKey(), Context.MODE_PRIVATE);
		AsyncLoadedImage mAsyncLoadedImage = new AsyncLoadedImage();
		mAsyncLoadedImage.execute();

		mImageAdapter = new ImageAdapter(this, mThemesNames, mMapImgs);
		gridview = (GridView) findViewById(R.id.gridview);
		gridview.setAdapter(mImageAdapter);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		 fileEffect = IsFileEffectUtile.isFileEffect(IsFileEffectUtile.SDpath);
		
		gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

			//	Log.e("SDpath", "mThemesNames.get(position)-- " + namepkg.get(position) + "");
			//	Log.e("SDpath", "-position- " + position + "");
			//	boolean fileEffect = isFileEffect(SDpath + namepkg.get(position));
			//	Log.e("SDpath", "fileEffect---->" + fileEffect);
			//	Log.e("SDpath", "file---->" + new File(SDpath + namepkg.get(position)));
				Log.e("SDpath", "namepkg.size() -- -----------------" + namepkg.size());
				WallpaperManager wallpaperManager = WallpaperManager.getInstance(ThemeActivity.this);
			//	Log.e(TAG, "position-----" + position +"\n"+ "namepkg.size()" + namepkg.size());
				if(fileEffect){
				if (position == namepkg.size()) {

					sp.edit().putString("theme_key", "default").commit();
					try {
						// getIdentifier  第一个参数为ID名，第二个为资源属性是ID或者是Drawable，第三个为包名。 
						wallpaperManager.setResource(R.drawable.bj);
						Log.e(TAG, "1");
					} catch (Exception e) {
						e.printStackTrace();
					}
					Log.e(TAG, "2");
					// 杀掉当前进程
					android.os.Process.killProcess(android.os.Process.myPid());
					Log.e(TAG, "3");
				} else if (IsFileEffectUtile.isFileEffect(IsFileEffectUtile.SDpath + namepkg.get(position))) {

					sp.edit().putString("theme_key", namepkg.get(position)).commit();

					try {
						Log.e(TAG, "====>"+IsFileEffectUtile.SDpath + namepkg.get(position) + "/"+ namepkg.get(position) + "_wallpaper.jpg");
						wallpaperManager.setBitmap(BitmapFactory.decodeFile(IsFileEffectUtile.SDpath + namepkg.get(position) + "/"
								+ namepkg.get(position) + "_wallpaper.jpg"));

						Log.e(TAG, "4");
					} catch (Exception e) {
						e.printStackTrace();
					}
					Log.e(TAG, "5");
					// Toast.makeText(getApplicationContext(), "设置成功", 0).show();
					android.os.Process.killProcess(android.os.Process.myPid());

				} else {
					// down load this theme
				}
			}else{//if else
				if (position == namepkg.size()) {

					sp.edit().putString("theme_key", "default").commit();
					try {
						
						wallpaperManager.setResource(R.drawable.bj);
						Log.e(TAG, "1");
					} catch (Exception e) {
						e.printStackTrace();
					}
					Log.e(TAG, "2");
					// 杀掉当前进程
					android.os.Process.killProcess(android.os.Process.myPid());
					Log.e(TAG, "3");
				}
			}

			}

		});
		// } //if end

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	/*Handler mAsyncHandler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				mImageAdapter.refreshDatas();
			}
		}
	};*/

	class AsyncLoadedImage extends AsyncTask<Object, Object, Object> {

		@Override
		protected Object doInBackground(Object... params) {//方法内部执行后台任务,不可在此方法内修改UI
			Log.i("Async", "fileArray new success");
			if ("".equals(IsFileEffectUtile.themepath)) {
				return null;
			} else {
				loadLogos(IsFileEffectUtile.themepath);
			}

			return null;
		}

		@Override
		public void onProgressUpdate(Object... value) {
			Log.e("Async", "onProgressUpdate addImage");
			mImageAdapter.refreshDatas();
		}

		@Override
		protected void onPostExecute(Object result) {
		}
		
		private void loadLogos(String root) {
			File rootDir = new File(root);
			if (rootDir.exists()) {
				File[] files = rootDir.listFiles();

				int flagIdx = 0;
				for (int i = 0; i < files.length; i++) {
					if (files[i].isFile()) {
						String path = files[i].getPath();
						Bitmap cover = getBitmap(path);
						if (cover != null) {
							String name_key = path;
							mMapImgs.put(path, getBitmap(path));
							mThemesNames.add(path);
							String[] split = name_key.split("/");
							String string = split[split.length - 1];
							Log.e(TAG, "----------------" + string);
							String[] name = string.split("\\.");
							String packagename = name[0];
							Log.e(TAG, "-------packagename--------" + name[0]);
							namepkg.add(packagename);//保存包名
						}
						flagIdx++;
						// Refresh
						if (mThemesNames.size() % 4 == 0 || flagIdx == (files.length)) {
							publishProgress(1);
							/*Message mMessage = mAsyncHandler.obtainMessage();
							mMessage.what = 1;
							mAsyncHandler.sendMessage(mMessage);*/
						}
					}
				}

			}
		}
	}
	
	private Bitmap getBitmap(String path) {
		Bitmap cover = null;
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			Bitmap bitmap = BitmapFactory.decodeFile(path, options);
			cover = ThumbnailUtils.extractThumbnail(bitmap, 450, 300);
			bitmap.recycle();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cover;

	}

	/*private String trimExtension(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int i = filename.lastIndexOf('.');
			if ((i > -1) && (i < (filename.length()))) {
				return filename.substring(0, i);
			}
		}
		return null;
	}*/

}

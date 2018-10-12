package com.tricheer.launcherk218;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.bluetooth.BluetoothA2dp;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.usb.UsbManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tricheer.launcher218.R;
import com.tricheer.launcherk218.AppInstallReceiver.AppChangedReceiverListener;
import com.tricheer.launcherk218.DraggableGridViewPager.OnPageChangeListener;
import com.tricheer.launcherk218.DraggableGridViewPager.OnRearrangeListener;
import com.tricheer.launcherk218.DraggableGridViewPager.OnTouchChangeListener;
import com.tricheer.launcherk218.MirrorLauncherReceiver.MirrorLauncherReceiverListener;
import com.tricheer.launcherk218.utility.DatabaseHelper;
import com.tricheer.launcherk218.utility.LctConst;
import com.tricheer.launcherk218.utility.Utils;
import com.tricheer.launcherk218.view.MyDialog;


//com.tricheer.launcherk218.ActivityMain
public class ActivityMain extends Activity implements MirrorLauncherReceiverListener, AppChangedReceiverListener {

	private static final String TAG = "ActivityMain";
	private static final int APP_PAGE_SIZE = AppAdapter.APP_PAGE_SIZE; // 每页显示个数
	public static int APP_ADD_TAG = 0;

	public static int Mhfp_a2dpState;

	public static int PageCount = 0;
	private static ScrollLayout mScrollLayout;
	private List<Map<String, Object>> mAllApps;
	private List<Map<String, Object>> mAllApps2;
	public static Context mContext;
	static ImageView[] mImageView = new ImageView[20];
	private LinearLayout mLinearLayout;
	private FrameLayout framlay_waring;
	private LinearLayout linearlay_main;
	private Button btok;
	private AppAdapter mappAdapter;
	DatabaseHelper mdbHelper;
	private ArrayList<ApplicationInfo> mApps;
	private static DraggableGridViewPager mdragScrollPage;

	public static String selectedPosin = "";
	private int SYSTEM_UI_FLAG_HIDE_NAVIGATION = 2;

	public static Resources resres = null;

	private GestureDetector gestureDetector;
	public static int MAX_COUNT = 6;
	public boolean isopen = false; // 数据库事务
	private MyDialog dialog;
	private int time = 11;

	SettingsThemeChangeContentObserver mSettingsThemeChangeContentObserver = null;
	SettingsFMChangeContentObserver mSettingsFMChangeContentObserver = null;
	SettingsBTMusicChangeContentObserver mSettingsBTMusicChangeContentObserver = null;
	SettingsMusicChangeContentObserver mSettingsMusicChangeContentObserver = null;
	public static int Theme = 0; // 主题
	private int positions =-1;
	int codekey = -1;  //key left 旋钮
	int positionPage = 0; //页面
	private Handler mHandler = new Handler();

	private String MIRROR_LAUNCHER_RECEIVER_TAG_MAINACTIVITY = "ActivityMain";

	// 捕获KEYCODE_HOME广播
	private final BroadcastReceiver mReceiverKeyCodeHome = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			codekey = -1;
			mappAdapter.setSelectPosition(codekey);
			
			Log.e(TAG, "mReceiverKeyCodeHome received KEYCODE_HOME broadcast:");

			String action = intent.getAction();

			Bundle localBundle = new Bundle();
			localBundle = intent.getExtras();
			if (localBundle == null) {
				Log.e(TAG, "mReceiverKeyCodeHome received KEYCODE_HOME broadcast:" + intent.toString()
						+ ", getExtras() is null!!!");
				return;
			}

			Log.e(TAG, "mScrollLayout.getCurScreen()=" + mScrollLayout.getCurScreen());
			Log.e(TAG, localBundle.getBoolean("isLauncher", false) + "");
			if (localBundle.getBoolean("isLauncher", false)) {
				mdragScrollPage.setCurrentItem(0);

			}
		}
	};

	// 判断当前界面显示的是哪个Activity
	public String getRunningActivityName(Context mContext) {
		ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
		String runningActivity = activityManager.getRunningTasks(1).get(0).topActivity.getClassName();
		return runningActivity;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			Resources res = getResources();
			Configuration newConfig = res.getConfiguration();
			Log.e(TAG, "onCreate() this=" + this + "\nnewConfig.densityDpi=" + newConfig.densityDpi
					+ "\nnewConfig.fontScale=" + newConfig.fontScale + "\nnewConfig.hardKeyboardHidden="
					+ newConfig.hardKeyboardHidden + "\nnewConfig.keyboard=" + newConfig.keyboard
					+ "\nnewConfig.keyboardHidden=" + newConfig.keyboardHidden + "\nnewConfig.locale="
					+ newConfig.locale + "\nnewConfig.getLayoutDirection()=" + newConfig.getLayoutDirection()
					+ "\nnewConfig.describeContents()=" + newConfig.describeContents() + "\nnewConfig.mcc="
					+ newConfig.mcc + "\nnewConfig.mnc=" + newConfig.mnc + "\nnewConfig.navigation="
					+ newConfig.navigation + "\nnewConfig.navigationHidden=" + newConfig.navigationHidden
					+ "\nnewConfig.orientation=" + newConfig.orientation + "\nnewConfig.screenHeightDp="
					+ newConfig.screenHeightDp + "\nnewConfig.screenLayout=" + newConfig.screenLayout
					+ "\nnewConfig.screenWidthDp=" + newConfig.screenWidthDp + "\nnewConfig.smallestScreenWidthDp="
					+ newConfig.smallestScreenWidthDp + "\nnewConfig.touchscreen=" + newConfig.touchscreen
					+ "\nnewConfig.uiMode=" + newConfig.uiMode);
		} catch (Exception e) {
			Log.e(TAG, "onCreate() enter this=" + this + ", e.getMessage()=" + e.getMessage());
		}
		mdbHelper = new DatabaseHelper(this);
		mdbHelper.openDatabase();
		mContext = getApplicationContext();
		// mAllApps = new ArrayList<ResolveInfo>();
		mAllApps = new ArrayList<Map<String, Object>>();
		mAllApps2 = new ArrayList<Map<String, Object>>();
		mApps = new ArrayList<ApplicationInfo>();
		setContentView(R.layout.scrolllayoutactivity);
		initView();
		//initLogWaring();
		init_Apps();
		initUI();
		myRegisterReceiver();
		// 订阅消息
		MirrorLauncherReceiver.registerNotifyObject(MIRROR_LAUNCHER_RECEIVER_TAG_MAINACTIVITY, this);
		AppInstallReceiver.registerNotifyObject(MIRROR_LAUNCHER_RECEIVER_TAG_MAINACTIVITY, this);
		// 监听主题切换
		mSettingsThemeChangeContentObserver = new SettingsThemeChangeContentObserver();
		getContentResolver().registerContentObserver(Settings.System.getUriFor(Utils.THEME_SETTING), true,
				mSettingsThemeChangeContentObserver);

		// 监听fm 播放动画
		mSettingsFMChangeContentObserver = new SettingsFMChangeContentObserver();
		getContentResolver().registerContentObserver(Settings.System.getUriFor(Utils.FM_SETTING), true,
				mSettingsFMChangeContentObserver);
		// 蓝牙音乐
		mSettingsBTMusicChangeContentObserver = new SettingsBTMusicChangeContentObserver();
		getContentResolver().registerContentObserver(Settings.System.getUriFor(Utils.BT_MUSIC_SETTING), true,
				mSettingsBTMusicChangeContentObserver);
		// music
		mSettingsMusicChangeContentObserver = new SettingsMusicChangeContentObserver();
		getContentResolver().registerContentObserver(Settings.System.getUriFor(Utils.MUSIC_SETTING), true,
				mSettingsMusicChangeContentObserver);

		// 判断三个应用是否在运行
		boolean isradioRun = isRun(mContext, "com.tricheer.radio");
		if (isradioRun) {
			mappAdapter.FMstartAnimation();
		} else {
			mappAdapter.FMstopAnimation();
		}
		boolean isMusicRun = isRun(mContext, "com.tricheer.player");
		if (isMusicRun) {
			mappAdapter.MusicstartAnimation();
		} else {
			mappAdapter.MusicstopAnimation();
		}
		boolean isbtMusic = isRun(mContext, "com.tricheer.bt");
		if (isbtMusic) {
			mappAdapter.BTMusicstartAnimation();
		} else {
			mappAdapter.BTMusicstartAnimation();
		}
	}

	private void initView() {
		mScrollLayout = (ScrollLayout) this.findViewById(R.id.ScrollLayout);
		mLinearLayout = (LinearLayout) this.findViewById(R.id.page_num);
		mdragScrollPage = (DraggableGridViewPager) this.findViewById(R.id.dragscrollpage);
		framlay_waring = (FrameLayout) findViewById(R.id.framlay_waring);
		framlay_waring.setVisibility(View.GONE);
		linearlay_main = (LinearLayout) findViewById(R.id.linearlay_main);
		btok = (Button) findViewById(R.id.bt_ok);
		btok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setSystemUIVisible(true);
				linearlay_main.setVisibility(View.VISIBLE);
				framlay_waring.setVisibility(View.GONE);
				
			}
		});
	}

	/**
	 * 判断应用是否在运行
	 * 
	 * @param context
	 * @return
	 */
	public boolean isRun(Context context, String packgname) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> list = am.getRunningTasks(100);
		boolean isAppRunning = false;
		String MY_PKG_NAME = packgname;
		// 100表示取的最大的任务数，info.topActivity表示当前正在运行的Activity，info.baseActivity表系统后台有此进程在运行
		for (RunningTaskInfo info : list) {
			if (info.topActivity.getPackageName().equals(MY_PKG_NAME)
					|| info.baseActivity.getPackageName().equals(MY_PKG_NAME)) {
				isAppRunning = true;
				Log.i("ActivityService isRun()", info.topActivity.getPackageName()
						+ " info.baseActivity.getPackageName()=" + info.baseActivity.getPackageName());
				break;
			}
		}
		Log.i("ActivityService isRun()", "com.ad 程序  ...isAppRunning......" + isAppRunning);
		return isAppRunning;
	}

	/**
	 * 弹出警告页面
	 */
	public void initLogWaring() {
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); // 隐藏状态栏
		int waring = Settings.System.getInt(this.getContentResolver(), "open_waring", 0);
		if (waring == 1) {
			linearlay_main.setVisibility(View.GONE);
			framlay_waring.setVisibility(View.VISIBLE);
		} else {
			linearlay_main.setVisibility(View.GONE);
			framlay_waring.setVisibility(View.VISIBLE);
			setSystemUIVisible(false);
			
		}
		//
	}
	//ybf2018/9/4
	WindowManager.LayoutParams wmParams;
	WindowManager mWindowManager;
	FrameLayout mSimpleNavi;
	private void createFloatView(String tvname){
		wmParams = new WindowManager.LayoutParams();
		mWindowManager = (WindowManager)getApplication().getSystemService(getApplication().WINDOW_SERVICE);
		wmParams.type = 2021; //LayoutParams.TYPE_PHONE;
        wmParams.format = PixelFormat.RGBA_8888; 
        wmParams.flags = 
        		WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
        		WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
        		WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        
        wmParams.gravity = Gravity.LEFT | Gravity.TOP; 
        
        wmParams.x = 0;
        wmParams.y = 0;

        wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        wmParams.height = WindowManager.LayoutParams.MATCH_PARENT;

        
        LayoutInflater inflater = LayoutInflater.from(getApplication());

        mSimpleNavi = (FrameLayout) inflater.inflate(R.layout.warming, null);
       ImageView am =  (ImageView) mSimpleNavi.findViewById(R.id.img_am);
       AnimationDrawable ams =  (AnimationDrawable) ((ImageView) am).getDrawable();
      TextView tv =(TextView) mSimpleNavi.findViewById(R.id.message);
      tv.setText(tvname);
       
        mWindowManager.addView(mSimpleNavi, wmParams);
        
        am.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setAction("");
				sendBroadcast(intent);
			}
		});

	}
	
	 private Runnable myRunnale = new Runnable() {
		    @Override
		    public void run() {
		      time--;
		      if(time>0){
		        mHandler.postDelayed(myRunnale,1000);
		        Log.e("TAG","剩余"+time+"s");
		      }else{
		    	  runOnUiThread(new Runnable() {
					public void run() {
						mWindowManager.removeView(mSimpleNavi);
					}
				});
		    	
		      }
		    }
		  };

	/**
	 * 隐藏导航栏 false 不可见 true 可见
	 * 
	 * @param show
	 */
	private void setSystemUIVisible(boolean show) {
		if (show) {
			int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
			uiFlags |= 0x00001000;
			getWindow().getDecorView().setSystemUiVisibility(uiFlags);
		} else {
			int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
					| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
					| View.SYSTEM_UI_FLAG_FULLSCREEN;
			uiFlags |= 0x00001000;
			getWindow().getDecorView().setSystemUiVisibility(uiFlags);
		}
	}

	/*
	 * 从数据库获取list 数据
	 */
	private ArrayList<ApplicationInfo> getDatabaseApps() {
		Log.e(TAG, "getDatabaseApps");
		ArrayList<ApplicationInfo> apps = new ArrayList<ApplicationInfo>();

		PackageManager packageManager = getPackageManager();
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

		List<ResolveInfo> allApps = packageManager.queryIntentActivities(mainIntent, 0);

		Object[][] whiteAppItemArray = LctConst.APP_ITEM_ARRAY;
		int appcount = allApps.size(); // 查询到所有launcher 属性的应用

		if (mdbHelper != null) {
			ArrayList<String> appNames = mdbHelper.getData();
			if (appNames.size() > 0) {
				for (String appName : appNames) {
					// Log.d(TAG, "getDatabaseApps() app name is "+appName);
					for (int i = 0; i < appcount; i++) {
						ResolveInfo resinfo = allApps.get(i);
						if (appName.equalsIgnoreCase(resinfo.activityInfo.name)) {
							ApplicationInfo appinfo = new ApplicationInfo();
							appinfo.setResolveInfo(resinfo);
							appinfo.setMainClassName(resinfo.activityInfo.name);

							boolean bfind = false;
							for (Object[] appitem : whiteAppItemArray) {
								String mainActivity = (String) appitem[0];
								if (mainActivity.equalsIgnoreCase(appName)) {
									if (mainActivity.equals("com.chartcross.gpstestplus.GPSTestPlus")) {
										File file = new File("/data/ftm_flag");
										if (!file.exists()) {
											Log.e(TAG, "[getDatabaseApps]no have ftm_falg 跳过");
											continue;
										}

									}
									bfind = true;

									// name
									Integer appNameID = (Integer) appitem[1];
									if (appNameID < 0) {
										appinfo.setTitle(resinfo.loadLabel(getPackageManager()).toString());
									} else {
										appinfo.setTitle(getString(appNameID));
									}

									// icon
									Integer appImageID = (Integer) appitem[2];
									if (appImageID < 0) {
										appinfo.setIcon(resinfo.loadIcon(getPackageManager()));
									} else {
										appinfo.setIcon(getDrawable(appImageID));
									}

									break;
								}
							}

							if (!bfind) {
								appinfo.setTitle(resinfo.loadLabel(getPackageManager()).toString());
								appinfo.setIcon(resinfo.loadIcon(getPackageManager()));
							}
							apps.add(appinfo);
						}// end if
					}// for appcount
				}// for appNames
			}// size > 0
		}

		return apps;
	}

	/*
	 * 
	 * 获取桌面显示的定制应用
	 */
	private ArrayList<ApplicationInfo> getShowApps() {
		Log.e(TAG, "getShowApps");
		ArrayList<ApplicationInfo> apps = new ArrayList<ApplicationInfo>();

		PackageManager packageManager = getPackageManager();
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

		List<ResolveInfo> allApps = packageManager.queryIntentActivities(mainIntent, 0);

		Object[][] whiteAppItemArray = LctConst.APP_ITEM_ARRAY;

		int appcount = allApps.size();

		// add white apps
		if (whiteAppItemArray != null && whiteAppItemArray.length > 0) {
			for (Object[] appitem : whiteAppItemArray) {
				String mainActivity = (String) appitem[0];
				for (int i = 0; i < appcount; i++) {
					ResolveInfo resinfo = allApps.get(i);
					// Log.e(TAG, "activity name = "+resinfo.activityInfo.name);
					if (mainActivity.equalsIgnoreCase(resinfo.activityInfo.name)) {
						ApplicationInfo appinfo = new ApplicationInfo();
						appinfo.setResolveInfo(resinfo);
						appinfo.setMainClassName(resinfo.activityInfo.name);
						// name
						Integer appNameID = (Integer) appitem[1];
						if (appNameID < 0) {
							appinfo.setTitle(resinfo.loadLabel(getPackageManager()).toString());
						} else {
							appinfo.setTitle(getString(appNameID));
						}

						// icon
						Integer appImageID = (Integer) appitem[2];
						if (appImageID < 0) {
							appinfo.setIcon(resinfo.loadIcon(getPackageManager()));
						} else {
							appinfo.setIcon(getDrawable(appImageID));
						}
						// Type
						Integer appType = (Integer) appitem[3];
						Log.e(TAG, "appType=" + appType);
						if (appType < 0) {
							// appinfo.setAppType(-1);
						} else {
							// appinfo.setAppType(appType);
						}

						if (resinfo.activityInfo.name.equals("com.chartcross.gpstestplus.GPSTestPlus")) {
							File file = new File("/data/ftm_flag");
							if (!file.exists()) {
								Log.e(TAG, "[getShowApps]no have ftm_falg 跳过");
								continue;
							}

						}
						apps.add(appinfo);
						Log.d(TAG, "getShowApps() the show app is " + resinfo.activityInfo.name);

					}
				}
			}
		}

		// add other apps
		String[] blackApps = getResources().getStringArray(R.array.black_applist);
		if (blackApps != null && blackApps.length > 0) {
			for (int i = 0; i < appcount; i++) {
				ResolveInfo resinfo = allApps.get(i);
				boolean bWhiteApp = false;
				// Log.d(TAG,
				// "getShowApps() the main class is "+resinfo.activityInfo.name);

				// is in the white apps list
				for (Object[] appitem : whiteAppItemArray) {
					String mainActivity = (String) appitem[0];
					if (mainActivity.equalsIgnoreCase(resinfo.activityInfo.name)) {
						bWhiteApp = true;
						break;
					}
				}

				// is in the black apps list
				boolean bblackApp = false;
				if (!bWhiteApp) {
					for (String item : blackApps) {
						if (item.equalsIgnoreCase(resinfo.activityInfo.name)) {
							bblackApp = true;
							// Log.d(TAG,
							// "getShowApps()  black apps list is "+resinfo.activityInfo.name);
							break;
						}
					}
				}

				// it's need add the show apps list
				// 不在黑名单也不在白名单里的不让显示，直接不用添加到集合里即可
				if ((!bblackApp) && (!bWhiteApp)) {
					ApplicationInfo appinfo = new ApplicationInfo();
					appinfo.setResolveInfo(resinfo);
					appinfo.setMainClassName(resinfo.activityInfo.name);
					appinfo.setTitle(resinfo.loadLabel(getPackageManager()).toString());
					appinfo.setIcon(resinfo.loadIcon(getPackageManager()));
					// 注释掉后不是白名单里的不是黑名单里的，都不让显示
					// apps.add(appinfo);

				}
			}// end for
		}// end if

		return apps;
	}

	/*
	 * 初始化 加载桌面应用
	 */
	public void init_Apps() {

		mApps.clear();

		SharedPreferences setting = getSharedPreferences("isfrist", 0);
		Boolean user_first = setting.getBoolean("FIRST", true);
		/*
		 * if (user_first) {// 判断第一次 setting.edit().putBoolean("FIRST",
		 * false).commit(); Theme = "yes"; Log.e(TAG, "first"); } else {
		 * Log.e(TAG, " not is first"); }
		 */

		ArrayList<ApplicationInfo> databaseApps = getDatabaseApps(); // 第一次数据库为空，不走
		ArrayList<ApplicationInfo> showApps = getShowApps();

		Log.d(TAG, "initApps() the show apps databaseApps count is " + databaseApps.size()
				+ "--initApps() the show apps showApps count is-->" + showApps.size());

		mApps = databaseApps;

		for (ApplicationInfo showinfo : showApps) {
			boolean bfind = false;
			for (ApplicationInfo info : databaseApps) {
				if (info.getMainClassName().equalsIgnoreCase(showinfo.getMainClassName())) {
					bfind = true;
					break;
				}
			}

			if (!bfind) {
				mApps.add(showinfo);
				Log.d(TAG, "initApps() the show apps name " + showinfo.getMainClassName());
			}
		}

		PageCount = (int) Math.ceil((double) mApps.size() / APP_PAGE_SIZE);
		Log.d(TAG, "initApps() the show apps count is " + mApps.size() + "..." + "init_Apps()$$$PageCount--->"
				+ PageCount);

		if (PageCount == 0) {
			PageCount = 1;
		}

		return;

	}

	public void initUI() {
		mappAdapter = new AppAdapter(this, mApps);
		mdragScrollPage.setAdapter(mappAdapter);
		saveData2Database();
		mdragScrollPage.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				Log.e(TAG, "ybf-Item-position=" + position);
				
				// mappAdapter.setSelectPosition(position); //点击不出现焦点框
				ArrayList<ApplicationInfo> allApps = mappAdapter.getAppsData();
				if (position < 0 || position >= allApps.size()) {
					return;
				}

				ResolveInfo res = allApps.get(position).getResolveInfo();

				if (res == null) {
					return;
				}
				if (res.resolvePackageName != null) {
					return;
				}

				String pkg = res.activityInfo.packageName;
				String cls = res.activityInfo.name;
				if (cls.equals("com.tricheer.reboot.MainActivity")) {
					boolean isWifiopen = Utils.isWiFiActive(mContext);
					if (!isWifiopen) {
						showWifiDialog();
						return;
					}
				}
				if (cls.equals("com.example.deviceinfo.MainActivity")) {

				}

				try {
					// 2017-5-12
					Log.e(TAG, "ybf---onclick---");
				
					ComponentName componet = new ComponentName(pkg, cls);

					Intent intent = new Intent();
					intent.setComponent(componet);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
					overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
					overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
					Log.e(TAG, "cls.." + cls + "---pkg" + pkg);
				} catch (Exception e) {
					e.printStackTrace();
					Log.d(TAG, "setOnItemClickListener() exceptiom is " + e.toString());
				}

			}

		});

		mdragScrollPage.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				return true;
			}

		});
		mdragScrollPage.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageSelected(int position) {
				// TODO Auto-generated method stub
				positionPage = position;
				for (int i = 0; i < PageCount; i++) {
					if (position == i) {
						mImageView[i].setBackgroundResource(R.drawable.home_page_on);
					} else {
						mImageView[i].setBackgroundResource(R.drawable.home_page_off);
					}
				}
				Log.d(TAG, "setOnPageChangeListener() position is " + position);
			}

			@Override
			public void onPageScrollStateChanged(int state) {
				// TODO Auto-generated method stub

			}
		});
		mdragScrollPage.setOnRearrangeListener(new OnRearrangeListener() {

			@Override
			public void onRearrange(int oldIndex, int newIndex) {
				// TODO Auto-generated method stub
				ApplicationInfo appinfo = (ApplicationInfo) mappAdapter.getItem(oldIndex);
				mappAdapter.removeByIndex(oldIndex);
				mappAdapter.insertByIndex(appinfo, newIndex);
				mappAdapter.notifyDataSetChanged();

				saveData2Database();
			}

		});
		mdragScrollPage.setOnTouchChangeListener(new OnTouchChangeListener() {

			@Override
			public boolean onTouch(int type, int touchItemPosition) {
				// TODO Auto-generated method stub
				mappAdapter.setTouchItemPosition(touchItemPosition);
				mappAdapter.notifyDataSetChanged();
				return false;
			}
		});

		addPagePoit();
	}

	/**
	 * Dvr 打开设置wifi界面
	 */
	protected void showWifiDialog() {
		// TODO Auto-generated method stub
		if (dialog == null) {
			dialog = new MyDialog(this);
		}
		View view = LayoutInflater.from(this).inflate(R.layout.dialog_common, null);
		dialog.setLayoutView(view);
		Window window = dialog.getWindow();
		WindowManager.LayoutParams p = window.getAttributes();
		p.gravity = Gravity.CENTER;
		p.width = 500;
		p.height = 300;
		window.setAttributes(p);
		window.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();

		TextView tv_body = (TextView) view.findViewById(R.id.tv_body);
		TextView tv_yes = (TextView) view.findViewById(R.id.tv_yes);
		TextView tv_no = (TextView) view.findViewById(R.id.tv_no);
		tv_body.setText("dvr");
		tv_yes.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setAction("android.tricheer.settings");
				intent.putExtra("launch", "wifi");
				startActivity(intent);
				dialog.dismiss();
			}
		});
		tv_no.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
	}

	/*
	 * 2017-6-22 互斥进程处理工具 杀死**进程
	 */

	public void killAll(String pkg) {
		try {
			ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
			// am.forceStopPackage(pkg);
		} catch (Exception e) {
		}
	}

	/*
	 * 添加页码 指示条
	 */
	private void addPagePoit() {

		mLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
		if (PageCount == 1)
			return;
		Log.e(TAG, "addPagePoit() () PageCount is " + PageCount);
		for (int i = 0; i < PageCount && i < 20; i++) {
			ImageView imageView = new ImageView(this);
			// imageView.setLayoutParams(new
			// LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
			mImageView[i] = imageView;
			mImageView[i].setBackgroundResource(R.drawable.home_page_off);
			if (i == 0) {
				mImageView[i].setBackgroundResource(R.drawable.home_page_on);
			}
			// 设置marginRight 20
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			lp.setMargins(0, 0, 20, 0);
			imageView.setLayoutParams(lp);

			mLinearLayout.addView(mImageView[i]);
			// mLinearLayout.setVisibility(View.GONE);//wwz
		}

	}

	public static void setMargins(View v, int l, int t, int r, int b) {
		if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
			ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
			p.setMargins(l, t, r, b);
			v.requestLayout();
		}
	}

	@Override
	public void onNotifyAppAdded(String packagename) {
		// TODO Auto-generated method stub

		Log.d(TAG, "onNotifyAppAdded() added app name is " + packagename);
		init_Apps();
		mLinearLayout.removeAllViews();
		addPagePoit();
		mdragScrollPage.setCurrentItem(0);

		ArrayList<ApplicationInfo> allApps = mappAdapter.getAppsData();
		for (ApplicationInfo info : mApps) {
			boolean bfind = false;
			for (int i = 0; i < allApps.size(); i++) {
				if (allApps.get(i).getMainClassName().equalsIgnoreCase(info.getMainClassName())) {
					bfind = true;
					break;
				}
			}

			if (!bfind) {
				mappAdapter.insertTail(info);
			}
		}

		mappAdapter.notifyDataSetChanged();

		saveData2Database();
	}

	@Override
	public void onNotifyAppRemove(String packagename) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onNotifyAppRemove() remove app name is " + packagename);

		init_Apps();
		mLinearLayout.removeAllViews();
		mdragScrollPage.setCurrentItem(0);

		ArrayList<ApplicationInfo> allApps = mappAdapter.getAppsData();
		for (int i = 0; i < allApps.size(); i++) {
			boolean bfind = false;
			for (ApplicationInfo info : mApps) {
				if (allApps.get(i).getMainClassName().equalsIgnoreCase(info.getMainClassName())) {
					bfind = true;
					break;
				}
			}

			if (!bfind) {
				mappAdapter.removeByIndex(i);
				break;
			}
		}
		initUI();

		mappAdapter.notifyDataSetChanged();

		saveData2Database();
	}

	@Override
	public void onNotifyAppReplace(String packagename) {
		// TODO Auto-generated method stub

	}

	/*
	 * 监听Settings Theme值的变化
	 */
	class SettingsThemeChangeContentObserver extends ContentObserver {

		public SettingsThemeChangeContentObserver(Handler handler) {
			super(handler);
			// TODO Auto-generated constructor stub
		}

		public SettingsThemeChangeContentObserver() {
			super(new Handler());
		}

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			mappAdapter.notifyDataSetChanged();
			Log.d(TAG, "SettingsMusicChangeContentObserver -->Theme");
			// android.os.Process.killProcess(android.os.Process.myPid());
		}
	}

	/*
	 * 监听FM 值的变化
	 */
	class SettingsFMChangeContentObserver extends ContentObserver {

		public SettingsFMChangeContentObserver(Handler handler) {
			super(handler);
			// TODO Auto-generated constructor stub
		}

		public SettingsFMChangeContentObserver() {
			super(new Handler());
		}

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			try {
				int FMstate = Settings.System.getInt(mContext.getContentResolver(), Utils.FM_SETTING);
				if (2 == FMstate) {
					mappAdapter.FMstartAnimation();
				} else {
					mappAdapter.FMstopAnimation();
				}
			} catch (SettingNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.d(TAG, "FM Change");
			mappAdapter.notifyDataSetChanged();
		}
	}

	/*
	 * 监听Music 值的变化
	 */
	class SettingsMusicChangeContentObserver extends ContentObserver {

		public SettingsMusicChangeContentObserver(Handler handler) {
			super(handler);
			// TODO Auto-generated constructor stub
		}

		public SettingsMusicChangeContentObserver() {
			super(new Handler());
		}

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			try {
				int Musicstate = Settings.System.getInt(mContext.getContentResolver(), Utils.MUSIC_SETTING);
				if (2 == Musicstate) {
					mappAdapter.MusicstartAnimation();
				} else {
					mappAdapter.MusicstopAnimation();
				}
			} catch (SettingNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.d(TAG, "Music Change");
			mappAdapter.notifyDataSetChanged();
		}
	}

	/*
	 * 监听BT music 值的变化
	 */
	class SettingsBTMusicChangeContentObserver extends ContentObserver {

		public SettingsBTMusicChangeContentObserver(Handler handler) {
			super(handler);
			// TODO Auto-generated constructor stub
		}

		public SettingsBTMusicChangeContentObserver() {
			super(new Handler());
		}

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			try {
				int BTMusicstate = Settings.System.getInt(mContext.getContentResolver(), Utils.BT_MUSIC_SETTING);
				if (2 == BTMusicstate) {
					mappAdapter.BTMusicstartAnimation();
				} else {
					mappAdapter.BtMusicstopAnimation();
				}
			} catch (SettingNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.d(TAG, "Bt Music Change");
			mappAdapter.notifyDataSetChanged();
		}
	}

	@Override
	protected void onDestroy() {

		Log.e(TAG, "onDestroy()2 enter!!!");

		try {
			unregisterReceiver(MyLauncherReceiver);
			unregisterReceiver(mReceiverKeyCodeHome);
			getContentResolver().unregisterContentObserver(mSettingsThemeChangeContentObserver);
			getContentResolver().unregisterContentObserver(mSettingsFMChangeContentObserver);
			// 注销订阅者模式
			MirrorLauncherReceiver.removeNotifyObject(MIRROR_LAUNCHER_RECEIVER_TAG_MAINACTIVITY);
			// saveData2Database();
		} catch (Exception e) {
			// 解决快速点击退出按钮时，调用了多次safeFinish(), 导致反注册了多次广播
			// Ignore this exception. This is exactly what is desired
			Log.e(TAG, "onDestroy() e.getMessage()=" + e.getMessage());
		}

		super.onDestroy();
	}

	/*
	 * 保存到数据库
	 */
	private void saveData2Database() {
		mdbHelper.clear();
		ArrayList<ApplicationInfo> allApps = mappAdapter.getAppsData();
		for (int i = 0; i < allApps.size(); i++) {
			ResolveInfo res = allApps.get(i).getResolveInfo();
			ContentValues value = new ContentValues();
			value.put(DatabaseHelper.TABLE_CLASS_NAME, res.activityInfo.name);

			Log.d(TAG, "saveData2Database()  app name is " + res.activityInfo.name);
			if (i == allApps.size() - 1) {
				isopen = true;
			} else {
				isopen = false;
			}
			Log.e(TAG, "saveData2Database isopen = " + isopen);
			mdbHelper.insertData(value, isopen);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.e(TAG, "onKeyDown"+keyCode);
		Log.e(TAG,"size="+mApps.size());
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_LEFT:
			codekey +=1;
			if(codekey >= (mApps.size())){
				codekey = 0;
			}
			setPage();
			mappAdapter.setSelectPosition(codekey);
			Log.e(TAG, "left" +"[codekey ="+codekey+"]"+"[positionPage="+positionPage+"]");
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			codekey -=1;	
			if(codekey <= 0){
				codekey = 0;
			}
			
			setPage();
			mappAdapter.setSelectPosition(codekey);
			Log.e(TAG, "right" +"[codekey ="+codekey+"]"+"[positionPage="+positionPage+"]");
			break;
		case KeyEvent.KEYCODE_ENTER:
			Log.e(TAG, "enter");
			
			ArrayList<ApplicationInfo> allApps = mappAdapter.getAppsData();
			if (codekey < 0 || codekey >= allApps.size()) {
				break;
			}
			ResolveInfo res = allApps.get(codekey).getResolveInfo();
			if (res == null) {
				break;
			}
			if (res.resolvePackageName != null) {
				break;
			}

			String pkg = res.activityInfo.packageName;
			String cls = res.activityInfo.name;
			
			ComponentName componet = new ComponentName(pkg, cls);

			Intent intent = new Intent();
			intent.setComponent(componet);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
			Log.e(TAG, "cls.." + cls + "---pkg" + pkg);
		
		default:
			break;
		}

		return super.onKeyDown(keyCode, event);

	}
	/**
	 * 设置旋钮旋转时，页面
	 */
	public void setPage(){
		if(codekey <8){
			mdragScrollPage.setCurrentItem(0);
		}else if(codekey >= 8 && codekey <16){
			mdragScrollPage.setCurrentItem(1);
		}else if(codekey >=16){
			mdragScrollPage.setCurrentItem(2);
		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onResume() {
		super.onResume();
		gestureDetector = new GestureDetector(this, onGestureListener);

		mappAdapter.notifyDataSetChanged();

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

	}

	@Override
	public void onBackPressed() {
		// super.onBackPressed();
	}

	OnTouchListener touchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			return gestureDetector.onTouchEvent(event);
		}
	};

	private GestureDetector.OnGestureListener onGestureListener = new GestureDetector.SimpleOnGestureListener() {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			if (e1 == null || e2 == null) {
				return false;
			}
			float x = e2.getX() - e1.getX();
			if (x < 50.0f) {
				onBackPressed();
				return true;
			}
			return false;
		}
	};

	public boolean onTouchEvent(MotionEvent event) {
		return gestureDetector.onTouchEvent(event);
	}

	public static int getDrawableId(Context paramContext, String paramString) {
		return paramContext.getResources().getIdentifier(paramString, "drawable", paramContext.getPackageName());
	}

	/*
	 * 注册广播
	 */
	private void myRegisterReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction("action.camera.STATE_CHANGE");
		filter.addAction("com.tricheer.video.PLAY_FROM_FILEMANAGER");
		filter.addAction("com.tricheer.KUWO.ACTION");// 2017-6-23
		filter.addAction(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED);
		filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
		filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
		filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
		registerReceiver(MyLauncherReceiver, filter);

		IntentFilter intentFilterKeyCodeHome = new IntentFilter();
		intentFilterKeyCodeHome.addAction("com.lct.KEYCODE_HOME");
		registerReceiver(mReceiverKeyCodeHome, intentFilterKeyCodeHome);

	}

	/**
	 * 
	 */
	private BroadcastReceiver MyLauncherReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			Log.v(TAG, "MyLauncherReceiver action " + action);
			if (action.equals("action.camera.STATE_CHANGE")) {
				Log.v(TAG, "action.camera.STATE_CHANGE update record. ");

			} else if (action.equals(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED)) {

			} else if (action.equals("com.tricheer.video.PLAY_FROM_FILEMANAGER")) {

				boolean isrunximalaya = runprocess(context, "com.ximalaya.ting.android.car");
				if (isrunximalaya) {
					killAll("com.ximalaya.ting.android.car");
					Log.e(TAG, "receiver kill ximalaya");
				}

			} else if (action.equals("com.tricheer.KUWO.ACTION")) {
				boolean isrunximalaya = runprocess(mContext, "com.ximalaya.ting.android.car");
				boolean isrunkuwo = runprocess(mContext, "cn.kuwo.kwmusiccar");
				if (isrunkuwo) {
					killAll("cn.kuwo.kwmusiccar");
				}
				if (isrunximalaya) {
					killAll("com.ximalaya.ting.android.car");
					Log.e(TAG, "receiver kill ximalaya");
				}
			} else if (action.equals("android.net.wifi.WIFI_STATE_CHANGED")) {
				int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1);

				if (wifiState == WifiManager.WIFI_STATE_DISABLING) {
					Log.e(TAG, " wifi 正在关闭");
				} else if (wifiState == WifiManager.WIFI_STATE_ENABLING) {
					Log.e(TAG, "wifi 正在打开");
				} else if (wifiState == WifiManager.WIFI_STATE_DISABLED) {
					Log.e(TAG, "wifi 已经关闭");
				} else if (wifiState == WifiManager.WIFI_STATE_ENABLED) {
					Log.e(TAG, "wifi 已经打开");
				} else {
					Log.e(TAG, "wifi 未知状态");
				}
			}else if(action.equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)){
				Toast.makeText(mContext, "attach="+Utils.isHasSupperUDisk(mContext), 0).show();
			}else if(action.equals(UsbManager.ACTION_USB_DEVICE_DETACHED)){
				Toast.makeText(mContext, "deattach="+Utils.isHasSupperUDisk(mContext), 0).show();
			}
		}
	};

	/*
	 * 判断某个进程是否开启
	 */
	public boolean runprocess(Context context, String packagename) {
		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> appProcessInfos = activityManager.getRunningAppProcesses();
		for (RunningAppProcessInfo runningAppProcessInfo : appProcessInfos) {
			if (runningAppProcessInfo.processName.equals(packagename)) {
				isRunning = true;
			}
		}
		// Log.e(TAG, "*isRunning----*"+isRunning);
		return isRunning;
	}

	/******************************* Order start *******************************/

	@Override
	public void onNotifyBootComplete() {
		// TODO Auto-generated method stub

		Log.d(TAG, "onNotifyBootComplete()");

	}

	@Override
	public void onNotifyShutDown() {
		// TODO Auto-generated method stub

		Log.d(TAG, "onNotifyShutDown()");

	}

	@Override
	public void onNotifySDCardMounted(int state) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onNotifySDCardMounted()");
	}

	@Override
	public void notifyTest(int which, int type) {
		// TODO Auto-generated method stub
		Log.d(TAG, "notifyTest(" + which + ", " + type + ")");

	}

	/*@Override
	public void onNotifyAppUDiskAttach() {
		// TODO Auto-generated method stub
		Toast.makeText(mContext, "attach="+Utils.isHasSupperUDisk(), 0).show();
		Log.e(TAG, "attatch"+ Utils.isHasSupperUDisk());
	}

	@Override
	public void onNotifyAppUDiskDeattach() {
		// TODO Auto-generated method stub
		Toast.makeText(mContext,"deattach="+Utils.isHasSupperUDisk()+"", 0).show();
		Log.e(TAG, "deattatch"+Utils.isHasSupperUDisk());
	}*/

	/******************************* Order end *******************************/

}

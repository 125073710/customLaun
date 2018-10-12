package com.tricheer.launcherk218.utility;

import com.tricheer.launcher218.R;









/**
 * 创建时间：20160406
 * 
 * @author liyongyong 类说明：配置文件
 * 
 */
public class LctConst {

	public static String TAG = "LctConst";

	// 为每种模式定义一个常量
	public static final int APP_ITEM_TYPE_RADIO = 0;
	public static final int APP_ITEM_TYPE_VIDEO = 1;
	public static final int APP_ITEM_TYPE_MUSIC = 2;
	public static final int APP_ITEM_TYPE_SETTING = 3;
	public static final int APP_ITEM_TYPE_FM = 4;
	public static final int APP_ITEM_TYPE_RECORD = 5;
	public static final int APP_ITEM_TYPE_OTHER = 6;
	public static final int APP_ITEM_TYPE_NULL = 7; // 统计布局的个数
	public static final int APP_ITEM_TYPE_NUM_MAX = 8; // 统计布局的个数
	
	

	// 主Activity类名, 名称, APP图标, 布局类型
	public static final String kAppItemMainActivity = "mainActivity";
	public static final String kAppItemName = "name";
	public static final String kAppItemImage = "image";
	public static final String kAppItemType = "type";
	public static final String kAppItemResolveInfo = "resolveInfo";
	public static final String kAppItemStatus = "status";
	public static final String kAppItemStatus2 = "status2";
	public static final String kAppItemText = "text";
/*	高德地图；电台（FM/AM）；本地音乐；在线音乐；本地视频；DVR预览；蓝牙电话；Carlife；Carplay；系统设置；*/
	// 主Activity类名, 名称, APP图标, 布局类型// 普通风格
	public static final Object APP_ITEM_ARRAY[][] = {
			{ "com.autonavi.auto.remote.fill.UsbFillActivity", -1, R.drawable.map, APP_ITEM_TYPE_NULL }, //map
			{ "com.tricheer.radio.MainActivity", -1, R.drawable.broadcastn, APP_ITEM_TYPE_RADIO }, // radio
			{ "com.tricheer.player.MusicPlayerActivity", -1, R.drawable.musicg, APP_ITEM_TYPE_MUSIC }, // Music
			{ "com.tricheer.player.VideoPlayerActivity", -1, R.drawable.videon,APP_ITEM_TYPE_VIDEO }, // VideoPlayer	
			{ "com.cruisecloud.dvr.MainActivity", -1, R.drawable.dvrn, APP_ITEM_TYPE_NULL }, //DVR
			{ "com.tricheer.settings.MainActivity", R.string.app_nusic, R.drawable.settingg, APP_ITEM_TYPE_SETTING }, // stting		
			{ "com.tricheer.bt.MainActivity", -1, R.drawable.phonen, APP_ITEM_TYPE_NULL }, //bt_phone
			{ "com.tricheer.bt.MusicActivity", -1, R.drawable.bmusicn, APP_ITEM_TYPE_NULL }, //bt_music
			{ "com.tricheer.carlife.MainActivity", -1, -1, APP_ITEM_TYPE_NULL },		//carlife
			{ "com.android.settings.Settings", -1, -1, APP_ITEM_TYPE_NULL },		//settings
			{ "com.chartcross.gpstestplus.GPSTestPlus", -1, -1, APP_ITEM_TYPE_NULL },		//settings
			
	};
	// 主Activity类名, 名称, APP图标, 布局类型// iphone风格  （没用 2018/8/15）
	public static final Object APP_ITEM_ARRAY_IPEHON[][] ={
		{ "com.tricheer.btmusic.MusicActivity", R.string.app_nusic, R.drawable.home_btn_didigou, APP_ITEM_TYPE_MUSIC }, // 酷我音乐
		{ "com.android.calendar.AllInOneActivity", R.string.app_fm, R.drawable.home_btn_didigou,APP_ITEM_TYPE_FM }, // FM发射
	};

	public static String ACTION_COM_TRICHEER_KUWO_MUSIC_OPEN = "com.tricheer.kuwo.music.open"; // 打开酷我音乐的时候，需要给我们自己开发的音乐播放器发送这个广播

	// ACTION
	public static String ACTION_ANDROID_BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED"; // 开机广播
	public static String ACTION_ANDROID_ACTION_SHUTDOWN = "android.intent.action.ACTION_SHUTDOWN"; // 关机广播
	public static String ACTION_ANDROID_MEDIA_MOUNTED = "android.intent.action.MEDIA_MOUNTED"; // SD卡挂载
	public static String ACTION_TEST = "com.mirrorlauncher.manual"; // 测试
	public static String ACTION_NOTE_OTHERAPK_MUSIC_PLAY = "com.tricheer.music.PLAY_FROM_LAUNCHER"; // 通知音乐播放器播放或者暂停音乐
	public static String ACTION_NOTE_OTHERAPK_QTFM_PLAY = "com.tricheer.qtfm.PLAY_FROM_LAUNCHER"; // 通知蜻蜓FM播放或者暂停音乐
	public static String ACTION_NOTE_OTHERAPK_RADAR_PLAY = "com.tricheer.radar.open"; // 开启雷达开关的广播
	public static String ACTION_NOTE_OTHERAPK_RADAR_STOP = "com.tricheer.radar.close"; // 关闭雷达开关的广播
	public static String ACTION_NOTE_OTHERAPK_EDOG_PLAY = "com.tricheer.edog.open"; // 开启电子狗开关的广播
	public static String ACTION_NOTE_OTHERAPK_EDOG_STOP = "com.tricheer.edog.close"; // 关闭电子狗开关的广播
	public static String ACTION_NOTE_OTHERAPK_FM_PLAY = "com.tricheer.fm.play"; // 开启FM开关的广播
	public static String ACTION_NOTE_OTHERAPK_FM_STOP = "com.tricheer.fm.stop"; // 关闭FM开关的广播
	public static String ACTION_NOTE_OTHERAPK_RECORD = "action.camera.ACTION_CONFIG_RECORD";
	public static String ACTION_ANDROID_GPS_FIX_CHANGE = "android.location.GPS_FIX_CHANGE"; // GPS是否定位状态改变的时候，系统会发这个广播
	public static String ACTION_ANDROID_GPS_FIX_CHANGE_EXTRA_GPS_ENABLED = "enabled";

}

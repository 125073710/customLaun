package com.tricheer.launcherk218;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.tricheer.launcher218.R;
import com.tricheer.launcherk218.utility.LctConst;
import com.tricheer.launcherk218.utility.Utils;

public class AppAdapter extends BaseAdapter {

	private static final String TAG = "AppAdapter";
	private final int MSG_TIME = 0;
	// private List<ResolveInfo> mList;
	private List<Map<String, Object>> mList;
	private Context mContext;
	private GridView mGridView = null;
	public static final int APP_PAGE_SIZE = 8;
	private PackageManager pm;
	private ArrayList<ApplicationInfo> mallApps;

	Timer mTimer = null;
	int mcount = 0;
	private TextView mTextTime = null;
	private boolean mIsFirst = false;
	// private Intent intent;
	private int defaultSelection = -1;
	AnimationDrawable fmanimatio;
	AnimationDrawable btmusicanimation;
	AnimationDrawable musicanimation;
	private int mTouchItemPosition = -1;

	/**
	 * 图片加载器
	 */

	public AppAdapter(Context context, List<Map<String, Object>> list, int page, GridView gridView) {
		mContext = context;

		mList = new ArrayList<Map<String, Object>>();

		int minIndex = page * APP_PAGE_SIZE;
		int maxIndex = minIndex + APP_PAGE_SIZE;
		while ((minIndex < list.size()) && (minIndex < maxIndex)) {
			mList.add(list.get(minIndex));
			minIndex++;
		}

		mGridView = gridView;

	}

	public AppAdapter(Context context, ArrayList<ApplicationInfo> apps) {
		mContext = context;
		mallApps = new ArrayList<ApplicationInfo>();

		Log.d(TAG, "AppsAdapter() apps size is " + apps.size());
		for (int i = 0; i < apps.size(); i++) {
			mallApps.add(apps.get(i));
		}

	}

	@Override
	public int getCount() {
		return mallApps.size();
	}

	@Override
	public Object getItem(int position) {

		return mallApps.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ApplicationInfo appinfo = mallApps.get(position);
		if (appinfo == null) {
			Log.d(TAG, "getView() appinfo is null. ");
			return convertView;
		}

		AppItem appItem = null;
		AppMapItem mapItem = null;
		AppRadioItem appRecordItem = null;
		AppMusicItem musicItem = null;
		AppVideoItem appVideoItem = null;
		AppDvrItem dvrItem = null;
		AppSettingItem settingItem = null;
		AppBtPhoneItem phoneItem = null;
		AppBtMusicItem btmusicItem = null;
		AppCarlifeItem carlifeItem = null;

		/**
		 * 状态比较多 1.按压状态，2 主题切换 3.是否禁灰 1.先判断主题（选择默认 或 苹果） 2.判断是否禁灰 （如果是禁灰
		 * 不走图片按压状态）
		 */
		/** 高德地图；电台（FM/AM）；本地音乐；在线音乐；本地视频；DVR预览；系统设置;蓝牙电话；Carlife；Carplay；； */
		if (convertView == null) { // 音乐
			if (appinfo.getMainClassName().equalsIgnoreCase("com.autonavi.auto.remote.fill.UsbFillActivity")) {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.apps_video_item, null);
				mapItem = new AppMapItem();
				mapItem.mAppIcon = (ImageView) convertView.findViewById(R.id.video_app_icon);
				mapItem.mAppName = (TextView) convertView.findViewById(R.id.video_app_name);
				mapItem.mAppName.setText(appinfo.getTitle());

				setMapIcon(position, mapItem);
				convertView.setTag(mapItem);
			} else if (appinfo.getMainClassName().equalsIgnoreCase("com.tricheer.radio.MainActivity")) {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.apps_radio_item, null);
				appRecordItem = new AppRadioItem();
				appRecordItem.mAppIcon = (ImageView) convertView.findViewById(R.id.app_icon);
				appRecordItem.mAppName = (TextView) convertView.findViewById(R.id.app_name);
				appRecordItem.anim = (ImageView) convertView.findViewById(R.id.anim);
				
				fmanimatio = (AnimationDrawable) appRecordItem.anim.getDrawable();
				// appRecordItem.mAppIcon.setImageDrawable(appinfo.getIcon());
				appRecordItem.mAppName.setText(appinfo.getTitle());

				// 主题切换
				setFMIcon(position, appRecordItem);

				convertView.setTag(appRecordItem);

			} else if (appinfo.getMainClassName().equalsIgnoreCase("com.tricheer.player.VideoPlayerActivity")) {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.apps_video_item, null);
				appVideoItem = new AppVideoItem();
				appVideoItem.mAppIcon = (ImageView) convertView.findViewById(R.id.video_app_icon);
				appVideoItem.mAppName = (TextView) convertView.findViewById(R.id.video_app_name);
				appVideoItem.anim = (ImageView) convertView.findViewById(R.id.video_anim);
				appVideoItem.mAppName.setText(appinfo.getTitle());
				setVideoIcon(position, appVideoItem);
				convertView.setTag(appVideoItem);
			} else if (appinfo.getMainClassName().equalsIgnoreCase("com.tricheer.player.MusicPlayerActivity")) {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.apps_music_item, null);
				musicItem = new AppMusicItem();
				musicItem.mAppIcon = (ImageView) convertView.findViewById(R.id.music_app_icon);
				musicItem.mAppName = (TextView) convertView.findViewById(R.id.music_app_name);
				musicItem.anim = (ImageView) convertView.findViewById(R.id.music_anim);
				musicanimation = (AnimationDrawable) musicItem.anim.getDrawable();
				musicItem.mAppName.setText(appinfo.getTitle());
				setMusicIcon(position, musicItem);
				convertView.setTag(musicItem);
			} else if (appinfo.getMainClassName().equalsIgnoreCase("com.cruisecloud.dvr.MainActivity")) {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.apps_dvr_item, null);
				dvrItem = new AppDvrItem();
				dvrItem.mAppIcon = (ImageView) convertView.findViewById(R.id.dvr_app_icon);
				dvrItem.mAppName = (TextView) convertView.findViewById(R.id.dvr_app_name);
				dvrItem.anim = (ImageView) convertView.findViewById(R.id.dvr_anim);
				dvrItem.mAppName.setText(appinfo.getTitle());
				setDvrIcon(position, dvrItem);
				convertView.setTag(dvrItem);

			} else if (appinfo.getMainClassName().equalsIgnoreCase("com.tricheer.settings.MainActivity")) {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.apps_settings_item, null);
				settingItem = new AppSettingItem();
				settingItem.mAppIcon = (ImageView) convertView.findViewById(R.id.setting_app_icon);
				settingItem.mAppName = (TextView) convertView.findViewById(R.id.setting_app_name);
				settingItem.anim = (ImageView) convertView.findViewById(R.id.setting_anim);
				settingItem.mAppName.setText(appinfo.getTitle());
				setSettingIcon(position, settingItem);
				convertView.setTag(settingItem);

			} else if (appinfo.getMainClassName().equalsIgnoreCase("com.tricheer.bt.MainActivity")) {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.apps_phone_item, null);
				phoneItem = new AppBtPhoneItem();
				phoneItem.mAppIcon = (ImageView) convertView.findViewById(R.id.phone_app_icon);
				phoneItem.mAppName = (TextView) convertView.findViewById(R.id.phone_app_name);
				phoneItem.anim = (ImageView) convertView.findViewById(R.id.phone_anim);
				phoneItem.mAppName.setText(appinfo.getTitle());
				setPhoneIcon(position, phoneItem);
				convertView.setTag(phoneItem);

			} else if (appinfo.getMainClassName().equalsIgnoreCase("com.tricheer.bt.MusicActivity")) {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.apps_btmusic_item, null);
				btmusicItem = new AppBtMusicItem();
				btmusicItem.mAppIcon = (ImageView) convertView.findViewById(R.id.btmusic_app_icon);
				btmusicItem.mAppName = (TextView) convertView.findViewById(R.id.btmusic_app_name);
				btmusicItem.anim = (ImageView) convertView.findViewById(R.id.btmusic_anim);
				btmusicanimation = (AnimationDrawable) btmusicItem.anim.getDrawable();
				btmusicItem.mAppName.setText(appinfo.getTitle());
				setBtMusicIcon(position, btmusicItem);
				convertView.setTag(btmusicItem);
			} else if (appinfo.getMainClassName().equalsIgnoreCase("com.tricheer.carlife.MainActivity")) {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.apps_carlifes_item, null);
				carlifeItem = new AppCarlifeItem();
				carlifeItem.mAppIcon = (ImageView) convertView.findViewById(R.id.carlife_app_icon);
				carlifeItem.mAppName = (TextView) convertView.findViewById(R.id.carlife_app_name);
				carlifeItem.anim = (ImageView) convertView.findViewById(R.id.carlife_anim);
				carlifeItem.mAppName.setText(appinfo.getTitle());
				setCarlifeIcon(position, carlifeItem);
				convertView.setTag(carlifeItem);

			} else {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.apps_item, null);
				appItem = new AppItem();
				appItem.mAppIcon = (ImageView) convertView.findViewById(R.id.app_icon);
				appItem.mAppName = (TextView) convertView.findViewById(R.id.app_name);

				// appItem.mAppIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.none));
				appItem.mAppName.setText(appinfo.getTitle());
				setAppIcon(position, appItem);

				convertView.setTag(appItem);
			}
			// -----------------------------------------------复用-------------------------------------------------------------------//
		} else {
			if (appinfo.getMainClassName().equalsIgnoreCase("com.autonavi.auto.remote.fill.UsbFillActivity")) {
				mapItem = (AppMapItem) convertView.getTag();
				mapItem.mAppName.setText(appinfo.getTitle());
				setMapIcon(position, mapItem);
			} else if (appinfo.getMainClassName().equalsIgnoreCase("com.tricheer.radio.MainActivity")) {
				appRecordItem = (AppRadioItem) convertView.getTag();
				appRecordItem.mAppName.setText(appinfo.getTitle());
				setFMIcon(position, appRecordItem);
			} else if (appinfo.getMainClassName().equalsIgnoreCase("com.tricheer.player.VideoPlayerActivity")) {
				appVideoItem = (AppVideoItem) convertView.getTag();
				appVideoItem.mAppName.setText(appinfo.getTitle());
				setVideoIcon(position, appVideoItem);
			} else if (appinfo.getMainClassName().equalsIgnoreCase("com.tricheer.player.MusicPlayerActivity")) {
				musicItem = (AppMusicItem) convertView.getTag();
				musicItem.mAppName.setText(appinfo.getTitle());
				setMusicIcon(position, musicItem);
			} else if (appinfo.getMainClassName().equalsIgnoreCase("com.cruisecloud.dvr.MainActivity")) {
				dvrItem = (AppDvrItem) convertView.getTag();
				dvrItem.mAppName.setText(appinfo.getTitle());
				setDvrIcon(position, dvrItem);

			} else if (appinfo.getMainClassName().equalsIgnoreCase("com.tricheer.settings.MainActivity")) {
				settingItem = (AppSettingItem) convertView.getTag();
				settingItem.mAppName.setText(appinfo.getTitle());
				setSettingIcon(position, settingItem);
			} else if (appinfo.getMainClassName().equalsIgnoreCase("com.tricheer.bt.MainActivity")) {
				phoneItem = (AppBtPhoneItem) convertView.getTag();
				phoneItem.mAppName.setText(appinfo.getTitle());
				setPhoneIcon(position, phoneItem);
			} else if (appinfo.getMainClassName().equalsIgnoreCase("com.tricheer.bt.MusicActivity")) {
				btmusicItem = (AppBtMusicItem) convertView.getTag();
				btmusicItem.mAppName.setText(appinfo.getTitle());
				setBtMusicIcon(position, btmusicItem);
			} else if (appinfo.getMainClassName().equalsIgnoreCase("com.tricheer.carlife.MainActivity")) {
				carlifeItem = (AppCarlifeItem) convertView.getTag();
				carlifeItem.mAppName.setText(appinfo.getTitle());
				setCarlifeIcon(position, carlifeItem);
			} else {
				appItem = (AppItem) convertView.getTag();
				// appItem.mAppIcon.setImageDrawable(appinfo.getIcon());

				// appItem.mAppIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.none));
				appItem.mAppName.setText(appinfo.getTitle());
				setAppIcon(position, appItem);
			
			}
		}

		// -------------------------------------------------------------------------------------------------------------//
		// 设置高亮
		Log.e(TAG, "defaultSelection="+defaultSelection);
		if (position == defaultSelection) {// 选中时设置单纯颜色
			convertView.setBackgroundResource(R.drawable.defat1);
		} else {// 未选中时设置selector
			int bg_selected_color = mContext.getResources().getColor(R.color.navigation_bg);// 背景选中的颜色
			convertView.setBackgroundColor(bg_selected_color);
		}/*if(-1 == defaultSelection){
			int bg_selected_color = mContext.getResources().getColor(R.color.navigation_bg);// 背景选中的颜色
			convertView.setBackgroundColor(bg_selected_color);
		}*/

		return convertView;

	}

	/**
	 * 默认 ICON
	 * 
	 * @param position
	 * @param appRecordItem
	 * 
	 *            先判断主题，然后判断按压， 后续需要在按压前 判断是否禁灰
	 */
	private void setAppIcon(int position, AppItem appItem) {
		try {
			int Theme = Settings.System.getInt(mContext.getContentResolver(), Utils.THEME_SETTING);
			if (Theme == 0) {
				if (mTouchItemPosition == position) {
					appItem.mAppIcon.setBackgroundResource(R.drawable.def_n);
				} else {
					appItem.mAppIcon.setBackgroundResource(R.drawable.def_n);
				}
			} else if (Theme == 1) { // 苹果风格
				if (mTouchItemPosition == position) {
					appItem.mAppIcon.setBackgroundResource(R.drawable.def);
				} else {
					appItem.mAppIcon.setBackgroundResource(R.drawable.def);
				}
			}
			Log.e(TAG, "Theme=" + Theme);
		} catch (SettingNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 设置地图 ICON
	 * 
	 * @param position
	 * @param appRecordItem
	 * 
	 *            先判断主题，然后判断按压， 后续需要在按压前 判断是否禁灰
	 */
	private void setMapIcon(int position, AppMapItem mapItem) {
		try {
			int Theme = Settings.System.getInt(mContext.getContentResolver(), Utils.THEME_SETTING);
			if (Theme == 0) {
				if (mTouchItemPosition == position) {
					mapItem.mAppIcon.setBackgroundResource(R.drawable.mapn);
				} else {
					mapItem.mAppIcon.setBackgroundResource(R.drawable.map);
				}
			} else if (Theme == 1) { // 苹果风格
				if (mTouchItemPosition == position) {
					mapItem.mAppIcon.setBackgroundResource(R.drawable.gddh);
				} else {
					mapItem.mAppIcon.setBackgroundResource(R.drawable.gddh);
				}
			}
			Log.e(TAG, "Theme=" + Theme);
		} catch (SettingNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 设置FM ICON
	 * 
	 * @param position
	 * @param appRecordItem
	 */
	private void setFMIcon(int position, AppRadioItem appRecordItem) {
		try {
			int Theme = Settings.System.getInt(mContext.getContentResolver(), Utils.THEME_SETTING);
			int FMstate = Settings.System.getInt(mContext.getContentResolver(), Utils.FM_SETTING);
			if (Theme == 0) {
				if (mTouchItemPosition == position) {
					appRecordItem.mAppIcon.setBackgroundResource(R.drawable.broadcastn);
					
				} else {
					appRecordItem.mAppIcon.setBackgroundResource(R.drawable.broadcastn);
				}	
				if(2 == FMstate ){
					appRecordItem.mAppIcon.setImageResource(R.drawable.shadow_bg);
					appRecordItem.anim.setVisibility(View.VISIBLE);
				}else{
					appRecordItem.mAppIcon.setImageResource(R.drawable.broadcastn);
					appRecordItem.anim.setVisibility(View.GONE);
				}
			} else if (Theme == 1) { // 苹果风格
				if (mTouchItemPosition == position) {
					appRecordItem.mAppIcon.setBackgroundResource(R.drawable.broadcast);
				} else {
					appRecordItem.mAppIcon.setBackgroundResource(R.drawable.broadcast);
				}
				if(2 == FMstate ){
					appRecordItem.mAppIcon.setImageResource(R.drawable.shadow_ios);
					appRecordItem.anim.setVisibility(View.VISIBLE);
				}else{
					appRecordItem.mAppIcon.setImageResource(R.drawable.broadcast);
					appRecordItem.anim.setVisibility(View.GONE);
				}
			}
			Log.e(TAG, "Theme=" + Theme);
		} catch (SettingNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 * 设置Music ICON
	 * 
	 * @param position
	 * @param appRecordItem
	 */
	private void setMusicIcon(int position, AppMusicItem musicItem) {
		try {
			int Theme = Settings.System.getInt(mContext.getContentResolver(), Utils.THEME_SETTING);
			int Musicstate = Settings.System.getInt(mContext.getContentResolver(), Utils.MUSIC_SETTING);
			if (Theme == 0) {
				if (mTouchItemPosition == position) {
					musicItem.mAppIcon.setBackgroundResource(R.drawable.musicg);
				} else {
					musicItem.mAppIcon.setBackgroundResource(R.drawable.musicg);
				}
				if(2 == Musicstate ){
					musicItem.mAppIcon.setImageResource(R.drawable.shadow_bg);
					musicItem.anim.setVisibility(View.VISIBLE);
				}else{
					musicItem.mAppIcon.setImageResource(R.drawable.musicg);
					musicItem.anim.setVisibility(View.GONE);
					}
			} else if (Theme == 1) { // 苹果风格
				if (mTouchItemPosition == position) {
					musicItem.mAppIcon.setBackgroundResource(R.drawable.music);
				} else {
					musicItem.mAppIcon.setBackgroundResource(R.drawable.music);
				}
				
				if(2 == Musicstate ){
					musicItem.mAppIcon.setImageResource(R.drawable.shadow_bg);
					musicItem.anim.setVisibility(View.VISIBLE);
				}else{
					musicItem.mAppIcon.setImageResource(R.drawable.music);
					musicItem.anim.setVisibility(View.GONE);
					}
			}
			Log.e(TAG, "Theme=" + Theme);
		} catch (SettingNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 * 设置Video ICON
	 * 
	 * @param position
	 */
	private void setVideoIcon(int position, AppVideoItem appVideoItem) {
		try {
			int Theme = Settings.System.getInt(mContext.getContentResolver(), Utils.THEME_SETTING);
			if (Theme == 0) {
				if (mTouchItemPosition == position) {
					appVideoItem.mAppIcon.setBackgroundResource(R.drawable.videon);
				} else {
					appVideoItem.mAppIcon.setBackgroundResource(R.drawable.videon);
				}
			} else if (Theme == 1) { // 苹果风格
				if (mTouchItemPosition == position) {
					appVideoItem.mAppIcon.setBackgroundResource(R.drawable.video);
				} else {
					appVideoItem.mAppIcon.setBackgroundResource(R.drawable.video);
				}
			}
			Log.e(TAG, "Theme=" + Theme);
		} catch (SettingNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 设置DVr ICON
	 * 
	 * @param position
	 */
	private void setDvrIcon(int position, AppDvrItem dvrItem) {
		try {
			int Theme = Settings.System.getInt(mContext.getContentResolver(), Utils.THEME_SETTING);
			if (Theme == 0) {
				if (mTouchItemPosition == position) {
					dvrItem.mAppIcon.setBackgroundResource(R.drawable.dvrn);
				} else {
					dvrItem.mAppIcon.setBackgroundResource(R.drawable.dvrn);
				}
			} else if (Theme == 1) { // 苹果风格
				if (mTouchItemPosition == position) {
					dvrItem.mAppIcon.setBackgroundResource(R.drawable.dvr);
				} else {
					dvrItem.mAppIcon.setBackgroundResource(R.drawable.dvr);
				}
			}
			Log.e(TAG, "Theme=" + Theme);
		} catch (SettingNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 设置Setting ICON
	 * 
	 * @param position
	 */
	private void setSettingIcon(int position, AppSettingItem settingItem) {
		// TODO Auto-generated method stub
		try {
			int Theme = Settings.System.getInt(mContext.getContentResolver(), Utils.THEME_SETTING);
			if (Theme == 0) {
				if (mTouchItemPosition == position) {
					settingItem.mAppIcon.setBackgroundResource(R.drawable.setting_c);
				} else {
					settingItem.mAppIcon.setBackgroundResource(R.drawable.settingg);
				}
			} else if (Theme == 1) { // 苹果风格
				if (mTouchItemPosition == position) {
					settingItem.mAppIcon.setBackgroundResource(R.drawable.setting);
				} else {
					settingItem.mAppIcon.setBackgroundResource(R.drawable.setting);
				}
			}
			Log.e(TAG, "Theme=" + Theme);
		} catch (SettingNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 设置PhoneICON
	 * 
	 * @param position
	 */
	private void setPhoneIcon(int position, AppBtPhoneItem phoneItem) {
		// TODO Auto-generated method stub
		try {
			int Theme = Settings.System.getInt(mContext.getContentResolver(), Utils.THEME_SETTING);
			if (Theme == 0) {
				if (mTouchItemPosition == position) {
					phoneItem.mAppIcon.setBackgroundResource(R.drawable.phonen);
				} else {
					phoneItem.mAppIcon.setBackgroundResource(R.drawable.phonen);
				}
			} else if (Theme == 1) { // 苹果风格
				if (mTouchItemPosition == position) {
					phoneItem.mAppIcon.setBackgroundResource(R.drawable.phone);
				} else {
					phoneItem.mAppIcon.setBackgroundResource(R.drawable.phone);
				}
			}
			Log.e(TAG, "Theme=" + Theme);
		} catch (SettingNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 设置bt musci ICON
	 * 
	 * @param position
	 */
	private void setBtMusicIcon(int position, AppBtMusicItem btmusicItem) {
		// TODO Auto-generated method stub
		try {
			int Theme = Settings.System.getInt(mContext.getContentResolver(), Utils.THEME_SETTING);
			int BTMusicstate = Settings.System.getInt(mContext.getContentResolver(), Utils.BT_MUSIC_SETTING);
			if (Theme == 0) {
				if (mTouchItemPosition == position) {
					btmusicItem.mAppIcon.setBackgroundResource(R.drawable.bmusicn);
				} else {
					btmusicItem.mAppIcon.setBackgroundResource(R.drawable.bmusicn);
				}
				
				if(2 == BTMusicstate ){
					btmusicItem.mAppIcon.setImageResource(R.drawable.shadow_bg);
					btmusicItem.anim.setVisibility(View.VISIBLE);
				}else{
					btmusicItem.mAppIcon.setImageResource(R.drawable.bmusicn);
					btmusicItem.anim.setVisibility(View.GONE);
					}
			} else if (Theme == 1) { // 苹果风格
				if (mTouchItemPosition == position) {
					btmusicItem.mAppIcon.setBackgroundResource(R.drawable.btmusic);
				} else {
					btmusicItem.mAppIcon.setBackgroundResource(R.drawable.btmusic);
				}
				
				if(2 == BTMusicstate ){
					btmusicItem.mAppIcon.setImageResource(R.drawable.shadow_bg);
					btmusicItem.anim.setVisibility(View.VISIBLE);
				}else{
					btmusicItem.mAppIcon.setImageResource(R.drawable.btmusic);
					btmusicItem.anim.setVisibility(View.GONE);
					}
			}
			Log.e(TAG, "Theme=" + Theme);
		} catch (SettingNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 设置carlife ICON
	 * 
	 * @param position
	 */
	private void setCarlifeIcon(int position, AppCarlifeItem carlifeItem) {
		// TODO Auto-generated method stub
		try {
			int Theme = Settings.System.getInt(mContext.getContentResolver(), Utils.THEME_SETTING);
			if (Theme == 0) {
				if (mTouchItemPosition == position) {
					carlifeItem.mAppIcon.setBackgroundResource(R.drawable.carlife_n);
				} else {
					carlifeItem.mAppIcon.setBackgroundResource(R.drawable.carlife_n);
				}
			} else if (Theme == 1) { // 苹果风格
				if (mTouchItemPosition == position) {
					carlifeItem.mAppIcon.setBackgroundResource(R.drawable.carlife);
				} else {
					carlifeItem.mAppIcon.setBackgroundResource(R.drawable.carlife);
				}
			}
			Log.e(TAG, "Theme=" + Theme);
		} catch (SettingNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setTouchItemPosition(int position) {
		mTouchItemPosition = position;
	}

	/**
	 * @param position
	 *            设置高亮状态的item
	 */
	public void setSelectPosition(int position) {
		if (!(position < 0 || position > mallApps.size())) {
			defaultSelection = position;
			notifyDataSetChanged();
		}else if(position == -1){
			defaultSelection = position;
			notifyDataSetChanged();
		}
	}

	class AppItem {
		ImageView mAppIcon;
		TextView mAppName;
	}

	class AppMapItem {
		ImageView anim;
		ImageView mAppIcon;
		TextView mAppName;
	}

	class AppRadioItem {
		ImageView anim;
		ImageView mAppIcon;
		TextView mAppName;
	}

	class AppMusicItem {
		ImageView anim;
		ImageView mAppIcon;
		TextView mAppName;
	}

	class AppVideoItem {
		ImageView anim;
		ImageView mAppIcon;
		TextView mAppName;
	}

	class AppDvrItem {
		ImageView anim;
		ImageView mAppIcon;
		TextView mAppName;
	}

	class AppSettingItem {
		ImageView anim;
		ImageView mAppIcon;
		TextView mAppName;
	}

	class AppBtPhoneItem {
		ImageView anim;
		ImageView mAppIcon;
		TextView mAppName;
	}

	class AppBtMusicItem {
		ImageView anim;
		ImageView mAppIcon;
		TextView mAppName;
	}

	class AppCarlifeItem {
		ImageView anim;
		ImageView mAppIcon;
		TextView mAppName;
	}

	/**
	 * FM开始播放
	 */
	public void FMstartAnimation() {
		if (fmanimatio != null && !fmanimatio.isRunning()) {
			fmanimatio.start();
		}
	}
	/**
	 * FM停止播放
	 */
	public void FMstopAnimation() {
		if (fmanimatio != null && fmanimatio.isRunning()) {
			fmanimatio.stop();
		}
	}
	/**
	 * Music开始播放
	 */
	public void MusicstartAnimation() {
		if (musicanimation != null && !musicanimation.isRunning()) {
			musicanimation.start();
		}
	}
	/**
	 * Music停止播放
	 */
	public void MusicstopAnimation() {
		if (musicanimation != null && musicanimation.isRunning()) {
			musicanimation.stop();
		}
	}
	
	/**
	 * BTMusic开始播放
	 */
	public void BTMusicstartAnimation() {
		if (btmusicanimation != null && !btmusicanimation.isRunning()) {
			btmusicanimation.start();
		}
	}
	/**
	 * BtMusic停止播放
	 */
	public void BtMusicstopAnimation() {
		if (btmusicanimation != null && btmusicanimation.isRunning()) {
			btmusicanimation.stop();
		}
	}
	
	

	public void setListData(List<Map<String, Object>> list) {
		mList = list;
	}

	public List<Map<String, Object>> getListData() {
		return mList;
	}

	public ArrayList<ApplicationInfo> getAppsData() {
		return mallApps;
	}

	public void removeByIndex(int index) {
		if (mallApps != null && index < mallApps.size()) {
			mallApps.remove(index);
		}
	}

	public void insertByIndex(ApplicationInfo appinfo, int index) {
		if (mallApps != null && index <= mallApps.size()) {
			ApplicationInfo info = appinfo;
			mallApps.add(index, info);
		}
	}

	public void insertTail(ApplicationInfo appinfo) {
		mallApps.add(appinfo);
	}

	public void insertHead(ApplicationInfo appinfo) {
		mallApps.add(0, appinfo);
	}

	/*
	 * 更新GridView单条Item UI
	 */
	public void updateItemUI(int position) {
		if (mList == null) {
			return;
		}

		if (position < 0 || position >= mList.size()) {
			Log.e(TAG, "updateItemData(" + position + ") is invalid data!!! mList.size()=" + mList.size());
			return;
		}

		Message msg = Message.obtain();
		msg.arg1 = position;

		// handler刷新界面
		mHandler.sendMessage(msg);
	}

	/*
	 * 从更新过的数据集中获取新数据，更新viwe中的数据（handler中操作，实现界面的刷新）
	 */
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			// updateItem(msg.arg1);

			switch (msg.what) {
			case MSG_TIME:
				if (mTextTime != null) {

				}

				// if(Utils.getRecordTime(mContext) <= 0){
				// notifyDataSetChanged();
				// }
				break;
			}
		};
	};

	/**
	 * 根据数据源的position返回需要显示的的layout的type
	 * 
	 * type的值必须从0开始
	 */
	@Override
	public int getItemViewType(int position) {
		if (mList == null) {
			return 0;
		}

		Map<String, Object> map = mList.get(position);
		int type = (Integer) map.get(LctConst.kAppItemType);
		Log.d(TAG, "getItemViewType(" + position + ") type=" + type);
		return type;
	}

	/**
	 * 返回所有的layout的数量
	 */
	@Override
	public int getViewTypeCount() {
		return LctConst.APP_ITEM_TYPE_NUM_MAX;
	}

}

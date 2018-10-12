package com.tricheer.launcherk218;




import com.tricheer.launcher218.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;

public class LogActivity extends Activity {
	private String TAG = "LogActivity";
	private boolean isopne = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		int waring = Settings.Global.getInt(this.getContentResolver(), "open_waring", 0);
		if (waring == 1) {
			Intent intent = new Intent(LogActivity.this, ActivityMain.class);
			startActivity(intent);
		}
		setContentView(R.layout.activity_log);
		initView();
		initBroadcast();
	}

	private void initBroadcast() {
		// TODO Auto-generated method stub
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.set.theme");
		registerReceiver(mMyReceiver, filter);
	}

	public void initView() {
		Button bt_ok = (Button) findViewById(R.id.bt_ok);
		bt_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(LogActivity.this, ActivityMain.class);
				startActivity(intent);
			}
		});
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		// super.onBackPressed();
	}

	BroadcastReceiver mMyReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if ("com.set.theme".equals(action)) {
				Log.e(TAG, "finish");
				Intent intents = new Intent(LogActivity.this, ActivityMain.class);
				startActivity(intents);
			}
		};
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mMyReceiver);
	};
}

package com.tricheer.launcherk218.utility;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.tricheer.launcher218.R;

public class WindowsView {
	//ybf2018/9/4
	private WindowManager.LayoutParams wmParams;
	private WindowManager mWindowManager;
	private FrameLayout mSimpleNavi;
	private Context mContext;
	
	private void createFloatView(Context mContext,String tvname){
		wmParams = new WindowManager.LayoutParams();
		mWindowManager = (WindowManager)mContext.getSystemService(mContext.WINDOW_SERVICE);
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

        
        LayoutInflater inflater = LayoutInflater.from(mContext);

        mSimpleNavi = (FrameLayout) inflater.inflate(R.layout.warming, null);
       ImageView am =  (ImageView) mSimpleNavi.findViewById(R.id.img_am);
       AnimationDrawable ams =  (AnimationDrawable) ((ImageView) am).getDrawable();
      TextView tv =(TextView) mSimpleNavi.findViewById(R.id.message);
      tv.setText(tvname);
       
        mWindowManager.addView(mSimpleNavi, wmParams);
        
      

	}
}

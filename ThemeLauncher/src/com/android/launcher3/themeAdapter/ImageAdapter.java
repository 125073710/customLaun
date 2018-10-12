package com.android.launcher3.themeAdapter;

import java.util.List;
import java.util.Map;

import com.android.launcher3.R;



import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

/**
* @ClassName: ImageAdapter
* @Description: gridAdapter
* @author yangbofeng
* @date 2017-3-8
* @see ImageAdapter
*/
public class ImageAdapter extends BaseAdapter {
	
	private String TAG = "ImageAdapter";
	private Context mContext;
	private List<String> mThemesNames = null;
	private Map<String, Bitmap> mMapImgs;
	
	private LayoutInflater mInflater;
	public ImageAdapter(Context context,List<String> mThemesNames,Map<String, Bitmap> mMapImgs) {
		this.mContext = context;
		this.mThemesNames = mThemesNames;
		this.mMapImgs=mMapImgs;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		Log.e(TAG, "mThemesNames.size() -- " + mThemesNames.size()+"");
		Log.e(TAG, mThemesNames.size()+"");
		return mThemesNames.size()+1;
	}
	
	public void refreshDatas() {
		notifyDataSetChanged();
	}

	@Override
	public Object getItem(int position) {//默认里面有一个图片
		if (position == mThemesNames.size())
			return BitmapFactory.decodeResource(mContext.getResources(), R.drawable.bj);
		else
			return mThemesNames.get(position);
	}

	@Override
	public long getItemId(int position) {
		//Log.e(TAG,"position...>"+position);
		return 0;
		
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//ImageView imageView;
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.theme_grid_item, parent, false);
			holder = new ViewHolder();
			holder.imageview = (ImageView) convertView.findViewById(R.id.image);
			convertView.setTag(holder);
			
		} else {
			//imageView = (ImageView) convertView;
			holder = (ViewHolder) convertView.getTag();
		}
		if (position == mThemesNames.size()){
			holder.imageview.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.bj));
		}else{
			loadSquare(mThemesNames.get(position), holder.imageview);
			}
		return convertView;
	}
	private final class ViewHolder {
		public ImageView imageview;
	}

	private void loadSquare(String path, ImageView iv) {
		iv.setImageBitmap(mMapImgs.get(path));
	}

}

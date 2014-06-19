package com.example.homeservice;

import java.util.ArrayList;

import com.baidu.mapapi.search.MKPoiInfo;
import com.example.homeservice.http.modle.Poi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PoiListAdapter extends BaseAdapter {
	private ArrayList<MKPoiInfo> mPoiInfos;
	private LayoutInflater mInflater;

	public PoiListAdapter(Context context, ArrayList<MKPoiInfo> poiInfos) {
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mPoiInfos = poiInfos;

	}

	@Override
	public int getCount() {
		if (mPoiInfos == null) {
			return 0;
		}
		return mPoiInfos.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.poi_list_row, null);
			viewHolder = new ViewHolder();
			viewHolder.nameView = (TextView) convertView
					.findViewById(R.id.poi_name_tv);
			viewHolder.phoneView = (TextView) convertView
					.findViewById(R.id.poi_phone_tv);
			viewHolder.indexView = (TextView) convertView
					.findViewById(R.id.poi_index_tv);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		MKPoiInfo info = mPoiInfos.get(position);
		if (info != null) {
			viewHolder.indexView.setText(position + 1 + "");
			viewHolder.nameView.setText(info.name);
			viewHolder.phoneView.setText(info.phoneNum);
		}
		return convertView;
	}

	private class ViewHolder {
		TextView indexView;
		TextView nameView;
		TextView phoneView;
	}
}

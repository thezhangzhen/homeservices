package com.example.homeservice;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

public class LocManage {
	public final static String LOG_TAG = "locationMap";

	public interface GotLocCallBack {
		public void onGotLoc(double lat, double lng);
	}

	private GotLocCallBack mGotLocCallBack;

	public LocationClient mLocationClient = null;
	public MyLocationListener myListener = new MyLocationListener();
	private int getLocationCount = 0;

	public LocManage(Context context) {
		mLocationClient = new LocationClient(context); // 声明LocationClient类
		mLocationClient.setAK(Config.BD_KEY);
		mLocationClient.registerLocationListener(myListener); // 注册监听函数
	}

	public void getLocation(GotLocCallBack gotLocCallBack) {
		mGotLocCallBack = gotLocCallBack;
		// 定位
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(false);
		option.setPriority(LocationClientOption.NetWorkFirst);
		option.setAddrType("all");// 返回的定位结果包含地址信息
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值bd09ll
		option.setScanSpan(1000);// 设置发起定位请求的间隔时间为5000ms
		option.disableCache(false);// 禁止启用缓存定位
		option.setPoiNumber(5); // 最多返回POI个数
		option.setPoiDistance(1000); // poi查询距离
		option.setPoiExtraInfo(true); // 是否需要POI的电话和地址等详细信息
		option.setProdName("wsoh");
		mLocationClient.setLocOption(option);
		mLocationClient.start();
		if (mLocationClient.isStarted()) {
			getLocationCount = 0;
			mLocationClient.requestLocation();
			Log.i(LOG_TAG, "start get location");
		}else {
			Log.i(LOG_TAG, "LocationClient not start");
			
		}
	}

	public class MyLocationListener implements BDLocationListener {
		@SuppressLint("NewApi")
		@Override
		public void onReceiveLocation(BDLocation location) {
			Log.i(LOG_TAG, "receive location");
			if (location == null)
				return;
			String addr = location.getAddrStr();
			if (addr != null) {
				// String lat =
				// Double.valueOf(location.getLatitude()).toString();
				// String lng =
				// Double.valueOf(location.getLongitude()).toString();
				double lat = location.getLatitude();
				double lng = location.getLongitude();
				mLocationClient.stop();
				if (mGotLocCallBack != null) {
					mGotLocCallBack.onGotLoc(lat, lng);
				}
				Log.i(LOG_TAG, "receive valide location: " + lat + " ... "
						+ lng);
			} else {
				getLocationCount++;
			}
			if (getLocationCount > 10) {
				mLocationClient.stop();
				Log.i(LOG_TAG, "receive location 10 times, failed.");
			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null) {
				return;
			}
			StringBuffer sb = new StringBuffer(256);
			sb.append("Poi time : ");
			sb.append(poiLocation.getTime());
			sb.append("\nerror code : ");
			sb.append(poiLocation.getLocType());
			sb.append("\nlatitude : ");
			sb.append(poiLocation.getLatitude());
			sb.append("\nlontitude : ");
			sb.append(poiLocation.getLongitude());
			sb.append("\nradius : ");
			sb.append(poiLocation.getRadius());
			if (poiLocation.getLocType() == BDLocation.TypeNetWorkLocation) {
				sb.append("\naddr : ");
				sb.append(poiLocation.getAddrStr());
			}
			if (poiLocation.hasPoi()) {
				sb.append("\nPoi:");
				sb.append(poiLocation.getPoi());
			} else {
				sb.append("noPoi information");
			}
			Log.i(LOG_TAG, "receive poi information: " + sb.toString());
		}
	}

}

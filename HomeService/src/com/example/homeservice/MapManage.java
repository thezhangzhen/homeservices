package com.example.homeservice;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.PoiOverlay;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiInfo;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.baidu.platform.comapi.map.Projection;
import com.example.homeservice.LocManage.GotLocCallBack;

public class MapManage {
	// mail: iwantate@163.com / a123456
	// baidu: iwantate@163.com / a123456

	public interface OnMapClick {
		public void onClick(GeoPoint point);
	}

	private OnMapClick mOnMapClick = null;

	private GetPoiCallback mGetPoiCallback;

	public interface GetPoiCallback {
		public void gotPoi(MKPoiResult result, int type, int error);

		public void gotPoiDetail(int type, int error);
	}

	private GetAddressCallback mGetAddressCallback;

	public interface GetAddressCallback {
		public void gotAddress(String addr);
	}

	private final static int SEARCH_SCOPE = 1000;
	public final static String LOG_TAG = "locationMap";
	// public final String BD_MAP_KEY =
	// "D1D3FC00CB8A016321901D454C9022CAA451BB60";
	private Activity mActivity;
	private BMapManager mBMapMan = null;
	MapController mMapController;
	private Context mContext;
	private MKSearch mMkSearch = null;
	private MapView mMapView;
	private MyLocationOverlay myLocationOverlay;
	private PoiOverlay poiOverlay;

	// private PopupOverlay addrPopupOverlay;

	// private MyLocationOverlay myLocationOverlay;
	public MapManage(Context context) {
		mContext = context;
		// mActivity = activity;
		mBMapMan = new BMapManager(mContext);
		mBMapMan.init(Config.BD_KEY, null);
		mMkSearch = new MKSearch();
		mMkSearch.init(mBMapMan, new MySearchListener());
	}

	public void initMap(Activity activity, MapView mapView) {
		mActivity = activity;
		mMapView = mapView;
		mMapView.regMapViewListener(mBMapMan, myMapViewListener);
		mapView.setBuiltInZoomControls(true);

		// 设置启用内置的缩放控件
		mMapController = mapView.getController();
		mMapController.setZoom(16);// 设置地图zoom级别

		// 得到mMapView的控制权,可以用它控制和驱动平移和缩放
		// GeoPoint point = new GeoPoint((int) (lat * 1E6), (int) (lng * 1E6));
		// 用给定的经纬度构造一个GeoPoint，单位是微度 (度 * 1E6)
		// mMapController.setCenter(point);// 设置地图中心点

		mMapView.setOnTouchListener(clickListener);
	}

	public void setOnMapClick(OnMapClick onMapClick) {
		this.mOnMapClick = onMapClick;
	}

	/**
	 * 设置地图点击事件监听
	 */
	private OnTouchListener clickListener = new OnTouchListener() {
		private GeoPoint currentPt = null;
		// private long clickTime;
		private boolean isClick;

		private float clickX = 0f, clickY = 0f;

		@Override
		public boolean onTouch(View view, MotionEvent event) {
			/**
			 * Projection 用于将屏幕坐标转换为地理位置坐标
			 */
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				clickX = event.getX();
				clickY = event.getY();
				// clickTime = Calendar.getInstance().getTimeInMillis();
				isClick = true;
			}
			if (event.getAction() == MotionEvent.ACTION_UP) {
				// if ((Calendar.getInstance().getTimeInMillis() - clickTime) >
				// 200) {
				// // 小于200毫米则不考虑是否移动
				// isClick = true;
				// }
				if ((Math.abs(clickX - event.getX()) + Math.abs(clickY
						- event.getY())) > 20) {
					return false;
				}
				if (isClick) {
					if (mOnMapClick != null) {
						currentPt = mMapView.getProjection().fromPixels(
								(int) clickX, (int) clickY);
						mOnMapClick.onClick(currentPt);
					}
				}
			}
			/**
			 * 传递点击事件给MapView,sdk会自己处理缩放，平移等手势操作
			 */
			return false;
		}
	};

	// 显示移动后中心的弹出层，进行确认
	// protected void showMovedCenter(String addr) {
	// if (addrPopupOverlay != null) {
	// mMapView.getOverlays().remove(addrPopupOverlay);
	// }
	//
	// final GeoPoint centerPoint = getMapCenterGeoPoint();
	//
	// addrPopupOverlay = new PopupOverlay(mMapView, null);
	// LayoutInflater inflater = (LayoutInflater) mContext
	// .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	// LinearLayout popupLayout = (LinearLayout) inflater.inflate(
	// R.layout.map_popup, null);
	// final TextView addressTextView = (TextView) popupLayout
	// .findViewById(R.id.view_map_popup_address_textview_tv);
	// popupLayout.findViewById(R.id.confirm_select_position_bt)
	// .setOnClickListener(new OnClickListener() {
	// @Override
	// public void onClick(View v) {
	// searchPoi(centerPoint);
	// addrPopupOverlay.hidePop();
	// }
	// });
	// addressTextView.setText(addr);
	// addrPopupOverlay.showPopup(popupLayout, centerPoint, 32);
	// mMapView.refresh();
	// }

	protected void displaySelectPoint(double lat, double lng) {

	}

	public void displayLocOnMap(double lat, double lng) {
		if (myLocationOverlay != null) {
			mMapView.getOverlays().remove(myLocationOverlay);
		}
		Log.i(LOG_TAG, "show local lat=" + lat + " lng=" + lng);
		myLocationOverlay = new MyLocationOverlay(mMapView);
		LocationData locData = new LocationData();
		locData.latitude = lat;
		locData.longitude = lng;
		myLocationOverlay.setData(locData);
		// myLocationOverlay.setMarker(mContext.getResources().getDrawable(
		// R.drawable.ic_launcher));
		mMapView.getOverlays().add(myLocationOverlay);
		mMapView.refresh();
		mMapView.getController().animateTo(
				new GeoPoint((int) (locData.latitude * 1e6),
						(int) (locData.longitude * 1e6)));
	}

	public void searchPoi(double lat, double lng, String key,
			GetPoiCallback gotPoiCallback) {
		GeoPoint geoPoint = new GeoPoint((int) (lat * 1E6), (int) (lng * 1E6));
		Log.i(LOG_TAG, "start search poi lat=" + lat + " lng=" + lng);
		searchPoi(geoPoint, key, gotPoiCallback);
	}

	public void searchPoi(GeoPoint geoPoint, String key,
			GetPoiCallback gotPoiCallback) {
		mGetPoiCallback = gotPoiCallback;
		mMkSearch.poiSearchNearBy(key, geoPoint, SEARCH_SCOPE);
	}

	public void searchAddress(GeoPoint geoPoint,
			GetAddressCallback getAddressCallback) {
		Log.i(LOG_TAG, "start address ing");
		mGetAddressCallback = getAddressCallback;
		mMkSearch.reverseGeocode(geoPoint);
	}

	public void showPoiOverlay(Activity activity, ArrayList<MKPoiInfo> mkpois) {
		Log.i(LOG_TAG, "start show pois");
		if (mkpois == null) {
			return;
		}
		if (poiOverlay != null) {
			mMapView.getOverlays().remove(poiOverlay);
		}
		// 将poi结果显示到地图上
		poiOverlay = new PoiOverlay(activity, mMapView);
		poiOverlay.setData(mkpois);
		mMapView.getOverlays().add(poiOverlay);
		mMapView.refresh();
	}

	public void start() {
		if (mBMapMan != null) {
			mBMapMan.start();
		}
	}

	public void stop() {
		if (mBMapMan != null) {
			mBMapMan.stop();
		}
	}

	public void destroy() {
		if (mBMapMan != null) {
			mBMapMan.destroy();
			mBMapMan = null;
		}
	}

	private class MySearchListener implements MKSearchListener {

		@Override
		public void onGetAddrResult(MKAddrInfo addrInfo, int e) {
			Log.i(LOG_TAG, "got address");
			if (mGetAddressCallback != null) {
				mGetAddressCallback.gotAddress(addrInfo.strAddr);
			}
		}

		@Override
		public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {

		}

		@Override
		public void onGetDrivingRouteResult(MKDrivingRouteResult arg0, int arg1) {

		}

		@Override
		public void onGetPoiDetailSearchResult(int type, int error) {
			if (mGetPoiCallback != null) {
				mGetPoiCallback.gotPoiDetail(type, error);
			}

		}

		@Override
		public void onGetPoiResult(MKPoiResult result, int type, int error) {
			Log.i(LOG_TAG, "got poi results, type=" + type + " error=" + error);
			if (mGetPoiCallback != null) {
				mGetPoiCallback.gotPoi(result, type, error);
			}
			showPoiOverlay(mActivity, result.getAllPoi());
		}

		@Override
		public void onGetShareUrlResult(MKShareUrlResult arg0, int arg1,
				int arg2) {

		}

		@Override
		public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {

		}

		@Override
		public void onGetTransitRouteResult(MKTransitRouteResult arg0, int arg1) {

		}

		@Override
		public void onGetWalkingRouteResult(MKWalkingRouteResult arg0, int arg1) {

		}
	}

	private MKMapViewListener myMapViewListener = new MKMapViewListener() {
		@Override
		public void onMapMoveFinish() {
			// searchAddress(getMapCenterGeoPoint(),null);
		}

		@Override
		public void onMapLoadFinish() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onMapAnimationFinish() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onGetCurrentMap(Bitmap arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onClickMapPoi(MapPoi arg0) {
			// TODO Auto-generated method stub

		}

		// private void mapMoved() {
		// searchAddress(getMapCenterGeoPoint());
		// }
	};

	private GeoPoint getMapCenterGeoPoint() {
		Point centerPoint = mMapView.getCenterPixel();
		Projection projection = mMapView.getProjection();
		GeoPoint geoPoint = projection.fromPixels(centerPoint.x, centerPoint.y);
		return geoPoint;
	}
}

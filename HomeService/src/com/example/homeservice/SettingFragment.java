package com.example.homeservice;

import java.util.ArrayList;

import android.R.integer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.search.MKPoiInfo;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.example.homeservice.LocManage.GotLocCallBack;
import com.example.homeservice.MapManage.GetAddressCallback;
import com.example.homeservice.MapManage.GetPoiCallback;
import com.example.homeservice.MapManage.OnMapClick;
import com.example.homeservice.base.BaseFragment;
import com.example.homeservice.base.MyApplication;
import com.example.homeservice.http.HttpProtocolService;
import com.example.homeservice.http.ProtocolType;
import com.example.homeservice.http.RequestResult;
import com.example.homeservice.http.modle.BaseResult;
import com.example.homeservice.http.modle.Poi;
import com.example.homeservice.util.StringUtil;
import com.example.homeservice.util.Util;

public class SettingFragment extends BaseFragment {

	public SettingFragment() {
	}

	private View rootView;

	private MapManage mMapManage;
	private MapView mapView;
	private TextView latView, lngView, addTextView;

	private EditText nameEditText, addEditText, phoneEditText, mailEditText,
			qqEditText, desEditText;

	private Spinner typeSpinner;
	private String[] typeStrings;

	private ListView poiListView;
	private PoiListAdapter poiListAdapter;
	private ArrayList<MKPoiInfo> mPoiInfos;

	private GeoPoint clickPoint;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_setting, null);
		initLayout();
		initViews();
		initPoiList();
		return rootView;
	}

	private void initPoiList() {
		poiListView = (ListView) rootView.findViewById(R.id.poi_lv);
		mPoiInfos = new ArrayList<MKPoiInfo>();
		
		poiListAdapter = new PoiListAdapter(this.getActivity(), mPoiInfos);
		poiListView.setAdapter(poiListAdapter);
		poiListView.setOnItemClickListener(poiItemClick);
	}

	private void updatePoiList(ArrayList<MKPoiInfo> newList) {
		mPoiInfos.clear();
		mPoiInfos.addAll(newList);
		poiListAdapter.notifyDataSetChanged();
	}

	private void initViews() {
		latView = (TextView) rootView.findViewById(R.id.lat_value_tv);
		lngView = (TextView) rootView.findViewById(R.id.lng_value_tv);
		addTextView = (TextView) rootView
				.findViewById(R.id.address_ro_value_tv);

		nameEditText = (EditText) rootView.findViewById(R.id.name_et);
		addEditText = (EditText) rootView.findViewById(R.id.address_w_et);
		phoneEditText = (EditText) rootView.findViewById(R.id.phone_et);
		mailEditText = (EditText) rootView.findViewById(R.id.mail_et);
		qqEditText = (EditText) rootView.findViewById(R.id.qq_et);
		desEditText = (EditText) rootView.findViewById(R.id.des_et);

		typeSpinner = (Spinner) rootView.findViewById(R.id.type_sp);
		typeStrings = getResources().getStringArray(R.array.typeArray);
		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(
				this.getActivity(), android.R.layout.simple_spinner_item,
				typeStrings);
		typeSpinner.setAdapter(spinnerAdapter);
		typeSpinner.setSelection(0);
	}

	private void initLayout() {
		mapView = (MapView) rootView.findViewById(R.id.map_mv);
		mMapManage = MyApplication.getMapManage();
		mMapManage.initMap(this.getActivity(), mapView);
		mMapManage.setOnMapClick(onMapClick);
		MyApplication.getLocManage().getLocation(gotLocCallBack);

		rootView.findViewById(R.id.save_bt).setOnClickListener(saveBtClick);
	}

	private OnMapClick onMapClick = new OnMapClick() {
		@Override
		public void onClick(GeoPoint point) {
			latView.setText(point.getLatitudeE6() / 1e6f + "");
			lngView.setText(point.getLongitudeE6() / 1e6f + "");
			clickPoint = point;
			mMapManage.searchAddress(clickPoint, getAddressCallback);
		}
	};

	protected void searchPois(GeoPoint point) {
		String key = getSelectType();
		if (StringUtil.isNotBlank(key)) {
			mMapManage.searchPoi(point, key, getPoiCallback);
		}
	}

	private String getSelectType() {
		int position = typeSpinner.getSelectedItemPosition();
		return typeStrings[position];
	}

	private GetAddressCallback getAddressCallback = new GetAddressCallback() {
		@Override
		public void gotAddress(String addr) {
			addTextView.setText(addr);
			addEditText.setText(addr);
			nameEditText.setText("");
			phoneEditText.setText("");
			searchPois(clickPoint);
		}
	};

	private GetPoiCallback getPoiCallback = new GetPoiCallback() {

		@Override
		public void gotPoiDetail(int type, int error) {

		}

		@Override
		public void gotPoi(MKPoiResult result, int type, int error) {
			if (result != null) {
				updatePoiList(result.getAllPoi());
			}
		}
	};

	private OnClickListener saveBtClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			atampSavePoi();
		}
	};

	private GotLocCallBack gotLocCallBack = new GotLocCallBack() {
		@Override
		public void onGotLoc(double lat, double lng) {
			MyApplication.getMapManage().displayLocOnMap(lat, lng);
		}
	};

	private OnItemClickListener poiItemClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position,
				long id) {
			MKPoiInfo info = mPoiInfos.get(position);
			setValueFromPoi(info);
		}
	};

	protected void setValueFromPoi(MKPoiInfo info) {
		nameEditText.setText(info.name);
		phoneEditText.setText(info.phoneNum);
	}

	protected void atampSavePoi() {
		String lat = latView.getText().toString();
		String lng = lngView.getText().toString();

		int type = typeSpinner.getSelectedItemPosition() + 1;

		String name = nameEditText.getText().toString();
		String addr = addEditText.getText().toString();
		String phone = phoneEditText.getText().toString();
		String mail = mailEditText.getText().toString();
		String qq = qqEditText.getText().toString();
		String des = desEditText.getText().toString();

		if (StringUtil.isBlank(lat)) {
			latView.setError(getString(R.string.null_error));
			return;
		}
		if (StringUtil.isBlank(lng)) {
			lngView.setError(getString(R.string.null_error));
			return;
		}
		if (StringUtil.isBlank(name)) {
			nameEditText.setError(getString(R.string.null_error));
			return;
		}
		if (StringUtil.isBlank(addr)) {
			addEditText.setError(getString(R.string.null_error));
			return;
		}

		Poi poi = new Poi();
		poi.setAddr(addr);
		poi.setName(name);
		poi.setDes(des);
		poi.setPhone(phone);
		poi.setLat(Float.parseFloat(lat));
		poi.setLng(Float.parseFloat(lng));
		poi.setMail(mail);
		poi.setQq(qq);
		poi.setType(type);

		HttpProtocolService.sendProtocol(
				Config.SERVER_URL + Config.ADD_SERVICE, poi, Config.GET,
				ProtocolType.BASE, addServiceResult);
	}

	private RequestResult addServiceResult = new RequestResult() {
		public void onSuccess(Object o) {
			Util.sendToast(R.string.save_success);
			clearViewValues();
		};

		public void onFailure(CharSequence failure) {
			Util.sendToast(failure);
		};

		public void OnErrorResult(CharSequence error) {
			Util.sendToast(R.string.save_failed);
		};
	};

	@Override
	public void onDestroyView() {
		mapView.destroy();
		if (mMapManage != null) {
			mMapManage.destroy();
		}
		super.onDestroy();
	}

	protected void clearViewValues() {
		nameEditText.setText("");
		phoneEditText.setText("");
	}

	@Override
	public void onPause() {
		mapView.onPause();
		if (mMapManage != null) {
			mMapManage.stop();
		}
		super.onPause();
	}

	@Override
	public void onResume() {
		mapView.onResume();
		if (mMapManage != null) {
			mMapManage.start();
		}
		super.onResume();
	}
}

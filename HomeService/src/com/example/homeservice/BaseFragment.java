package com.example.homeservice;

import java.util.ArrayList;
import java.util.List;

import com.baidu.mapapi.map.MapView;
import com.example.homeservice.LocManage.GotLocCallBack;
import com.example.homeservice.base.MyApplication;
import com.example.homeservice.http.HttpProtocolService;
import com.example.homeservice.http.ProtocolType;
import com.example.homeservice.http.RequestResult;
import com.example.homeservice.http.modle.GetServices;
import com.example.homeservice.http.modle.Poi;
import com.example.homeservice.http.modle.ServiceListResult;
import com.example.homeservice.util.BDUtil;
import com.example.homeservice.util.Util;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class BaseFragment extends Fragment {

	public final static String ARG_TYPE_STR = "typeStr";
	private View rootView;

	private MapManage mMapManage;
	private MapView mapView;
	private String typeStr;
	private ArrayList<Poi> poiInfos = new ArrayList<Poi>();
	private PoiListAdapter mAdapter;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_base, null);

		typeStr = getArguments().getString(ARG_TYPE_STR);
		initLayout();

		return rootView;
	};

	private void initLayout() {
		initMapView();
		initListView();
	}

	private void initListView() {
		ListView serviceListView = (ListView) rootView
				.findViewById(R.id.poi_lv);
		mAdapter = new PoiListAdapter(this.getActivity(), poiInfos);
		serviceListView.setAdapter(mAdapter);
		serviceListView.setOnItemClickListener(servicesItemClickListener);
	}

	private OnItemClickListener servicesItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> listView, View parent,
				int position, long id) {
		}
	};

	private void initMapView() {
		mapView = (MapView) rootView.findViewById(R.id.map_mv);
		mMapManage = MyApplication.getMapManage();
		mMapManage.initMap(this.getActivity(), mapView);
		MyApplication.getLocManage().getLocation(gotLocCallBack);
	}

	private GotLocCallBack gotLocCallBack = new GotLocCallBack() {
		@Override
		public void onGotLoc(double lat, double lng) {
			mMapManage.displayLocOnMap(lat, lng);
			getDataByLocal(lat, lng);
		}
	};

	protected void getDataByLocal(double lat, double lng) {
		GetServices getServices = new GetServices();
		getServices.setLat(lat);
		getServices.setLng(lng);
		getServices.setType(typeStr);

		HttpProtocolService.sendProtocol(
				Config.SERVER_URL + Config.GET_SERVICE, getServices,
				Config.GET, ProtocolType.BASE, servicesResult);
	}

	private RequestResult servicesResult = new RequestResult() {
		public void onSuccess(Object o) {
			ServiceListResult result = (ServiceListResult) o;
			if (result != null) {
				updateDatas(result.getData());
			}
		};

		public void onFailure(CharSequence failure) {
			Util.print(failure);
		};

		public void OnErrorResult(CharSequence error) {
			Util.print(R.string.get_services_failed);
		};
	};

	protected void updateDatas(List<Poi> newPois) {
		updateList(newPois);
		updateMapView(newPois);
	}

	private void updateMapView(List<Poi> newPois) {
		mMapManage.showPoiOverlay(this.getActivity(),
				BDUtil.convertPoiInfos(newPois));
	}

	private void updateList(List<Poi> newPois) {
		poiInfos.clear();
		poiInfos.addAll(newPois);
		mAdapter.notifyDataSetChanged();
	}

}

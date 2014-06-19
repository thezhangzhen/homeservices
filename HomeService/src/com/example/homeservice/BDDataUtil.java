package com.example.homeservice;

import java.util.ArrayList;

import com.baidu.mapapi.search.MKPoiInfo;
import com.example.homeservice.http.modle.Poi;

public class BDDataUtil {
	public static Poi mkPoiInfo2Poi(MKPoiInfo info) {
		if (info == null) {
			return null;
		}
		Poi poi = new Poi();
		poi.setName(info.name);
		poi.setAddr(info.address);
		poi.setPhone(info.phoneNum);
		return poi;
	}

	public static ArrayList<Poi> mkPoiInfos2Pois(ArrayList<MKPoiInfo> infos) {
		if (infos == null) {
			return null;
		}
		ArrayList<Poi> pois = new ArrayList<Poi>();
		for (MKPoiInfo mkPoiInfo : infos) {
			Poi poi = mkPoiInfo2Poi(mkPoiInfo);
			pois.add(poi);
		}
		return pois;
	}
}

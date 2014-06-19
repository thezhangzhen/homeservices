package com.example.homeservice.util;

import java.util.ArrayList;
import java.util.List;

import com.baidu.mapapi.search.MKPoiInfo;
import com.example.homeservice.http.modle.Poi;

public class BDUtil {
	public static ArrayList<MKPoiInfo> convertPoiInfos(List<Poi> pois) {
		ArrayList<MKPoiInfo> mkInfos = new ArrayList<MKPoiInfo>();
		for (Poi poi : pois) {
			MKPoiInfo mkInfo = convertPoiInfo(poi);
			mkInfos.add(mkInfo);
		}
		return mkInfos;
	}

	public static MKPoiInfo convertPoiInfo(Poi poi) {
		MKPoiInfo mkInfo = new MKPoiInfo();
		mkInfo.name = poi.getName();
		mkInfo.phoneNum = poi.getName();
		mkInfo.address = poi.getAddr();
		return mkInfo;
	}
}

package com.example.homeservice.http.modle;

import android.R.integer;

public class GetServices {
private double lat;
private double lng;
private String type;
private integer range;
public double getLat() {
	return lat;
}
public void setLat(double lat) {
	this.lat = lat;
}
public double getLng() {
	return lng;
}
public void setLng(double lng) {
	this.lng = lng;
}
public String getType() {
	return type;
}
public void setType(String type) {
	this.type = type;
}
public integer getRange() {
	return range;
}
public void setRange(integer range) {
	this.range = range;
}
}

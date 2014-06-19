package com.example.homeservice.http.modle;

import java.util.List;

public class ServiceListResult extends BaseResult {
	private List<Poi> data;

	public List<Poi> getData() {
		return data;
	}

	public void setData(List<Poi> data) {
		this.data = data;
	}
}

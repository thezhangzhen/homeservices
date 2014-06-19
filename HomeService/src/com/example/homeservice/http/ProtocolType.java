package com.example.homeservice.http;

import com.example.homeservice.http.modle.BaseResult;

public enum ProtocolType {
	BASE(BaseResult.class);

	private Class<?> cls;

	ProtocolType(Class<?> cls) {
		this.cls = cls;

	}

	public Class<?> getCls() {
		return cls;
	}

	public static ProtocolType getFrom(String cls) {
		for (ProtocolType type : ProtocolType.values()) {
			if (type.name() == cls)
				return type;
		}
		return null;
	}
}

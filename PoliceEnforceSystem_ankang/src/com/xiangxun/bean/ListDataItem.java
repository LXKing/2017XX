package com.xiangxun.bean;

public class ListDataItem {
	private int index;
	private String id;
	private String Capture;

	ListDataItem(int nindex, String sid, String sCapture) {
		index = nindex;
		id = sid;
		Capture = sCapture;
	};

	public String getid() {
		return id;
	};

	public int getindex() {
		return index;
	}

	public String getCapture() {
		return Capture;
	}

	@Override
	public String toString() {
		return Capture;
	}
}

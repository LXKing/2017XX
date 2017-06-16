package com.xiangxun.bean;

import java.util.HashMap;

public class PlateMap {
	static HashMap<String, String> map = new HashMap<String, String>();

	static {
		map.put("0", "27");
		map.put("1", "02");
		map.put("2", "27");
		map.put("3", "01");
		map.put("4", "27");
		map.put("5", "27");
		map.put("6", "27");
		map.put("7", "27");
		map.put("8", "27");
		map.put("9", "27");
		map.put("10", "27");
		map.put("11", "27");
		map.put("12", "27");
		map.put("13", "27");
		map.put("14", "27");
	}

	public static String getString(String key) {
		return map.get(key);
	}
}

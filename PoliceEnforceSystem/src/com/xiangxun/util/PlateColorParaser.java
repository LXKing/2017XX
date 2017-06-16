package com.xiangxun.util;

public class PlateColorParaser {

	public static int parasePlateColor(String color) {

		if (color.equals("白色")) {
			return 0;
		} else if(color.equals("黄色")){
			return 1;
		}else if(color.equals("蓝色")){
			return 2;
		}else if(color.equals("黑色")){
			return 3;
		}else if(color.equals("其他")){
			return 4;
		} else return 4;
	}
}

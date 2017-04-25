package com.xiangxun.util;

public class MessageTypeParaser {

	public static String paraseMessageType(int type){
		
		switch (type) {
		case 1:
			return "报警消息";
		case 2:
			return "勤务消息";
		case 3:
			return "事故消息";
		case 6:
			return "预警消息";
		case 99:
			return "其他消息";
		default:
			break;
		}
		return "其他消息";
	}
}

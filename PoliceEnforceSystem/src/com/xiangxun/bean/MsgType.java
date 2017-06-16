package com.xiangxun.bean;

public class MsgType {
	public static final String getMsgType(int id) {
		switch (id) {
		case 0:
			return "登录成功";
		case 1:
			return "登录失败";
		case 2:
			return "";
		default:
			break;
		}
		return "类型 " + id;
	}

	public static final String getMsgIsRead(int id) {
		switch (id) {
			case 0:
				return "未读";
			case 1:
				return "已读";

			default:
				return "未知";
		}
	}

	public static final String getMsgIsSign(int id) {
		switch (id) {
			case 0:
				return "未签";
			case 1:
				return "已签";

			default:
				return "未知";
		}
	}

	public static final String getMsgIsAck(int id) {
		switch (id) {
			case 0:
				return "未反馈";
			case 1:
				return "已反馈";
			default:
				return "未知";
		}
	}
}

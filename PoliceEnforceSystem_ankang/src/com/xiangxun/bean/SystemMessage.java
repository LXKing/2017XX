package com.xiangxun.bean;

import java.io.Serializable;

public class SystemMessage  implements Serializable {
	private static final long serialVersionUID = 1L;
	public int _id;
	public int msgtype;
	public String text;
	public int isread; 
	public String datetime;
	public int isSigned;
	public int isAck;
}

/**
 * 
 */
package com.xiangxun.bean;

import java.io.Serializable;
import java.util.List;

public class CarInfoList implements Serializable {
	private static final long serialVersionUID = -4010358353980446295L;
	public int count = -1;
	public int sumcount = -1;
	public int firstindex = -1;
	public List<CarInfo> list = null;
	public int type = -1;
	public String tagindex = null;
	public byte[] pic = null;
}

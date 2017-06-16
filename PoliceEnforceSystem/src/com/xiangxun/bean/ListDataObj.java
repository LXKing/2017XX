package com.xiangxun.bean;

import java.util.ArrayList;
import java.util.List;

public class ListDataObj {
	private static List<ListDataItem> listitem = null;

	public ListDataObj() {
		listitem = new ArrayList<ListDataItem>();
	}

	public void add(String id, String text) {
		ListDataItem listdata = new ListDataItem(listitem.size(), id, text);
		listitem.add(listdata);
	}

	public List<ListDataItem> getlist() {
		return listitem;
	}

	public void add(String[][] strlist) {
		for (int i = 0; i < strlist.length; i++) {
			add(strlist[i][0], strlist[i][1]);
		}
	}
}

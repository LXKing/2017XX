package com.xiangxun.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;

/**
 * @package: com.xiangxun.common
 * @ClassName: HuatekBusiness.java
 * @Description: activity 管理类
 * @author: HanGJ
 * @date: 2015-7-29 上午9:45:41
 */
@SuppressLint("UseSparseArrays")
public class ActivityBusiness {

	private static ActivityBusiness xwBusiness;
	private Stack<Activity> activities = new Stack<Activity>();
	private Map<Integer, Object> businessEntryMap = new HashMap<Integer, Object>();

	private ActivityBusiness() {
		super();
	}

	public static ActivityBusiness instance() {
		if (null == xwBusiness) {
			xwBusiness = new ActivityBusiness();
		}
		return xwBusiness;
	}

	public Object getData(int buisnessId) {
		return businessEntryMap.get(buisnessId);
	}

	public void addData(int buisnessId, Object data) {
		if (businessEntryMap.containsKey(buisnessId)) {
			businessEntryMap.remove(buisnessId);
		}
		businessEntryMap.put(buisnessId, data);
	}

	public void removeData(int buisnessId) {
		businessEntryMap.remove(buisnessId);
	}

	public void addBusiness(Activity activity) {
		Stack<Activity> temp = new Stack<Activity>();
		while (!activities.isEmpty()) {
			if (activity == activities.peek()) {
				activities.pop();
				break;
			} else {
				temp.push(activities.pop());
			}
		}
		while (!temp.isEmpty()) {
			activities.push(temp.pop());
		}
		activities.push(activity);
	}

	public void removeBusiness(Activity activity) {
		Stack<Activity> temp = new Stack<Activity>();
		while (!activities.isEmpty()) {
			if (activity == activities.peek()) {
				activities.pop();
				break;
			} else {
				temp.push(activities.pop());
			}
		}
		while (!temp.isEmpty()) {
			activities.push(temp.pop());
		}
	}

	/**
	 * 
	 * @param destinationAtivityClass
	 *            目标activity对应的class对象
	 * @param intent
	 *            需要携带参数的intent
	 * @param boolean destinationAtivityFinishIdNeed 目标activity如果存在，是否需要finish
	 */

	public void toBusiness(Class<? extends Activity> destinationAtivityClass, Intent intent, boolean destinationAtivityFinishIdNeed) {
		if (activities.isEmpty() || null == destinationAtivityClass) {
			return;
		}
		boolean hasActivity = false;
		for (Activity activity : activities) {
			if (activity.getClass().equals(destinationAtivityClass)) {
				hasActivity = true;
			}
		}
		Activity activity = activities.pop();
		if (hasActivity) {
			Activity activityTemp = null;
			while (!activities.isEmpty()) {
				activityTemp = activities.peek();
				if (activityTemp.getClass().equals(destinationAtivityClass)) {
					break;
				}
				activityTemp.finish();
				activities.pop();
			}
			if (destinationAtivityFinishIdNeed) {
				activityTemp.finish();
				activities.push(activity);
			} else {
				activities.pop();
				activities.push(activity);
				activities.push(activityTemp);
			}
		} else {
			activities.push(activity);
		}
		activity.startActivity(null == intent ? new Intent(activity, destinationAtivityClass) : intent);
	}

	public void backBusiness(Class<? extends Activity> destinationAtivityClass, boolean destinationAtivityClassNeedFinish) {
		if (activities.isEmpty() || null == destinationAtivityClass) {
			return;
		}
		boolean hasActivity = false;
		for (Activity activity : activities) {
			if (activity.getClass().equals(destinationAtivityClass)) {
				hasActivity = true;
			}
		}
		if (hasActivity) {
			Activity activityTemp = null;
			while (!activities.isEmpty()) {
				activityTemp = activities.peek();
				if (activityTemp.getClass().equals(destinationAtivityClass)) {
					if (destinationAtivityClassNeedFinish) {
						activityTemp.finish();
						activities.pop();
					}
					break;
				}
				activityTemp.finish();
				activities.pop();
			}
		}
	}

	/**
	 * 结束所有 Activity
	 */
	public void finishAllActivity() {
		for (int i = 0, size = activities.size(); i < size; i++) {
			if (null != activities.get(i)) {
				activities.get(i).finish();
			}
		}
		activities.clear();
	}

}

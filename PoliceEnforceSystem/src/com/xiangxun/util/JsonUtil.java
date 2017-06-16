package com.xiangxun.util;



import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xiangxun.bean.FORMAT;
import com.xiangxun.bean.FORMAT.DATETIME;


/*
 * @author zhouhaijian
 * @version 1.0
 */
public abstract class JsonUtil {

	private static GsonBuilder builder = new GsonBuilder();   

	public static Gson getGsonInstance(){
		Gson gson = builder.setDateFormat(FORMAT.DATETIME.DEFAULT).create();   
		return gson;
	}
	/**  
	 * 
     * 把数据对象转换成json字符串  
     * DTO对象形如：{"id" : idValue, "name" : nameValue, ...}  
     * 数组对象形如：[{}, {}, {}, ...]  
     * map对象形如：{key1 : {"id" : idValue, "name" : nameValue, ...}, key2 : {}, ...}  
     * @param object  
     * @return  
     */  
    public static String toJson(Object object) throws Exception{   
    	return getGsonInstance().toJson(object);
    }
    /**  
     * 从json字符反转为bean
     * @param json  
     * @return  
     */  
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static Object toBean(String json,Class cls) {   
    	return getGsonInstance().fromJson(json, cls);
    }   
}



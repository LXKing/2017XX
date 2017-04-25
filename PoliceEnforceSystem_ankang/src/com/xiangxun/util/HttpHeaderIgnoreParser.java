package com.xiangxun.util;

import java.util.Map;

import com.xiangxun.volley.Cache;
import com.xiangxun.volley.NetworkResponse;
import com.xiangxun.volley.toolbox.HttpHeaderParser;

/**
 * 
 * @ClassName: HttpHeaderIgnoreParser.java
 * @Description: 始终缓存
 * @date: 2015年01月15日 上午11:59:34
 */
public class HttpHeaderIgnoreParser extends HttpHeaderParser {

	public static Cache.Entry parseIgnoreCacheHeaders(NetworkResponse response) {
		long now = System.currentTimeMillis();

		Map<String, String> headers = response.headers;
		long serverDate = 0;
		String serverEtag = null;
		String headerValue;

		headerValue = headers.get("Date");
		if (headerValue != null) {
			serverDate = parseDateAsEpoch(headerValue);
		}

		serverEtag = headers.get("ETag");

		final long cacheHitButRefreshed = 1000; // in 3 minutes cache
		// will be hit, but
		// also refreshed on
		// background
		final long cacheExpired = 24 * 60 * 60 * 1000 * 30; // in 24 hours this
															// cache
		// entry expires
		// completely
		final long softExpire = now + cacheHitButRefreshed;
		final long ttl = now + cacheExpired;

		Cache.Entry entry = new Cache.Entry();
		entry.data = response.data;
		entry.etag = serverEtag;
		entry.softTtl = softExpire;
		entry.ttl = ttl;
		entry.serverDate = serverDate;
		entry.responseHeaders = headers;

		return entry;
	}

}

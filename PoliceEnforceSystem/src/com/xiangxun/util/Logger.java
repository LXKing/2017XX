package com.xiangxun.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.xiangxun.request.Api;
import com.xiangxun.request.AppBuildConfig;

/**
 * @package: com.xiangxun.util
 * @ClassName: Logger.java
 * @Description: log日志
 * @author: HanGJ
 * @date: 2015-9-8 上午8:30:42
 */
public class Logger {
	private static boolean debug = AppBuildConfig.DEBUGLOGGER;
	public static String tag = "Logger";
	private final static int logLevel = Log.VERBOSE;
	private static Hashtable<String, Logger> logger = new Hashtable<String, Logger>();
	private static String name;
	private static Object selfClassName;

	private Logger(String name) {
		Logger.name = name;
		selfClassName = this.getClass().getName();
	}

	/**
	 * 
	 * @param className
	 * @return
	 */
	public static Logger getLogger(String name) {
		Logger classLogger = (Logger) logger.get(name);
		if (classLogger == null) {
			classLogger = new Logger(name);
			logger.put(name, classLogger);
		}
		return classLogger;
	}

	/**
	 * Get The Current Function Name
	 * 
	 * @return
	 */
	private static String getFunctionName() {
		StackTraceElement[] sts = Thread.currentThread().getStackTrace();
		if (sts == null) {
			return null;
		}
		for (StackTraceElement st : sts) {
			if (st.isNativeMethod()) {
				continue;
			}
			if (st.getClassName().equals(Thread.class.getName())) {
				continue;
			}
			if (st.getClassName().equals(selfClassName)) {
				continue;
			}
			return name + "[ " + Thread.currentThread().getName() + ": " + st.getFileName() + ":" + st.getLineNumber() + " " + st.getMethodName() + " ]";
		}
		return null;
	}

	public static boolean isDebug() {
		return debug;
	}

	/**
	 * The Log Level:i
	 * 
	 * @param str
	 */
	public static void i(Object str) {
		print(str.toString());
		if (debug) {
			if (logLevel <= Log.INFO) {
				String name = getFunctionName();
				if (name != null) {
					Log.i(tag, name + "\n" + str);
				} else {
					Log.i(tag, str.toString());
				}
			}
		}
	}

	public static void s(Object str) {
		print(str.toString());
		if (debug) {
			if (logLevel <= Log.INFO) {
				String name = getFunctionName();
				if (name != null) {
					System.out.println(name + "\n" + str);
				} else {
					System.out.println(str.toString());
				}
			}
		}

	}

	/**
	 * The Log Level:d
	 * 
	 * @param str
	 */
	public static void d(Object str) {
		print(str.toString());
		if (debug) {
			if (logLevel <= Log.DEBUG) {
				String name = getFunctionName();
				if (name != null) {
					Log.d(tag, name + "\n" + str);
				} else {
					Log.d(tag, str.toString());
				}
			}
		}
	}

	/**
	 * The Log Level:V
	 * 
	 * @param str
	 */
	public static void v(Object str) {
		print(str.toString());
		if (debug) {
			if (logLevel <= Log.VERBOSE) {
				String name = getFunctionName();
				if (name != null) {
					Log.v(tag, name + "\n" + str);
				} else {
					Log.v(tag, str.toString());
				}
			}
		}
	}

	/**
	 * The Log Level:w
	 * 
	 * @param str
	 */
	public static void w(Object str) {
		print(str.toString());
		if (debug) {
			if (logLevel <= Log.WARN) {
				String name = getFunctionName();
				if (name != null) {
					Log.w(tag, name + "\n" + str);
				} else {
					Log.w(tag, str.toString());
				}
			}
		}
	}

	/**
	 * The Log Level:e
	 * 
	 * @param str
	 */
	public static void e(Object str) {
		print(str.toString());
		if (debug) {
			if (logLevel <= Log.ERROR) {
				String name = getFunctionName();
				if (name != null) {
					Log.e(tag, name + "\n" + str);
				} else {
					Log.e(tag, str.toString());
				}
			}
		}
	}

	/**
	 * The Log Level:e
	 * 
	 * @param ex
	 */
	public static void e(Exception ex) {
		print(ex.toString());
		if (debug) {
			if (logLevel <= Log.ERROR) {
				Log.e(tag, "error", ex);
			}
		}
	}

	/**
	 * The Log Level:e
	 * 
	 * @param log
	 * @param tr
	 */
	public static void e(String log, Throwable tr) {
		print(log + tr.toString());
		if (debug) {
			String line = getFunctionName();
			Log.e(tag, "{Thread:" + Thread.currentThread().getName() + "}" + "[" + name + line + ":] " + log + "\n", tr);
		}
	}

	public void initExceptionHandler(Context context) {
		ExceptionHandler exceptionHandler = ExceptionHandler.getInstance(context);
		exceptionHandler.setFilename("outDebug");
		exceptionHandler.setFileDirs(Api.xXDir.concat("logs/"));
		exceptionHandler.setSave(true);
	}

	// *******************log writer In file**************************
	private static String mPath;

	private static Writer mWriter;

	private static SimpleDateFormat df;

	private static final String OUTDEBUGFILE = Api.xXDir + "/logs";

	@SuppressLint("SimpleDateFormat")
	public void initLogWriter(String file_path) {
		mPath = file_path;
		File mFile = new File(mPath);
		File pf = mFile.getParentFile();
		if (!pf.exists()) {
			pf.mkdirs();
		}
		try {
			if (!mFile.exists()) {
				mFile.createNewFile();
			}
			mWriter = new BufferedWriter(new FileWriter(mPath, true), 2048);
		} catch (IOException e) {
			Log.w("IOException", "log create file fail");
			e.printStackTrace();
		}
		df = new SimpleDateFormat("[yy-MM-dd hh:mm:ss]: ");
	}

	public static void close() throws IOException {
		mWriter.close();
	}

	public static void print(String log) {
		if (mWriter != null && new File(OUTDEBUGFILE).exists()) {
			try {
				mWriter.write(df.format(new Date()));
				mWriter.write(log);
				mWriter.write("\n");
				mWriter.flush();
			} catch (IOException e) {
				Log.w("IOException", "write LOG fail");
				e.printStackTrace();
			}
		}
	}

	public static void print(Class<?> cls, String log) {
		if (mWriter != null && new File(OUTDEBUGFILE).exists()) {
			try {
				mWriter.write(df.format(new Date()));
				mWriter.write(cls.getSimpleName() + " ");
				mWriter.write(log);
				mWriter.write("\n");
				mWriter.flush();
			} catch (IOException e) {
				Log.w("IOException", "write CLASS LOG fail");
				e.printStackTrace();
			}
		}
	}
}

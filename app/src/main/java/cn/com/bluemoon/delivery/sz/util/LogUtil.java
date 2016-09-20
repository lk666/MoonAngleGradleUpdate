package cn.com.bluemoon.delivery.sz.util;

import android.util.Log;

public class LogUtil{
	
	static String className;
	static String methodName;
	static int lineNumber;
	static boolean DEBUGGABLE = true; //false/true
	
    private LogUtil(){
        /* Protect from instantiations */
    }

	public static boolean isDebuggable() {
		return DEBUGGABLE;
	}

	private static String createLog( String log ) {

		StringBuffer buffer = new StringBuffer();
		buffer.append("[");
		buffer.append(methodName);
		buffer.append(":");
		buffer.append(lineNumber);
		buffer.append("]");
		buffer.append(log);

		return buffer.toString();
	}
	
	private static void getMethodNames(StackTraceElement[] sElements){
		className = sElements[1].getFileName();
		methodName = sElements[1].getMethodName();
		lineNumber = sElements[1].getLineNumber();
	}

	public static void e(String message){
		if (!isDebuggable())
			return;

		// Throwable instance must be created before any methods  
		getMethodNames(new Throwable().getStackTrace());
		Log.e(className, createLog(message));
	}

	public static void i(String message){
		if (!isDebuggable())
			return;

		getMethodNames(new Throwable().getStackTrace());
		Log.i(className, createLog(message));
	}
	
	public static void d(String message){
		if (!isDebuggable())
			return;

		getMethodNames(new Throwable().getStackTrace());
		Log.d(className, createLog(message));
	}
	
	public static void v(String message){
		if (!isDebuggable())
			return;

		getMethodNames(new Throwable().getStackTrace());
		Log.v(className, createLog(message));
	}
	
	public static void w(String message){
		if (!isDebuggable())
			return;

		getMethodNames(new Throwable().getStackTrace());
		Log.w(className, createLog(message));
	}
	
	public static void wtf(String message){
		if (!isDebuggable())
			return;

		getMethodNames(new Throwable().getStackTrace());
		Log.wtf(className, createLog(message));
	}


	public static void showMultiLog(String str) {
		try {
			str = str.trim();
			int index = 0;
			int maxLength = 4000;
			String sub = "";
			while (index <= str.length()) {
				// java的字符不允许指定超过总的长度end
				if (index>=0){
					if (str.length() <= index + maxLength) {
						sub = str.substring(index);
					} else{
						sub = str.substring(index, index+maxLength);
					}
				}
				index += maxLength;
				LogUtil.e("多行数据----->"+sub.trim());
			}

		}catch (Exception e){
			e.printStackTrace();
		}
	}

}



package cn.com.bluemoon.delivery.utils;  

import android.content.Context;
import android.content.SharedPreferences;

public class PreUtils {

	private static final String BIND_FLAG = "bind_flag";

	public static final String PREFERENCES = "AlarmClock";

	public static boolean isBind(Context context)
	{
		SharedPreferences sp = context.getSharedPreferences("pre_tuisong",
				Context.MODE_PRIVATE);
		return sp.getBoolean(BIND_FLAG, false);
	}

	public static void bind(Context context)
	{
		SharedPreferences sp = context.getSharedPreferences("pre_tuisong",
				Context.MODE_PRIVATE);
		sp.edit().putBoolean(BIND_FLAG, true).commit();
	}
	
	
	public static void unbind(Context context)
	{
		SharedPreferences sp = context.getSharedPreferences("pre_tuisong",
				Context.MODE_PRIVATE);
		sp.edit().putBoolean(BIND_FLAG, false).commit();
	}

}
  

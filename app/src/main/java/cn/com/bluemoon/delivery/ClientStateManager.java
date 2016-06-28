package cn.com.bluemoon.delivery;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.kymjs.kjframe.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.bluemoon.delivery.app.AppContext;
import cn.com.bluemoon.delivery.app.api.model.UserRight;
import cn.com.bluemoon.delivery.app.api.model.card.ResultWorkPlaceList;
import cn.com.bluemoon.delivery.app.api.model.card.TipsItem;
import cn.com.bluemoon.delivery.app.api.model.card.Workplace;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.LogUtils;


public class ClientStateManager {

	private static final String TOTAL_PRICE_KEY = "TOTAL_PRICE_KEY";
	private static final String LOGIN_TOKEN_KEY= "LOGIN_TOKEN_KEY";
	private static final String IS_BUY_GOODS= "IS_BUY_GOODS";
	private static final String USER_NAME_KEY = "USER_NAME_KEY";
	private static final String CHANNEL_ID="CHANNEL_ID";
	private static final String CARD_SEARCH_HISTORY = "CARD_SEARCH_HISTORY";
	private static final String CARD_TIPS_LOCATION = "CARD_TIPS_LOCATION";
	private static final String COUPON_DEFUAT = "COUPON_DEFUAT";
	private static final String MENU_ORDER = "MENU_ORDER";
	private static final String LATITUDE="LATITUDE";
	private static final String LONGITUDE="LONGITUDE";
	private static final String ALTITUDE="ALTITUDE";
	public static final String HISTORY_GROUP = "HISTORY_GROUP";
	public static final String HISTORY_MEMBER = "HISTORY_MEMBER";
	public static final String HISTORY_SELECT_MEMBER = "HISTORY_SELECT_MEMBER";
	
	public static void clearData(Context context)
	{
		ClientStateManager.setLoginToken(context, "");
		MenuFragment.user = null;
	}
	
	
	
	
	// total price of goods
	public static String getTotalPrice(Context ctx) {

		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		return pref.getString(TOTAL_PRICE_KEY, "0.00");
	}

	public static boolean setTotalPrice(Context ctx, String price) {
		try {
			SharedPreferences pref = PreferenceManager
					.getDefaultSharedPreferences(ctx);
			pref.edit().putString(TOTAL_PRICE_KEY, price).commit();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	// token store

	public static String getLoginToken(Context ctx) {

		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		return pref.getString(LOGIN_TOKEN_KEY, "");
	}

	public static boolean setLoginToken(Context ctx, String token) {
		try {
			SharedPreferences pref = PreferenceManager
					.getDefaultSharedPreferences(ctx);
			pref.edit().putString(LOGIN_TOKEN_KEY, token).commit();
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	public static String getUserName(Context ctx) {

		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		return pref.getString(USER_NAME_KEY, "");
	}

	public static boolean setUserName(Context ctx, String username) {
		try {
			SharedPreferences pref = PreferenceManager
					.getDefaultSharedPreferences(ctx);
			pref.edit().putString(USER_NAME_KEY, username).commit();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	
	public static String getChannelId(Context ctx) {

		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		return pref.getString(CHANNEL_ID,"");
	}

	public static boolean setChannelId(Context ctx, String channelId) {
		try {
			SharedPreferences pref = PreferenceManager
					.getDefaultSharedPreferences(ctx);
			pref.edit().putString(CHANNEL_ID, channelId).commit();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public static List<Workplace> getCardSearchHistory(Context ctx) {

		List<Workplace> list = null;
		try {
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
			String str = pref.getString(CARD_SEARCH_HISTORY, "");
			if(!StringUtils.isEmpty(str)){
				ResultWorkPlaceList resultWorkPlaceList = JSON.parseObject(str, ResultWorkPlaceList.class);
				if(resultWorkPlaceList!=null){
					list = resultWorkPlaceList.getWorkplaceList();
				}
			}
		}catch (Exception e){
			LogUtils.e("ClientStateManager", e.getMessage());
		}
		if(list == null){
			list = new ArrayList<Workplace>();
		}
		return list;
	}

	public static boolean setCardSearhHistory(Context ctx, List<Workplace> list) {
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("workplaceList", list);
			String jsonString = JSONObject.toJSONString(params);
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
			pref.edit().putString(CARD_SEARCH_HISTORY, jsonString).commit();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public static boolean setTipsByDay(Context ctx, TipsItem tipsItem) {
		try {
			String tips = "0-0";
			if(tipsItem!=null&&tipsItem.getTimestamp()>=0&&tipsItem.getCount()>=0){
				tips = tipsItem.getTimestamp() + "-" + tipsItem.getCount();
			}
			SharedPreferences pref = PreferenceManager
					.getDefaultSharedPreferences(ctx);
			pref.edit().putString(CARD_TIPS_LOCATION, tips).commit();
		} catch (Exception e) {
			return false;
		}
		return true;
	}


	public static TipsItem getTipsByDay(Context ctx) {

		TipsItem item = new TipsItem();
		try {
			SharedPreferences pref = PreferenceManager
					.getDefaultSharedPreferences(ctx);
			String str =  pref.getString(CARD_TIPS_LOCATION, "");
			String[] strs = str.split("-");
			item.setTimestamp(Long.parseLong(strs[0]));
			item.setCount(Integer.parseInt(strs[1]));
		}catch (Exception e){
			item.setTimestamp(0);
			item.setCount(0);
		}
		return item;
	}

	public static boolean setActivityCode(Context ctx, String code) {
		try {
			String str = System.currentTimeMillis()+"-"+code+"-"+getUserName(ctx);
			SharedPreferences pref = PreferenceManager
					.getDefaultSharedPreferences(ctx);
			pref.edit().putString(COUPON_DEFUAT, str).commit();
		} catch (Exception e) {
			return false;
		}
		return true;
	}


	public static String getActivityCode(Context ctx) {
		String str = "0";
		try {
			SharedPreferences pref = PreferenceManager
					.getDefaultSharedPreferences(ctx);
			str =  pref.getString(COUPON_DEFUAT, "");
			if ("".equals(str) || !str.split("-")[2].equals(getUserName(ctx)) ) {
				return "0";
			} else {
				long time = Long.valueOf(str.split("-")[0]);
				if (DateUtil.isToday(time)) {
					return str.split("-")[1];
				} else {
					return "0";
				}
			}

		}catch (Exception e){
			return "0";
		}
	}

	public static boolean setMenuOrder(Context ctx, List<UserRight> list) {
		try {
			String strs = "";
			if(list==null){
				list = new ArrayList<>();
			}
			for (int i=0;i<list.size();i++){
				if(i==0){
					strs += list.get(i).getMenuCode();
				}else{
					strs += ","+list.get(i).getMenuCode();
				}
			}
			SharedPreferences pref = PreferenceManager
					.getDefaultSharedPreferences(ctx);
			pref.edit().putString(MENU_ORDER, strs).commit();
		} catch (Exception e) {
			return false;
		}
		return true;
	}


	public static List<String> getMenuOrder(Context ctx) {

		List<String> list = new ArrayList<>();
		try {
			SharedPreferences pref = PreferenceManager
					.getDefaultSharedPreferences(ctx);
			String str =  pref.getString(MENU_ORDER, "");
			LogUtils.d("test", "result =" + str);
			if(!StringUtils.isEmpty(str)){
				String[] strs = str.split(",");
				list = Arrays.asList(strs);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return list;
	}

	public static String getLatitude(Context ctx) {

		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		return pref.getString(LATITUDE,"999");
	}

	public static boolean setLatitude(Context ctx, String latitude) {
		try {
			SharedPreferences pref = PreferenceManager
					.getDefaultSharedPreferences(ctx);
			pref.edit().putString(LATITUDE, latitude).commit();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public static String getLongitude(Context ctx) {

		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		return pref.getString(LONGITUDE,"999");
	}

	public static boolean setLongitude(Context ctx, String longitude) {
		try {
			SharedPreferences pref = PreferenceManager
					.getDefaultSharedPreferences(ctx);
			pref.edit().putString(LONGITUDE, longitude).commit();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public static String getAltitude(Context ctx) {

		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		return pref.getString(ALTITUDE,"0");
	}

	public static boolean setAltitude(Context ctx, String altitude) {
		try {
			SharedPreferences pref = PreferenceManager
					.getDefaultSharedPreferences(ctx);
			pref.edit().putString(ALTITUDE, altitude).commit();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public static List<String> getHistory(String key) {

		List<String> list = new ArrayList<>();
		try {
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(AppContext.getInstance());
			String str = pref.getString(key, "");
			if(!StringUtils.isEmpty(str)){
				String[] strs = str.split(",");
				for (String s :strs){
					list.add(s);
				}
			}
		}catch (Exception e){
			LogUtils.e("ClientStateManager", e.getMessage());
		}
		return list;
	}

	public static boolean setHistory(List<String> list,String key) {
		if(list==null) return false;
		try {
			String result = "";
			for (String str : list){
				result += str + ",";
			}
			if(result.length()>0){
				result.substring(0,result.length()-1);
			}
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(AppContext.getInstance());
			pref.edit().putString(key, result).commit();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

}

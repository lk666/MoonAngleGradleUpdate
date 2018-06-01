package cn.com.bluemoon.delivery.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.kymjs.kjframe.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.bluemoon.delivery.AppContext;
import cn.com.bluemoon.delivery.MenuFragment;
import cn.com.bluemoon.delivery.app.api.model.card.ResultWorkPlaceList;
import cn.com.bluemoon.delivery.app.api.model.card.TipsItem;
import cn.com.bluemoon.delivery.app.api.model.card.Workplace;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.LogUtils;


public class ClientStateManager {

    private static final String LOGIN_TOKEN_KEY = "LOGIN_TOKEN_KEY";
    private static final String USER_NAME_KEY = "USER_NAME_KEY";
    private static final String CLIENT_ID = "CLIENT_ID";
    private static final String CARD_SEARCH_HISTORY = "CARD_SEARCH_HISTORY";
    private static final String CARD_TIPS_LOCATION = "CARD_TIPS_LOCATION";
    private static final String COUPON_DEFUAT = "COUPON_DEFUAT";
    private static final String LATITUDE = "LATITUDE";
    private static final String LONGITUDE = "LONGITUDE";
    private static final String ALTITUDE = "ALTITUDE";
    private static final String MENU_NUM = "MENU_NUM";
    public static final String PROMOTE_KEY = "PROMOTE_KEY";
    public static final String COMMUNITY_KEY = "COMMUNITY_KEY";
    public static final String HISTORY_GROUP = "HISTORY_GROUP";
    public static final String HISTORY_MEMBER = "HISTORY_MEMBER";
    public static final String HISTORY_SELECT_MEMBER = "HISTORY_SELECT_MEMBER";
    public static final String HISTORY_SELECT_AREA = "HISTORY_SELECT_AREA";
    public static final String USER_AGENT = "USER_AGENT";

    public static void clearData() {
        ClientStateManager.setLoginToken("");
        MenuFragment.user = null;
    }


    // token store

    public static String getLoginToken(Context ctx) {

        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        return pref.getString(LOGIN_TOKEN_KEY, "");
    }

    public static String getLoginToken() {

        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(AppContext.getInstance());
        return pref.getString(LOGIN_TOKEN_KEY, "");
    }

    public static boolean setLoginToken(String token) {
        try {
            SharedPreferences pref = PreferenceManager
                    .getDefaultSharedPreferences(AppContext.getInstance());
            pref.edit().putString(LOGIN_TOKEN_KEY, token).commit();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static String getUserName() {

        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(AppContext.getInstance());
        return pref.getString(USER_NAME_KEY, "");
    }

    public static boolean setUserName(String username) {
        try {
            SharedPreferences pref = PreferenceManager
                    .getDefaultSharedPreferences(AppContext.getInstance());
            pref.edit().putString(USER_NAME_KEY, username).commit();
        } catch (Exception e) {
            return false;
        }
        return true;
    }


    public static String getClientId() {

        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(AppContext.getInstance());
        return pref.getString(CLIENT_ID, "");
    }

    public static boolean setClientId(String channelId) {
        try {
            SharedPreferences pref = PreferenceManager
                    .getDefaultSharedPreferences(AppContext.getInstance());
            pref.edit().putString(CLIENT_ID, channelId).commit();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static List<Workplace> getCardSearchHistory() {

        List<Workplace> list = null;
        try {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(AppContext.getInstance());
            String str = pref.getString(CARD_SEARCH_HISTORY, "");
            if (!StringUtils.isEmpty(str)) {
                ResultWorkPlaceList resultWorkPlaceList = JSON.parseObject(str, ResultWorkPlaceList.class);
                if (resultWorkPlaceList != null) {
                    list = resultWorkPlaceList.getWorkplaceList();
                }
            }
        } catch (Exception e) {
            LogUtils.e("ClientStateManager", e.getMessage());
        }
        if (list == null) {
            list = new ArrayList<>();
        }
        return list;
    }

    public static boolean setCardSearhHistory(List<Workplace> list) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("workplaceList", list);
            String jsonString = JSONObject.toJSONString(params);
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(AppContext
                    .getInstance());
            pref.edit().putString(CARD_SEARCH_HISTORY, jsonString).commit();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean setTipsByDay(Context ctx, TipsItem tipsItem) {
        try {
            String tips = "0-0";
            if (tipsItem != null && tipsItem.getTimestamp() >= 0 && tipsItem.getCount() >= 0) {
                tips = tipsItem.getTimestamp() + "-" + tipsItem.getCount();
            }
            SharedPreferences pref = PreferenceManager
                    .getDefaultSharedPreferences(ctx);
            pref.edit().putString(CARD_TIPS_LOCATION, tips).commit();
        } catch (Exception e) {
            LogUtils.e("setTipsByDay", "error", e);
            return false;
        }
        return true;
    }


    public static TipsItem getTipsByDay(Context ctx) {

        TipsItem item = new TipsItem();
        try {
            SharedPreferences pref = PreferenceManager
                    .getDefaultSharedPreferences(ctx);
            String str = pref.getString(CARD_TIPS_LOCATION, "");
            String[] strs = str.split("-");
            item.setTimestamp(Long.parseLong(strs[0]));
            item.setCount(Integer.parseInt(strs[1]));
        } catch (Exception e) {
            item.setTimestamp(0);
            item.setCount(0);
        }
        return item;
    }

    public static boolean setActivityCode(Context ctx, String code) {
        try {
            String str = System.currentTimeMillis() + "-" + code + "-" + getUserName();
            SharedPreferences pref = PreferenceManager
                    .getDefaultSharedPreferences(ctx);
            pref.edit().putString(COUPON_DEFUAT, str).commit();
        } catch (Exception e) {
            return false;
        }
        return true;
    }


    public static String getActivityCode(Context ctx) {
        String str;
        try {
            SharedPreferences pref = PreferenceManager
                    .getDefaultSharedPreferences(ctx);
            str = pref.getString(COUPON_DEFUAT, "");
            if ("".equals(str) || !str.split("-")[2].equals(getUserName())) {
                return "0";
            } else {
                long time = Long.valueOf(str.split("-")[0]);
                if (DateUtil.isToday(time)) {
                    return str.split("-")[1];
                } else {
                    return "0";
                }
            }

        } catch (Exception e) {
            return "0";
        }
    }

    public static String getLatitude() {

        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(AppContext.getInstance());
        return pref.getString(LATITUDE, "999");
    }

    public static boolean setLatitude(String latitude) {
        try {
            SharedPreferences pref = PreferenceManager
                    .getDefaultSharedPreferences(AppContext.getInstance());
            pref.edit().putString(LATITUDE, latitude).commit();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static String getLongitude() {

        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(AppContext.getInstance());
        return pref.getString(LONGITUDE, "999");
    }

    public static boolean setLongitude(String longitude) {
        try {
            SharedPreferences pref = PreferenceManager
                    .getDefaultSharedPreferences(AppContext.getInstance());
            pref.edit().putString(LONGITUDE, longitude).commit();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static String getAltitude() {

        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(AppContext.getInstance());
        return pref.getString(ALTITUDE, "0");
    }

    public static boolean setAltitude(String altitude) {
        try {
            SharedPreferences pref = PreferenceManager
                    .getDefaultSharedPreferences(AppContext.getInstance());
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
            if (!StringUtils.isEmpty(str)) {
                String[] strs = str.split(",");
                for (String s : strs) {
                    list.add(s);
                }
            }
        } catch (Exception e) {
            LogUtils.e("ClientStateManager", e.getMessage());
        }
        return list;
    }

    public static boolean setHistory(List<String> list, String key) {
        if (list == null) return false;
        try {
            String result = "";
            for (String str : list) {
                result += str + ",";
            }
            if (result.length() > 0) {
                result.substring(0, result.length() - 1);
            }
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(AppContext.getInstance());
            pref.edit().putString(key, result).commit();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 桌面角标数量
     * @return
     */
    public static int getMenuNum() {

        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(AppContext.getInstance());
        return pref.getInt(MENU_NUM,0);
    }

    public static boolean setMenuNum(int menuNum) {
        try {
            SharedPreferences pref = PreferenceManager
                    .getDefaultSharedPreferences(AppContext.getInstance());
            pref.edit().putInt(MENU_NUM, menuNum).commit();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 用户代理
     */
    public static String getUserAgent() {

        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(AppContext.getInstance());
        return pref.getString(USER_AGENT,"");
    }

    public static boolean setUserAgent(String userAgent) {
        try {
            SharedPreferences pref = PreferenceManager
                    .getDefaultSharedPreferences(AppContext.getInstance());
            pref.edit().putString(USER_AGENT, userAgent).commit();
        } catch (Exception e) {
            return false;
        }
        return true;
    }


}

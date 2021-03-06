package cn.com.bluemoon.delivery.utils;

import android.net.Uri;

import java.util.HashMap;
import java.util.Map;

import cn.com.bluemoon.delivery.AppContext;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.common.ClientStateManager;

public class Constants {

    public static final String OS_TYPE = "android";
    public static final String APP_TYPE = "moonAngel";

    public static final String TYPE_RESET = "reset";
    public static final int RESPONSE_RESULT_SUCCESS = 0;
    public static final int RESPONSE_RESULT_TOKEN_EXPIRED = 2301;
    public static final int RESPONSE_RESULT_SMS_VAILD = 2403;
    public static final int RESPONSE_RESULT_TOKEN_NOT_EXIST = 2302;
    public static final int RESPONSE_RESULT_CARD_FAIL = 1207;
    public static final int RESPONSE_RESULT_NOT_SUFFICIENT_FUNDS = 80601;

    public static final int SALE_MAX_NUM = 99999;
    public static final double UNKNOW_LATITUDE = 999;
    public static final double UNKNOW_LONGITUDE = 999;
    public static final Double DEFAUL_LOCATION = 4.9E-324;

    public static final int TOKEN_EFFECTIVE_TIME = 12 * 60; //12 hours

    public static final long FORCE_CHECK_VERSION_TIME = 1000 * 60 * 60 * 2;    // equivalent to 2
    // hours
    public static final long SPLASH_SCREEN_MIN_SHOW_TIME = 1000;    // equivalent to 1 sec

    public static final String PRIVATE_KEY = "Er78s1hcT4Tyoaj2";

    public static final String PRIVATE_KEY_NAME = "secrect";

    public static final String SERVICE_PHONE = "400-111-1118";

    public final static String DES_KEY = "19490101";

    public final static String DES = "DES";

    public static final Uri ALARM_CONTENT_URI =
            Uri.parse("content://cn.com.bluemoon.delivery/alarm");


    public static final String[] ALARM_QUERY_COLUMNS = {
            "remindId", "hour", "minute", "remindWeek", "remindTime",
            "isClose", "remindTitle", "remindContent" };



    public static final String DEFAULT_SORT_ORDER =
            "isClose ASC,hour, minute ASC";

    public static final String Fillter_SORT_ORDER =
            "remindWeek ASC";

    public static final String WHERE_ENABLE =  " isClose = 0";

    /************************
     * location
     ***************************/
    public static final String LOCATION_ACTION = "locationAction";
    public static final String LONGITUDE = "Longitude";
    public static final String LATITUDE = "Latitude";
    public static final String ALTITUDE = "Altitude";

    public static final Double UNKNOW_VALUE = 4.9E-324;
    public static final String WIFI_GPS = "WIFI_GPS";
    public static final String GPS_GPS = "GPS_GPS";
    public static final String GPRS_GPS = "GPRS_GPS";


    /********************
     * code scan
     ***********************/
    public static final int REQUEST_SCAN = 0x00;

    public static final int RESULT_SCAN = 0x04;
    public static final int RESULT_INPUT = 0x05;
    public static final String RESULT_CODE = "code";

    public static final int TAKE_PIC_RESULT = 0x01;
    public static final int CHOSE_PIC_RESULT = 0x02;


    public static final int RESULT_ID_CARD_FRONT = 0x11;
    public static final int RESULT_ID_CARD_BACK = 0x12;
    public static final int RESULT_BANK_CARD = 0x13;

    /************
     * account
     *******************/

    public static final int MODE_GENERAL = 0x08;
    public static final int MODE_CHECK = 0x09;


    public static final String TYPE_RETURN = "return";
    public static final String TYPE_EXCHANGE = "exchange";


    public static final String TYPE_DICTINFO = "MM_RETURN_REASON";
    public static final String CRM_DISPATCH_CANCEL_REASON = "CRM_DISPATCH_CANCEL_REASON";
    public static final String CRM_DISPATCH_FEEDBACK_INFO = "CRM_DISPATCH_FEEDBACK_INFO";

    public static final int RETURN_TYPE = 1;
    public static final int EXCHANGE_TYPE = 2;
    public static final String INTENTFILTER_ACTION = "cn.com.bluemoon.delivery.module.order";

    public static final String QRCODE_ANGEL = "BM_ANGEL";
    public static final String QRCODE_MOONMALL = "BM_MOONMALL";

    /********************
     * ѡ�񳡹ݳ���
     **************************/
    public static final String TYPE_VENUE = "venue";
    public static final String TYPE_TIMES = "times";

    /************************
     * paper detail
     ****************************/
    public static final int TYPE_NOTICE = 0;
    public static final int TYPE_KNOWLEDGE = 1;


    public static final Map<Integer, String> ERROR_MAP = new HashMap<Integer, String>() {
    };

    public static final Map<String, String> WASH_STATUS_MAP = new HashMap<String, String>() {
        {
            put("ANGEL_LAUNDRYING", AppContext.string(R.string.ANGEL_LAUNDRYING));
            put("WASHINGCENTER_ACCEPTING", AppContext.string(R.string.WASHINGCENTER_ACCEPTING));
            put("WASHINGCENTER_RECEIVE_END", AppContext.string(R.string.WASHINGCENTER_RECEIVE_END));
        }
    };

    /************************
     * push jump key
     ****************************/
    public static final String PUSH_VIEW = "view";
    public static final String PUSH_URL = "url";
    public static final String PUSH_H5 = "h5";


    /************************
     * 洗衣服务取消和同意状态
     ****************************/
    public static final String STATUS_CANCEL_ORDER = "CANCEL";
    public static final String STATUS_ACCEPTL_ORDER = "RECEIVE";

    /*****************************team**********************************/
    public final static String TYPE_ADD = "add";
    public final static String TYPE_UPDATE = "update";
    public final static String TYPE_SCAN = "scan";
    public final static String TYPE_INPUT = "input";
    public final static String RELTYPE_GROUP = "group";
    public final static String RELTYPE_COMMUNITY = "community";
    public final static String WORKTYPE_FULL = "fullTime";
    public final static String WORKTYPE_PART = "partTime";

    public final static long LARGETIME = 253402185600000l;
    /**
     * {@link cn.com.bluemoon.delivery.ui.ImageGridView}点击添加按钮时的requestcode
     */
    public static final int REQUEST_ADD_IMG = 10;
    /**
     * {@link cn.com.bluemoon.delivery.ui.ImageGridView}点击图片时的requestcode
     */
    public static final int REQUEST_PREVIEW_IMG = 20;

    public static final String ICON_ADD = "000000"; //图片选择默认URL

    //wecaht pay
    public final static String APP_ID = "wx3b6e66b753fd84c2";
    public final static String PARTNER_ID = "1410234202";

    public final static int RESPONSE_RESULT_LOCAL_PARAM_ERROR = -123;

    public final static String OUTER_ACCEPT_CLOTHES = "OUTER_ACCEPT_CLOTHES";	//收衣进行中
    public final static String OUTER_WAIT_PAY = "OUTER_WAIT_PAY";	//待付款
    public final static String OUTER_TO_BE_WASHED = "OUTER_TO_BE_WASHED";	//送洗中
    public final static String OUTER_CANCEL = "OUTER_CANCEL";	//已取消
    public final static String OUTER_WASHING_CLOTHES = "OUTER_WASHING_CLOTHES";	//洗涤中
    public final static String OUTER_BACK_CLOTHES = "OUTER_BACK_CLOTHES";	//还衣中
    public final static String OUTER_ALREADY_RECIVERED = "OUTER_ALREADY_RECIVERED";	//已签收

    //线下培训状态
    public final static String OFFLINE_STATUS_WAITING_CLASS = "waitingClass";//未上课
    public final static String OFFLINE_STATUS_IN_CLASS = "inClass";//上课中
    public final static String OFFLINE_STATUS_END_CLASS = "endClass";//结束上课
    public final static String OFFLINE_STATUS_CLOSE_CLASS = "closeClass";//关闭上课

    public static final String TAG_HTTP_RESPONSE_SUCCESS = "TAG_HTTP_RESPONSE_SUCCESS";
    public static final String TAG_HTTP_RESPONSE_NOT_SUCCESS = "TAG_HTTP_RESPONSE_NOT_SUCCESS";
    public static final String TAG_HTTP_RESPONSE_EXCEPTION = "TAG_HTTP_RESPONSE_EXCEPTION";
    public static final String TAG_HTTP_RESPONSE_FAILURE = "TAG_HTTP_RESPONSE_FAILURE";
    public static final String TAG_HTTP_REQUEST = "TAG_HTTP_REQUEST";

    //埋点返回码
    public static final String RESPONSE_RESULT_SUCCESS_TRACK = "100";
    public static final String RESPONSE_RESULT_TOO_LONG_TRACK = "101";
    public static final String RESPONSE_RESULT_ERROR_TRACK = "102";

    public static final int PAGE_SIZE = 10;
    public static final String URL_LAN_YUE = "http://193.112.194.73/lanyueTS/index" +
            ".php?userId=" + ClientStateManager.getUserName();
}
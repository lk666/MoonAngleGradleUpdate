package cn.com.bluemoon.delivery.utils;

import android.net.Uri;
import android.os.Environment;

import java.util.HashMap;
import java.util.Map;

import cn.com.bluemoon.delivery.AppContext;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.common.photopicker.PhotoPickerActivity;

public class Constants {

    public static final String OS_TYPE = "android";
    public static final String APP_TYPE = "moonAngel";

    public static final String TYPE_RESET = "reset";
    public static final int RESPONSE_RESULT_SUCCESS = 0;
    public static final int RESPONSE_RESULT_TOKEN_EXPIRED = 2301;
    public static final int RESPONSE_RESULT_SMS_VAILD = 2403;
    public static final int RESPONSE_RESULT_TOKEN_NOT_EXIST = 2302;
    public static final int RESPONSE_RESULT_CARD_FAIL = 1207;

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

    /************
     * account
     *******************/

    public static final int MODE_GENERAL = 0x08;
    public static final int MODE_CHECK = 0x09;


    public static final String TYPE_RETURN = "return";
    public static final String TYPE_EXCHANGE = "exchange";


    public static final String TYPE_DICTINFO = "MM_RETURN_REASON";

    /*************************
     * file path
     *******************************/
    public static final String PATH_MAIN = Environment.getExternalStorageDirectory() +
			"/BMDelivery";
    public static final String PATH_PHOTO = PATH_MAIN + "/images";
    public static final String PATH_TEMP = PATH_MAIN + "/temp";
    public static final String PATH_CACHE = PATH_MAIN + "/cache";
    public static final String PATH_CAMERA = Environment.getExternalStorageDirectory() +
			"/DCIM/Camera";

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
     * baidu push jump key
     ****************************/
    public static final String KEY_JUMP = "jumpCode";


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
}

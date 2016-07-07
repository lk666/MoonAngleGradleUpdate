package cn.com.bluemoon.delivery.utils;

import java.util.HashMap;
import java.util.Map;

import android.os.Environment;

public class Constants {
	
	public static final String OS_TYPE="android";
	public static final String APP_TYPE="moonAngel";
	
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

	public static final int TOKEN_EFFECTIVE_TIME = 12*60; //12 hours
	
	public static final long FORCE_CHECK_VERSION_TIME = 1000 * 60 * 60 * 2; 	// equivalent to 2 hours
	public static final long SPLASH_SCREEN_MIN_SHOW_TIME = 1000;	// equivalent to 1 sec
	
    public static final String PRIVATE_KEY="Er78s1hcT4Tyoaj2";
	
	public static final String PRIVATE_KEY_NAME="secrect";

	public static final String SERVICE_PHONE = "400-111-1118";
	
	public final static String DES_KEY = "19490101";
	
	public final static String DES = "DES";

	/************************location***************************/
	public static final String LOCATION_ACTION = "locationAction";
	public static final String LONGITUDE = "Longitude";
	public static final String LATITUDE = "Latitude";
	public static final String ALTITUDE = "Altitude";
	

	/********************code scan***********************/
	public static final int REQUEST_SCAN = 0x00;
	
	public static final int TAKE_PIC_RESULT = 0x01;
	public static final int CHOSE_PIC_RESULT = 0x02;
	
	/************account*******************/

	public static final int MODE_PERSON = 0x07;
	public static final int MODE_GENERAL = 0x08;
	public static final int MODE_CHECK = 0x09;
	
	
	public static final String TYPE_RETURN = "return";
	public static final String TYPE_EXCHANGE = "exchange";
	

	public static final String TYPE_DICTINFO = "MM_RETURN_REASON";
	
	/*************************file path*******************************/
	public static final String PATH_MAIN = Environment.getExternalStorageDirectory() + "/BMDelivery";
	public static final String PATH_PHOTO = PATH_MAIN + "/images";
	public static final String PATH_TEMP = PATH_MAIN + "/temp";
	public static final String PATH_CAMERA = Environment.getExternalStorageDirectory() + "/DCIM/Camera";
	
	public static final int RETURN_TYPE = 1;
	public static final int EXCHANGE_TYPE = 2;
	public static final String INTENTFILTER_ACTION = "cn.com.bluemoon.delivery.order";
	
	public static final String QRCODE_ANGEL = "BM_ANGEL";
	public static final String QRCODE_MOONMALL = "BM_MOONMALL";
	
	/********************ѡ�񳡹ݳ���**************************/
	public static final String TYPE_VENUE = "venue";
	public static final String TYPE_TIMES = "times";

	/************************paper detail****************************/
	public static final int TYPE_NOTICE = 0;
	public static final int TYPE_KNOWLEDGE = 1;

	
	public static final Map<Integer, String> ERROR_MAP = new HashMap<Integer,String>(){
	};

	/************************baidu push jump key****************************/
	public static final String KEY_JUMP = "jumpCode";

	/*****************************team**********************************/
	public final static String TYPE_ADD = "add";
	public final static String TYPE_UPDATE = "update";
	public final static String RELTYPE_GROUP = "group";
	public final static String RELTYPE_COMMUNITY = "community";
	public final static String WORKTYPE_FULL = "fullTime";
	public final static String WORKTYPE_PART = "partTime";

	public final static long LARGETIME = 253402185600000l;
}

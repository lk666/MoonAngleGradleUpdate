package cn.com.bluemoon.delivery.sz.util;

import android.os.Environment;

import java.io.File;

/**
 * Created by dujiande on 2016/8/11.
 */
public class Constants {
    public static final int RESPONSE_RESULT_SUCCESS = 100;
    public static final String PATH_SCHEDUAL = Environment.getExternalStorageDirectory() + File.separator+"scheuleSys";
    public static final int UPDATE_TIME = 3*60*1000;//更新机制间隔时间
}

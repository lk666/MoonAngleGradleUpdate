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

    public static final String MAIN_MSG_WAIT_REMIND        = "1000";//待办提醒
    public static final String MAIN_MSG_MEETING_REMIND     = "2000";//会议提醒
    public static final String MAIN_MSG_ADVICE_REMIND      = "3000";//建议提醒
    public static final String MAIN_MSG_CONFLICT_REMIND    = "4000";//任务冲突提醒
    public static final String MAIN_MSG_DELEGATION_REMIND  = "5000";//委托人消息提醒
}

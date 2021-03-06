package cn.com.bluemoon.delivery.module.offline.utils;

import cn.com.bluemoon.delivery.AppContext;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.utils.Constants;

/**
 * Created by tangqiwei on 2017/6/4.
 */

public class OfflineUtil {
    /**
     * 根据状态显示状态文本 教师
     *
     * @param state
     * @return
     */
    public static String stateToString(String state) {
        switch (state) {
            case Constants.OFFLINE_STATUS_WAITING_CLASS:
                return AppContext.getInstance().getString(R.string
                        .offline_course_teacher_state_no_start);
            case Constants.OFFLINE_STATUS_IN_CLASS:
                return AppContext.getInstance().getString(R.string
                        .offline_course_teacher_state_ing);
            case Constants.OFFLINE_STATUS_END_CLASS:
                return AppContext.getInstance().getString(R.string
                        .offline_course_teacher_state_finish);
            case Constants.OFFLINE_STATUS_CLOSE_CLASS:
                return AppContext.getInstance().getString(R.string
                        .offline_course_teacher_state_close);
            default:
                return AppContext.getInstance().getString(R.string
                        .offline_course_teacher_state_no_start);
        }
    }

    //获取课程状态对应的文本
    public static String getTextByStatus(String status, boolean isSign) {
        return stateToString(status) + "-" + (isSign ? AppContext.getInstance().getString(R
                .string.offline_signed) : AppContext.getInstance().getString(R.string
                .offline_unsign));
    }

    /**
     * 提取二维码的链接参数 只会有其一
     * roomCode 培训室编码
     * planCode 排课编码
     */
    public static String getUrlParamsByCode(String url) {
        String code = null;
        if (url != null) {
            String arg = url.substring(url.indexOf("?") + 1, url.length());
            String[] strs = arg.split("&");
            for (int x = 0; x < strs.length; x++) {
                if (strs[x].indexOf("=") > 0) {
                    String key = strs[x].substring(0, strs[x].indexOf("="));
                    String value = strs[x].substring(strs[x].indexOf("=") + 1);
                    if ("roomCode".equals(key)) {
                        code = value;
                        break;
                    }
                    if ("planCode".equals(key)) {
                        code = value;
                        break;
                    }
                }
            }
        }
        return code;
    }
}

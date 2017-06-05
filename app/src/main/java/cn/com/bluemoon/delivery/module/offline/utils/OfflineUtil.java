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
        String text = "";
        if (Constants.OFFLINE_STATUS_WAITING_CLASS.equals(status)) {
            text = AppContext.getInstance().getString(R.string
                    .offline_course_teacher_state_no_start);
        } else if (Constants.OFFLINE_STATUS_IN_CLASS.equals(status)) {
            text = AppContext.getInstance().getString(R.string.offline_course_teacher_state_ing);
        } else if (Constants.OFFLINE_STATUS_END_CLASS.equals(status)) {
            text = AppContext.getInstance().getString(R.string.offline_course_teacher_state_finish);
        } else if (Constants.OFFLINE_STATUS_CLOSE_CLASS.equals(status)) {
            text = AppContext.getInstance().getString(R.string.offline_course_teacher_state_close);
        }
        return text + "-" + (isSign ? AppContext.getInstance().getString(R.string.offline_signed)
                : AppContext.getInstance().getString(R.string
                .offline_unsign));
    }
}

package cn.com.bluemoon.delivery.app.api;

import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.HashMap;
import java.util.Map;

import cn.com.bluemoon.delivery.app.api.model.offline.request.AssignData;
import cn.com.bluemoon.delivery.app.api.model.offline.request.CancelData;
import cn.com.bluemoon.delivery.app.api.model.offline.request.CourseListData;
import cn.com.bluemoon.delivery.app.api.model.offline.request.EvaluateData;
import cn.com.bluemoon.delivery.app.api.model.offline.request.SignDetailData;

/**
 * Created by bm on 2017/5/25.
 */

public class OffLineApi extends DeliveryApi {

    /**
     * 1.1取消签到
     *
     * @param token
     * @param courseCode
     * @param planCode
     * @param studentCode
     * @param handler
     */
    public static void cancel(String token, String courseCode, String planCode, String studentCode,
                              AsyncHttpResponseHandler handler) {
        if (null == token || null == courseCode || null == planCode || null == studentCode) {
            onError(handler);
            return;
        }

        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("data", new CancelData(courseCode, planCode, studentCode));
        postRequest(params, "course/student/assign/cancel%s", handler);
    }

    /**
     * 1.2签到
     *
     * @param token
     * @param courseCode
     * @param planCode
     * @param handler
     */
    public static void assign(String token, String courseCode, String planCode,
                              AsyncHttpResponseHandler handler) {
        if (null == token || null == courseCode || null == planCode) {
            onError(handler);
            return;
        }

        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("data", new AssignData(courseCode, planCode));
        postRequest(params, "course/student/assign%s", handler);
    }

    /**
     * 1.3获取学员的课程列表
     *
     * @param token
     * @param date
     * @param status
     * @param handler
     */
    public static void list(String token, long date, String status, AsyncHttpResponseHandler
            handler) {
        if (null == token || null == status) {
            onError(handler);
            return;
        }

        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("data", new CourseListData(date, status));
        postRequest(params, "course/student/list%s", handler);
    }

    /**
     * 1.4获取签到信息
     *
     * @param token
     * @param roomCode
     * @param handler
     */
    public static void signDetail(String token, String roomCode, AsyncHttpResponseHandler
            handler) {
        if (null == token || null == roomCode) {
            onError(handler);
            return;
        }

        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("data", new SignDetailData(roomCode));
        postRequest(params, "student/assign/detail%s", handler);
    }

    /**
     * 1.5获取课程详情
     * @param token
     * @param courseCode
     * @param planCode
     * @param handler
     */
    public static void CourseDetail(String token, String courseCode, String planCode,
                                    AsyncHttpResponseHandler handler) {
        if (null == token || null == courseCode || null == planCode) {
            onError(handler);
            return;
        }

        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("data", new AssignData(courseCode, planCode));
        postRequest(params, "course/student/detail%s", handler);
    }

    /**
     * 1.6评价
     *
     * @param token
     * @param comment
     * @param courseCode
     * @param courseStar
     * @param planCode
     * @param teacherStar
     * @param handler
     */
    public static void evaluate(String token, String comment, String courseCode, int courseStar,
                                String planCode, int teacherStar, AsyncHttpResponseHandler
                                        handler) {
        if (null == token || null == courseCode || null == planCode || null == comment) {
            onError(handler);
            return;
        }

        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("data", new EvaluateData(comment, courseCode, courseStar, planCode,
                teacherStar));
        postRequest(params, "course/student/evaluate%s", handler);
    }


}

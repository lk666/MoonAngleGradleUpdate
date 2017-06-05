package cn.com.bluemoon.delivery.app.api;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.HashMap;
import java.util.Map;

import cn.com.bluemoon.delivery.AppContext;
import cn.com.bluemoon.delivery.app.api.model.offline.request.AssignData;
import cn.com.bluemoon.delivery.app.api.model.offline.request.CancelData;
import cn.com.bluemoon.delivery.app.api.model.offline.request.CourseListData;
import cn.com.bluemoon.delivery.app.api.model.offline.request.EvaluateData;
import cn.com.bluemoon.delivery.app.api.model.offline.request.ListNumData;
import cn.com.bluemoon.delivery.app.api.model.offline.request.SignDetailData;
import cn.com.bluemoon.delivery.app.api.model.offline.request.StartOrEndCourseData;
import cn.com.bluemoon.delivery.app.api.model.offline.request.TaecherGetEvaluateDetailData;
import cn.com.bluemoon.delivery.app.api.model.offline.request.TeacherEvaluateData;
import cn.com.bluemoon.delivery.app.api.model.offline.request.TeacherEvaluateStudentListData;
import cn.com.bluemoon.delivery.module.base.WithContextTextHttpResponseHandler;

/**
 * Created by bm on 2017/5/25.
 */

public class OffLineApi extends DeliveryApi {


    /**
     * 提交http请求
     *
     * @param params  参数列表
     * @param subUrl  请求的url子部
     * @param handler 回调
     */
    protected static void postRequest(Map<String, Object> params, String subUrl,
                                      AsyncHttpResponseHandler handler) {
        String jsonString = JSONObject.toJSONString(params);
        String url = String.format(subUrl, ApiClientHelper.getParamUrl());

        Context context = AppContext.getInstance();
        if (handler instanceof WithContextTextHttpResponseHandler) {
            context = ((WithContextTextHttpResponseHandler) handler).getContext();
        }

        ApiHttpClient.postOffline(context, url, jsonString, handler);
    }
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
    public static void studentTrainlist(String token, long date, String status, AsyncHttpResponseHandler
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
    public static void courseDetail(String token, String courseCode, String planCode,
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
    public static void evaluate(String token, String comment, String courseCode, float courseStar,
                                String planCode, float teacherStar, AsyncHttpResponseHandler
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

    /**
     * 1.5获取评价信息
     * @param token
     * @param courseCode
     * @param planCode
     * @param handler
     */
    public static void evaluateDetail(String token, String courseCode, String planCode,
                                    AsyncHttpResponseHandler handler) {
        if (null == token || null == courseCode || null == planCode) {
            onError(handler);
            return;
        }

        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("data", new AssignData(courseCode, planCode));
        postRequest(params, "course/student/evaluate/detail%s", handler);
    }


    /**
     * 2.1 教师开始上课
     *
     * @param token
     * @param courseCode
     * @param planCode
     * @param handler
     */
    public static void startCourse(String token, String courseCode, String planCode, AsyncHttpResponseHandler
                                        handler) {
        if (null == token || null == courseCode || null == planCode) {
            onError(handler);
            return;
        }

        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("data", new StartOrEndCourseData(courseCode, planCode));
        postRequest(params, "course/teacher/start%s", handler);
    }
    /**
     * 2.2 教师结束上课
     *
     * @param token
     * @param courseCode
     * @param planCode
     * @param handler
     */
    public static void endCourse(String token, String courseCode, String planCode, AsyncHttpResponseHandler
            handler) {
        if (null == token || null == courseCode || null == planCode) {
            onError(handler);
            return;
        }

        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("data", new StartOrEndCourseData(courseCode, planCode));
        postRequest(params, "course/teacher/end%s", handler);
    }

    /**
     * 2.3 教师评价
     * @param token
     * @param comment
     * @param courseCode
     * @param planCode
     * @param score
     * @param studentCode
     * @param studentName
     * @param handler
     */
    public static void teacherEvaluate(String token, String comment, String courseCode, String planCode, int score, String studentCode, String studentName, AsyncHttpResponseHandler
            handler) {
        if (null == token || null == comment || null == courseCode||null == planCode) {
            onError(handler);
            return;
        }

        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("data", new TeacherEvaluateData(comment,courseCode,planCode,score,studentCode,studentName));
        postRequest(params, "course/teacher/evaluate%s", handler);
    }

    /**
     * 2.4 教师评价学员列表
     * @param token
     * @param courseCode
     * @param pageSize
     * @param planCode
     * @param timeStamp
     * @param type
     * @param handler
     */
    public static void teacherEvaluateStudentList(String token, String courseCode, int pageSize, String planCode, long timeStamp, int type, AsyncHttpResponseHandler
            handler) {
        if (null == token || null == courseCode || pageSize<=0||null==planCode||(type!=1&&type!=2&&type!=3)) {
            onError(handler);
            return;
        }

        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("data", new TeacherEvaluateStudentListData(courseCode, pageSize, planCode, timeStamp, type));
        postRequest(params, "course/teacher/student/list%s", handler);
    }

    /**
     * 2.5 教师课程详情
     * @param token
     * @param courseCode
     * @param planCode
     * @param handler
     */
    public static void teacherCourseDetail(String token, String courseCode, String planCode, AsyncHttpResponseHandler
            handler) {
        if (null == token || null == courseCode || null == planCode) {
            onError(handler);
            return;
        }

        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("data", new StartOrEndCourseData(courseCode, planCode));
        postRequest(params, "course/teacher/detail%s", handler);
    }

    /**
     * 2.6 获取讲师的课程列表
     * @param token
     * @param date
     * @param status
     * @param handler
     */
    public static void teacherCourseList(String token, long date, String status, AsyncHttpResponseHandler
            handler) {
        if (null == token  || null == status) {
            onError(handler);
            return;
        }

        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("data", new CourseListData(date, status));
        postRequest(params, "course/teacher/list%s", handler);
    }

    /**
     * 2.7 教师获取评价信息
     * @param token
     * @param courseCode
     * @param planCode
     * @param studentCode
     * @param handler
     */
    public static void teacherGetEvaluateDetail(String token, String courseCode,String planCode,String studentCode, AsyncHttpResponseHandler
            handler) {
        if (null == token || null==courseCode || null == planCode|| null == studentCode) {
            onError(handler);
            return;
        }

        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("data", new TaecherGetEvaluateDetailData(courseCode,planCode,studentCode));
        postRequest(params, "course/teacher/evaluate/detail%s", handler);
    }
    /**
     * 2.8 获取评价学员页面的评价数量
     * @param token
     * @param courseCode
     * @param planCode
     * @param handler
     */
    public static void taecherEvaluateNum(String token, String courseCode,String planCode,AsyncHttpResponseHandler
            handler) {
        if (null == token || null==courseCode || null == planCode) {
            onError(handler);
            return;
        }

        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("data", new StartOrEndCourseData(courseCode,planCode));
        postRequest(params, "course/teacher/evaluate/num%s", handler);
    }
    /**
     * 2.9 获取列表角标
     * @param type
     * @param handler
     */
    public static void listNum(String token,String type,AsyncHttpResponseHandler
            handler) {
        if (null==token||null==type) {
            onError(handler);
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("data", new ListNumData(type));
        postRequest(params, "common/list/num%s", handler);
    }
}

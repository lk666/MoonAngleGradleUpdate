package cn.com.bluemoon.delivery.module.track;

import android.os.Handler;

import com.alibaba.fastjson.JSON;

import cz.msebera.android.httpclient.Header;

import org.apache.http.protocol.HTTP;

import java.util.ArrayList;
import java.util.List;

import cn.com.bluemoon.delivery.AppContext;
import cn.com.bluemoon.delivery.db.entity.ReqBody;
import cn.com.bluemoon.delivery.db.entity.ShareParam;
import cn.com.bluemoon.delivery.db.manager.ReqBodyDaoManager;
import cn.com.bluemoon.delivery.module.track.api.ResultTrack;
import cn.com.bluemoon.delivery.module.track.api.TrackApi;
import cn.com.bluemoon.delivery.module.track.api.WithStatusTextHttpResponseHandler;
import cn.com.bluemoon.delivery.module.track.bean.Menu;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.liblog.LogUtils;
import cn.com.bluemoon.liblog.NetLogUtils;

/**
 * 数据埋点管理类
 * Created by bm on 2017/5/10.
 */

public class TrackManager {

    private static final String TAG = "GreenDao";

    /**
     * 启动应用检测数据，如果还有上次未上传的就上传
     */
    public static void checkData() {
//        LogUtils.d(TAG,getAllByJson());
        uploadTracks(ReqBodyDaoManager.getAllList());
    }

    /**
     * 增加分享数据
     *
     * @param url   分享的url
     * @param toTag 分享的平台
     */
    public static void addBody(String url, String toTag) {
        List<ShareParam> list = new ArrayList<>();
        list.add(new ShareParam(url, toTag));
        String param = JSON.toJSONString(list);
        ReqBodyDaoManager.addBody(TrackCode.CODE_SHARE, param);
        LogUtils.d(TAG, "add==> param:" + param);
    }

    /**
     * 增加菜单埋点数据
     */
    public static void addMenu(String code, String url) {
        List<Menu> list = new ArrayList<>();
        list.add(new Menu(url));
        String param = JSON.toJSONString(list);
        ReqBodyDaoManager.addBody(code, param);
        LogUtils.d(TAG, "add==> code:" + code + " param:" + param);
    }

    /**
     * 把数据库所有数据转化为json字符串
     */
    public static String getAllByJson() {
        return JSON.toJSONString(ReqBodyDaoManager.getAllList());
    }

    /**
     * 上传指定的埋点数据
     *
     * @param list
     */
    public static void uploadTracks(List<ReqBody> list) {
        if (!list.isEmpty()) {
            LogUtils.d(TAG, "uploadTracks size==>" + list.size());
            //获取当前时间
            long status = System.currentTimeMillis();
            //把需要上传的状态改为上传状态
            ReqBodyDaoManager.updateListToUpload(status);
            //上传数据
            TrackApi.postTrack(list, getHandler(0, status));
        }
    }

    /**
     * 上传新的埋点数据（退出后台时使用）
     */
    public static void uploadNewTracks() {
        uploadTracks(ReqBodyDaoManager.getNewList());
    }


    /**
     * 上传埋点数据
     */
    private static WithStatusTextHttpResponseHandler getHandler(final int requestCode, final long
            status) {
        WithStatusTextHttpResponseHandler handler = new WithStatusTextHttpResponseHandler(
                HTTP.UTF_8, AppContext.getInstance(), requestCode, status, null) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    ResultTrack result = JSON.parseObject(responseString, ResultTrack.class);
                    if (Constants.RESPONSE_RESULT_SUCCESS_TRACK.equals(result.getRespCode())
                            || Constants.RESPONSE_RESULT_ERROR_TRACK.equals(result.getRespCode())) {

                        //删除这个时间点上传的数据
                        ReqBodyDaoManager.deleteListByStatus(status);
                        LogUtils.d(TAG, "uploadTracks success==>" + DateUtil.getTime(status));

                        NetLogUtils.dNetResponse(Constants.TAG_HTTP_RESPONSE_SUCCESS, getUuid(),
                                System.currentTimeMillis(), responseString);
                    } else if (Constants.RESPONSE_RESULT_TOO_LONG_TRACK.equals(result.getRespCode
                            ())) {
                        NetLogUtils.eNetResponse(Constants.TAG_HTTP_RESPONSE_NOT_SUCCESS,
                                getUuid(), System
                                        .currentTimeMillis(), responseString, new Exception
                                        ("resultBase.getResponseCode() = " + result.getRespCode()
                                                + "-->" + responseString));
                        // TODO: 2016/12/27 数据太长处理
                    }
                } catch (Exception e) {
                    NetLogUtils.eNetResponse(Constants.TAG_HTTP_RESPONSE_EXCEPTION, getUuid(),
                            System.currentTimeMillis(), responseString, e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable
                    throwable) {
                NetLogUtils.eNetResponse(Constants.TAG_HTTP_RESPONSE_FAILURE, getUuid(), System
                        .currentTimeMillis(), responseString, throwable);

            }
        };
        return handler;
    }


}

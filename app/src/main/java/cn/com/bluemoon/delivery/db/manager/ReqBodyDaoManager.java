package cn.com.bluemoon.delivery.db.manager;


import android.os.Build;
import android.text.TextUtils;

import java.util.List;

import cn.com.bluemoon.delivery.AppContext;
import cn.com.bluemoon.delivery.BuildConfig;
import cn.com.bluemoon.delivery.app.api.ApiClientHelper;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.db.entity.ReqBody;
import cn.com.bluemoon.delivery.db.gen.ReqBodyDao;
import cn.com.bluemoon.delivery.utils.NetWorkUtil;
import cn.com.bluemoon.liblog.LogUtils;

/**
 * 埋点数据表管理类
 * Created by bm on 2016/12/19.
 */

public class ReqBodyDaoManager {


    private final static String TAG = "GreenDao";
    private static final String TRACK_APP_ID = "APP_ANGEL";
    private static final String EVENT_TYPE = "btn";
    //状态标识，已上传时间为值，值为0表示还没有上传
    private static final long STATUS_NEW = 0;


    /**
     * 增加数据
     *
     * @param code  当前节点
     * @param param 附带参数（目前是gps）
     */
    public static void addBody(String code, String param) {
        String uid = ClientStateManager.getUserName();
        if (TextUtils.isEmpty(uid)) {
            uid = "nologin";
        }
        ReqBody body = new ReqBody(System.currentTimeMillis(), AppContext.getInstance().getAppId(),
                TRACK_APP_ID, BuildConfig.VERSION_NAME, ApiClientHelper.CLIENT, uid, code,
                EVENT_TYPE, param, STATUS_NEW, NetWorkUtil.getLocalIpAddress(AppContext
                .getInstance()), "", "", NetWorkUtil.getMacAddressFromIp(AppContext.getInstance()
        ), Build.BRAND, Build.MODEL, ClientStateManager.getUserAgent());
        DBHelper.getDaoSession().getReqBodyDao().insert(body);
        LogUtils.d(TAG, "insert==>" + body.toString());
    }


    /**
     * 根据主键删除Body
     *
     * @param id Body的主键Id
     */
    public static void deleteBodyById(long id) {
        DBHelper.getDaoSession().getReqBodyDao().deleteByKey(id);
        LogUtils.d(TAG, "delete==>" + id);
    }

    /**
     * 删除Body全部数据
     */
    public static void deleteAll() {
        DBHelper.getDaoSession().getReqBodyDao().deleteAll();
        LogUtils.d(TAG, "delete==>all");
    }


    /**
     * 更新数据
     *
     * @param body 需要更新的实体
     */
    public static void updateBody(ReqBody body) {
        DBHelper.getDaoSession().getReqBodyDao().update(body);
        LogUtils.d(TAG, "update==>" + body.getCode());
    }


    /**
     * 查找所有数据列表
     */
    public static List<ReqBody> getAllList() {
        return DBHelper.getDaoSession().getReqBodyDao().loadAll();
    }

    /**
     * 根据某个时间点状态获取数据埋点
     *
     * @param status
     * @return
     */
    public static List<ReqBody> getListByStatus(long status) {
        return DBHelper.getDaoSession().getReqBodyDao()
                .queryBuilder()
                .where(ReqBodyDao.Properties.Status.eq(status)).build()
                .list();
    }

    /**
     * 获取上传状态下的所有
     *
     * @return
     */
    public static List<ReqBody> getUploadList() {
        return DBHelper.getDaoSession().getReqBodyDao()
                .queryBuilder()
                .where(ReqBodyDao.Properties.Status.notEq(STATUS_NEW)).build()
                .list();
    }

    /**
     * 获取未上传状态的数据
     *
     * @return
     */
    public static List<ReqBody> getNewList() {
        return getListByStatus(STATUS_NEW);
    }

    /**
     * 批量更新未上传状态的所有数据为上传状态
     *
     * @param status 上传的时间点
     */
    public static void updateListToUpload(long status) {
        List<ReqBody> list = getNewList();
        if (!list.isEmpty()) {
            for (ReqBody body : list) {
                body.setStatus(status);
                updateBody(body);
            }
        }
    }

    /**
     * 批量删除某种状态的所有数据
     *
     * @param status 上传的时间点
     */
    public static void deleteListByStatus(long status) {
        List<ReqBody> list = getListByStatus(status);
        for (ReqBody reqBody : list) {
            DBHelper.getDaoSession().getReqBodyDao().delete(reqBody);
        }
    }


    /**
     * 批量删除某个时间点上传的数据
     *
     * @param status 上传的时间点
     */
    public static void deleteUploadList(long status) {
        deleteListByStatus(status);
    }

}


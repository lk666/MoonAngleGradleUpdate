package cn.com.bluemoon.delivery.sz.sqlite;

import android.content.ContentValues;
import android.content.Context;

import android.database.sqlite.SQLiteDatabase;


import cn.com.bluemoon.delivery.AppContext;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.utils.StringUtil;

/**
 * Created by dujiande on 2016/8/10.
 */
public class MsgDBUtil {
    private final static String TAG = MsgDBUtil.class.getSimpleName();

    private Context context;
    private DBOpenHelper helper;

    private static MsgDBUtil instance = null;

    private MsgDBUtil(){}

    public synchronized static MsgDBUtil getInstance(){
        if(instance == null){
            instance = new MsgDBUtil();
        }
        instance.init();
        return instance;
    }

    private void init() {
        context = AppContext.getInstance().getApplicationContext();
        String username = ClientStateManager.getUserName();
        if(!StringUtil.isEmptyString(username)){
            DBOpenHelper.DB_NAME = username+".db";
        }
        helper   = new DBOpenHelper(context);
    }

    /**
     * 插入营销推送消息
     * @param yunBaMsgVo
     * @return
     */
//    public long insertBeaconMsg( final YunBaMsgVo yunBaMsgVo) {
//        long rowId = -1;
//        SQLiteDatabase db = null;
//        if(yunBaMsgVo == null){
//            return rowId;
//        }
//        try {
//            ContentValues values =  new ContentValues();
//            values.put("title",yunBaMsgVo.getTitle());
//            values.put("locid",yunBaMsgVo.getLocid());
//            values.put("alert",yunBaMsgVo.getAlert());
//            values.put("shopid",yunBaMsgVo.getShopid());
//            values.put("content",yunBaMsgVo.getContent());
//            values.put("button",yunBaMsgVo.getButton());
//            values.put("img_url",yunBaMsgVo.getImg_url());
//            values.put("insert_time", System.currentTimeMillis());
//            values.put("button_url",yunBaMsgVo.getButton_url());
//            values.put("has_look",yunBaMsgVo.isHasLook() ? 1 : 0);
//            db = helper.getWritableDatabase();
//            rowId = db.insert(TableOpenHelper.MSG_TBL, null, values);
//        } catch (Exception e){
//            e.printStackTrace();
//        } finally {
//            if(null != db)
//                db.close();
//        }
//        return rowId;
//    }



    /**
     * 获取前20条营销推送消息
     * @return
     */
//    public ArrayList<YunBaMsgVo> queryBeaconMsg(){
//        ArrayList<YunBaMsgVo> beaconMsgList = null;
//        YunBaMsgVo yunBaMsgVo = null;
//        SQLiteDatabase db = null;
//        Cursor cursor = null;
//        if (null != helper) {
//            try {
//                db = helper.getReadableDatabase();
//                cursor = db.query(TableOpenHelper.MSG_TBL, null,
//                        null, null, null, null,"insert_time desc","20");
//                if (cursor != null && cursor.getCount() > 0) {
//                    beaconMsgList = new ArrayList<YunBaMsgVo>();
//                    while (cursor.moveToNext()) {
//                        yunBaMsgVo = new YunBaMsgVo();
//                        yunBaMsgVo.setId(cursor.getInt(cursor.getColumnIndex("_id")));
//                        yunBaMsgVo.setTitle(cursor.getString(cursor.getColumnIndex("title")));
//                        beaconMsgList.add(yunBaMsgVo);
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//
//                if (null != cursor) {
//                    cursor.close();
//                }
//
//                if (null != db) {
//                    db.close();
//                }
//            }
//
//        }
//        return beaconMsgList;
//    }

    /**
     * 更新已读状态
     */
    public void updateBeaconMsgRead(int id){
        SQLiteDatabase db = null;
        String sql = null;
        try {
            db= helper.getWritableDatabase();
            sql = "update "+TableOpenHelper.MSG_TBL+" set has_look=1 where _id = "+ id;
            db.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            if(null != db)
                db.close();
        }
    }
}

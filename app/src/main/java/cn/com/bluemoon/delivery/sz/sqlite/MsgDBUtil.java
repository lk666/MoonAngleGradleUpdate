package cn.com.bluemoon.delivery.sz.sqlite;

import android.content.ContentValues;
import android.content.Context;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import cn.com.bluemoon.delivery.AppContext;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.sz.vo.PushMsgVo;
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
     * 插入或更新最新的日程ID信息
     * @param pushMsgVo
     * @return
     */
    public long insertPushMsg( final PushMsgVo pushMsgVo) {
        long rowId = -1;
        SQLiteDatabase db = null;
        if(pushMsgVo == null){
            return rowId;
        }
        String lastSchedualId = queryLastSchedualId(pushMsgVo);

        if(lastSchedualId.equals("0")){
            try {
                ContentValues values =  new ContentValues();
                values.put("b_date",pushMsgVo.getbDate());
                values.put("type",pushMsgVo.getType());
                values.put("target_no",pushMsgVo.getTargetNo());
                values.put("schedual_id",pushMsgVo.getSchedualId());
                db = helper.getWritableDatabase();
                rowId = db.insert(TableOpenHelper.MSG_TBL, null, values);
            } catch (Exception e){
                e.printStackTrace();
            } finally {
                if(null != db)
                    db.close();
            }
        }else{
            long lastId = Long.parseLong(lastSchedualId);
            long currentId = Long.parseLong(pushMsgVo.getSchedualId());
            if(currentId > lastId){
                try {
                    ContentValues values =  new ContentValues();
                    values.put("b_date",pushMsgVo.getbDate());
                    values.put("type",pushMsgVo.getType());
                    values.put("target_no",pushMsgVo.getTargetNo());
                    values.put("schedual_id",pushMsgVo.getSchedualId());
                    db = helper.getWritableDatabase();
                    rowId = db.update(TableOpenHelper.MSG_TBL, values,"b_date=? and type=? and target_no=?",
                            new String[]{pushMsgVo.getbDate(), pushMsgVo.getType(), pushMsgVo.getTargetNo()});
                } catch (Exception e){
                    e.printStackTrace();
                } finally {
                    if(null != db)
                        db.close();
                }
            }
        }

        return rowId;
    }


    /**
     * 查询数据库保存的日程ID
     * @param pushMsgVo
     * @return
     */
    public String queryLastSchedualId(PushMsgVo pushMsgVo){
        SQLiteDatabase db = null;
        Cursor cursor = null;
        String lastSchedualId = "0";
        if (null != helper) {
            try {
                db = helper.getReadableDatabase();
                cursor = db.query(TableOpenHelper.MSG_TBL, new String[]{"schedual_id"},
                        "b_date=? and type=? and target_no=?",
                        new String[]{pushMsgVo.getbDate(), pushMsgVo.getType(), pushMsgVo.getTargetNo()}, null, null, null);


                if (cursor != null && cursor.getCount() > 0) {
                    if (cursor.moveToFirst()) {
                        lastSchedualId = cursor.getString(cursor.getColumnIndex("schedual_id"));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

                if (null != cursor) {
                    cursor.close();
                }

                if (null != db) {
                    db.close();
                }
            }
        }
        return lastSchedualId;
    }

    /**
     * 更新已读状态
     */
//    public void updateBeaconMsgRead(int id){
//        SQLiteDatabase db = null;
//        String sql = null;
//        try {
//            db= helper.getWritableDatabase();
//            sql = "update "+TableOpenHelper.MSG_TBL+" set has_look=1 where _id = "+ id;
//            db.execSQL(sql);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally{
//            if(null != db)
//                db.close();
//        }
//    }
}

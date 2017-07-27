package cn.com.bluemoon.delivery.db.manager;


import cn.com.bluemoon.delivery.AppContext;
import cn.com.bluemoon.delivery.db.gen.DaoMaster;
import cn.com.bluemoon.delivery.db.gen.DaoSession;

/**
 * Created by bm on 2016/12/19.
 */
public class DBHelper {

    private static DBHelper instance;
    private static DaoMaster daoMaster;
    private static DaoSession daoSession;

    private DBHelper() {
        DaoMaster.OpenHelper helper = new DaoMaster.DevOpenHelper(AppContext.getInstance(),
                "data_track.db", null);
        daoMaster = new DaoMaster(helper.getWritableDatabase());
        daoSession = daoMaster.newSession();
    }

    public static DBHelper getInstance() {
        if(instance == null){
            instance = new DBHelper();
        }
        return instance;
    }

    //取得DaoMaster
    public static DaoMaster getDaoMaster() {
        return daoMaster;
    }

    //取得DaoSession
    public static DaoSession getDaoSession() {
        return daoSession;
    }
}

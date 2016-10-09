package cn.com.bluemoon.delivery.sz.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by dujiande on 2016/8/10.
 */
public class DBOpenHelper extends SQLiteOpenHelper {

    public static String DB_NAME = "bluemoon_sqlite.db";//根据每个用户创建一份数据库
    public static final int VERSION = 1;// 数据库版本

    public DBOpenHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    public DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                        int version) {
        super(context, name, factory, version);
    }

    /**
     * 创建数据库时调用
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        ORMOpenHelper.createTables(db);
    }

    /**
     * 当数据库更新时调用
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        ORMOpenHelper.upgradeTables(db, TableOpenHelper.getTableNames());
    }
}

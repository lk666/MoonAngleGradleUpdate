package cn.com.bluemoon.delivery.sz.sqlite;

/**
 * Created by dujiande on 2016/8/10.
 */
public class TableOpenHelper {

    public static final String MSG_TBL   = "message_tbl"; //消息数据库

    /**
     * 创建消息表sql语句
     */
    public static String MSG_TBL_SQL =
            "create table if not exists "
                    + MSG_TBL
                    + "("
                    + " b_date text, "//日期
                    + " type text, "//类型
                    + " target_no text , "//目标用户编号
                    + " schedual_id text "//百度推送最新的日程ID
                    + " )";

    /**
     * 获取数据库所有表名
     * @return
     */
    public static String[] getTableNames(){
        return new String[]{
                MSG_TBL,//消息表
        };
    }
}

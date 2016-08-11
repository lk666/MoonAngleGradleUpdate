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
                    + " userid text primary key, "//用户ID
                    + " username text, "//用户姓名
                    + " is_payment integer, "//是否已绑定支付方式 0:未绑定 1:绑定
                    + " insert_time long , "//时间戳
                    + " lasttime text, "//最后登录时间
                    + " idcard text "//身份证号
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

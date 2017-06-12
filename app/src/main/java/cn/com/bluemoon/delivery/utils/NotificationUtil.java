package cn.com.bluemoon.delivery.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import cn.com.bluemoon.delivery.R;

/**
 * Created by bm on 2016/10/24.
 */
public class NotificationUtil {

    public static int TYPE_NORMAL = 10000;

    /**
     * 展示简单通知
     * @param context
     * @param title
     * @param content
     * @param intent
     */
    public static void showSimpleNotify(Context context,String title, String content, Intent intent){
        showNotification(context, getSimpleNotify(context,title,content,intent));
    }

    /**
     * 获取简单通知
     * @param context
     * @param title
     * @param content
     * @param intent
     * @return
     */
    public static Notification getSimpleNotify(Context context, String title, String content, Intent intent) {
        //为了版本兼容  选择V7包下的NotificationCompat进行构造
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        //Ticker是状态栏显示的提示
        builder.setTicker(title);
        //第一行内容  通常作为通知栏标题
        builder.setContentTitle(title);
        //第二行内容 通常是通知正文
        builder.setContentText(content);
        //第三行内容 通常是内容摘要什么的 在低版本机器上不一定显示
        //builder.setSubText(null);
        //设置是否显示时间
        builder.setShowWhen(true);
        //设置推送时间
        //builder.setWhen(System.currentTimeMillis());
        //ContentInfo 在通知的右侧 时间的下面 用来展示一些其他信息
        //builder.setContentInfo("2");
        //number设计用来显示同种通知的数量和ContentInfo的位置一样，如果设置了ContentInfo则number会被隐藏
        //builder.setNumber(2);
        //可以点击通知栏的删除按钮删除
        builder.setAutoCancel(true);
        //系统状态栏显示的小图标
        builder.setSmallIcon(R.mipmap.icon);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            //下拉显示的大图标
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.icon));
        }
        //Intent intent = new Intent(this,MainActivity.class);
        //设置intent的extras为实时更新模式PendingIntent.FLAG_UPDATE_CURRENT（共四种模式）
        PendingIntent pIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent
                .FLAG_UPDATE_CURRENT);
        //点击跳转的intent
        builder.setContentIntent(pIntent);
        //通知默认的声音 震动 呼吸灯
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        return  builder.build();
    }

    /**
     * 展示通知
     * @param context
     * @param notification
     * @param notificationId
     */
    public static void showNotification(Context context,Notification notification,int notificationId){
        if(notification!=null){
            NotificationManager manger = (NotificationManager) context.getSystemService(Context
                    .NOTIFICATION_SERVICE);
            manger.notify(notificationId, notification);
        }
    }

    public static void showNotification(Context context,Notification notification){
        showNotification(context,notification, TYPE_NORMAL++);
    }
}

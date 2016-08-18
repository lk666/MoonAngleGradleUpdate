package cn.com.bluemoon.delivery.sz.util;

import android.view.View;
import android.widget.TextView;

/**
 * Created by dujiande on 2016/8/17.
 */
public class ViewUtil {

    public static void setTipsNum(TextView textView,int num){
        if(num > 0 && num < 100){
            textView.setVisibility(View.VISIBLE);
            textView.setText(""+num);
        }else if(num >= 100){
            textView.setVisibility(View.VISIBLE);
            textView.setText("99+");
        }else{
            textView.setVisibility(View.INVISIBLE);
        }
    }

    public static String createHtmlStr(String meet1,String meet2){
        String html = "";
        String meet1Html = getYellowFont(meet1);
        String meet2Html = getYellowFont(meet2);
        html = meet1Html+getBlackFont("与")+meet2Html+getBlackFont("时间安排有冲突，请根据实际情况进行调整");

        return html;
    }

    public static String getYellowFont(String text){
        return "<font color=\"#FF863E\">" + text + "</font> ";
    }

    public static String getBlackFont(String text){
        return "<font color=\"#464646\">" + text + "</font> ";
    }
}

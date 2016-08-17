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
}

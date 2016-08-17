package cn.com.bluemoon.delivery.sz.util;

import android.content.Context;
import android.widget.Toast;

import cn.com.bluemoon.delivery.utils.PublicUtil;


/**
 * Created by dujiande on 2016/3/21.
 */
public class AsyncHttpClientUtil {

    public static void onFailure(Context context, int statusCode){
        if(statusCode == 401){
            PublicUtil.showToast("Token 失效，请重新登录");
        }else if(statusCode == 0){
            PublicUtil.showToastServerOvertime(context);
        }else if(statusCode == 408){
            PublicUtil.showToast("请求超时");
        }else if(statusCode == 504){
            PublicUtil.showToast("网关超时");
        }else{
            PublicUtil.showToast("API 错误："+statusCode);
        }
    }

}

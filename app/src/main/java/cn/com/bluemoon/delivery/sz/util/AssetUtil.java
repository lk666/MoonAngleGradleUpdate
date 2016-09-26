package cn.com.bluemoon.delivery.sz.util;

import android.content.Context;

import org.apache.http.util.EncodingUtils;

import java.io.InputStream;



/**
 * Created by dujiande on 2016/8/11.
 */
public class AssetUtil {

    public static String getContent(Context context, String fileName){

        String ret = "";
        try {
            InputStream is = context.getResources().getAssets().open(fileName);
            int len = is.available();
            byte[] buffer = new byte[len];
            is.read(buffer);
            ret = EncodingUtils.getString(buffer, "utf-8");
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }
}

package cn.com.bluemoon.lib_iflytek.utils;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * 初始化工具类
 * Created by bm on 2017/10/24.
 */

public class SpeechUtil {

    public static String PATH;
    public static boolean RELEASE;

    /**
     * (每个sdk对应的id都是固定的，所以直接写死在依赖包)
     */
    public static void init(Application app,String cachePath,boolean release){
        PATH = cachePath + "/msc/iat.wav";
        RELEASE = release;
        SpeechUtility.createUtility(app, SpeechConstant.APPID + "=5a5430a3");
    }


    /**
     * 读取asset目录下文件。
     * @return content
     */
    public static String readFile(Context mContext, String file, String code)
    {
        int len = 0;
        byte []buf = null;
        String result = "";
        try {
            InputStream in = mContext.getAssets().open(file);
            len  = in.available();
            buf = new byte[len];
            in.read(buf, 0, len);

            result = new String(buf,code);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    /**
     * 将字节缓冲区按照固定大小进行分割成数组
     * @param buffer 缓冲区
     * @param length 缓冲区大小
     * @param spsize 切割块大小
     * @return
     */
    public ArrayList<byte[]> splitBuffer(byte[] buffer, int length, int spsize)
    {
        ArrayList<byte[]> array = new ArrayList<byte[]>();
        if(spsize <= 0 || length <= 0 || buffer == null || buffer.length < length)
            return array;
        int size = 0;
        while(size < length)
        {
            int left = length - size;
            if(spsize < left)
            {
                byte[] sdata = new byte[spsize];
                System.arraycopy(buffer,size,sdata,0,spsize);
                array.add(sdata);
                size += spsize;
            }else
            {
                byte[] sdata = new byte[left];
                System.arraycopy(buffer,size,sdata,0,left);
                array.add(sdata);
                size += left;
            }
        }
        return array;
    }
    /**
     * 获取语记是否包含离线听写资源，如未包含跳转至资源下载页面
     *1.PLUS_LOCAL_ALL: 本地所有资源
     2.PLUS_LOCAL_ASR: 本地识别资源
     3.PLUS_LOCAL_TTS: 本地合成资源
     */
    public static String checkLocalResource(){
        String resource = SpeechUtility.getUtility().getParameter(SpeechConstant.PLUS_LOCAL_ASR);
        try {
            JSONObject result = new JSONObject(resource);
            int ret = result.getInt(SpeechUtility.TAG_RESOURCE_RET);
            switch (ret) {
                case ErrorCode.SUCCESS:
                    JSONArray asrArray = result.getJSONObject("result").optJSONArray("asr");
                    if (asrArray != null) {
                        int i = 0;
                        // 查询否包含离线听写资源
                        for (; i < asrArray.length(); i++) {
                            if("iat".equals(asrArray.getJSONObject(i).get(SpeechConstant.DOMAIN))){
                                //asrArray中包含语言、方言字段，后续会增加支持方言的本地听写。
                                //如："accent": "mandarin","language": "zh_cn"
                                break;
                            }
                        }
                        if (i >= asrArray.length()) {

                            SpeechUtility.getUtility().openEngineSettings(SpeechConstant.ENG_ASR);
                            return "没有听写资源，跳转至资源下载页面";
                        }
                    }else {
                        SpeechUtility.getUtility().openEngineSettings(SpeechConstant.ENG_ASR);
                        return "没有听写资源，跳转至资源下载页面";
                    }
                    break;
                case ErrorCode.ERROR_VERSION_LOWER:
                    return "语记版本过低，请更新后使用本地功能";
                case ErrorCode.ERROR_INVALID_RESULT:
                    SpeechUtility.getUtility().openEngineSettings(SpeechConstant.ENG_ASR);
                    return "获取结果出错，跳转至资源下载页面";
                case ErrorCode.ERROR_SYSTEM_PREINSTALL:
                    //语记为厂商预置版本。
                default:
                    break;
            }
        } catch (Exception e) {
            SpeechUtility.getUtility().openEngineSettings(SpeechConstant.ENG_ASR);
            return "获取结果出错，跳转至资源下载页面";
        }
        return "";
    }

    /**
     * 读取asset目录下音频文件。
     *
     * @return 二进制文件数据
     */
    public static byte[] readAudioFile(Context context, String filename) {
        try {
            InputStream ins = context.getAssets().open(filename);
            byte[] data = new byte[ins.available()];

            ins.read(data);
            ins.close();

            return data;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    // 判断权限集合
    public static boolean lacksPermissions(Context context, String... permissions) {
        for (String permission : permissions) {
            if (lacksPermission(context, permission)) {
                return true;
            }
        }
        return false;
    }

    // 判断是否缺少权限
    public static boolean lacksPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) !=
                PackageManager.PERMISSION_GRANTED;
    }

    // 请求权限兼容低版本
    public static void requestPermissions(Activity aty, String[] permissions, int requestCode) {
        ActivityCompat.requestPermissions(aty, permissions, requestCode);
    }

    // 检测权限配置
    public static boolean checkPermissions(Activity aty, String[] permissions, int requestCode) {
        if (permissions != null && permissions.length > 0) {
            if (lacksPermissions(aty, permissions)) {
                requestPermissions(aty, permissions, requestCode);
                return false;
            }
        }
        return true;
    }

    //录音+文件 权限
    public static final String[] PERMISSION_FILE_MICROPHONE = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,// 写入权限
            Manifest.permission.READ_EXTERNAL_STORAGE, //读取权限
            Manifest.permission.RECORD_AUDIO
    };

    /**
     * 检测录音文件权限
     */
    public static boolean checkRecordPermission(Activity aty){
        return checkPermissions(aty,PERMISSION_FILE_MICROPHONE,0);
    }

    /**
     * dp转px
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 设置控件可见性
     */
    public static void setViewVisibility(View view, int visibility) {
        if (view != null && view.getVisibility() != visibility) {
            view.setVisibility(visibility);
        }
    }

    /**
     * 设置沉浸式
     */
    public static void initTop(Activity aty) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = aty.getWindow();
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            window.setAttributes(layoutParams);
        }
    }
}

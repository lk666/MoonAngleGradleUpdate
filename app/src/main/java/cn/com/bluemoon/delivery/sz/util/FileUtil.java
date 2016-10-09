package cn.com.bluemoon.delivery.sz.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import cn.com.bluemoon.delivery.sz.bean.SchedualCommonBean;
import cn.com.bluemoon.delivery.sz.sqlite.MsgDBUtil;
import cn.com.bluemoon.delivery.sz.vo.PushMsgVo;
import cn.com.bluemoon.delivery.utils.StringUtil;


/**
 * Created by dujiande on 2016/8/15.
 */
public class FileUtil {

    public static String TAG = FileUtil.class.getSimpleName();

    public static final String notifycationCountFileName = "notificationCount.json";


    private static String readFromFile(File targetFile){
        String readedStr="";
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            try{
                if(targetFile.exists()){
                    InputStream in = new BufferedInputStream(new FileInputStream(targetFile));
                    BufferedReader br= new BufferedReader(new InputStreamReader(in, "UTF-8"));
                    String tmp;

                    while((tmp=br.readLine())!=null){
                        readedStr+=tmp;
                    }
                    br.close();
                    in.close();

                    return readedStr;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            Log.e(TAG,"未发现SD卡！");
        }
       return readedStr;
    }


    public static void savedToSD(File targetFile,String stringToWrite){

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            OutputStreamWriter osw;

            try{
                if(!targetFile.exists()){
                    targetFile.createNewFile();
                    osw = new OutputStreamWriter(new FileOutputStream(targetFile),"utf-8");
                    osw.write(stringToWrite);
                    osw.close();
                }else{
                    osw = new OutputStreamWriter(new FileOutputStream(targetFile,false),"utf-8");
                    osw.write(stringToWrite);
                    osw.flush();
                    osw.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            Log.e(TAG,"未发现SD卡！");
        }



    }

    public static String getPath(String userNo, String date, String type){
        if(type.equals("1")){
            type = "meeting";
        }else{
            type = "schedual";
        }

        String foldername =  Constants.PATH_SCHEDUAL;
        File folder = new File(foldername);
        if (folder == null || !folder.exists()) {
            folder.mkdir();
        }

        foldername = Constants.PATH_SCHEDUAL + File.separator + userNo;
        folder = new File(foldername);
        if (folder == null || !folder.exists()) {
            folder.mkdir();
        }

        foldername = Constants.PATH_SCHEDUAL + File.separator + userNo + File.separator + type;
        folder = new File(foldername);
        if (folder == null || !folder.exists()) {
            folder.mkdir();
        }

        String path = Constants.PATH_SCHEDUAL + File.separator + userNo + File.separator +
                type + File.separator + userNo+"_"+date+".json";

        return  path;
    }

    public static String getSchedual(String userNo, String date, String type){
        String path = getPath(userNo, date, type);
        File file = new File(path);
        String json = readFromFile(file);
        return  json;
    }

    public static void setSchedual(String userNo, String date, String type,String jsonContent){
        String path = getPath(userNo, date, type);
        File file = new File(path);
        savedToSD(file,jsonContent);
    }

    public static void deleteSchedual(String userNo, String date, String type,String jsonContent){
        String path = getPath(userNo, date, type);
        File file = new File(path);
       file.delete();
    }

    public static boolean isUpdateSchedual(String userNo, String date, String type,SchedualCommonBean itemBean){
        String path = getPath(userNo, date, type);
        File file = new File(path);
        if(!file.exists()){
            return true;
        }
        long lastModified = file.lastModified();
        if(System.currentTimeMillis() - lastModified > Constants.UPDATE_TIME){
            return true;
        }

        if(itemBean!= null && !StringUtil.isEmptyString(itemBean.getSmID())){
            PushMsgVo pushMsgVo = new PushMsgVo();
            pushMsgVo.setbDate(date);
            pushMsgVo.setTargetNo(userNo);
            pushMsgVo.setType(type);


            String lastSchedualId = MsgDBUtil.getInstance().queryLastSchedualId(pushMsgVo);
            long lastId = Long.parseLong(lastSchedualId);
            long currentId = Long.parseLong(itemBean.getSmID());
            if(lastId > currentId){
                return true;
            }
        }

        return  false;
    }

    public static String getMainMsgCountPath(String userNo){
        String foldername =  Constants.PATH_SCHEDUAL;
        File folder = new File(foldername);
        if (folder == null || !folder.exists()) {
            folder.mkdir();
        }

        foldername = Constants.PATH_SCHEDUAL + File.separator + userNo;
        folder = new File(foldername);
        if (folder == null || !folder.exists()) {
            folder.mkdir();
        }
        String path = foldername+File.separator+notifycationCountFileName;
        return path;
    }

    public static String getMainMsgCount(String userNo){
        String path = getMainMsgCountPath(userNo);
        File file = new File(path);
        String json = readFromFile(file);
        return  json;
    }

    public static void setMainMsgCount(String userNo,String jsonContent){
        String path = getMainMsgCountPath(userNo);
        File file = new File(path);
        savedToSD(file,jsonContent);
    }

    public static void deleteMainMsgCount(String userNo){
        String path = getMainMsgCountPath(userNo);
        File file = new File(path);
        file.delete();
    }

    public static boolean isUpdateMsgMainTypeCount(String userNo){
        String path = Constants.PATH_SCHEDUAL + File.separator + userNo+File.separator+notifycationCountFileName;
        File file = new File(path);
        if(!file.exists()){
            return true;
        }
        long lastModified = file.lastModified();
        if(System.currentTimeMillis() - lastModified > Constants.UPDATE_TIME){
            return true;
        }
        return false;
    }

    public static String getSubMsgPath(String userNo,int msgType){
        String foldername =  Constants.PATH_SCHEDUAL;
        File folder = new File(foldername);
        if (folder == null || !folder.exists()) {
            folder.mkdir();
        }

        foldername = Constants.PATH_SCHEDUAL + File.separator + userNo;
        folder = new File(foldername);
        if (folder == null || !folder.exists()) {
            folder.mkdir();
        }
        String path = foldername+File.separator+"subMsg_"+msgType+".json";
        return path;
    }

    public static String getSubMsg(String userNo,int msgType){
        String path = getSubMsgPath(userNo,msgType);
        File file = new File(path);
        String json = readFromFile(file);
        return  json;
    }

    public static void setSubMsg(String userNo,int msgType,String jsonContent){
        String path = getSubMsgPath(userNo,msgType);
        File file = new File(path);
        savedToSD(file,jsonContent);
    }

    public static void deleteSubMsg(String userNo,int msgType){
        String path = getSubMsgPath(userNo,msgType);
        File file = new File(path);
        file.delete();
    }
}

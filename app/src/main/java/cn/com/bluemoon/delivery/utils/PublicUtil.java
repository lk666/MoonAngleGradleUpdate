package cn.com.bluemoon.delivery.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Paint;
import android.location.LocationManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Xml;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import cn.com.bluemoon.delivery.ClientStateManager;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.account.LoginActivity;
import cn.com.bluemoon.delivery.app.AppContext;
import cn.com.bluemoon.delivery.app.api.ApiClientHelper;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.card.TipsItem;
import cn.com.bluemoon.delivery.card.CardTabActivity;
import cn.com.bluemoon.delivery.order.OrderDetailActivity;
import cn.com.bluemoon.delivery.web.WebViewActivity;
import cn.com.bluemoon.lib.callback.JsConnectCallBack;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib.qrcode.utils.BarcodeUtil;
import cn.com.bluemoon.lib.qrcode.utils.Configure;
import cn.com.bluemoon.lib.utils.JsConnectManager;
import cn.com.bluemoon.lib.utils.LibPublicUtil;
import cn.com.bluemoon.lib.view.CommonAlertDialog;

public class PublicUtil extends LibPublicUtil {


    /**
     * 生成月亮天使图片文件夹路径下的图片名
     *
     * @return 返回图片名（取当前时间）
     */
    public static String getPhotoPath() {
        String fileName = "";
        String pathUrl = Constants.PATH_PHOTO;
        String imageName = System.currentTimeMillis() + ".jpg";
        File file = new File(pathUrl);
        file.mkdirs();
        fileName = pathUrl + "/" + imageName;
        return fileName;
    }

    /**
     * 生成月亮天使图片文件夹路径下的图片名（用户名+时间）
     *
     * @param id 用户名
     * @return 返回图片名（取当前时间）
     */
    public static String getPhotoPath(String id) {
        String fileName = "";
        String pathUrl = Constants.PATH_PHOTO;
        String imageName = id + "_" + System.currentTimeMillis() + ".jpg";
        File file = new File(pathUrl);
        file.mkdirs();
        fileName = pathUrl + "/" + imageName;
        return fileName;
    }

    /**
     * 生成缓存文件夹路径下的文件路径
     *
     * @param 文件名
     * @return 返回图片名
     */
    public static String getTempPath() {
        String filepath = "";
        String path = Constants.PATH_TEMP;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        filepath = path + File.separator + getPhotoName(".png");
        return filepath;
    }

    /**
     * 显示账户过期对话框
     *
     * @param context
     */
    public static void showMessageTokenExpire(final Activity context) {
        new CommonAlertDialog.Builder(context)
                .setCancelable(false)
                .setMessage(context.getString(R.string.token_out))
                .setPositiveButton(R.string.btn_ok,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // TODO Auto-generated method stub
                                Intent intent = new Intent(context, LoginActivity.class);
                                context.startActivity(intent);
                                context.finish();
                            }
                        }).show();
    }

    public static void showToast(String msg) {
        showToast(AppContext.getInstance(), msg);
    }

    public static void showToast(int resId) {
        try {
            showToast(AppContext.getInstance(), AppContext.getInstance().getString(resId));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 没网络提示
     *
     * @param context
     */
    public static void showToastNoInternet() {
        showToast(AppContext.getInstance(), AppContext.getInstance().getString(R.string.request_no_internet));
    }

    /**
     * 服务器繁忙提示
     *
     * @param context
     */
    public static void showToastServerBusy() {
        showToast(AppContext.getInstance(), AppContext.getInstance().getString(R.string.request_server_busy));
    }

    /**
     * 服务器超时提示
     *
     * @param context
     */
    public static void showToastServerOvertime() {
        showToast(AppContext.getInstance(), AppContext.getInstance().getString(R.string.request_server_overtime));
    }

    /**
     * 获取数据错误提示
     *
     * @param context
     */
    public static void showToastErrorData() {
        showToast(AppContext.getInstance(), AppContext.getInstance().getString(R.string.get_data_busy));
    }

    private static void initScanConfigure(Activity aty) {
        Configure.TITLE_BG_COLOR = aty.getResources().getColor(R.color.title_background);
        Configure.TITLE_TXT_COLOR = aty.getResources().getColor(R.color.white);
        Configure.BTN_TXT_COLOR = aty.getResources().getColor(R.color.white);
        Configure.MENU_VISIBILITY = View.GONE;
        Configure.TICKET_TITLE_VISIBILITY = View.GONE;
    }


    /**
     * 打开签收扫描界面
     *
     * @param aty
     * @param fragment
     * @param requestCode
     * @param resultCode  大于1的正整数
     */
    public static void openScanOrder(Activity aty, Fragment fragment, String title,
                                     String btnString, int requestCode, int resultCode) {
        initScanConfigure(aty);
        Configure.TITLE_TXT = title;
        Configure.BTN_CLICK_TXT = btnString;
        Configure.BUTTON_VISIBILITY = View.VISIBLE;
        if(fragment!=null){
            BarcodeUtil.openScan(aty, fragment, requestCode, resultCode);
        }else{
            BarcodeUtil.openScan(aty, requestCode, resultCode);
        }
    }

    public static void openScanTicket(Activity aty, String ticketName,
                                      String ticketCount, int requestCode, int resultCode) {
        initScanConfigure(aty);
        Configure.TICKET_COUNT_TIME = ticketCount;
        Configure.TICKET_COUNT_NAME = ticketName;
        Configure.TITLE_TXT = aty.getString(R.string.ticket_check_title);
        Configure.BTN_CLICK_TXT = aty.getString(R.string.ticket_code_btn_text);
        Configure.TICKET_TITLE_VISIBILITY = View.VISIBLE;
        Configure.BUTTON_VISIBILITY = View.VISIBLE;
        BarcodeUtil.openScan(aty, requestCode, resultCode);
        /*Configure.IS_KEEP_OPEN = true;
		Intent it_open = new Intent(aty,ScanActivity.class);
		it_open.putExtra("resultCode", resultCode);
		aty.startActivityForResult(it_open, requestCode);*/
    }

    /**
     * 打开打卡扫描界面
     */
    public static void openScanCard(Activity aty, Fragment fragment, String title, int requestCode) {
        initScanConfigure(aty);
        Configure.BUTTON_VISIBILITY = View.GONE;
        Configure.TITLE_TXT = title;
        if (fragment != null) {
            BarcodeUtil.openScan(aty, fragment, requestCode);
        } else {
            BarcodeUtil.openScan(aty, requestCode);
        }
    }

    public static void openScanCard(Activity aty, String title, int requestCode) {
        openScanCard(aty, null, title, requestCode);
    }

    public static String sha1(String str) {
        if (str == null || str.length() == 0) {
            return null;
        }

        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};

        try {
            MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
            mdTemp.update(str.getBytes());

            byte[] md = mdTemp.digest();
            int j = md.length;
            char buf[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                buf[k++] = hexDigits[byte0 >>> 4 & 0xf];
                buf[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(buf);
        } catch (Exception e) {
            return null;
        }
    }

    public static List<String> stringsToList(final String[] src) {
        if (src == null || src.length == 0) {
            return null;
        }
        final List<String> result = new ArrayList<String>();
        for (int i = 0; i < src.length; i++) {
            result.add(src[i]);
        }
        return result;
    }

    public final static String getMessageDigest(byte[] buffer) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(buffer);
            byte[] md = mdTemp.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }


    public static String genApiSign(String[] params) {

        //Arrays.sort(params);
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < params.length; i++) {
            content.append(params[i]);
        }

        return MD5.getMessageDigest(content.toString().getBytes())
                .toLowerCase();
    }


    public static String toXml(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();
        sb.append("<xml>");
        for (int i = 0; i < params.size(); i++) {
            sb.append("<" + params.get(i).getName() + ">");

            sb.append(params.get(i).getValue());
            sb.append("</" + params.get(i).getName() + ">");
        }
        sb.append("</xml>");

        Log.e("orion", sb.toString());
        return sb.toString();
    }

    public static Map<String, String> decodeXml(String content) {

        try {
            Map<String, String> xml = new HashMap<String, String>();
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(new StringReader(content));
            int event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {

                String nodeName = parser.getName();
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:

                        break;
                    case XmlPullParser.START_TAG:

                        if ("xml".equals(nodeName) == false) {
                            // 实例化student对象
                            xml.put(nodeName, parser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                event = parser.next();
            }

            return xml;
        } catch (Exception e) {
            Log.e("orion", e.toString());
        }
        return null;

    }

    public static String genNonceStr() {
        Random random = new Random();
        return MD5.getMessageDigest(String.valueOf(random.nextInt(10000))
                .getBytes());
    }

    public static long genTimeStamp() {
        return System.currentTimeMillis() / 1000;
    }

    public static String getCurrentTimeStamp() {
        return String.valueOf(System.currentTimeMillis());
    }

    private static String genOutTradNo() {
        Random random = new Random();
        return MD5.getMessageDigest(String.valueOf(random.nextInt(10000))
                .getBytes());
    }

    // 获得指定文件的byte数组
    public static byte[] getBytes(File file) {
        byte[] buffer = null;
        try {

            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    public static byte[] getBytes(Bitmap bit) {
        byte[] buffer = null;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bit.compress(CompressFormat.PNG, 95, out);
            buffer = out.toByteArray();
            out.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return buffer;
    }

    public static void showErrorMsg(Activity context, ResultBase resultBase) {
        if (Constants.RESPONSE_RESULT_TOKEN_EXPIRED == resultBase
                .getResponseCode()) {
            showMessageTokenExpire(context);
        } else {
            String msg = Constants.ERROR_MAP.get(resultBase.getResponseCode());
            if (StringUtils.isEmpty(msg)) {
                showToast(context, resultBase.getResponseMsg());
            } else {
                showToast(context, msg);
            }
        }
    }

    /**
     * 跳转详情页
     *
     * @param context
     * @param orderId
     * @param orderType
     */
    public static void showOrderDetailView(Context context, String orderId) {
        Intent intent = new Intent();
        intent.setClass(context, OrderDetailActivity.class);
        intent.putExtra("orderId", orderId);
        context.startActivity(intent);
    }

    /**
     * 调用打电话对话框
     *
     * @param aty
     * @param phoneNo
     */
    public static void showCallPhoneDialog(final Activity aty, final String phoneNo) {
        showCallPhoneDialog(aty, null, null, phoneNo, phoneNo);
    }

    public static void showCallPhoneDialog(final Activity aty, View view, final String phoneNo) {
        showCallPhoneDialog(aty, null, view, phoneNo, phoneNo);
    }

    /**
     * 调用打电话对话框
     *
     * @param aty
     * @param phoneNo
     */
    public static void showCallPhoneDialog(final Activity aty, String title, View view, String msg, final String phoneNo) {
        CommonAlertDialog.Builder dialog = new CommonAlertDialog.Builder(aty);
        if (!StringUtils.isEmpty(title)) dialog.setTitle(title);
        if (view != null) {
            dialog.setView(view);
        } else {
            dialog.setMessage(msg);
        }
        dialog.setNegativeButton(R.string.btn_cancel, null);
        dialog.setPositiveButton(R.string.btn_call, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                callPhone(aty, phoneNo);
            }
        });
        dialog.show();
    }

    /**
     * 调用打电话对话框
     *
     * @param aty
     * @param phoneNo
     */
    public static void showCallPhoneDialog2(final Activity aty, final String phoneNo) {
        showCallPhoneDialog2(aty, null, null, phoneNo, phoneNo);
    }

    /**
     * 调用打电话对话框
     *
     * @param aty
     * @param phoneNo
     */
    public static void showCallPhoneDialog2(final Activity aty, String title, View view, String msg, final String phoneNo) {
        CommonAlertDialog.Builder dialog = new CommonAlertDialog.Builder(aty);
        if (!StringUtils.isEmpty(title)) dialog.setTitle(title);
        if (view != null) {
            dialog.setView(view);
        } else {
            dialog.setMessage(msg);
        }
        dialog.setPositiveButton(R.string.btn_cancel, null);
        dialog.setNegativeButton(R.string.btn_call, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                callPhone(aty, phoneNo);
            }
        });
        dialog.show();
    }

    public static void showMessageService(final Activity aty) {
        CommonAlertDialog.Builder dialog = new CommonAlertDialog.Builder(aty);
        dialog.setTitle(aty.getString(R.string.service_dialog_title));
        dialog.setMessageSize(14);
        dialog.setMessage(aty.getString(R.string.service_call)
                + "\n"
                + aty.getString(R.string.service_weixin));
        dialog.setPositiveButton(aty.getString(R.string.service_weixin_btn),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showWeixinApp(aty, aty.getString(R.string.no_weixin));
                    }
                });
        dialog.setNegativeButton(aty.getString(R.string.service_call_btn),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callPhone(aty, Constants.SERVICE_PHONE);
                    }
                });
        dialog.show();
    }


    public static String getTimeCount(String startTime, String endTime) {
        try {
            startTime = startTime.substring(startTime.length() - 8,
                    startTime.length() - 3);
            endTime = endTime.substring(endTime.length() - 8,
                    endTime.length() - 3);
        } catch (Exception e) {
            // TODO: handle exception
        }
        return startTime + "-" + endTime;
    }

    /**
     * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
     *
     * @param context
     * @return true 表示开启
     */
    public static boolean isOPenLocation(Context context) {
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }
        return false;
    }

    public static void showLocationSettingDialog(final Activity aty) {
        new CommonAlertDialog.Builder(aty)
                .setMessage(R.string.card_get_location_tips)
                .setNegativeButton(R.string.btn_setting,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                aty.startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
                            }
                        })
                .setPositiveButton(R.string.btn_later, null)
                .show();
    }

    public static void showPunchCardView(final Activity aty, boolean isPunchCard) {
        Intent intent = new Intent(aty, CardTabActivity.class);
        intent.putExtra("isPunchCard", isPunchCard);
//		intent.putExtra("isPunchCard",false);
        aty.startActivity(intent);
    }

    public static boolean isTipsByDay(Context context) {
        TipsItem item = ClientStateManager.getTipsByDay(context);
        if (DateUtil.isToday(item.getTimestamp())) {
            if (item.getCount() < 2 && !isOPenLocation(context)) {
                item.setCount(item.getCount() + 1);
                ClientStateManager.setTipsByDay(context, item);
                return true;
            }
        } else if (!isOPenLocation(context)) {
            item.setTimestamp(System.currentTimeMillis());
            item.setCount(1);
            ClientStateManager.setTipsByDay(context, item);
            return true;
        }
        return false;
    }


    public static String getStringByLengh(String str, int count) {
        if (str != null && str.length() > count) {
            return str.substring(0, count) + "...";
        }
        return str;
    }

    public static boolean jsConnect(WebView view, String url, JsConnectCallBack callBack) {
        return JsConnectManager.jsConnect(JsConnectManager.URL_ANGEL, view, url, callBack);
    }

    public static void openWebView(Context context, String url, String title, boolean isActionBar,
                                   boolean isHorizontalProgress, boolean isBackByJs, boolean isBackFinish) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("title", title);
        intent.putExtra("actionbar", isActionBar);
        intent.putExtra("progress", isHorizontalProgress);
        intent.putExtra("back", isBackByJs);
        intent.putExtra("isBackFinish", isBackFinish);
        context.startActivity(intent);
    }

    public static void openWebView(Context context, String url, String title) {
        openWebView(context, url, title, false, false, false, false);
    }

    public static String getAppInfo() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("version", AppContext.getInstance().getPackageInfo().versionName);
        params.put("client", ApiClientHelper.CLIENT);
        return JSONObject.toJSONString(params);
    }

    public static View getEmptyView(String content,int imgResid){
        LayoutInflater inflater = LayoutInflater.from(AppContext.getInstance());
        View view = inflater.inflate(R.layout.layout_empty, null);
        ((TextView) view.findViewById(R.id.txt_content)).setText(content);
        if(imgResid!=0){
            ((ImageView)view.findViewById(R.id.img_empty)).setImageResource(imgResid);
        }
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(
                AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.MATCH_PARENT);
        view.setLayoutParams(params);
        return view;
    }

    public static void setEmptyView(View listview,View emptyView) {
        ((ViewGroup) listview.getParent()).addView(emptyView);
        try{
            ((PullToRefreshListView)listview).setEmptyView(emptyView);
        }catch (Exception e){
            ((ListView)listview).setEmptyView(emptyView);
        }
    }

    public static void setEmptyView(View listview,String content) {
        View emptyView = getEmptyView(content,0);
        setEmptyView(listview, emptyView);
    }

    public static void setEmptyView(View listview,String content,int imgResid) {
        View emptyView = getEmptyView(content,imgResid);
        setEmptyView(listview, emptyView);
    }

    public static String getString2(String param1,String param2){
       return String.format(AppContext.getInstance().getString(R.string.param_two), param1, param2);
    }

    public static TextView getPhoneView(final Activity aty,final TextView txtPhone){
        txtPhone.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        txtPhone.getPaint().setAntiAlias(true);
        txtPhone.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                PublicUtil.showCallPhoneDialog(aty, txtPhone.getText().toString());
            }
        });
        return txtPhone;
    }


}

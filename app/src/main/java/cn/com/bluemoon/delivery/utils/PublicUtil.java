package cn.com.bluemoon.delivery.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.location.LocationManager;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Xml;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.xmlpull.v1.XmlPullParser;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.bluemoon.delivery.AppContext;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.ApiClientHelper;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.card.TipsItem;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.common.ScanActivity;
import cn.com.bluemoon.delivery.common.ScanCodeActivity;
import cn.com.bluemoon.delivery.common.ScanInputActivity;
import cn.com.bluemoon.delivery.common.WebViewActivity;
import cn.com.bluemoon.delivery.module.account.LoginActivity;
import cn.com.bluemoon.delivery.module.card.CardTabActivity;
import cn.com.bluemoon.delivery.module.order.OrderDetailActivity;
import cn.com.bluemoon.lib.callback.JsConnectCallBack;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib.utils.JsConnectManager;
import cn.com.bluemoon.lib.utils.LibPublicUtil;
import cn.com.bluemoon.lib.utils.LibViewUtil;
import cn.com.bluemoon.lib.view.CommonAlertDialog;
import cn.com.bluemoon.lib.view.CommonEmptyView;

public class PublicUtil extends LibPublicUtil {


    /**
     * 生成月亮天使图片文件夹路径下的图片名
     *
     * @return 返回图片名（取当前时间）
     */
    public static String getPhotoPath() {
        return getPhotoPath(Constants.PATH_PHOTO, null);
    }


    public static String getTempPath() {
        return getPhotoPath(Constants.PATH_PHOTO, null);
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
     * 服务器繁忙提示
     */
    public static void showToastServerBusy() {
        showToast(R.string.request_server_busy);
    }

    /**
     * 服务器超时提示
     */
    public static void showToastServerOvertime() {
        showToast(R.string.request_server_overtime);
    }

    /**
     * 打开签收扫描界面
     *
     * @param resultCode 大于1的正整数
     */
    public static void openScanOrder(Activity aty, Fragment fragment, String title,
                                     String btnString, int requestCode, int resultCode) {
        ScanInputActivity.actStart(aty,fragment,title,btnString,requestCode,resultCode);
    }

    /**
     * 打开新扫描界面
     *
     * @param resultCode 大于1的正整数
     */
    public static void openNewScanOrder(Activity aty, Fragment fragment, String title,
                                        String btnString, int requestCode, int resultCode) {
        ScanCodeActivity.actStart(aty,fragment,title,btnString,requestCode,resultCode);
    }

    /**
     * 打开扫描界面
     *
     * @param resultCode 大于1的正整数
     */
    public static void openNewScan(Activity aty, String title,
                                   String btnString, int requestCode, int resultCode) {
        openNewScanOrder(aty,null,title,btnString,requestCode,resultCode);
    }

    public static void openScanTicket(Activity aty, String ticketName,
                                      String ticketCount, int requestCode, int resultCode) {
        ScanInputActivity.actStart(aty,null,
                aty.getString(R.string.ticket_check_title),
                aty.getString(R.string.ticket_code_btn_text),
                ticketName,ticketCount,requestCode,resultCode);
    }

    /**
     * 打开默认的扫描界面
     */
    public static void openScanView(Activity aty, Fragment fragment, String title, int requestCode) {
        ScanActivity.actStart(aty,fragment,title,requestCode);
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

    /**
     * list转化为xml格式
     *
     * @param params
     * @return
     */
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

    /**
     * xml转化为map格式
     *
     * @return
     */
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

    /**
     * 跳转详情页
     *
     * @param context
     * @param orderId
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
                                LoginActivity.actStart(context);
                                context.finish();
                            }
                        }).show();
    }

    public static void showErrorMsg(Activity context, ResultBase resultBase) {
        if (Constants.RESPONSE_RESULT_TOKEN_EXPIRED == resultBase
                .getResponseCode()) {
            showMessageTokenExpire(context);
        } else {
            String msg = Constants.ERROR_MAP.get(resultBase.getResponseCode());
            if (StringUtils.isEmpty(msg)) {
                LibViewUtil.toast(context, resultBase.getResponseMsg());
            } else {
                LibViewUtil.toast(context, msg);
            }
        }
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


    public static boolean jsConnect(WebView view, String url, JsConnectCallBack callBack) {
        return JsConnectManager.jsConnect(JsConnectManager.URL_ANGEL, view, url, callBack);
    }

    public static void openWebView(Context context, String url, String title, boolean isActionBar,
                                   boolean isBackByJs) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("title", title);
        intent.putExtra("actionbar", isActionBar);
        intent.putExtra("back", isBackByJs);
        context.startActivity(intent);
    }

    public static void openWebView(Context context, String url, String title, boolean isBackByJs) {
        openWebView(context, url, title, !JsConnectManager.isHideTitleByUrl(url), isBackByJs);
    }

    public static void openWebView(Context context, String url, String title) {
        openWebView(context, url, title, false, false);
    }

    /**
     * 获取app信息，转化为json对象
     * @return
     */
    public static String getAppInfo() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("version", AppContext.getInstance().getPackageInfo().versionName);
        params.put("client", ApiClientHelper.CLIENT);
        params.put("cuid", AppContext.getInstance().getAppId());
        params.put("token", ClientStateManager.getLoginToken(AppContext.getInstance()));
        return JSONObject.toJSONString(params);
    }

    /**
     * 获取空数据页
     * @param content
     * @param listener
     * @return
     */
    public static CommonEmptyView getEmptyView(String content, CommonEmptyView.EmptyListener listener) {
        CommonEmptyView emptyView = new CommonEmptyView(AppContext.getInstance());
        if (content != null) {
            emptyView.setContentText(content);
        }
        if (listener != null) {
            emptyView.setEmptyListener(listener);
        }
        return emptyView;
    }

    /**
     * 设置空数据页
     * @param listview
     * @param emptyView
     */
    public static void setEmptyView(View listview, View emptyView) {
        if (listview instanceof PullToRefreshListView) {
            ((PullToRefreshListView) listview).setEmptyView(emptyView);
        } else if (listview instanceof ListView) {
            ((ViewGroup) listview.getParent()).addView(emptyView);
            ((ListView) listview).setEmptyView(emptyView);
        }
        ViewUtil.setViewVisibility(emptyView, View.GONE);
    }

    /**
     * 设置空数据页（content只传入页面标题即可）
     * @param listview
     * @param content
     * @param listener
     * @return
     */
    public static CommonEmptyView setEmptyView(View listview, String content, CommonEmptyView.EmptyListener listener) {
        if(content!=null){
            content = AppContext.getInstance().getString(R.string.empty_hint3, content);
        }
        CommonEmptyView emptyView = getEmptyView(content, listener);
        setEmptyView(listview, emptyView);
        return emptyView;
    }

    /**
     * 设置空数据页（content只传入页面标题id即可）
     * @param listview
     * @param ResId
     * @param listener
     * @return
     */
    public static CommonEmptyView setEmptyView(View listview, int ResId, CommonEmptyView.EmptyListener listener) {
        String content = AppContext.getInstance().getString(ResId);
        return setEmptyView(listview,content,listener);
    }

    /**
     * 将textview设置为蓝色下划线，并添加点击拨打电话功能
     * @param aty
     * @param txtPhone
     * @return
     */
    public static TextView getPhoneView(final Activity aty, final TextView txtPhone) {
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

    public static void setGravity(final EditText tv) {
        tv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int lineCount = tv.getLineCount();
                if (lineCount > 1) {
                    tv.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                } else {
                    tv.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

}

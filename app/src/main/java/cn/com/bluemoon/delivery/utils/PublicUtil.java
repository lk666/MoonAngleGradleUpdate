package cn.com.bluemoon.delivery.utils;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Paint;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Xml;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.bluemoon.lib_badger.BadgeUtil;
import com.bluemoon.umengshare.ShareCallBack;
import com.bluemoon.umengshare.ShareHelper;
import com.bluemoon.umengshare.ShareModel;
import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.apache.commons.lang3.StringUtils;
import org.xmlpull.v1.XmlPullParser;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.bluemoon.delivery.AppContext;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.card.TipsItem;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.common.WebViewActivity;
import cn.com.bluemoon.delivery.common.XScanActivity;
import cn.com.bluemoon.delivery.common.qrcode.ContinuityScanActivity;
import cn.com.bluemoon.delivery.common.qrcode.ScanActivity;
import cn.com.bluemoon.delivery.common.qrcode.ScanCodeActivity;
import cn.com.bluemoon.delivery.common.qrcode.ScanInputActivity;
import cn.com.bluemoon.delivery.module.account.LoginActivity;
import cn.com.bluemoon.delivery.module.card.CardTabActivity;
import cn.com.bluemoon.delivery.module.order.ScanWithInputActivity;
import cn.com.bluemoon.delivery.module.track.TrackManager;
import cn.com.bluemoon.delivery.module.wash.collect.ClothScanCodeActivity;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib.utils.LibPublicUtil;
import cn.com.bluemoon.lib.utils.LibViewUtil;
import cn.com.bluemoon.lib.view.CommonAlertDialog;
import cn.com.bluemoon.lib.view.CommonEmptyView;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.protocol.HTTP;

public class PublicUtil extends LibPublicUtil {


    /**
     * 生成月亮天使图片文件夹路径下的图片名
     *
     * @return 返回图片名（取当前时间）
     */
    public static String getPhotoPath() {
        return getPhotoPath(FileUtil.getPathPhoto(), null);
    }


    public static String getTempPath() {
        return getPhotoPath(FileUtil.getPathTemp(), null);
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
        ScanInputActivity.actStart(aty, fragment, title, btnString, requestCode, resultCode);
    }

    /**
     * 打开签收扫描界面
     */
    public static void openOrderWithInput(Fragment fragment, String title,
                                          String btnString, int requestCode) {
        ScanWithInputActivity.actStart(fragment, title, btnString, requestCode);
    }

    /**
     * 洗衣服务扫描界面
     */
    public static void openClothScan(Activity aty, Fragment fragment, String title,
                                     String btnString, int requestCode) {
        ClothScanCodeActivity.actStart(aty, fragment, title, btnString, null, requestCode);
    }

    /**
     * 洗衣服务扫描界面
     */
    public static void openClothScan(Activity aty, String title, String btnString, int
            requestCode) {
        openClothScan(aty, null, title, btnString, requestCode);
    }

    public static void openScanTicket(Activity aty, String ticketName,
                                      String ticketCount, int requestCode, int resultCode) {
        ScanInputActivity.actStart(aty, null,
                aty.getString(R.string.ticket_check_title),
                aty.getString(R.string.ticket_code_btn_text),
                ticketName, ticketCount, requestCode, resultCode);
    }

    /**
     * 打开默认的扫描界面
     */
    public static void openScanView(Activity aty, Fragment fragment, String title, int
            requestCode) {
        ScanActivity.actStart(aty, fragment, title, requestCode);
    }

    /**
     * 打开默认的扫描界面(目前只有网页调起扫描用到)
     */
    public static void openScanView(Activity aty, String title, boolean isContinue, int
            requestCode) {
        if (isContinue) {
            ContinuityScanActivity.actStart(aty, title, requestCode);
        } else {
            ScanActivity.actStart(aty, title, requestCode);
        }
    }

    /**
     * 最新的统一扫描界面
     */
    public static void openNewScanView(Activity aty, String title, String btnString, String code,
                                       int requestCode) {
        ScanCodeActivity.actStart(aty, title, btnString, code, requestCode);
    }

    /**
     * 最新的统一扫描界面
     */
    public static void openNewScanView(Fragment fragment, String title, String btnString, String
            code, int requestCode) {
        ScanCodeActivity.actStart(fragment, title, btnString, code, requestCode);
    }

    /**
     * 打开主菜单统一扫描界面
     */
    public static void openXScanView(Activity aty, Fragment fragment, String title, int
            requestCode) {
        XScanActivity.actStart(aty, fragment, title, requestCode);
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
    public static void showCallPhoneDialog(final Activity aty, String title, View view, String
            msg, final String phoneNo) {
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
    public static void showCallPhoneDialog2(final Activity aty, String title, View view, String
            msg, final String phoneNo) {
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

    private static CommonAlertDialog tokenExpireDialog;

    private static CommonAlertDialog getTokenExpireDialog(final Activity context) {
        if (tokenExpireDialog == null) {
            tokenExpireDialog = new CommonAlertDialog.Builder(context)
                    .setCancelable(false)
                    .setMessage(context.getString(R.string.token_out))
                    .setPositiveButton(R.string.btn_ok,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    LoginActivity.actStart(context);
                                    tokenExpireDialog = null;
                                    context.finish();
                                }
                            }).create();
        }
        return tokenExpireDialog;
    }

    /**
     * 显示账户过期对话框
     *
     * @param context
     */
    public static void showMessageTokenExpire(final Activity context) {
        if (!getTokenExpireDialog(context).isShowing()) {
            getTokenExpireDialog(context).show();
        }

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

                                aty.startActivity(new Intent("android.settings" +
                                        ".LOCATION_SOURCE_SETTINGS"));


                            }
                        })
                .setPositiveButton(R.string.btn_later, null)
                .show();
    }

    public static void showAppSettingDialog(final Activity aty) {
        new CommonAlertDialog.Builder(aty)
                .setMessage(R.string.card_get_location_tips)
                .setNegativeButton(R.string.btn_setting,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                Intent localIntent = new Intent();
                                localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                                localIntent.setData(Uri.fromParts("package", aty.getPackageName(), null));
                                aty.startActivity(localIntent);


                            }
                        })
                .setPositiveButton(R.string.btn_later, null)
                .show();
    }

    public static void showPunchCardView(final Activity aty, boolean isPunchCard) {
//        Intent intent = new Intent(aty, CardTabActivity.class);
//        intent.putExtra("isPunchCard", isPunchCard);
//        aty.startActivity(intent);
        CardTabActivity.actionStart(aty, isPunchCard);

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

    public static void openWebView(Context context, String url, String title) {
        WebViewActivity.startAction(context, url, title, false, null);
    }

    /**
     * 获取空数据页
     */
    public static CommonEmptyView getEmptyView(String content, CommonEmptyView.EmptyListener
            listener) {
        CommonEmptyView emptyView = new CommonEmptyView(AppContext.getInstance());
        if (content != null) {
            emptyView.setContentText(content);
        }
        emptyView.setEmptyListener(listener);
        return emptyView;
    }

    /**
     * 设置空数据页
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
     */
    public static CommonEmptyView setEmptyView(View listview, String content, CommonEmptyView
            .EmptyListener listener) {
        if (content != null) {
            content = AppContext.getInstance().getString(R.string.empty_hint3, content);
        }
        CommonEmptyView emptyView = getEmptyView(content, listener);
        setEmptyView(listview, emptyView);
        return emptyView;
    }

    /**
     * 设置空数据页（content只传入页面标题id即可）
     */
    public static CommonEmptyView setEmptyView(View listview, int ResId, CommonEmptyView
            .EmptyListener listener) {
        String content = AppContext.getInstance().getString(ResId);
        return setEmptyView(listview, content, listener);
    }

    /**
     * 将textview设置为蓝色下划线，并添加点击拨打电话功能
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

    /**
     * 获取intent的值
     */
    public static String getExtraValue(Intent intent, String key) {
        return intent == null ? null : intent.getStringExtra(key);
    }

    public static String getPushView(Intent intent) {
        return getExtraValue(intent, Constants.PUSH_VIEW);
    }

    public static String getPushUrl(Intent intent) {
        return getExtraValue(intent, Constants.PUSH_URL);
    }

    /**
     * find the home launcher Package
     *
     * @param context
     * @return
     */
    public static String findLauncherPackage(Context context) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo resolveInfo = context.getPackageManager().resolveActivity(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return resolveInfo.activityInfo.packageName;
    }

    /**
     * 更新桌面角标
     *
     * @param context
     * @param count
     */
    public static void setMainAmount(Context context, int count, Notification notification) {
        LogUtils.d("badge count ：" + count);
        if (count != ClientStateManager.getMenuNum()) {
            LogUtils.d("update badge count ：" + count);
            ClientStateManager.setMenuNum(count);
            BadgeUtil.applyCount(context, count, notification);
        } else {
            NotificationUtil.showNotification(context, notification);
        }
    }

    public static void setMainAmount(Context context, int count) {
        setMainAmount(context, count, null);
    }


    public static void share(final Activity activity, final String topic, final String content,
                             final String picUrl, final String url) {
        String shareUrl = (url.indexOf('?') > 0 ? url + "&account=" : url + "?account=") +
                ClientStateManager.getUserName();
        shareHelper(activity, topic, content, picUrl, shareUrl);
        /*LogUtils.d("share long url = " + shareUrl);
        //获取分享短链接
        AsyncHttpResponseHandler handler = new TextHttpResponseHandler(HTTP.UTF_8) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Object resultObj;
                    resultObj = JSON.parseObject(responseString, ResultGenShortShare.class);
                    if (resultObj instanceof ResultGenShortShare) {
                        ResultGenShortShare resultBase = (ResultGenShortShare) resultObj;
                        if (resultBase.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                            if (StringUtils.isNoneBlank(resultBase.getUrlShort())) {
                                LogUtils.d("share short url = " + resultBase.getUrlShort());
                                shareHelper(activity,topic,content,picUrl, resultBase.getUrlShort
                                ());
                            } else {
                                LogUtils.e("get short url is null " + resultBase.getUrlShort());
                            }
                        } else {
                            PublicUtil.showErrorMsg(activity, resultBase);
                        }
                    } else {
                        throw new IllegalArgumentException();
                    }
                } catch (Exception e) {
                    PublicUtil.showToastServerOvertime();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  String responseString, Throwable throwable) {
                PublicUtil.showToastServerBusy();
            }
        };
        String requestUrl = String.format(BuildConfig.SHARE_TO_SHORT,
        "moonMall-proxy-web/util/genUrlToShort");
        Map<String, String> params = new HashMap<>();
        params.put("urlLong", shareUrl);
        String jsonString = JSONObject.toJSONString(params);
        ByteArrayEntity entity = null;
        try {
            entity = new ByteArrayEntity(jsonString.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            //e.printStackTrace();
        }
        ApiHttpClient.getHttpClient().post(activity, requestUrl, entity, "application/json",
        handler);*/
    }

    private static void shareHelper(final Activity activity, final String topic, final String
            content, final String picUrl, final String shareUrl) {
        String contentTemp;
        if (StringUtils.isEmpty(content)) {
            contentTemp = topic;
        } else {
            contentTemp = content;
        }
        contentTemp = StringUtil.getStringByLengh(contentTemp, 17);
        ShareHelper.share(activity, new ShareModel(activity, picUrl, shareUrl, topic,
                contentTemp), new ShareCallBack() {
            @Override
            public void shareStart(SHARE_MEDIA platform, String platformString, ShareModel
                    shareModel) {
                //数据埋点
                TrackManager.addBody(shareModel.getuMTargetUrl(), platformString);
            }

            @Override
            public void shareSuccess(SHARE_MEDIA platform, String platformString, ShareModel
                    shareModel) {
                DeliveryApi.saveShareInfo(ClientStateManager.getLoginToken(), topic,
                        platformString, new TextHttpResponseHandler(HTTP.UTF_8) {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers,
                                                  String responseString) {
                                LogUtils.d("plat", responseString);
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers,
                                                  String responseString, Throwable throwable) {
                                LogUtils.d("plat", "onFailure");
                            }
                        });

            }

            @Override
            public void shareCancel(SHARE_MEDIA platform, String platformString, ShareModel
                    shareModel) {

            }

            @Override
            public void shareError(SHARE_MEDIA platform, String platformString, ShareModel
                    shareModel, String errorMsg) {

            }
        });

    }

}

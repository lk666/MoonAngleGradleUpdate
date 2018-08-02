package cn.com.bluemoon.delivery.common;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.tencent.smtt.sdk.WebView;
import com.umeng.analytics.MobclickAgent;

import cn.com.bluemoon.delivery.BuildConfig;
import cn.com.bluemoon.delivery.module.wxmini.WXMiniItem;
import cn.com.bluemoon.delivery.module.wxmini.WXMiniManager;
import cn.com.bluemoon.mapnavigation.lib.MapActivity;
import cn.com.bluemoon.mapnavigation.lib.model.MapMarker;
import cz.msebera.android.httpclient.Header;

import org.apache.http.protocol.HTTP;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.List;

import bluemoon.com.lib_x5.base.BaseX5WebViewActivity;
import bluemoon.com.lib_x5.bean.BaseParam;
import bluemoon.com.lib_x5.bean.LocationParam;
import bluemoon.com.lib_x5.bean.TitleStyle;
import bluemoon.com.lib_x5.utils.JsBridgeUtil;
import bluemoon.com.lib_x5.utils.JsUtil;
import bluemoon.com.lib_x5.utils.ToastUtil;
import cn.com.bluemoon.cardocr.lib.CaptureActivity;
import cn.com.bluemoon.cardocr.lib.bean.AssetTagInfo;
import cn.com.bluemoon.cardocr.lib.bean.BankInfo;
import cn.com.bluemoon.cardocr.lib.bean.IdCardInfo;
import cn.com.bluemoon.cardocr.lib.common.CardType;
import cn.com.bluemoon.delivery.AppContext;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.ApiHttpClient;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.scan.ResultBankInfo;
import cn.com.bluemoon.delivery.app.api.model.scan.ResultIDCard;
import cn.com.bluemoon.delivery.common.photopicker.PhotoPickerActivity;
import cn.com.bluemoon.delivery.entity.ResultAsset;
import cn.com.bluemoon.delivery.module.base.WithContextTextHttpResponseHandler;
import cn.com.bluemoon.delivery.module.base.interf.IHttpResponse;
import cn.com.bluemoon.delivery.module.event.ScanEvent;
import cn.com.bluemoon.delivery.module.event.ScanResultEvent;
import cn.com.bluemoon.delivery.module.speech.VoiceActivity;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.FileUtil;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.ViewUtil;
import cn.com.bluemoon.delivery.utils.manager.ActivityManager;
import cn.com.bluemoon.lib_iflytek.SpeakManager;

public class WebViewActivity extends BaseX5WebViewActivity implements IHttpResponse {

    public LocationClient mLocationClient = null;
    //是否接收下载完成监听
    private boolean isReceived;

    private WXMiniManager miniManager;

    /**
     * 网页界面启动方法
     *
     * @param context 调用的类
     * @param url     网页地址
     * @param title   网页标题 title为null时标题隐藏
     * @param style   标题栏的样式
     */
    public static void startAction(Context context, String url, String title,
                                   boolean isFull, TitleStyle style) {
        startAction(context, url, title, isFull, style, null, WebViewActivity.class);
    }

    /**
     * APP项目来源，在JsBridgeUtil中有定义
     */
    @Override
    public String getSource() {
        return JsBridgeUtil.SOURCE_ANGEL;
    }

    /**
     * 设备项目唯一标识，用于网络请求公参
     */
    @Override
    public String getAppId() {
        return AppContext.getInstance().getAppId();
    }

    /**
     * 项目token
     */
    @Override
    public String getToken() {
        return ClientStateManager.getLoginToken();
    }

    /**
     * 推送设备id
     */
    @Override
    public String getClientId() {
        return ClientStateManager.getClientId();
    }

    /**
     * 获取下载文件夹的路径
     */
    @Override
    public String getDownPath() {
        return FileUtil.getPathDown();
    }


    /**
     * 新建网页界面
     *
     * @param url        网页链接
     * @param title      网页标题
     * @param titleStyle title类型
     */
    @Override
    public void newWebView(String url, String title, boolean isFull, TitleStyle titleStyle) {
        startAction(aty, url, title, isFull, titleStyle);
    }

    /**
     * 打开扫描界面操作
     *
     * @param requestCode 请求用到的requestCode（必须用这个）
     */
    @Override
    public void openScanView(String title, boolean isContinue, boolean isScanner, int requestCode) {
        PublicUtil.openScanView(aty, title, isContinue, requestCode);
    }

    /**
     * 打开最新的选择图片的界面，至尊有集成（目前只处理单选，与openPhotoView2方法只能实现一种）
     */
    @Override
    public void openPhotoView(int requestCode) {
        PhotoPickerActivity.actStart(this, requestCode);
    }

    /**
     * 界面处理最新选择图片的返回图片路径(目前只处理单选)
     */
    @Override
    public Uri getResultImagePath(int requestCode, Intent data) {
        if (data != null) {
            List<String> list = data.getStringArrayListExtra(PhotoPickerActivity.EXTRA_RESULT);
            if (list != null && list.size() > 0) {
                return Uri.fromFile(new File(list.get(0)));
            }
        }

        return null;
    }

    /**
     * 打开旧版的选择图片的界面，OA上集成
     */
    @Override
    public void openPhotoView2(int chooseRequestCode, int takeRequestCode) {

    }

    /**
     * 处理旧版选择图片的选择图片Uri
     */
    @Override
    public Uri getChooseImagePath(int requestCode, Intent data) {
        return null;
    }

    /**
     * 处理旧版选择图片的拍照的图片Uri
     */
    @Override
    public Uri getTakeImagePath(int requestCode, Intent data) {
        return null;
    }

    /**
     * 处理分享操作（需将结果用Js方法通过callbackName方法名回调，参数为BaseParam）
     */
    @Override
    public void share(String topic, String content, String picUrl, String url, String
            callbackName) {
        PublicUtil.share(aty, topic, content, picUrl, url);
    }

    /**
     * 设置标题栏样式（参照TitleStyle的参数）
     */
    @Override
    public TitleStyle getTitleStyle() {
        return new TitleStyle(getResources().getColor(R.color.title_background), getResources()
                .getColor(R.color.title_background), 0, 0);
    }

    /**
     * 处理退出登录操作
     */
    @Override
    public void logout(WebView view, String callbackName) {
        PublicUtil.showMessageTokenExpire(aty);
    }

    /**
     * 网页登录返回的token
     */
    @Override
    public void setToken(WebView view, String token, String callbackName) {

    }

    /**
     * 扫描验证反馈接口
     */
    @Override
    public void scanFeedback(WebView view, boolean isSuccess, boolean isClose, String callback) {
        EventBus.getDefault().post(new ScanResultEvent(isSuccess, isClose));
    }

    /**
     * 语音播报接口
     */
    @Override
    public void voiceReminder(WebView view, String content, String callback) {
        SpeakManager.getInstance().startSpeaking(this, content);
    }

    @Override
    public void toast(String content, String milliSec, String callback) {
        ToastUtil.toast(this, content);
    }

    @Override
    public void onDownFile(String url, String mimetype, boolean isOpen, String callbackName) {
        // TODO: 2018/1/25 如果有调用下载方法，就设为true
        isReceived = true;
        super.onDownFile(url, mimetype, isOpen, callbackName);
    }

    @Override
    public void onDownStart(long downloadId, String url, String path) {
        super.onDownStart(downloadId, url, path);
        ToastUtil.toast(this, getString(R.string.down_start));
    }

    @Override
    public void onDownFinish(long downloadId, String url, boolean isSuccess) {
        // TODO: 2018/1/22 如果已经跳转到其他页面，就不做下载完成处理 ，这里需要考虑更优方案
        if (!isReceived) return;
        super.onDownFinish(downloadId, url, isSuccess);
        ToastUtil.toast(this, getString(isSuccess ? R.string.down_success : R.string.down_fail));
    }

    @Override
    public void intention(WebView view, String code, String columnCode, String pageCode, String
            callback) {
        //线上管院模板跳转
    }

    @Override
    public void mapNavigation(WebView view, float gpsLongitude, float gpsLatitude, String
            placeName, String address, String callback) {
        // 地图导航
        MapActivity.startAct(this, new MapMarker(placeName, address, gpsLatitude, gpsLongitude));
    }

    @Override
    public void miniProgram(WebView view, String type, int miniprogramType, String userName,
                            String path, String webpageUrl, String title, String description,
                            String thumbUrl, String callback) {
        if(!PublicUtil.isExistWeiXin(this)){
            ToastUtil.toast(this, R.string.not_found_weixin);
            return;
        }
        if (miniManager == null) {
            miniManager = new WXMiniManager(this);
        }
        if ("open".equals(type)) {
            boolean result = miniManager.openWxMini(new WXMiniItem(miniprogramType, userName, path));
            JsUtil.runJsMethod(view, callback, result);
        } else if ("share".equals(type)) {
            miniManager.shareWXMiniWithUrl(new WXMiniItem(miniprogramType, userName, path,
                    webpageUrl, title, description, thumbUrl),view,callback);
        }

    }

    @Override
    public void publicLink(WebView view, String data, String event, int requestCode) {
        PublicLinkManager.gotoPage(this, data, event, requestCode);
    }

    @Override
    protected String getLinkResult(Intent data) {
        int code = data.getIntExtra(PublicLinkManager.PDF_CODE, 0);
        return JSONObject.toJSONString(new PublicLinkManager.ResultBean(true, code));
    }

    /**
     * 处理定位操作
     */
    @Override
    public void getLocation() {
        startLocation();
    }

    @Override
    public void openSpeech(int requestCode) {
        VoiceActivity.actStart(this, requestCode);
    }

    @Override
    public void openIDCard(boolean isFront, int requestCode) {
        //调起身份证识别界面，isFront是否是正反面
        CaptureActivity.startAction(this, isFront ? CardType.TYPE_ID_CARD_FRONT : CardType
                .TYPE_ID_CARD_BACK, requestCode);
    }

    @Override
    protected String getIDCardResult(Intent data, boolean isFront) {
        //处理身份证识别界面返回
        IdCardInfo idCardInfo = (IdCardInfo) data.getSerializableExtra(CaptureActivity.BUNDLE_DATA);
        return JSONObject.toJSONString(new ResultIDCard(idCardInfo, isFront));
    }

    @Override
    public void openBankCard(int requestCode) {
        //调起银行卡识别界面
        CaptureActivity.startAction(this, CardType.TYPE_BANK, requestCode);
    }

    @Override
    public void openAsset(int requestCode) {
        CaptureActivity.startAction(this, CardType.TYPE_ASSET_TAG_OCR, requestCode);
    }

    @Override
    protected String getBankCardResult(Intent data) {
        //处理银行卡返回结果
        BankInfo bankInfo = (BankInfo) data.getSerializableExtra(CaptureActivity.BUNDLE_DATA);
        return JSONObject.toJSONString(new ResultBankInfo(bankInfo));
    }

    @Override
    protected String getAssetResult(Intent data) {
        //处理资产标签识别界面返回
        AssetTagInfo assetTagInfo = (AssetTagInfo) data.getSerializableExtra(CaptureActivity
                .BUNDLE_DATA);
        return JSONObject.toJSONString(new ResultAsset(assetTagInfo));
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        // TODO: 2018/1/25 如果标题返回网址，就不设置
        if (title != null && !title.startsWith("http://") && !title.startsWith("https://")) {
            super.onReceivedTitle(view, title);
        }
    }

    //初始化定位
    private void startLocation() {
        if (mLocationClient != null && mLocationClient.isStarted()) {
            mLocationClient.stop();
        }
        mLocationClient = new LocationClient(aty);
        mLocationClient.registerLocationListener(locationListener);

        LocationClientOption mOption = new LocationClientOption();
        mOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        mOption.setCoorType("bd09ll");
        mOption.setOpenGps(true);
        mOption.setIsNeedAddress(true);
        mLocationClient.setLocOption(mOption);

        mLocationClient.start();
    }

    //定位结果监听
    private BDLocationListener locationListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            mLocationClient.stop();
            //Receive Location
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            double altitude = 0;
            boolean isSuccess = true;
            String gpsType;

            if (latitude == Constants.UNKNOW_VALUE) {
                latitude = 999;
                isSuccess = false;
            }

            if (longitude == Constants.UNKNOW_VALUE) {
                longitude = 999;
                isSuccess = false;
            }

            if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果

                if (location.getAltitude() != Constants.UNKNOW_VALUE) {
                    altitude = location.getAltitude();// 单位：米
                }
                gpsType = Constants.GPS_GPS;

            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                gpsType = Constants.WIFI_GPS;
            } else {
                gpsType = Constants.GPRS_GPS;
            }
            String address = location.getAddrStr();
            if (TextUtils.isEmpty(address)) {
                address = "";
            }
            LocationParam param = new LocationParam();
            param.setGpsType(gpsType);
            param.setGpsHeight(altitude);
            param.setGpsLatitude(latitude);
            param.setGpsLongitude(longitude);
            param.setGpsAddress(address);
            param.setResult(isSuccess);
            //反馈信息给web端
            requestLocation(param);
        }

    };


    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
        ActivityManager.getInstance().pushOneActivity(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && !BuildConfig.RELEASE) {
            if (0 != (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE)) {
                WebView.setWebContentsDebuggingEnabled(true);
            }
        }
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        super.initView();
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        isReceived = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onDestroy() {
        if (mLocationClient != null && mLocationClient.isStarted()) {
            mLocationClient.stop();
        }
        if (isCancelAllRequest()) {
            ApiHttpClient.cancelAll(this);
        }
        EventBus.getDefault().unregister(this);
        //释放语音播放资源
        SpeakManager.getInstance().destroy();
        super.onDestroy();
        ActivityManager.getInstance().popOneActivity(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ScanEvent event) {
        requestScanCode(event.code);
    }

    /****************网络请求******************/

    /**
     * 在调用Api的方法时使用
     *
     * @param clazz 解析转义json的data.class
     */
    final public WithContextTextHttpResponseHandler getNewHandler(final int requestcode, final
    Class clazz) {
        return getHandler(requestcode, clazz, this, true);
    }

    private WithContextTextHttpResponseHandler getHandler(final int requestcode, Class clazz,
                                                          final IHttpResponse iHttpResponse,
                                                          final boolean isShowDialog) {
        WithContextTextHttpResponseHandler handler = new WithContextTextHttpResponseHandler(
                HTTP.UTF_8, this, requestcode, clazz) {

            @Override
            public void onFinish() {
                super.onFinish();
                if (isShowDialog) {
                    hideWaitDialog();
                }
                onFinishResponse(getReqCode());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                if (iHttpResponse == null) {
                    return;
                }
                LogUtils.d(getDefaultTag(), "mainHandler requestCode:" + getReqCode() + " -->" +
                        " " + "result = " + responseString);
                try {
                    Object resultObj;
                    resultObj = JSON.parseObject(responseString, getClazz());
                    if (resultObj instanceof ResultBase) {
                        ResultBase resultBase = (ResultBase) resultObj;
                        if (resultBase.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                            iHttpResponse.onSuccessResponse(getReqCode(), responseString,
                                    resultBase);
                        } else {
                            iHttpResponse.onErrorResponse(getReqCode(), resultBase);
                        }
                    } else {
                        throw new IllegalArgumentException();
                    }
                } catch (Exception e) {
                    LogUtils.e(getDefaultTag(), e.getMessage());
                    iHttpResponse.onSuccessException(getReqCode(), e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  String responseString, Throwable throwable) {
                if (iHttpResponse == null) {
                    return;
                }
                LogUtils.e(getDefaultTag(), throwable.getMessage());
                iHttpResponse.onFailureResponse(getReqCode(), throwable);
            }
        };
        return handler;
    }

    /**
     * 是否撤销所有请求
     */
    protected boolean isCancelAllRequest() {
        return true;
    }

    /**
     * 请求返回非OK
     */
    @Override
    public void onErrorResponse(int requestCode, ResultBase result) {
        PublicUtil.showErrorMsg(this, result);
    }

    /**
     * 请求失败
     */
    @Override
    public void onFailureResponse(int requestCode, Throwable t) {
        ViewUtil.toastOvertime();
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {

    }

    @Override
    public void onSuccessException(int requestCode, Throwable t) {
        ViewUtil.toastBusy();
    }

    public void onFinishResponse(int requestCode) {

    }


}

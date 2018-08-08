package cn.com.bluemoon.delivery.module.wxmini;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXMiniProgramObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.smtt.sdk.WebView;

import bluemoon.com.lib_x5.utils.JsUtil;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.ViewUtil;

/**
 * app与小程序交互
 * Created by bm on 2018/7/24.
 */

public class WXMiniManager {

    private Context mContext;
    private IWXAPI wxApi;

    public WXMiniManager(Context context) {
        this.mContext = context;
    }

    /**
     * 打开微信小程序
     */
    public boolean openWxMini(WXMiniItem item) {
        if (item == null) {
            return false;
        }
        if (wxApi == null) {
            wxApi = WXAPIFactory.createWXAPI(mContext, Constants.APP_ID);
        }
        WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
        req.userName = item.userName; // 填小程序原始id
        req.path = item.path;//拉起小程序页面的可带参路径，不填默认拉起小程序首页
        req.miniprogramType = item.miniprogramType;// 正式版:0，测试版:1，体验版:2
        return wxApi.sendReq(req);
    }

    /**
     * 分享微信小程序(带图片地址)
     */
    public void shareWXMiniWithUrl(final WXMiniItem item) {
        shareWXMiniWithUrl(item, null, null);
    }

    /**
     * 通过WebView分享微信小程序(带图片地址)
     */
    public void shareWXMiniWithUrl(final WXMiniItem item, final WebView view, final String
            callback) {
        Glide.with(mContext.getApplicationContext())
                .load(item.thumbUrl)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap>
                            glideAnimation) {
                        item.bytes = WXMiniUtil.bmpToByteArray(resource, false);
                        shareWXMiniByWeb(item, view, callback);
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        super.onLoadFailed(e, errorDrawable);
                        ViewUtil.toast(R.string.mini_share_fail);
                    }
                });
    }

    /**
     * 通过WebView桥接分享小程序
     */
    public boolean shareWXMiniByWeb(WXMiniItem item, WebView view, String callback) {
        boolean result = shareWXMini(item);
        if (view != null) {
            JsUtil.runJsMethod(view, callback, result);
        }
        return result;
    }

    /**
     * 分享微信小程序（带byte数组）
     */
    public boolean shareWXMini(WXMiniItem item) {
        if (item == null) {
            return false;
        }
        if (item.bytes == null || item.bytes.length <= 0) {
            ViewUtil.toast(R.string.mini_share_fail);
            return false;
        }
        if (wxApi == null) {
            wxApi = WXAPIFactory.createWXAPI(mContext, Constants.APP_ID);
        }
        if (TextUtils.isEmpty(item.webpageUrl)) {
            item.webpageUrl = " ";
        }
        WXMiniProgramObject miniProgram = new WXMiniProgramObject();
        miniProgram.webpageUrl = item.webpageUrl; // 兼容低版本的网页链接
        miniProgram.miniprogramType = item.miniprogramType;// 正式版:0，测试版:1，体验版:2
        miniProgram.userName = item.userName;// 小程序原始id
        miniProgram.path = item.path;//小程序页面路径
        WXMediaMessage msg = new WXMediaMessage(miniProgram);
        msg.title = item.title;// 小程序消息title
        msg.description = item.description;// 小程序消息desc
        msg.thumbData = item.bytes;// 小程序消息封面图片，小于128k

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = WXMiniUtil.buildTransaction("webpage");
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneSession;  // 目前支持会话
        return wxApi.sendReq(req);
    }

}

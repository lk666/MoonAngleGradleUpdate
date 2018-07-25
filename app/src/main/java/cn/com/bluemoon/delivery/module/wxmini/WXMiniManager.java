package cn.com.bluemoon.delivery.module.wxmini;

import android.content.Context;
import android.graphics.Bitmap;
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

import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.utils.Constants;

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

    //默认测试数据
    public WXMiniItem getItem() {
        WXMiniItem item = new WXMiniItem();
        item.miniprogramType = WXMiniProgramObject.MINIPROGRAM_TYPE_TEST;
        item.userName = "gh_880580ab416b";
        item.path = "pages/index/index?userId=" + ClientStateManager.getUserName();
        item.title = "分享小程序title";
        item.description = "小程序描述";
        item.thumbUrl = "https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=2030065636," +
                "303385792&fm=27&gp=0.jpg";
        return item;
    }

    /**
     * 打开微信小程序
     */
    public void openWxMini(WXMiniItem item) {
        if (item == null) {
            return;
        }
        if (wxApi == null) {
            wxApi = WXAPIFactory.createWXAPI(mContext, Constants.APP_ID);
        }
        WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
        req.userName = item.userName; // 填小程序原始id
        req.path = item.path;//拉起小程序页面的可带参路径，不填默认拉起小程序首页
        req.miniprogramType = item.miniprogramType;// 正式版:0，测试版:1，体验版:2
        wxApi.sendReq(req);
    }

    /**
     * 分享微信小程序(带图片地址)
     */
    public void shareWXMiniWithUrl(final WXMiniItem item) {
        Glide.with(mContext.getApplicationContext())
                .load(item.thumbUrl)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap>
                            glideAnimation) {
                        item.bytes = WXMiniUtil.bmpToByteArray(resource, true);
                        shareWXMini(item);
                    }
                });
    }

    /**
     * 分享微信小程序（带byte数组）
     */
    public void shareWXMini(WXMiniItem item) {
        if (item == null) {
            return;
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
        wxApi.sendReq(req);
    }

}

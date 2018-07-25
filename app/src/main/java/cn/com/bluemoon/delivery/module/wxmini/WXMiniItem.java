package cn.com.bluemoon.delivery.module.wxmini;

/**
 * Created by bm on 2018/7/24.
 */

public class WXMiniItem {

    /**
     * 打开小程序需要配置
     */
    public int miniprogramType;// 正式版:0，测试版:1，体验版:2
    public String userName;// 小程序原始id
    public String path;//小程序页面路径

    /**
     * 分享小程序需要增加配置
     */
    public String webpageUrl;// 兼容低版本的网页链接
    public String title;// 小程序消息title
    public String description;// 小程序消息desc
    public String thumbUrl;// 小程序消息封面图片对应的路径

    public byte[] bytes;// 小程序消息封面图片，小于128k（通过thumbUrl得到bytes）
}

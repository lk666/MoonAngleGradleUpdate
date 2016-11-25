package com.bluemoon.umengshare;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.text.ClipboardManager;
import android.widget.Toast;

import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;

/**
 * Created by chinwe on 2016/11/11.
 */

public class ShareHelper {

	/**
	 * 初始化，放在Application执行
	 * @param appContext
	 */
	public static void iniShare(Application appContext) {
		PlatformConfig.setWeixin("wx3b6e66b753fd84c2", "DSF23FewrwerE2342378934ds4879877");
		PlatformConfig.setSinaWeibo("4090679472", "e8d1ffe1012a89cb7e34a353d3693990");
		PlatformConfig.setQQZone("1104979860", "Qkg4yWZ5Gr07K0K5");
		UMShareAPI.get(appContext);
	}

	public static void share(final Activity activity, final ShareModel shareModel) {
		share(activity, shareModel, null);
	}


	//urlParam 必须为  key1=value1&key2=value2...
	public static void share(final Activity activity, final ShareModel shareModel, final ShareCallBack callBack) {

		final UMShareListener shareListener = new UMShareListener() {
			@Override
			public void onResult(SHARE_MEDIA platform) {
				Toast.makeText(activity, shareMediaToChineseString(platform) + " 分享成功啦", Toast.LENGTH_SHORT).show();
				if (null != callBack) {
					callBack.shareSuccess(platform, shareMediaToString(platform), shareModel);
				}
			}

			@Override
			public void onError(SHARE_MEDIA platform, Throwable t) {
				Toast.makeText(activity, shareMediaToChineseString(platform) + " 分享失败啦", Toast.LENGTH_SHORT).show();
				if (null != callBack) {
					callBack.shareError(platform, shareMediaToString(platform), shareModel, t.getMessage());
				}
			}

			@Override
			public void onCancel(SHARE_MEDIA platform) {
				Toast.makeText(activity, shareMediaToChineseString(platform) + " 分享取消了", Toast.LENGTH_SHORT).show();
				if (null != callBack) {
					callBack.shareCancel(platform, shareMediaToString(platform), shareModel);
				}
			}
		};

		ShareBoardlistener boardListener = new ShareBoardlistener() {
			@Override
			public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {

				if (null != callBack) {
					callBack.boardClickCallBack(share_media, shareMediaToString(share_media), shareModel);
				}
				String shareUrl = (shareModel.getuMTargetUrl().indexOf('?') > 0 ? shareModel.getuMTargetUrl() + "&platform=" : shareModel.getuMTargetUrl() + "?platform=")
						+ shareMediaToString(share_media);

				if (share_media == null) {
					if (snsPlatform.mKeyword.equals("share_button_custom_link")) {
						ClipboardManager cmb = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
						cmb.setText(shareUrl);
						Toast.makeText(activity, "复制链接成功", Toast.LENGTH_LONG).show();
					}

				} else {


					new ShareAction(activity).setPlatform(share_media).setCallback(shareListener)
							.withTitle(shareModel.getuMTitle())
							.withText(shareModel.getuMText())
							.withTargetUrl(shareUrl)
							.withMedia(shareModel.getuMImage())
							.share();
				}
			}
		};
		new ShareAction(activity)
				.setDisplayList(SHARE_MEDIA.SINA, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.WEIXIN_FAVORITE)
				.addButton("share_button_custom_link", "share_button_custom_link", "link", "link")
				.setShareboardclickCallback(boardListener).open();

	}

	private static String shareMediaToString(SHARE_MEDIA share_media) {
		if (null != share_media) {
			if (share_media.equals(SHARE_MEDIA.QQ)) {
				return "QQ";
			} else if (share_media.equals(SHARE_MEDIA.QZONE)) {
				return "qzone";
			} else if (share_media.equals(SHARE_MEDIA.SINA)) {
				return "sina";
			} else if (share_media.equals(SHARE_MEDIA.WEIXIN)) {
				return "wechatSession";
			} else if (share_media.equals(SHARE_MEDIA.WEIXIN_CIRCLE)) {
				return "wechatTimeLine";
			} else if (share_media.equals(SHARE_MEDIA.WEIXIN_FAVORITE)) {
				return "wechatFavorite";
			}
		}
		return "link";
	}

	private static String shareMediaToChineseString(SHARE_MEDIA share_media) {
		if (null != share_media) {
			if (share_media.equals(SHARE_MEDIA.QQ)) {
				return "QQ";
			} else if (share_media.equals(SHARE_MEDIA.QZONE)) {
				return "QQ空间";
			} else if (share_media.equals(SHARE_MEDIA.SINA)) {
				return "新浪";
			} else if (share_media.equals(SHARE_MEDIA.WEIXIN)) {
				return "微信";
			} else if (share_media.equals(SHARE_MEDIA.WEIXIN_CIRCLE)) {
				return "微信朋友圈";
			} else if (share_media.equals(SHARE_MEDIA.WEIXIN_FAVORITE)) {
				return "微信收藏";
			}
		}
		return "复制链接";
	}


}

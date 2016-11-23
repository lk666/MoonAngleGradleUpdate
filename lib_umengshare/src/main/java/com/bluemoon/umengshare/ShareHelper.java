package com.bluemoon.umengshare;

import android.app.Activity;
import android.app.Application;
import android.text.TextUtils;
import android.widget.Toast;

import com.umeng.socialize.*;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.utils.Log;

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
		PlatformConfig.setSinaWeibo("3418238449", "65f58f0b0b517ee241e11ffce080e676");
		PlatformConfig.setQQZone("1104979860", "Qkg4yWZ5Gr07K0K5");
		UMShareAPI.get(appContext);
	}

	public static void openShare(final Activity activity, ShareModel content) {
		openShare(activity, content, new UMShareListener() {
			@Override
			public void onResult(SHARE_MEDIA platform) {
				Log.d("plat", "platform" + platform);

				Toast.makeText(activity, platform + " 分享成功啦", Toast.LENGTH_SHORT).show();

			}

			@Override
			public void onError(SHARE_MEDIA platform, Throwable t) {
				Toast.makeText(activity, platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
				if (t != null) {
					Log.d("throw", "throw:" + t.getMessage());
				}
			}

			@Override
			public void onCancel(SHARE_MEDIA platform) {
				Toast.makeText(activity, platform + " 分享取消了", Toast.LENGTH_SHORT).show();
			}
		});
	}

	/**
	 * 打开分享面板
	 * @param activity
	 * @param content
	 * @param umShareListener
	 */
	public static void openShare(final Activity activity, final ShareModel content, final UMShareListener umShareListener) {
		if (activity==null||content == null||umShareListener == null) {
			return;
		}
		ShareAction action = new ShareAction(activity);
		action.setDisplayList(SHARE_MEDIA.SINA, SHARE_MEDIA.QQ,SHARE_MEDIA.QZONE, SHARE_MEDIA.WEIXIN,SHARE_MEDIA.WEIXIN_CIRCLE);
		if (!TextUtils.isEmpty(content.getuMTitle())) {
			action.withTitle(content.getuMTitle());
		}
		if (!TextUtils.isEmpty(content.getuMText())) {
			action.withText(content.getuMText());
		}
		if (!TextUtils.isEmpty(content.getuMTargetUrl())) {
			action.withTargetUrl(content.getuMTargetUrl());
		}
		if (content.getuMImage() != null) {
			action.withMedia(content.getuMImage());
		}
//		if (content.getuMusic() != null) {
//			action.withMedia(content.getuMusic());
//		}
//		if (content.getuMVideo() != null) {
//			action.withMedia(content.getuMVideo());
//		}
		action.setCallback(umShareListener);
		action.open();
	}
}

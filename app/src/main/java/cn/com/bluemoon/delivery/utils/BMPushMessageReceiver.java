/** 
 * Description:
 * Copyright: Copyright (c) 2016
 * Company: BLUE MOON
 * @author: Eric Liang 
 * @version 1.0
 * @date: 2016/1/22
 */
package cn.com.bluemoon.delivery.utils;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import cn.com.bluemoon.delivery.AppStartActivity;
import cn.com.bluemoon.delivery.ClientStateManager;
import cn.com.bluemoon.delivery.account.LoginActivity;

import com.baidu.android.pushservice.PushMessageReceiver;

/*
 *0 - Success
 *10001 - Network Problem
 *10101  Integrate Check Error
 *30600 - Internal Server Error
 *30601 - Method Not Allowed
 *30602 - Request Params Not Valid
 *30603 - Authentication Failed
 *30604 - Quota Use Up Payment Required
 *30605 -Data Required Not Found
 *30606 - Request Time Expires Timeout
 *30607 - Channel Token Timeout
 *30608 - Bind Relation Not Found
 *30609 - Bind Number Too Many

 */

public class BMPushMessageReceiver extends PushMessageReceiver {
	/** TAG to Log */
	public static final String TAG = BMPushMessageReceiver.class
			.getSimpleName();


	@Override
	public void onBind(Context context, int errorCode, String appid,
			String userId, String channelId, String requestId) {
		String responseString = "onBind errorCode=" + errorCode + " appid="
				+ appid + " userId=" + userId + " channelId=" + channelId
				+ " requestId=" + requestId;
		LogUtils.d(TAG, responseString);

		if (errorCode == 0) {
			LogUtils.d(TAG, "bind is success");

			if (!ClientStateManager.getChannelId(context).equals(channelId)) {
				ClientStateManager.setChannelId(context, channelId);
			}
		}


	}


	@Override
	public void onMessage(Context context, String message,
			String customContentString) {
		String messageString = " message=\"" + message
				+ "\" customContentString=" + customContentString;
		LogUtils.d(TAG, messageString);


		if (!TextUtils.isEmpty(customContentString)) {
			JSONObject customJson = null;
			try {
				customJson = new JSONObject(customContentString);
				String myvalue = null;
				if (!customJson.isNull("mykey")) {
					myvalue = customJson.getString("mykey");
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}


	@Override
	public void onNotificationClicked(Context context, String title,
			String description, String customContentString) {
		String notifyString = "notify title=\"" + title + "\" description=\""
				+ description + "\" customContent=" + customContentString;
		LogUtils.d(TAG, notifyString);

		if (!TextUtils.isEmpty(customContentString)) {
			JSONObject customJson = null;
			try {
				customJson = new JSONObject(customContentString);
				String value = null;
				if (!customJson.isNull("bluemoon")) {
					value = customJson.getString("bluemoon");
					if ("open".equals(value)) {
						Intent intent = new Intent();
						intent.putExtra("key", "value-->");
						intent.setClass(context.getApplicationContext(),
								LoginActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						context.getApplicationContext().startActivity(intent);
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		 updateContent(context, notifyString);
	}



	@Override
	public void onNotificationArrived(Context context, String title,
			String description, String customContentString) {

		String notifyString = "onNotificationArrived  title=\"" + title
				+ "\" description=\"" + description + "\" customContent="
				+ customContentString;
		LogUtils.d(TAG, notifyString);

		if (!TextUtils.isEmpty(customContentString)) {
			JSONObject customJson = null;
			try {
				customJson = new JSONObject(customContentString);
				String myvalue = null;
				if (!customJson.isNull("mykey")) {
					myvalue = customJson.getString("mykey");
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}


	@Override
	public void onSetTags(Context context, int errorCode,
			List<String> sucessTags, List<String> failTags, String requestId) {
		String responseString = "onSetTags errorCode=" + errorCode
				+ " sucessTags=" + sucessTags + " failTags=" + failTags
				+ " requestId=" + requestId;
		LogUtils.d(TAG, responseString);

	}


	@Override
	public void onDelTags(Context context, int errorCode,
			List<String> sucessTags, List<String> failTags, String requestId) {
		String responseString = "onDelTags errorCode=" + errorCode
				+ " sucessTags=" + sucessTags + " failTags=" + failTags
				+ " requestId=" + requestId;
		LogUtils.d(TAG, responseString);


	}


	@Override
	public void onListTags(Context context, int errorCode, List<String> tags,
			String requestId) {
		String responseString = "onListTags errorCode=" + errorCode + " tags="
				+ tags;
		LogUtils.d(TAG, responseString);

	}

	@Override
	public void onUnbind(Context context, int errorCode, String requestId) {
		String responseString = "onUnbind errorCode=" + errorCode
				+ " requestId = " + requestId;
		LogUtils.d(TAG, responseString);

		if (errorCode == 0) {
			LogUtils.d(TAG, "���ɹ�");
		}

	}

	private void updateContent(Context context, String content) {
		LogUtils.d(TAG, "updateContent");
		
		Intent intent = new Intent();
		intent.setClass(context.getApplicationContext(),
				AppStartActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("","");
		context.getApplicationContext().startActivity(intent);
	}

}

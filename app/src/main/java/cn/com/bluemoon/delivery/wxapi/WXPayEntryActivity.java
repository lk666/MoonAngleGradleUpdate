package cn.com.bluemoon.delivery.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.manager.ActivityManager;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {


	private IWXAPI api;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay_result);
		// 加入管理器
		ActivityManager manager = ActivityManager.getInstance();
		manager.pushOneActivity(this);

		api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
		api.handleIntent(getIntent(), this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		api.handleIntent(intent, this);
	}


	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
		String payresult;
		switch (resp.errCode) {
			case BaseResp.ErrCode.ERR_OK:
				//发送成功
				payresult = "success";
				break;
			/*case BaseResp.ErrCode.ERR_USER_CANCEL:
				//发送取消
			case BaseResp.ErrCode.ERR_AUTH_DENIED:
				//发送被拒绝*/
			default:
				//其他
				payresult = "error";
				break;
		}
		Intent mIntent = new Intent("PAY_SUCCESS");
		mIntent.putExtra("payResult", payresult);
		sendBroadcast(mIntent);
		finish();
	}
}
package cn.com.bluemoon.delivery.utils.service;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.unionpay.UPPayAssistEx;

import java.util.Map;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.entity.IPayOnlineResult;
import cn.com.bluemoon.delivery.entity.PayResult;
import cn.com.bluemoon.delivery.entity.WXPayInfo;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;

/**
 * @author liangjiangli
 */

public class PayService {

    private String TAG = "PayService";
    private IPayOnlineResult listener;

    private Activity mContext;
    private static final int SDK_PAY_FLAG = 1;
    private IWXAPI msgApi;
    private PayReq req;
    private final String ACTION = "PAY_SUCCESS";

    public PayService(Activity context, IPayOnlineResult listener) {
        this.mContext = context;
        this.listener = listener;
    }

    public BroadcastReceiver getWXResutReceiver() {
        return mBroadcastReceiver;
    }

    public IntentFilter getWXIntentFilter() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(ACTION);
        return myIntentFilter;
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ACTION)) {
                String payresult = intent.getStringExtra("payResult");
                if ("success".equals(payresult)) {
                    listener.isSuccess(true);
                } else {
                    listener.isSuccess(false);
                }
            }
        }

    };


    public void wxPay(WXPayInfo wxPayInfo) {
        msgApi = WXAPIFactory.createWXAPI(mContext, Constants.APP_ID);
        if (!msgApi.isWXAppInstalled()) {
            Toast.makeText(mContext,mContext.getString(R.string.pay_online_wechat_not_exist), Toast.LENGTH_SHORT).show();
            return;
        }
        msgApi.registerApp(Constants.APP_ID);
        req = new PayReq();
        req.appId = Constants.APP_ID;
        req.partnerId = Constants.PARTNER_ID;
        req.prepayId = wxPayInfo.getPrepay_id();
        req.packageValue = "Sign=WXPay";
        req.nonceStr = wxPayInfo.getNonceStr();
        req.timeStamp = String.valueOf(wxPayInfo.getTimeStamp());
        req.sign = wxPayInfo.getSign();
        msgApi.sendReq(req);
    }


    public void aliPay(final String aliPayInfo) {
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(mContext);
                String version = alipay.getVersion();
                LogUtils.d("alipay version = " + version);
                // 调用支付接口，获取支付结果
                Map<String,String> result = alipay.payV2(aliPayInfo, true);
                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();

    }

    public void unionPay(String tn) {
        if (UPPayAssistEx.checkInstalled(mContext)) {
            cn.com.bluemoon.liblog.LogUtils.e(TAG, " plugin not found or need upgrade!!!");
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle(mContext.getString(R.string.prompt_title));
            builder.setMessage(mContext.getString(R.string.pay_online_unionpay_not_exist));

            builder.setNegativeButton(mContext.getString(R.string.btn_ok),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            UPPayAssistEx.installUPPayPlugin(mContext);
                            dialog.dismiss();
                        }
                    });

            builder.setPositiveButton(mContext.getString(R.string.btn_cancel),
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            dialog.dismiss();
                        }
                    });
            builder.create().show();

        } else {
            UPPayAssistEx.startPay(mContext, null, null, tn, "00");
        }
    }

    private boolean verify(String msg, String sign64, String mode) {
        // 此处的verify，商户需送去商户后台做验签
        return true;

    }


    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String,String>) msg.obj);
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    LogUtils.d("test", "alipay resultStatus = " + resultStatus);
                    LogUtils.d("test", "alipay resultInfo = " + resultInfo);
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        listener.isSuccess(true);
                    } else {
                        listener.isSuccess(false);
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        // "6001" 用户取消
                        // "6002" 网络失败
                        // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                    }
                    break;
                }
                default:
                    break;
            }

        }

        ;
    };


}


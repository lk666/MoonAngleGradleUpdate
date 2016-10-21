package cn.com.bluemoon.delivery.utils.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;

import cn.com.bluemoon.delivery.utils.LogUtils;

public abstract class PushGTReceiver extends BroadcastReceiver {

    protected static final String TAG = "push";

    /**
     * 应用未启动, 个推 service已经被唤醒,保存在该时间段内离线消息
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        LogUtils.d(TAG, "onReceive() action=" + bundle.getInt("action"));
        switch (bundle.getInt(PushConsts.CMD_ACTION)) {
            case PushConsts.GET_MSG_DATA:
                // 获取透传数据
                // String appid = bundle.getString("appid");
                byte[] payload = bundle.getByteArray("payload");
                String taskid = bundle.getString("taskid");
                String messageid = bundle.getString("messageid");

                // smartPush第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
                boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskid,
                        messageid, 90001);
                LogUtils.d(TAG, "第三方回执接口调用" + (result ? "成功" : "失败"));

                String data = null;
                if (payload != null) {
                    data = new String(payload);
                }
                LogUtils.d(TAG, "receiver payload : " + data);

                onResult(context, data, result);

                break;

            case PushConsts.GET_CLIENTID:
                // 获取ClientID(CID)
                // 第三方应用需要将CID上传到第三方服务器，并且将当前用户帐号和CID进行关联，以便日后通过用户帐号查找CID进行消息推送
                String cid = bundle.getString("clientid");
                onClientId(context, cid);
                break;
            case PushConsts.GET_SDKONLINESTATE:
                boolean online = bundle.getBoolean("onlineState");
                Log.d("GetuiSdkDemo", "online = " + online);
                onSdkOnlineState(context, online);
                break;

            case PushConsts.SET_TAG_RESULT:
                String sn = bundle.getString("sn");
                String code = bundle.getString("code");

                String text = "设置标签失败, 未知异常";
                switch (Integer.valueOf(code)) {
                    case PushConsts.SETTAG_SUCCESS:
                        text = "设置标签成功";
                        break;

                    case PushConsts.SETTAG_ERROR_COUNT:
                        text = "设置标签失败, tag数量过大, 最大不能超过200个";
                        break;

                    case PushConsts.SETTAG_ERROR_FREQUENCY:
                        text = "设置标签失败, 频率过快, 两次间隔应大于1s";
                        break;

                    case PushConsts.SETTAG_ERROR_REPEAT:
                        text = "设置标签失败, 标签重复";
                        break;

                    case PushConsts.SETTAG_ERROR_UNBIND:
                        text = "设置标签失败, 服务未初始化成功";
                        break;

                    case PushConsts.SETTAG_ERROR_EXCEPTION:
                        text = "设置标签失败, 未知异常";
                        break;

                    case PushConsts.SETTAG_ERROR_NULL:
                        text = "设置标签失败, tag 为空";
                        break;

                    case PushConsts.SETTAG_NOTONLINE:
                        text = "还未登陆成功";
                        break;

                    case PushConsts.SETTAG_IN_BLACKLIST:
                        text = "该应用已经在黑名单中,请联系售后支持!";
                        break;

                    case PushConsts.SETTAG_NUM_EXCEED:
                        text = "已存 tag 超过限制";
                        break;

                    default:
                        break;
                }

                LogUtils.d(TAG, "settag result sn = " + sn + ", code = " + code);
                LogUtils.d(TAG, "settag result sn = " + text);
                break;
            case PushConsts.THIRDPART_FEEDBACK:

                String appid2 = bundle.getString("appid");
                String taskid2 = bundle.getString("taskid");
                String actionid2 = bundle.getString("actionid");
                String result2 = bundle.getString("result");
                long timestamp2 = bundle.getLong("timestamp");

                LogUtils.d(TAG, "appid：" + appid2 + "\n" + "taskid：" + taskid2 + "\n" +
                        "actionid：" + actionid2 + "\n" + "result：" + result2 + "\n" +
                        "timestamp：" + timestamp2);

                break;

            default:
                break;
        }
    }

    protected abstract void onClientId(Context context, String clientId);

    protected abstract void onResult(Context context, String data, boolean isSuccess);

    protected void onSdkOnlineState(Context context, boolean state) {

    }

}

package cn.com.bluemoon.delivery.module.clothing.collect.withoutorder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.ClientStateManager;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.clothing.ResultActivityDesc;
import cn.com.bluemoon.delivery.module.base.BaseActionBarActivity;
import cn.com.bluemoon.delivery.module.clothing.collect.withoutorder.createclothesinfo
        .CreateClothesInfoActivity;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;

/**
 * 活动收衣
 * Created by luokai on 2016/6/28.
 */
public class ActivityDescActivity extends BaseActionBarActivity {
    /**
     * 有进行过成功的收衣操作
     */
    public static final int RESULT_HAS_COLLECT = 0x66;

    /**
     * 活动编码
     */
    private final static String EXTRA_ACTIVITY_CODE = "EXTRA_ACTIVITY_CODE";
    private static final int REQUEST_CODE_CREATE_COLLECT_ORDER = 0x77;

    /**
     * 活动编号
     */
    private String activityCode;

    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.tv_desc)
    TextView tvDesc;
    @Bind(R.id.btn_start)
    Button btnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_desc);
        ButterKnife.bind(this);

        setIntentData();
        initView();
        getData();
    }

    private void setIntentData() {
        activityCode = getIntent().getStringExtra(EXTRA_ACTIVITY_CODE);
        if (activityCode == null) {
            activityCode = "";
        }
    }

    private void initView() {
    }

    private void getData() {
        showProgressDialog();
        DeliveryApi.getActivityInfo(ClientStateManager.getLoginToken(this), activityCode,
                getActivityInfoHandler);

    }

    /**
     * 获取订单详情数据返回
     */
    AsyncHttpResponseHandler getActivityInfoHandler = new TextHttpResponseHandler(HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            LogUtils.d(getDefaultTag(), "获取订单详情数据返回 result = " + responseString);
            dismissProgressDialog();
            try {
                ResultActivityDesc result = JSON.parseObject(responseString,
                        ResultActivityDesc.class);
                if (result.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    setData(result);

                } else {
                    PublicUtil.showErrorMsg(ActivityDescActivity.this, result);
                }
            } catch (Exception e) {
                LogUtils.e(getDefaultTag(), e.getMessage());
                PublicUtil.showToastServerBusy();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers,
                              String responseString, Throwable throwable) {
            LogUtils.e(getDefaultTag(), throwable.getMessage());
            dismissProgressDialog();
            PublicUtil.showToastServerOvertime();
        }
    };

    private void setData(ResultActivityDesc result) {
        tvTitle.setText(result.getActivityName());
        tvDesc.setText(result.getActivityDesc());
    }

    @Override
    protected int getActionBarTitleRes() {
        return R.string.title_activity_dec;
    }

    @OnClick(R.id.btn_start)
    public void onClick() {
        // 创建收衣订单
        CreateCollectOrderActivity.actionStart(this, REQUEST_CODE_CREATE_COLLECT_ORDER,
                activityCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED) {
            return;
        }

        switch (requestCode) {
            // 收衣
            case REQUEST_CODE_CREATE_COLLECT_ORDER:
                if (resultCode == CreateCollectOrderActivity.RESULT_COLLECT_SCUUESS) {
                    setResult(RESULT_HAS_COLLECT);
                    return;
                }
                break;
            default:
                break;
        }
    }

    public static void actionStart(Activity activity, int requestCode, String activityCode) {
        Intent intent = new Intent(activity, ActivityDescActivity.class);
        intent.putExtra(EXTRA_ACTIVITY_CODE, activityCode);
        activity.startActivityForResult(intent, requestCode);
    }

}

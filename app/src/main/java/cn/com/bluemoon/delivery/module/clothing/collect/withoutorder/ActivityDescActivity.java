package cn.com.bluemoon.delivery.module.clothing.collect.withoutorder;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;

public class ActivityDescActivity extends BaseActionBarActivity {

    /**
     * 活动编码
     */
    public final static String EXTRA_ACTIVITY_CODE = "EXTRA_ACTIVITY_CODE";
    /**
     * 有进行过成功的收衣操作
     */
    public static final int RESULT_HAS_COLLECT = 0x66;

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
        // TODO: lk 2016/6/28 收衣登记
        PublicUtil.showToast("收衣登记" + activityCode);
    }
}

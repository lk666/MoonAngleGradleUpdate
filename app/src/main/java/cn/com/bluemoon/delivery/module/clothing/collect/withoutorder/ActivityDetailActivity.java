package cn.com.bluemoon.delivery.module.clothing.collect.withoutorder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.bluemoon.delivery.ClientStateManager;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.clothing.ResultActivityDesc;
import cn.com.bluemoon.delivery.app.api.model.clothing.ResultClothesDeliverInfos;
import cn.com.bluemoon.delivery.module.base.BaseActionBarActivity;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;

/**
 * Created by allenli on 2016/6/24.
 */
public class ActivityDetailActivity extends BaseActionBarActivity {

    @Bind(R.id.txt_activity_name)
    TextView txtActivityName;
    @Bind(R.id.txt_activity_desc)
    TextView txtActivityDesc;

    private String activityCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clothing_activity_detail);
        activityCode = getIntent().getStringExtra("activityCode");
        ButterKnife.bind(this);
        init();
    }

    @Override
    protected int getActionBarTitleRes() {
        return R.string.clothing_without_order_activity_desc;
    }

    private void init() {
        showProgressDialog();
        DeliveryApi.getActivityInfo(ClientStateManager.getLoginToken(ActivityDetailActivity.this), activityCode, activityHandler);
    }

    private void initView(ResultActivityDesc desc){
         txtActivityName.setText(desc.getActivityName());
        txtActivityDesc.setText(desc.getActivityDesc());
    }
    public static void actionStart(Context context, String activityCode) {
        Intent intent = new Intent(context, ActivityDetailActivity.class);
        intent.putExtra("activityCode", activityCode);
        context.startActivity(intent);
    }

    AsyncHttpResponseHandler activityHandler = new TextHttpResponseHandler(
            HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            LogUtils.d(getDefaultTag(), "activityHandler result = " + responseString);
            dismissProgressDialog();
            try {
                ResultActivityDesc result = JSON.parseObject(responseString,
                        ResultActivityDesc.class);
                if (result.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    initView(result);

                } else {
                    PublicUtil.showErrorMsg(ActivityDetailActivity.this, result);
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


}

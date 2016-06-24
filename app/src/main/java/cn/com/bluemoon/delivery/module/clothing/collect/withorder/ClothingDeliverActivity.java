package cn.com.bluemoon.delivery.module.clothing.collect.withorder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.ClientStateManager;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.clothing.ResultClothesDeliverInfos;
import cn.com.bluemoon.delivery.app.api.model.clothing.ResultUserInfo;
import cn.com.bluemoon.delivery.app.api.model.clothing.collect.ClothesDeliverInfo;
import cn.com.bluemoon.delivery.module.base.BaseActionBarActivity;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.delivery.utils.ViewHolder;

/**
 * Created by allenli on 2016/6/22.
 */
public class ClothingDeliverActivity extends BaseActionBarActivity {

    @Bind(R.id.ed_user_id)
    EditText editDeliverId;
    @Bind(R.id.txt_deliver_name)
    TextView txtDeliverName;
    @Bind(R.id.txt_deliver_phone)
    TextView txtDeliverPhone;
    @Bind(R.id.txt_deliver_remark)
    TextView txtDeliverRemark;
    @Bind(R.id.btn_search)
    Button btnSearch;
    @Bind(R.id.btn_ok)
    Button btnConforim;
    @Bind(R.id.btn_cancel)
    Button btnCancel;
    @Bind(R.id.layout_name)
    RelativeLayout layoutName;
    @Bind(R.id.layout_phone)
    RelativeLayout layoutPhone;
    @Bind(R.id.listview_log)
    ListView listViewLog;
    @Bind(R.id.line_name)
    View lineName;
    @Bind(R.id.line_phone)
    View linePhone;
    private String collectCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clothing_deliver);
        collectCode = getIntent().getStringExtra("collectCode");
        ButterKnife.bind(this);
        init();
    }

    @Override
    protected int getActionBarTitleRes() {
        return R.string.clothing_deliver_title;
    }

    public static void actionStart(Context context, String collectCode) {
        Intent intent = new Intent(context, ClothingDeliverActivity.class);
        intent.putExtra("collectCode", collectCode);
        context.startActivity(intent);
    }

    private void init() {
        showProgressDialog();
        DeliveryApi.queryClothesDeliverInfo(ClientStateManager.getLoginToken(ClothingDeliverActivity.this), collectCode, logHandler);
    }


    @OnClick({R.id.btn_ok, R.id.btn_cancel, R.id.btn_search})
    public void onClick(View view) {
        switch (view.getId()) {
            // 确定
            case R.id.btn_ok:
                confrim();
                break;
            case R.id.btn_cancel:
                finish();
                break;
            case R.id.btn_search:
                search();
                break;
        }
    }

    private void confrim() {
        if (!StringUtil.isEmpty(editDeliverId.getText().toString()) && !StringUtil.isEmpty(txtDeliverName.getText().toString())
                && !StringUtil.isEmpty(txtDeliverPhone.getText().toString())) {
            showProgressDialog();
            DeliveryApi.turnOrderInfo(ClientStateManager.getLoginToken(this), collectCode, editDeliverId.getText().toString(), txtDeliverName.getText().toString(), txtDeliverPhone.getText().toString()
                    , txtDeliverRemark.getText().toString(), baseHandler);
        }
    }

    private void search() {
        showProgressDialog();
        DeliveryApi.getEmp(ClientStateManager.getLoginToken(this), editDeliverId.getText().toString(), searchHandler);
    }


    private void initLogs(List<ClothesDeliverInfo> clothesDeliverInfos) {
        if (null != clothesDeliverInfos && clothesDeliverInfos.size() > 0) {
            DeliverAdapter deliverAdpter = new DeliverAdapter(this, null);
            deliverAdpter.setList(clothesDeliverInfos);
            listViewLog.setAdapter(deliverAdpter);
        }
    }

    AsyncHttpResponseHandler searchHandler = new TextHttpResponseHandler(
            HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            LogUtils.d(getDefaultTag(), "searchHandler result = " + responseString);
            dismissProgressDialog();
            try {
                ResultUserInfo result = JSON.parseObject(responseString,
                        ResultUserInfo.class);
                if (result.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    layoutName.setVisibility(View.VISIBLE);
                    layoutPhone.setVisibility(View.VISIBLE);
                    lineName.setVisibility(View.VISIBLE);
                    linePhone.setVisibility(View.VISIBLE);
                    txtDeliverPhone.setText(result.getPhone());
                    txtDeliverName.setText(result.getEmpName());
                    txtDeliverRemark.setText("");
                } else {
                    PublicUtil.showErrorMsg(ClothingDeliverActivity.this, result);
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

    AsyncHttpResponseHandler logHandler = new TextHttpResponseHandler(
            HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            LogUtils.d(getDefaultTag(), "logHandler result = " + responseString);
            dismissProgressDialog();
            try {
                ResultClothesDeliverInfos result = JSON.parseObject(responseString,
                        ResultClothesDeliverInfos.class);
                if (result.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    initLogs(result.getClothesDeliverInfo());
                } else {
                    PublicUtil.showErrorMsg(ClothingDeliverActivity.this, result);
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

    class DeliverAdapter extends BaseListAdapter<ClothesDeliverInfo> {

        public DeliverAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.item_deliver_log;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            final ClothesDeliverInfo deliver = (ClothesDeliverInfo) getItem(position);
            if (deliver == null) {
                return;
            }

            TextView tvContent = ViewHolder.get(convertView, R.id.txt_content);

            tvContent.setText(String.format("%s,%s,%s,%s\n%s", DateUtil.getTime(deliver.getTransmitTime(), "yyyy-MM-dd HH:mm:ss"),
                    deliver.getReceiverCode(),
                    deliver.getReceiverName(),
                    deliver.getReceiverPhone(),
                    deliver.getRefusalReason()));

        }

    }
}
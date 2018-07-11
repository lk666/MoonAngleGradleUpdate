package cn.com.bluemoon.delivery.module.wash.collect.withorder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;
import org.apache.http.protocol.HTTP;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.clothing.ResultClothesDeliverInfos;
import cn.com.bluemoon.delivery.app.api.model.clothing.ResultUserInfo;
import cn.com.bluemoon.delivery.app.api.model.clothing.collect.ClothesDeliverInfo;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.module.wash.collect.DeliverAdapter;
import cn.com.bluemoon.delivery.module.oldbase.BaseActionBarActivity;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.lib.utils.LibViewUtil;

/**
 * 衣物转交
 * Created by allenli on 2016/6/22.
 */
public class ClothingDeliverActivity extends BaseActionBarActivity {

    @BindView(R.id.ed_user_id)
    EditText editDeliverId;
    @BindView(R.id.txt_deliver_name)
    TextView txtDeliverName;
    @BindView(R.id.txt_deliver_phone)
    TextView txtDeliverPhone;
    @BindView(R.id.txt_deliver_remark)
    EditText txtDeliverRemark;
    @BindView(R.id.btn_search)
    Button btnSearch;
    @BindView(R.id.btn_ok)
    Button btnConforim;
    @BindView(R.id.btn_cancel)
    Button btnCancel;
    @BindView(R.id.layout_name)
    RelativeLayout layoutName;
    @BindView(R.id.layout_phone)
    RelativeLayout layoutPhone;
    @BindView(R.id.listview_log)
    ListView listViewLog;
    @BindView(R.id.line_name)
    View lineName;
    @BindView(R.id.line_phone)
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

    public static void actionStart(Fragment fragment, String collectCode, int requestCode) {
        Intent intent = new Intent(fragment.getActivity(), ClothingDeliverActivity.class);
        intent.putExtra("collectCode", collectCode);
        fragment.startActivityForResult(intent, requestCode);
    }

    private void init() {
        btnSearch.setEnabled(false);
        btnConforim.setEnabled(false);

        editDeliverId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (StringUtil.isEmpty(s.toString())) {
                    layoutName.setVisibility(View.GONE);
                    layoutPhone.setVisibility(View.GONE);
                    txtDeliverName.setText("");
                    txtDeliverPhone.setText("");
                    btnSearch.setEnabled(false);
                    btnConforim.setEnabled(false);
                } else {
                    btnSearch.setEnabled(true);
                    // btnConforim.setEnabled(true);
                }
            }
        });


        txtDeliverRemark.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (txtDeliverRemark.getLineCount() > 1) {
                    txtDeliverRemark.setGravity(Gravity.START);
                } else {
                    txtDeliverRemark.setGravity(Gravity.END);
                }
            }
        });

        showProgressDialog();
        DeliveryApi.queryTransmitInfo(ClientStateManager.getLoginToken(ClothingDeliverActivity
                .this), collectCode, logHandler);
    }


    @OnClick({R.id.btn_ok, R.id.btn_cancel, R.id.btn_search})
    public void onClick(View view) {
        switch (view.getId()) {
            // 确定
            case R.id.btn_ok:
                confrim();

                break;
            case R.id.btn_cancel:
                setResult(Activity.RESULT_CANCELED);
                finish();
                break;
            case R.id.btn_search:
                LibViewUtil.hideIM(editDeliverId);
                search();
                break;
        }
    }

    private void confrim() {
        if (!StringUtil.isEmpty(editDeliverId.getText().toString()) && !StringUtil.isEmpty
                (txtDeliverName.getText().toString())
                && !StringUtil.isEmpty(txtDeliverPhone.getText().toString())) {
            showProgressDialog();
            DeliveryApi.turnOrderInfo(ClientStateManager.getLoginToken(this), collectCode,
                    editDeliverId.getText().toString(), txtDeliverName.getText().toString(),
                    txtDeliverPhone.getText().toString()
                    , txtDeliverRemark.getText().toString(), createResponseHandler(new IHttpResponseHandler() {
                        @Override
                        public void onResponseSuccess(String responseString) {
                            setResult(Activity.RESULT_OK);
                            finish();
                        }
                    }));
        } else {
            PublicUtil.showToast(getString(R.string.no_user_error_message));
        }
    }

    private void search() {
        if (!StringUtil.isEmpty(editDeliverId.getText().toString())) {
            showProgressDialog();
            DeliveryApi.getEmp(ClientStateManager.getLoginToken(this), editDeliverId.getText()
                    .toString(), searchHandler);
        }
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
                    //txtDeliverRemark.setText("");
                    btnConforim.setEnabled(true);
                    return;
                } else {
                    PublicUtil.showErrorMsg(ClothingDeliverActivity.this, result);
                }
            } catch (Exception e) {
                LogUtils.e(getDefaultTag(), e.getMessage());
                PublicUtil.showToastServerBusy();
            }
            btnConforim.setEnabled(false);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers,
                              String responseString, Throwable throwable) {
            LogUtils.e(getDefaultTag(), throwable.getMessage());
            dismissProgressDialog();
            PublicUtil.showToastServerOvertime();
            btnConforim.setEnabled(false);
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


}

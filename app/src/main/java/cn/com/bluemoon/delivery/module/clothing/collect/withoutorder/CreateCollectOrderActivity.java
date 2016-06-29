package cn.com.bluemoon.delivery.module.clothing.collect.withoutorder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.ClientStateManager;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.clothing.ResultQueryActivityLimitNum;
import cn.com.bluemoon.delivery.app.api.model.clothing.collect.UploadClothesInfo;
import cn.com.bluemoon.delivery.module.base.BaseActionBarActivity;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.module.clothing.collect.ClothesInfoAdapter;
import cn.com.bluemoon.delivery.ui.NoScrollListView;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;

/**
 * 创建收衣订单
 * Created by luokai on 2016/6/28.
 */
public class CreateCollectOrderActivity extends BaseActionBarActivity implements
        OnListItemClickListener {

    /**
     * 创建收衣订单成功
     */
    public static final int RESULT_COLLECT_SCUUESS = 0x77;

    /**
     * 活动编码
     */
    private final static String EXTRA_ACTIVITY_CODE = "EXTRA_ACTIVITY_CODE";

    @Bind(R.id.et_name)
    EditText etName;
    @Bind(R.id.et_phone)
    EditText etPhone;
    @Bind(R.id.tv_province_county)
    TextView tvProvinceCounty;
    @Bind(R.id.tv_province_street_village)
    TextView tvProvinceStreetVillage;
    @Bind(R.id.et_address)
    EditText etAddress;
    @Bind(R.id.tv_collect_brcode)
    TextView tvCollectBrcode;
    @Bind(R.id.tv_appoint_back_time)
    TextView tvAppointBackTime;
    @Bind(R.id.v_div_appoint_back_time)
    View vDivAppointBackTime;
    @Bind(R.id.ll_appoint_back_time)
    LinearLayout llAppointBackTime;
    @Bind(R.id.tv_actual_collect_count)
    TextView tvActualCollectCount;
    @Bind(R.id.btn_add)
    Button btnAdd;
    @Bind(R.id.lv_order_receive)
    NoScrollListView lvOrderReceive;
    @Bind(R.id.btn_finish)
    Button btnFinish;

    /**
     * 活动编号
     */
    private String activityCode;

    /**
     * 最多衣物件数
     */
    private int limitNum;

    /**
     * 记录的待提交衣物
     */
    private List<UploadClothesInfo> clothesInfo;

    private ClothesInfoAdapter clothesInfoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_collect_order);
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
        etName.addTextChangedListener(etChangedWatcher);
        etPhone.addTextChangedListener(etChangedWatcher);
        etAddress.addTextChangedListener(etChangedWatcher);

        vDivAppointBackTime.setVisibility(View.GONE);
        llAppointBackTime.setVisibility(View.GONE);

        btnAdd.setEnabled(false);
        btnFinish.setEnabled(false);

        clothesInfo = new ArrayList<>();
        clothesInfoAdapter = new ClothesInfoAdapter(this, this);
        lvOrderReceive.setAdapter(clothesInfoAdapter);
    }

    private TextWatcher etChangedWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (TextUtils.isEmpty(s.toString())) {
                checkBtnFinishEnable();
            }
        }
    };


    /**
     * 设置完成按钮的可点击性
     */
    private void checkBtnFinishEnable() {
        if (TextUtils.isEmpty(etName.getText().toString()) ||
                TextUtils.isEmpty(etPhone.getText().toString()) ||
                TextUtils.isEmpty(tvProvinceCounty.getText().toString()) ||
                TextUtils.isEmpty(tvProvinceStreetVillage.getText().toString()) ||
                TextUtils.isEmpty(etAddress.getText().toString()) ||
                clothesInfo == null || clothesInfo.size() < 1) {
            btnFinish.setEnabled(false);
        } else {
            btnFinish.setEnabled(true);
        }
    }

    private void getData() {
        showProgressDialog();
        DeliveryApi.queryActivityLimitNum(ClientStateManager.getLoginToken(this), activityCode,
                queryActivityLimitNumHandler);

    }

    /**
     * 查询活动收衣上限返回
     */
    AsyncHttpResponseHandler queryActivityLimitNumHandler = new TextHttpResponseHandler(HTTP
            .UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            LogUtils.d(getDefaultTag(), "查询活动收衣上限返回 result = " + responseString);
            dismissProgressDialog();
            try {
                ResultQueryActivityLimitNum result = JSON.parseObject(responseString,
                        ResultQueryActivityLimitNum.class);
                if (result.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    setData(result);

                } else {
                    PublicUtil.showErrorMsg(CreateCollectOrderActivity.this, result);
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

    private void setData(ResultQueryActivityLimitNum result) {
        limitNum = result.getLimitNum();

        if (limitNum > 0) {
            btnAdd.setEnabled(true);
        } else {
            btnAdd.setEnabled(false);
        }
    }

    @Override
    protected int getActionBarTitleRes() {
        return R.string.title_activity_dec;
    }

    @OnClick(R.id.btn_start)
    public void onClick() {
        // TODO: lk 2016/6/29  完成收衣
//        DeliveryApi.registerCreatedCollectInfo
    }

    @OnClick({R.id.tv_province_county, R.id.tv_province_street_village, R.id.tv_collect_brcode,
            R.id.tv_appoint_back_time, R.id.btn_add, R.id.btn_finish})
    public void onClick(View view) {
        switch (view.getId()) {
            // TODO: lk 2016/6/28 省市选择
            case R.id.tv_province_county:
                break;
            // TODO: lk 2016/6/28 街道选择
            case R.id.tv_province_street_village:
                break;
            // TODO: lk 2016/6/28 收衣单条码扫描/输入
            case R.id.tv_collect_brcode:
                break;
            // TODO: lk 2016/6/28 预约时间选择
            case R.id.tv_appoint_back_time:
                break;
            // TODO: lk 2016/6/28 添加衣物
            case R.id.btn_add:
                break;
            // TODO: lk 2016/6/28 完成收衣
            case R.id.btn_finish:
                break;
        }
    }

    public static void actionStart(Activity activity, int requestCode, String activityCode) {
        Intent intent = new Intent(activity, CreateCollectOrderActivity.class);
        intent.putExtra(EXTRA_ACTIVITY_CODE, activityCode);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    public void onItemClick(Object item, View view, int position) {
        // TODO: lk 2016/6/28
    }
}

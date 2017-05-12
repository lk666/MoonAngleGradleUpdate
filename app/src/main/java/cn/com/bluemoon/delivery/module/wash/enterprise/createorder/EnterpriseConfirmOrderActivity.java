package cn.com.bluemoon.delivery.module.wash.enterprise.createorder;

import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.AppContext;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.EnterpriseApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.wash.enterprise.ClothesInfo;
import cn.com.bluemoon.delivery.app.api.model.wash.enterprise.ResultEnterpriseDetail;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.wash.enterprise.event.ConfirmEvent;
import cn.com.bluemoon.delivery.ui.NoScrollListView;
import cn.com.bluemoon.delivery.utils.DensityUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;

/**
 * 确认订单信息
 * Created by tangqiwei on 2017/5/5.
 */

public class EnterpriseConfirmOrderActivity extends BaseActivity {

    public static final int RESULT_CANCEL_CONFIRM = 0x7777;
    @Bind(R.id.txt_order_code)
    TextView txtOrderCode;
    @Bind(R.id.txt_state)
    TextView txtState;
    @Bind(R.id.txt_name)
    TextView txtName;
    @Bind(R.id.txt_name_code)
    TextView txtNameCode;
    @Bind(R.id.txt_phone)
    TextView txtPhone;
    @Bind(R.id.lv_clothes)
    NoScrollListView lvClothes;
    @Bind(R.id.txt_collect_bag)
    TextView txtCollectBag;
    @Bind(R.id.et_backup)
    EditText etBackup;
    @Bind(R.id.llayout_screen_bottom)
    LinearLayout layoutScreenBottom;
    @Bind(R.id.llayout_scroll)
    LinearLayout layoutScroll;
    @Bind(R.id.llayout_order_details)
    LinearLayout layoutOrderDetails;


    private final static String PREFIX_NUMBER = "x";
    private final static String PREFIX_PRICE = "¥";

    private List<Map<String, String>> list;
    private ResultEnterpriseDetail enterpriseDetail;
    private String outerCode;

    private int layoutHeight;

    /**
     * 商品名称、商品数量、商品价格、衣物ID、商品编码
     */
    private String[] KEYS = new String[]{"name", "number", "price", "clothesId", "washCode"};

    public static void actionStart(Activity context, String outerCode, int requestCode) {
        Intent intent = new Intent(context, EnterpriseConfirmOrderActivity.class);
        intent.putExtra("outerCode", outerCode);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_confirm_order;
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.title_affirm_order_info);
    }

    @Override
    public void initView() {

        etBackup.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (etBackup.getLineCount() > 1) {
                    etBackup.setGravity(Gravity.START);
                } else {
                    etBackup.setGravity(Gravity.END);
                }
            }
        });
    }

    @Override
    public void initData() {
        outerCode = getIntent().getStringExtra("outerCode");
        EnterpriseApi.getWashEnterpriseDetail(outerCode, getToken(), getNewHandler(1,
                ResultEnterpriseDetail.class));
        ViewTreeObserver layoutVto = layoutOrderDetails.getViewTreeObserver();
        layoutVto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                layoutOrderDetails.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                layoutHeight = layoutOrderDetails.getHeight();
                setButtonLocation();
            }
        });
    }

    /**
     * 设置按钮位置
     */
    private void setButtonLocation() {
        if (layoutHeight > 0) {
            if (AppContext.getInstance().getDisplayHeight() > layoutHeight + DensityUtil.dip2px
                    (this, 45 + 50)) {
                layoutScreenBottom.setVisibility(View.VISIBLE);
            } else {
                layoutScroll.setVisibility(View.VISIBLE);
            }
        }
    }

    private void iniListDate(List<ClothesInfo> clothesList) {
        list = new ArrayList<>();
        for (ClothesInfo clothesDetailsBean
                : clothesList) {
            boolean isNew = true;//默认是新的商品编码
            for (Map<String, String> stringMap : list) {
                if (stringMap.get(KEYS[4]).equals(clothesDetailsBean.washCode)) {
                    stringMap.put(KEYS[1], String.valueOf(Integer.valueOf(stringMap.get(KEYS[1]))
                            + 1));
                    isNew = false;//发现已有商品，直接增加数量
                    break;
                }
            }
            if (isNew) {
                Map<String, String> m = new HashMap<>();
                m.put(KEYS[0], clothesDetailsBean.washName);
                m.put(KEYS[1], "1");
                m.put(KEYS[2], StringUtil.formatPriceByFen(clothesDetailsBean.memberPrice));
                m.put(KEYS[3], clothesDetailsBean.clothesId);
                m.put(KEYS[4], clothesDetailsBean.washCode);
                list.add(m);
            }
        }
    }

    /**
     * 初始化订单信息
     */
    private void initOrderData(ResultEnterpriseDetail enterpriseDetail) {

        txtOrderCode.setText(enterpriseDetail.enterpriseOrderInfo.outerCode);
        txtName.setText(enterpriseDetail.employeeInfo.employeeName);
        txtNameCode.setText(enterpriseDetail.employeeInfo.employeeCode);
        txtPhone.setText(enterpriseDetail.employeeInfo.employeePhone);

        iniListDate(enterpriseDetail.enterpriseOrderInfo.clothesDetails);
        for (Map<String, String> stringMap : list) {//增加前缀
            stringMap.put(KEYS[1], new StringBuffer().append(PREFIX_NUMBER).append(stringMap.get
                    (KEYS[1])).toString());
            stringMap.put(KEYS[2], new StringBuffer().append(PREFIX_PRICE).append(stringMap.get
                    (KEYS[2])).toString());
        }
        if (list.size() > 0) {
            lvClothes.setVisibility(View.VISIBLE);
        }
        lvClothes.setAdapter(new SimpleAdapter(this, list, R.layout.item_confirm_order, new
                String[]{KEYS[0], KEYS[1], KEYS[2]}, new int[]{R.id.txt_commodity_name, R.id
                .txt_commodity_number, R.id.txt_commodity_price}));

        txtCollectBag.setText(enterpriseDetail.enterpriseOrderInfo.collectBrcode);
        etBackup.setText(enterpriseDetail.enterpriseOrderInfo.remark);
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        switch (requestCode) {
            case 0:
                EventBus.getDefault().post(new ConfirmEvent(outerCode));
                if (!isFinishing())
                    finish();
                break;
            case 1:
                enterpriseDetail = (ResultEnterpriseDetail) result;
                if (enterpriseDetail != null)
                    initOrderData(enterpriseDetail);
                break;
        }
    }


    @OnClick({R.id.btn_deduction_cancel_scroll, R.id.btn_deduction_cancel_screen_bottom})
    public void cancel() {
        setResult(RESULT_CANCEL_CONFIRM);
        finish();
    }

    @OnClick({R.id.btn_deduction_affirm_scroll, R.id.btn_deduction_affirm_screen_bottom})
    public void affirm() {
        if (enterpriseDetail != null && !TextUtils.isEmpty(outerCode)) {
            EnterpriseApi.payWashEnterpriseOrder(outerCode, etBackup.getText().toString(),
                    getToken(), getNewHandler(0, ResultBase.class));
        }
    }

}

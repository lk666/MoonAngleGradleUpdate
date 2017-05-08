package cn.com.bluemoon.delivery.module.wash.enterprise.createorder;

import android.text.TextUtils;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.EnterpriseApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.wash.enterprise.ResultEnterpriseDetail;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.ui.NoScrollListView;

/**
 * 确认订单信息
 * Created by tangqiwei on 2017/5/5.
 */

public class EnterpriseConfirmOrderActivity extends BaseActivity {

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
    @Bind(R.id.txt_collect_remark)
    TextView txtCollectRemark;

    private List<Map<String, String>> list;
    private ResultEnterpriseDetail enterpriseDetail;
    private String outerCode;

    /**
     * 商品名称、商品数量、商品价格、衣物ID、商品编码
     */
    private String[] KEYS = new String[]{"name", "number", "price", "clothesId", "washCode"};

    @Override
    protected int getLayoutId() {
        return R.layout.activity_confirm_order;
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {
        outerCode = getIntent().getStringExtra("outerCode");
        EnterpriseApi.getWashEnterpriseDetail(outerCode, getToken(), getNewHandler(1, ResultEnterpriseDetail.class));
    }

    private void iniListDate(List<ResultEnterpriseDetail.EnterpriseOrderInfoBean.ClothesDetailsBean> clothesList) {
        list = new ArrayList<>();
        for (ResultEnterpriseDetail.EnterpriseOrderInfoBean.ClothesDetailsBean clothesDetailsBean : clothesList) {
            boolean isNew=true;//默认是新的商品编码
            for (Map<String, String> stringMap : list) {
                if (stringMap.get(KEYS[4]).equals(clothesDetailsBean.washCode)) {
                    stringMap.put(KEYS[1], String.valueOf(Integer.valueOf(stringMap.get(KEYS[1])) + 1));
                    isNew=false;//发现已有商品，直接增加数量
                    break;
                }
            }
            if(isNew){
                Map<String, String> m = new HashMap<>();
                m.put(KEYS[0],clothesDetailsBean.washName);
                m.put(KEYS[1],"1");
                m.put(KEYS[2],String.valueOf(clothesDetailsBean.memberPrice));
                m.put(KEYS[3],clothesDetailsBean.clothesId);
                m.put(KEYS[4],clothesDetailsBean.washCode);
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
        lvClothes.setAdapter(new SimpleAdapter(this,list,R.layout.item_confirm_order,new String[]{KEYS[0],KEYS[1],KEYS[2]},new int[]{R.id.txt_commodity_name,R.id.txt_commodity_number,R.id.txt_commodity_price}));

        txtCollectBag.setText(enterpriseDetail.enterpriseOrderInfo.collectBrcode);
        txtCollectRemark.setText(enterpriseDetail.enterpriseOrderInfo.remark);
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        switch (requestCode) {
            case 0:
                EventBus.getDefault().post(new Object());
                if(!isFinishing())
                finish();
                break;
            case 1:
                enterpriseDetail = (ResultEnterpriseDetail) result;
                if(enterpriseDetail!=null)
                initOrderData(enterpriseDetail);
                break;
        }
    }


    @OnClick(R.id.btn_deduction_cancel)
    public void cancel() {
        finish();
    }

    @OnClick(R.id.btn_deduction_affirm)
    public void affirm() {
        if(enterpriseDetail!=null&& !TextUtils.isEmpty(outerCode)){
            EnterpriseApi.payWashEnterpriseOrder(outerCode,getToken(),getNewHandler(0,ResultBase.class));
        }
    }

}

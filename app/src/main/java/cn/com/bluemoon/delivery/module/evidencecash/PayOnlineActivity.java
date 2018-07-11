package cn.com.bluemoon.delivery.module.evidencecash;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.EvidenceCashApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.evidencecash.ResultCombo;
import cn.com.bluemoon.delivery.app.api.model.evidencecash.ResultSaveCashInfo;
import cn.com.bluemoon.delivery.entity.IPayOnlineResult;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.delivery.utils.TextWatcherUtils;
import cn.com.bluemoon.delivery.utils.service.PayService;

/**
 * Created by ljl on 2016/11/16.
 */
public class PayOnlineActivity extends BaseActivity {
    @BindView(R.id.gridview_package)
    GridView gridviewPackage;
    @BindView(R.id.et_recharge_money)
    EditText etRechargeMoney;

    private PayService payService;
    private IPayOnlineResult payResult = new IPayOnlineResult() {
        @Override
        public void isSuccess(boolean isSuccess) {
            PayResultActivity.actStart(PayOnlineActivity.this, isSuccess);
            if (isSuccess) {
                finish();
            }
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_pay_online;
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.pay_online_title);
    }

    @Override
    public void initView() {
        payService = new PayService(this, payResult);
        registerReceiver(payService.getWXResutReceiver(), payService.getWXIntentFilter());
        TextWatcherUtils.setMaxNumberWatcher(etRechargeMoney, 20, 2, null);
    }

    @Override
    public void initData() {
        showWaitDialog();
        EvidenceCashApi.combo(getToken(), getNewHandler(1, ResultCombo.class));
    }

    @OnClick({R.id.layout_alipay,R.id.layout_wechat_pay})
    public void onClick(View view) {
        String rechargeMoney = etRechargeMoney.getText().toString();
        if (StringUtils.isNotBlank(rechargeMoney)) {
            long money = (long) (Double.valueOf(rechargeMoney) * 100);
            showWaitDialog();
            switch (view.getId()) {
                case R.id.layout_alipay :
                    EvidenceCashApi.saveCashInfo(money,"", getToken(),"Alipay", getNewHandler(2, ResultSaveCashInfo.class));
                    break;
                case R.id.layout_wechat_pay :
                    EvidenceCashApi.saveCashInfo(money,"", getToken(),"wxpay", getNewHandler(3, ResultSaveCashInfo.class));
                    break;
            }
        } else {
            toast(getString(R.string.please_input_package));
        }

    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        hideWaitDialog();
        if (requestCode == 1) {
            final ResultCombo resultCombo = (ResultCombo)result;
            if (resultCombo.getComboArray() != null && !resultCombo.getComboArray().isEmpty()) {
                PackageAdapter gridAdapter = new PackageAdapter(this);
                gridAdapter.setList(resultCombo.getComboArray());
                gridviewPackage.setAdapter(gridAdapter);
                gridviewPackage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        long money = resultCombo.getComboArray().get(position);
                        etRechargeMoney.setText(String.valueOf(money));
                        etRechargeMoney.setSelection(String.valueOf(money).length());
                    }
                });
            }
        } else if (requestCode == 2) {
            ResultSaveCashInfo resultSaveCashInfo = (ResultSaveCashInfo)result;
            payService.aliPay(resultSaveCashInfo.getPayInfo());
        } else if (requestCode == 3) {
            ResultSaveCashInfo resultSaveCashInfo = (ResultSaveCashInfo)result;
            payService.wxPay(resultSaveCashInfo.getPayInfoObj());
        }

    }

    class PackageAdapter extends BaseListAdapter<Long> {
        public PackageAdapter(Context context) {
            super(context, null);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.money_gridview_item;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            TextView txtMoneyAmount = getViewById(R.id.txt_money_amount);
            txtMoneyAmount.setText(getString(R.string.money_package, StringUtil.formatIntMoney(list.get(position))));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(payService.getWXResutReceiver());
    }
}

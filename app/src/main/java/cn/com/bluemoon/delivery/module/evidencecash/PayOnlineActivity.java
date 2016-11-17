package cn.com.bluemoon.delivery.module.evidencecash;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.EvidenceCashApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.evidencecash.ResultCombo;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;

/**
 * Created by ljl on 2016/11/16.
 */
public class PayOnlineActivity extends BaseActivity {
    @Bind(R.id.gridview_package)
    GridView gridviewPackage;
    @Bind(R.id.et_recharge_money)
    EditText etRechargeMoney;

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
        showWaitDialog();
        EvidenceCashApi.combo(getToken(), getNewHandler(1, ResultCombo.class));
    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.layout_alipay,R.id.layout_wechat_pay})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_alipay :
                break;
            case R.id.layout_wechat_pay :

                break;
        }
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        hideWaitDialog();
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
            txtMoneyAmount.setText(getString(R.string.money_package, list.get(position)));
        }

    }
}

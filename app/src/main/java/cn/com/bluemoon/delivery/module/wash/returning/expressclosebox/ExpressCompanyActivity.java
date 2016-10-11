package cn.com.bluemoon.delivery.module.wash.returning.expressclosebox;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.ReturningApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.app.api.model.wash.expressclosebox.ResultExpressCompany;

public class ExpressCompanyActivity extends BaseActivity {
    @Bind(R.id.list_company)
    ListView listCompany;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_delivery_company;
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.select_company);
    }

    @Override
    public void initView() {
        showWaitDialog();
        ReturningApi.queryExpressList(getToken(), getNewHandler(1, ResultExpressCompany.class));
    }

    @Override
    public void initData() {

    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        hideWaitDialog();
        ResultExpressCompany r = (ResultExpressCompany) result;
        List<ResultExpressCompany.CompanyListBean> list = r.getCompanyList();
        if (list != null && !list.isEmpty()) {
            CompanyAdapter adpter = new CompanyAdapter(ExpressCompanyActivity.this);
            String companyCode = getIntent().getStringExtra("companyCode");
            if (StringUtils.isNotBlank(companyCode)) {
                for (ResultExpressCompany.CompanyListBean bean : list) {
                    bean.setSelect(companyCode.equals(bean.getCompanyCode()));
                }
            }
            adpter.setList(list);
            listCompany.setAdapter(adpter);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    class CompanyAdapter extends BaseListAdapter<ResultExpressCompany.CompanyListBean> {

        public CompanyAdapter(Context context) {
            super(context, null);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.list_item_checkbox_text_base;
        }

        @Override
        protected void setView(final int position, View convertView, ViewGroup parent, boolean isNew) {
            final CheckBox cbSelect = getViewById(R.id.cb_select);
            TextView txtCouponName = getViewById(R.id.txt_coupon_name);
            LinearLayout layoutCoupon = getViewById(R.id.layout_coupon);
            final ResultExpressCompany.CompanyListBean bean = list.get(position);
            txtCouponName.setText(bean.getCompanyName());
            cbSelect.setChecked(bean.isSelect());
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i = 0; i < list.size(); i++) {
                        list.get(i).setSelect(i == position);
                    }
                    notifyDataSetChanged();
                    Intent intent = new Intent();
                    intent.putExtra("companyCode", list.get(position).getCompanyCode());
                    intent.putExtra("companyName", list.get(position).getCompanyName());
                    setResult(1,intent);
                    finish();
                }
            };
            cbSelect.setOnClickListener(listener);
            convertView.setOnClickListener(listener);
        }
    }
}

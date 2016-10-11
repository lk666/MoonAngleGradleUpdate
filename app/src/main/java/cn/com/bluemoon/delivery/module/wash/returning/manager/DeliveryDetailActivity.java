package cn.com.bluemoon.delivery.module.wash.returning.manager;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.Bind;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.ReturningApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.app.api.model.wash.manager.ResultBackOrder;
import cn.com.bluemoon.lib.utils.LibViewUtil;

/**
 * Created by ljl on 2016/9/22.
 */
public class DeliveryDetailActivity extends BaseActivity {
    @Bind(R.id.txt_code)
    TextView txtCode;
    @Bind(R.id.txt_count)
    TextView txtCount;
    @Bind(R.id.listView)
    ListView listView;
    private String expressCode;
    private String expressName;
    private int count;


    @Override
    protected String getTitleString() {
        return getString(R.string.manage_delivery_title);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_returning_delivery_detail;
    }

    @Override
    public void initView() {
        showWaitDialog();
        expressCode = getIntent().getStringExtra("expressCode");
        expressName = getIntent().getStringExtra("expressName");
        count = getIntent().getIntExtra("count", 0);
        ReturningApi.seeExpressDetail(expressCode, getToken(), getNewHandler(1, ResultBackOrder.class));
    }

    @Override
    public void initData() {

    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        hideWaitDialog();
        ResultBackOrder r = (ResultBackOrder) result;
        txtCode.setText(expressName + "-" + expressCode);
        txtCount.setText(getString(R.string.total_amount2, count));
        DeliveryListAdapter adapter = new DeliveryListAdapter(this, null);
        adapter.setList(r.getBackOrderList());
        listView.setAdapter(adapter);
        LibViewUtil.setListViewHeight2(listView);
    }


    class DeliveryListAdapter extends BaseListAdapter<ResultBackOrder.BackOrderListBean> {

        public DeliveryListAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.list_item_returning_delivery_detail;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            final ResultBackOrder.BackOrderListBean r = list.get(position);
            TextView txtReturnCode = getViewById(R.id.txt_return_code);
            TextView txtCustomerName = getViewById(R.id.txt_customer_name);
            TextView txtAddress = getViewById(R.id.txt_address);

            txtReturnCode.setText(getString(R.string.manage_return_clothes_code, r.getBackOrderCode()));
            txtCustomerName.setText(getString(R.string.manage_customer_name, r.getCustomerName()));
            txtAddress.setText(r.getProvince() + "  " + r.getCity() + "  "
                    + r.getVillage() + "  " + r.getStreet() + "  " + r.getAddress());

        }
    }
}

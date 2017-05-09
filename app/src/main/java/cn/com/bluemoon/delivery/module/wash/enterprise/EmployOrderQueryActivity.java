package cn.com.bluemoon.delivery.module.wash.enterprise;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.EnterpriseApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.wash.enterprise.BranchBean;
import cn.com.bluemoon.delivery.app.api.model.wash.enterprise.Employee;
import cn.com.bluemoon.delivery.app.api.model.wash.enterprise.EnterpriseOrderListBeanBase;
import cn.com.bluemoon.delivery.app.api.model.wash.enterprise.ResultGetWashEnterpriseQuery;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.module.wash.enterprise.createorder.AddClothesActivity;
import cn.com.bluemoon.delivery.module.wash.enterprise.createorder.CreateOrderActivity;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.ViewUtil;
import cn.com.bluemoon.lib.view.CommonEmptyView;
import cn.com.bluemoon.lib.view.CommonSearchView;

public class EmployOrderQueryActivity extends BaseActivity implements OnListItemClickListener {

    public static void startAct(Fragment fragment, int requestCode) {
        Intent intent = new Intent(fragment.getActivity(), EmployOrderQueryActivity.class);
        fragment.startActivityForResult(intent, requestCode);
    }

    @Bind(R.id.search_view)
    CommonSearchView searchView;
    @Bind(R.id.layout_init)
    LinearLayout layoutInit;
    @Bind(R.id.layout_title)
    LinearLayout layoutTitle;
    @Bind(R.id.txt_title)
    TextView txtTitle;
    @Bind(R.id.lv_query)
    ListView lvQuery;
    @Bind(R.id.layout_scan)
    LinearLayout layoutScan;
    private ItemAdapter adapter;
    private List<BranchBean> items = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_query_employ_order;
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.hand_query);
    }

    @Override
    public void initView() {
        adapter = new ItemAdapter(this, this);
        adapter.setList(items);
        lvQuery.setAdapter(adapter);
        searchView.setSearchViewListener(new CommonSearchView.SearchViewListener() {
            @Override
            public void onSearch(CommonSearchView view, String str) {
                getList();
            }

            @Override
            public void onCancel(CommonSearchView view) {

            }
        });
        ViewUtil.setViewVisibility(layoutInit, View.VISIBLE);
        ViewUtil.setViewVisibility(lvQuery, View.GONE);
        PublicUtil.setEmptyView(lvQuery, null, new CommonEmptyView.EmptyListener() {
            @Override
            public void onRefresh() {
                showWaitDialog();
                getList();
            }
        });
    }

    @Override
    public void initData() {
    }

    @OnClick({R.id.layout_scan})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_scan:
                setResult(1);
                finish();
                break;
        }
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        ResultGetWashEnterpriseQuery queryResult = (ResultGetWashEnterpriseQuery) result;
        ViewUtil.setViewVisibility(layoutInit, View.GONE);
        ViewUtil.setViewVisibility(lvQuery, View.VISIBLE);
        setData(queryResult);

    }


    private void getList() {
        EnterpriseApi.getWashEnterpriseQuery(searchView.getText(), getToken(), getNewHandler(0,
                ResultGetWashEnterpriseQuery.class));
    }

    private void setData(ResultGetWashEnterpriseQuery queryResult) {
        List<BranchBean> branchList = new ArrayList<>();
        if (queryResult.employeeList != null && queryResult.employeeList.size() > 0) {
            layoutTitle.setVisibility(View.VISIBLE);
            txtTitle.setText(R.string.txt_employee_info);
            for (Employee bean : queryResult.employeeList) {
                branchList.add(bean);
            }
        } else if (queryResult.enterpriseOrderList != null && queryResult.enterpriseOrderList
                .size() > 0) {
            layoutTitle.setVisibility(View.VISIBLE);
            txtTitle.setText(R.string.txt_order_info);
            for (EnterpriseOrderListBeanBase bean : queryResult.enterpriseOrderList) {
                branchList.add(bean);
            }
        }
        items.clear();
        if (branchList.size() > 0) {
            items.addAll(branchList);
        } else {
            layoutTitle.setVisibility(View.GONE);
        }
        adapter.notifyDataSetChanged();
    }

    class ItemAdapter extends BaseListAdapter<BranchBean> {


        public ItemAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.item_employee;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            Object branchBean = getItem(position);
            View view = getViewById(R.id.view);
            TextView txtPhone = getViewById(R.id.txt_phone);
            TextView txtName = getViewById(R.id.txt_name);
            TextView txtCode = getViewById(R.id.txt_code);
            TextView txtAddress = getViewById(R.id.txt_address);
            if (branchBean instanceof Employee) {
                view.setVisibility(View.GONE);
                txtPhone.setVisibility(View.VISIBLE);
                Employee bean = (Employee) branchBean;
                txtName.setText(bean.employeeName);
                txtCode.setText(bean.employeeCode);
                txtPhone.setText(bean.employeePhone);
                txtAddress.setText(bean.branchName);
            } else {
                EnterpriseOrderListBeanBase bean = (EnterpriseOrderListBeanBase) branchBean;
                view.setVisibility(View.VISIBLE);
                txtPhone.setVisibility(View.GONE);
                txtName.setText(bean.outerCode);
                txtCode.setText(bean.collectBrcode);
                txtAddress.setText(bean.branchName);
            }
            setClickEvent(true, position, convertView);
        }
    }

    @Override
    public void onItemClick(Object item, View view, int position) {
        if (item instanceof Employee) {
            Employee employee = (Employee) item;
            CreateOrderActivity.actionStart(this, employee);
        } else {
            EnterpriseOrderListBeanBase order = (EnterpriseOrderListBeanBase) item;
            // 跳到添加衣物
            AddClothesActivity.actionStart(this, order.outerCode);
        }
        finish();
    }
}

package cn.com.bluemoon.delivery.module.wash.enterprise;

import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
import cn.com.bluemoon.delivery.module.wash.enterprise.event.ConfirmEvent;
import cn.com.bluemoon.delivery.module.wash.enterprise.event.SaveOrderEvent;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.ViewUtil;
import cn.com.bluemoon.lib.view.CommonEmptyView;
import cn.com.bluemoon.lib.view.CommonSearchView;

public class EmployOrderQueryActivity extends BaseActivity implements OnListItemClickListener {

    public static void startAct(Context context) {
        Intent intent = new Intent(context, EmployOrderQueryActivity.class);
        context.startActivity(intent);
    }

    @Bind(R.id.search_view)
    CommonSearchView searchView;
    @Bind(R.id.image_search)
    ImageView imageSearch;
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
    private String searchKey;

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
        ViewUtil.setViewVisibility(imageSearch, View.VISIBLE);
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
                finish();
                break;
        }
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        ResultGetWashEnterpriseQuery queryResult = (ResultGetWashEnterpriseQuery) result;
        ViewUtil.setViewVisibility(imageSearch, View.GONE);
        ViewUtil.setViewVisibility(lvQuery, View.VISIBLE);
        setData(queryResult);

    }


    private void getList() {
        searchKey = searchView.getText();
        EnterpriseApi.getWashEnterpriseQuery(searchKey, getToken(), getNewHandler(0,
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
                setSpan(txtName, bean.employeeName);
                setSpan(txtCode, bean.employeeExtension);
                setSpan(txtPhone, bean.employeePhone);
                txtAddress.setText(bean.branchName);
            } else {
                EnterpriseOrderListBeanBase bean = (EnterpriseOrderListBeanBase) branchBean;
                view.setVisibility(View.VISIBLE);
                txtPhone.setVisibility(View.GONE);
                txtName.setText(bean.outerCode);
                setSpan(txtCode, bean.collectBrcode);
                txtAddress.setText(bean.branchName);
            }
            setClickEvent(true, position, convertView);
        }
    }

    private void setSpan(TextView tv, String str) {
        if (StringUtils.isNoneBlank(searchKey) && str.contains(searchKey)) {
            SpannableStringBuilder builder = new SpannableStringBuilder(str);
            ForegroundColorSpan redSpan = new ForegroundColorSpan(getResources().getColor(R.color.btn_red));
            int index = str.indexOf(searchKey);
            builder.setSpan(redSpan, index, index + searchKey.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tv.setText(builder);
        } else {
            tv.setText(str);
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
    }

    @Override
    protected boolean isUseEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ConfirmEvent event) {
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SaveOrderEvent event) {
        finish();
    }
}

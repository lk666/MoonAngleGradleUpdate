package cn.com.bluemoon.delivery.module.wash.enterprise;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.EnterpriseApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.wash.enterprise.ResultEnterpriseList;
import cn.com.bluemoon.delivery.app.api.model.wash.enterprise.ResultEnterpriseList.EnterpriseOrderListBean;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.BasePullToRefreshListViewFragment;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;

/**
 * 企业收衣界面
 * Created by ljl on 2017/4/28.
 */
public class EnterpriseFragment extends BasePullToRefreshListViewFragment {

    private static final int REQUEST_CODE_CANCEL = 0x777;
    private static final int REQUEST_CODE_CREATE_COLLECT = 0x77;
    private int index;
    /**
     * 分页标识
     */
    private int currentPage = 1;

    @Override
    protected String getTitleString() {
        return getString(R.string.tab_enterprise_txt);
    }

    @Override
    protected void initPullToRefreshListView(PullToRefreshListView ptrlv) {
        ptrlv.getRefreshableView().setDivider(null);
        ptrlv.getRefreshableView().setDividerHeight(0);
    }

    @Override
    protected PullToRefreshBase.Mode getMode() {
        return PullToRefreshBase.Mode.BOTH;
    }

    @Override
    protected void invokeGetDataDeliveryApi(int requestCode) {
        currentPage = 1;
        invokeGetMoreDeliveryApi(requestCode);
    }

    @Override
    protected List<EnterpriseOrderListBean> getGetDataList(
            ResultBase result) {
        return getGetMoreList(result);
    }

    @Override
    protected void invokeGetMoreDeliveryApi(int requestCode) {
        EnterpriseApi.getWashEnterpriseList(currentPage, getToken(),
                getNewHandler(requestCode, ResultEnterpriseList.class));
    }

    @Override
    protected List<EnterpriseOrderListBean> getGetMoreList(
            ResultBase result) {
        currentPage++;
        ResultEnterpriseList resultObj = (ResultEnterpriseList) result;
        return resultObj.getEnterpriseOrderList();
    }

    @Override
    protected ItemAdapter getNewAdapter() {
        return new ItemAdapter(getActivity(), this);
    }

    class ItemAdapter extends BaseListAdapter<EnterpriseOrderListBean> {


        public ItemAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.item_enterprise ;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            EnterpriseOrderListBean item = (EnterpriseOrderListBean) getItem(position);

            if (item == null) {
                return;
            }

            TextView tvNumber = getViewById(R.id.tv_number);
            TextView tvTime = getViewById(R.id.tv_time);
            TextView tvState = getViewById(R.id.tv_state);
            TextView tvEmployeeCode = getViewById(R.id.tv_employee_code);
            LinearLayout layoutRightAction = getViewById(R.id.layout_right_action);
            TextView tvCustomerName = getViewById(R.id.tv_customer_name);
            TextView tvAddress = getViewById(R.id.tv_address);
            TextView tvAmount = getViewById(R.id.tv_amount);
            TextView tvPrice = getViewById(R.id.tv_price);
            TextView tvAddClothes = getViewById(R.id.tv_add_clothes);
            TextView tvCancelOrder = getViewById(R.id.tv_cancel_order);
            tvNumber.setText(item.getOuterCode());
            tvTime.setText(DateUtil.getTime(item.getCreateTime(), "yyyy/MM/dd HH:mm:ss"));
            tvState.setText(item.getStateName());
            tvEmployeeCode.setText(item.getCollectBrcode());
            // 机构名称
            tvCustomerName.setText(item.getEmployeeName());
            tvAddress.setText(item.getBranchName());
            if (Constants.OUTER_ACCEPT_CLOTHES.equals(item.getState())) {
                tvAddClothes.setVisibility(View.VISIBLE);
            } else {
                tvAddClothes.setVisibility(View.GONE);
            }
            if (Constants.OUTER_CANCEL.equals(item.getState())) {
                tvCancelOrder.setVisibility(View.INVISIBLE);
            } else {
                tvCancelOrder.setVisibility(View.VISIBLE);
            }
            tvAmount.setText(getString(R.string.enterprise_order_amount, item.getActualCount()));
            tvPrice.setText(getString(R.string.order_money, PublicUtil.getPriceFrom(item.getPayTotal())));
            setClickEvent(isNew, position, layoutRightAction, tvCancelOrder);
        }
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        super.onSuccessResponse(requestCode, jsonString, result);
        if (requestCode == REQUEST_CODE_CANCEL) {
            toast(result.getResponseMsg());
            getList().remove(index);
            getAdapter().notifyDataSetChanged();
            if (getList().isEmpty()) {
                initData();
            }
        }
    }

    @Override
    public void onItemClick(Object obj, View view, int position) {
        EnterpriseOrderListBean bean = (EnterpriseOrderListBean)obj;
        index = position;
        switch (view.getId()) {
            case R.id.layout_right_action :
                EnterpriseOrderDetailActivity.startAct(getActivity(), bean.getOuterCode(), bean.getState());
                break;
            case R.id.tv_cancel_order :
                showWaitDialog();
                EnterpriseApi.cancelWashEnterpriseOrder(bean.getOuterCode(), getToken(), getNewHandler(REQUEST_CODE_CANCEL, ResultBase.class));
                break;
        }
    }


}
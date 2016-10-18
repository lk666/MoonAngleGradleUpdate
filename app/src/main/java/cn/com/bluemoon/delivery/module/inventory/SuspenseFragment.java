package cn.com.bluemoon.delivery.module.inventory;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.apache.commons.lang3.text.StrBuilder;

import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.inventory.OrderVo;
import cn.com.bluemoon.delivery.app.api.model.inventory.ResultCustormerService;
import cn.com.bluemoon.delivery.app.api.model.inventory.ResultOrderVo;
import cn.com.bluemoon.delivery.module.base.BaseFragment;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.BasePullToRefreshListViewFragment;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.delivery.utils.ViewHolder;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;

/**
 * 待收发货
 */
public class SuspenseFragment extends BasePullToRefreshListViewFragment {

    private static final int REQUEST_CODE_QUERY_OPERATOR_PERSONS = 0x1;

    private String type;
    private TextView txtCount;
    private TextView txtPrice;
    private TextView txtTotalBoxes;
    private String customerNum;
    private String customerName;

    private int orderTotalNum;
    private long orderTotalMoney;
    private double orderTotalCase;

    @Override
    protected void onBeforeCreateView() {
        Bundle bundle = getArguments();
        type = (String) bundle.getSerializable(BaseFragment.EXTRA_BUNDLE_DATA);

        DeliveryApi.queryOperatorPersons(getToken(),
                getNewHandler(REQUEST_CODE_QUERY_OPERATOR_PERSONS, ResultCustormerService.class));
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase resultBase) {
        super.onSuccessResponse(requestCode, jsonString, resultBase);
        switch (requestCode) {
            case REQUEST_CODE_QUERY_OPERATOR_PERSONS:
                ResultCustormerService custormerResult = (ResultCustormerService) resultBase;
                customerName = custormerResult.getCustormerServiceName();
                customerNum = custormerResult.getCustormerServicePhone();
                break;
            default:
                break;
        }
    }

    @Override
    public void onSuccessException(int requestCode, Throwable t) {
        super.onSuccessException(requestCode, t);
    }

    @Override
    public void onErrorResponse(int requestCode, ResultBase result) {
        super.onErrorResponse(requestCode, result);
    }

    @Override
    public void onFailureResponse(int requestCode, Throwable t) {
        super.onFailureResponse(requestCode, t);
    }

    @Override
    protected void initPullToRefreshListView(PullToRefreshListView ptrlv) {
        ptrlv.getRefreshableView().setDivider(null);
        ptrlv.getRefreshableView().setDividerHeight(0);
    }

    @Override
    protected int getHeadLayoutId() {
        return R.layout.head_fragment_tab_inventory;
    }

    @Override
    protected void initHeadViewEvent(View headView) {
        super.initHeadViewEvent(headView);

        txtCount = (TextView) headView.findViewById(R.id.txt_count);
        txtPrice = (TextView) headView.findViewById(R.id.txt_price);
        txtTotalBoxes = (TextView) headView.findViewById(R.id.txt_total_boxes);

        setCountAndPrice(0, null, 0);

    }

    private void setCountAndPrice(int size, String price, double boxes) {
        String count;
        if (size > 99) {
            count = "99+";
        } else {
            count = String.valueOf(size);
        }
        count = String.format(getString(R.string.order_history_totalcount), count);
        if (StringUtil.isEmpty(price) || "0".equals(price)
                || "0.0".equals(price)) {
            price = "0.00";
        }
        price = String.format(getString(R.string.order_history_price), price);
        txtCount.setText(count);
        txtPrice.setText(price);
        txtTotalBoxes.setText(String.format(getString(R.string.order_boxes_num), StringUtil
                .formatBoxesNum(boxes)));
    }

    @Override
    protected String getTitleString() {
        String title = getString(R.string.tab_title_delivery_text);
        if (type.equals(InventoryTabActivity.RECEIVE_MANAGEMENT)) {
            title = getString(R.string.tab_title_receive_text);
        }
        return title;
    }

    @Override
    protected void setActionBar(CommonActionBar actionBar) {
        super.setActionBar(actionBar);
        actionBar.getImgRightView().setImageResource(R.mipmap.ico_customer_service);
        actionBar.getImgRightView().setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActionBarBtnRightClick() {
        super.onActionBarBtnRightClick();
        if (StringUtil.isEmpty(customerName) || StringUtil.isEmpty(customerNum)) {
            PublicUtil.showMessage(getActivity(), getString(R.string.dialog_customer_error));
        } else {
            View view = getActivity().getLayoutInflater().inflate(R.layout
                    .dialog_content_call_phone, null);
            TextView txtCustomer = (TextView) view.findViewById(R.id
                    .txt_customer_service);
            TextView txtPhone = (TextView) view.findViewById(R.id.txt_phone_num);
            txtCustomer.setText(String.format(getString(R.string
                    .dialog_customer_name), customerName));
            txtPhone.setText(customerNum);
            PublicUtil.showCallPhoneDialog(getActivity(), view, customerNum);
        }
    }

    @Override
    protected void showEmptyView() {
        super.showEmptyView();
        // 可在此处设置head等
        setHeadViewVisibility(View.VISIBLE);
        getBaseTabActivity().setAmount(0, 0);
    }

    @Override
    protected void showNetErrorView() {
        super.showNetErrorView();
        // 可在此处设置head等
        setHeadViewVisibility(View.GONE);
    }

    @Override
    protected void showRefreshView() {
        super.showRefreshView();
        // 列表数据刷新，如可在此处设置head等
        setHeadViewVisibility(View.VISIBLE);

        getBaseTabActivity().setAmount(0, getList().size());
        setCountAndPrice(orderTotalNum, StringUtil.formatPriceByFen(orderTotalMoney),
                orderTotalCase);
    }

    @Override
    protected SuspenseAdapter getNewAdapter() {
        return new SuspenseAdapter(getActivity(), this);
    }

    /**
     * Mode不包含上拉加载时，可这样重写此方法
     *
     * @param result 继承ResultBase的json字符串数据，不为null，也非空数据
     */
    @Override
    protected List<OrderVo> getGetMoreList(ResultBase result) {
        return null;
    }

    @Override
    protected List<OrderVo> getGetDataList(ResultBase result) {
        ResultOrderVo resultObj = (ResultOrderVo) result;
        orderTotalNum = resultObj.getOrderTotalNum();
        orderTotalMoney = resultObj.getOrderTotalMoney();
        orderTotalCase = resultObj.getOrderTotalCase();
        return resultObj.getOrderList();
    }

    @Override
    protected PullToRefreshBase.Mode getMode() {
        return PullToRefreshBase.Mode.PULL_FROM_START;
    }

    @Override
    protected void invokeGetDataDeliveryApi(int requestCode) {
        if (type.equals(InventoryTabActivity.RECEIVE_MANAGEMENT)) {
            DeliveryApi.getWaitReceiptOrders(getToken(), getNewHandler(requestCode,
                    ResultOrderVo.class));
        } else {
            DeliveryApi.getWaitOutOrders(getToken(), getNewHandler(requestCode,
                    ResultOrderVo.class));
        }
    }

    /**
     * Mode不包含上拉加载时，可这样重写此方法
     */
    @Override
    protected void invokeGetMoreDeliveryApi(int requestCode) {
    }

    class SuspenseAdapter extends BaseListAdapter<OrderVo> {

        public SuspenseAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.order_suspense_item;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            OrderVo order = (OrderVo) getItem(position);
            if (order == null) {
                return;
            }

            TextView txtOrderId = ViewHolder.get(convertView, R.id.txt_order_id);
            TextView txtDate = ViewHolder.get(convertView, R.id.txt_date);
            TextView txtPrice = ViewHolder.get(convertView, R.id.txt_price);
            TextView txtBoxesCount = ViewHolder.get(convertView, R.id.txt_boxes_count);
            Button btnSuspense = ViewHolder.get(convertView, R.id.btn_suspense);

            txtOrderId.setText(order.getOrderCode());
            txtPrice.setText(new StrBuilder(getString(R.string.order_money_sign))
                    .append(StringUtil.formatPriceByFen(order.getTotalAmountRmb())).toString());
            txtDate.setText(DateUtil.getTime(order.getOrderDate(), "yyyy-MM-dd"));

            txtBoxesCount.setText(
                    new StrBuilder(String.format(getString(R.string.order_boxes_count),
                            StringUtil.formatBoxesNum(order.getTotalCase())))
                            .append(String.format(getString(R.string.order_product_count),
                                    order.getTotalNum())).toString()
            );
            int index = position % 2;
            if (index == 1) {
                convertView.setBackgroundResource(R.drawable.list_item_grep_bg);
            } else {
                convertView
                        .setBackgroundResource(R.drawable.list_item_white_bg);
            }
            if (type.equals(InventoryTabActivity.RECEIVE_MANAGEMENT)) {
                btnSuspense.setText(R.string.btn_text_receive);

            } else {
                btnSuspense.setText(R.string.btn_text_delivery);
            }

            setClickEvent(isNew, position, convertView);
            setClickEvent(isNew, position, btnSuspense);
        }

    }

    @Override
    public void onItemClick(Object item, View view, int position) {
        OrderVo order = (OrderVo) item;
        if (null != order) {
            if (type.equals(InventoryTabActivity.RECEIVE_MANAGEMENT)) {
                OrderReceiveDetailActivity.actionStart(SuspenseFragment.this, order.getOrderCode());
            } else {
                OrderDeliverDetailActivity.actionStart(SuspenseFragment.this, order.getOrderCode());
            }
        }
    }
}

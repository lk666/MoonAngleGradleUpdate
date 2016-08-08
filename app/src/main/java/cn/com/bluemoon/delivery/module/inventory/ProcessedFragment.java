package cn.com.bluemoon.delivery.module.inventory;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.apache.commons.lang3.text.StrBuilder;

import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.inventory.OrderVo;
import cn.com.bluemoon.delivery.app.api.model.inventory.ResultOrderVo;
import cn.com.bluemoon.delivery.module.base.BaseFragment;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.BasePullToRefreshListViewFragment;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.module.order.TimerFilterWindow;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib.utils.LibDateUtil;

/**
 * 已收发货
 */
public class ProcessedFragment extends BasePullToRefreshListViewFragment {

    private String type;
    private TextView txtCount;
    private TextView txtPrice;
    private TextView txtTotalBoxes;
    View popStart;

    private int orderTotalNum;
    private long orderTotalMoney;
    private double orderTotalCase;

    private long startTime = 0;
    private long endTime = 0;

    @Override
    protected void onBeforeCreateView() {
        Bundle bundle = getArguments();
        type = (String) bundle.getSerializable(BaseFragment.EXTRA_BUNDLE_DATA);
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

        popStart = (View) headView.findViewById(R.id.view_pop_start);
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
        txtTotalBoxes.setText(String.format(getString(R.string.order_boxes_num),
                StringUtil.formatBoxesNum(boxes)));
    }

    @Override
    protected String getTitleString() {
        String title = getString(R.string.tab_title_delivered_text);
        if (type.equals(InventoryTabActivity.RECEIVE_MANAGEMENT)) {
            title = getString(R.string.tab_title_received_text);
        }
        return title;
    }


    @Override
    protected void setActionBar(CommonActionBar actionBar) {
        super.setActionBar(actionBar);

        actionBar.getTvRightView().setText(R.string.btn_txt_fillter);
        actionBar.getTvRightView().setCompoundDrawablePadding(10);

        Drawable drawableFillter = getResources().getDrawable(R.mipmap.icon_filter);
        drawableFillter.setBounds(0, 0, drawableFillter.getMinimumWidth(), drawableFillter
                .getMinimumHeight());
        actionBar.getTvRightView().setCompoundDrawables(drawableFillter, null, null, null);
        actionBar.getTvRightView().setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActionBarBtnRightClick() {
        TimerFilterWindow popupWindow = new TimerFilterWindow(getActivity(), new
                TimerFilterWindow.TimerFilterListener() {
                    @Override
                    public void callBack(long startDate, long endDate) {
                        if (startDate >= 0 && endDate >= startDate) {
                            startTime = LibDateUtil.getTimeByCustTime(startDate);
                            endTime = LibDateUtil.getTimeByCustTime(endDate);

                            if (DateUtil.getTimeOffsetMonth(startTime, 6) >
                                    endTime) {
                                if (startTime == 0 && endTime == 0) {
                                    return;
                                }

                                setCountAndPrice(0, null, 0);
                                showWaitDialog();
                                getData();
                            } else {
                                PublicUtil.showMessage(getActivity(), getString(R.string
                                        .txt_order_fillter_date_error));
                            }

                        }
                    }
                });
        popupWindow.showPopwindow(popStart);
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
            DeliveryApi.getReceiptOrders(getToken(), startTime / 1000, endTime / 1000,
                    getNewHandler(requestCode,
                    ResultOrderVo.class));
        } else {
            DeliveryApi.getOutOrders(getToken(), startTime / 1000, endTime / 1000, getNewHandler
                    (requestCode,
                    ResultOrderVo.class));
        }
    }

    /**
     * Mode不包含上拉加载时，可这样重写此方法
     */
    @Override
    protected void invokeGetMoreDeliveryApi(int requestCode) {
    }

    @SuppressLint("InflateParams")
    class SuspenseAdapter extends BaseListAdapter<OrderVo> {

        public SuspenseAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.order_processed_item;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            OrderVo order = (OrderVo) getItem(position);

            TextView txtOrderId = getViewById(R.id.txt_order_id);
            TextView txtDate = getViewById(R.id.txt_date);
            TextView txtPrice = getViewById(R.id.txt_price);
            TextView txtBoxesCount = getViewById(R.id.txt_boxes_count);

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

            setClickEvent(isNew, position, convertView);
        }
    }

    @Override
    public void onItemClick(Object item, View view, int position) {
        OrderVo order = (OrderVo) item;
        if (null != order) {
            if (type.equals(InventoryTabActivity.RECEIVE_MANAGEMENT)) {
                OrderEndReceiveDetailActivity.actionStart(getActivity(),
                        InventoryTabActivity.RECEIVE_MANAGEMENT, order.getOrderCode());
            } else {
                OrderEndDeliverDetailActivity.actionStart(getActivity(),
                        InventoryTabActivity.DELIVERY_MANAGEMENT, order.getOrderCode());
            }
        }
    }
}

package cn.com.bluemoon.delivery.module.order;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.kymjs.kjframe.utils.StringUtils;

import java.util.List;

import cn.com.bluemoon.delivery.AppContext;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.HistoryOrderType;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.other.Order;
import cn.com.bluemoon.delivery.app.api.model.other.ResultOrder;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.BasePullToRefreshListViewFragment;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.module.extract.HistoryOrderDetailActivity;
import cn.com.bluemoon.delivery.module.order.TimerFilterWindow.TimerFilterListener;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase.Mode;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib.utils.LibDateUtil;

public class HistoryFragment extends BasePullToRefreshListViewFragment implements
        OnListItemClickListener {
    private LinearLayout layoutDate;
    private TextView txtCount;
    private TextView txtPrice;
    public static HistoryOrderType ordertype;
    private View popStart;
    private long startTime = 0;
    private long endTime = 0;
    private long timestamp = 0;


    @Override
    protected String getTitleString() {
        return getString(R.string.tab_history);
    }

    @Override
    protected int getHeadLayoutId() {
        return R.layout.head_order_history;
    }

    @Override
    protected void initHeadViewEvent(View headView) {
        super.initHeadViewEvent(headView);
        popStart = headView.findViewById(R.id.view_pop_start);
        layoutDate = (LinearLayout) headView.findViewById(R.id.layout_date);
        txtCount = (TextView) headView.findViewById(R.id.txt_count);
        txtPrice = (TextView) headView.findViewById(R.id.txt_price);
        setCountAndPrice(0, null);
        layoutDate.setOnClickListener(onClicker);
    }

    @Override
    protected void initPullToRefreshListView(PullToRefreshListView ptrlv) {
        super.initPullToRefreshListView(ptrlv);
        ptrlv.getRefreshableView().setDivider(null);
        ptrlv.getRefreshableView().setDividerHeight(0);
    }

    @Override
    protected void showRefreshView() {
        super.showRefreshView();
        setHeadViewVisibility(View.VISIBLE);
    }

    @Override
    protected void showNetErrorView() {
        super.showNetErrorView();
        setHeadViewVisibility(View.VISIBLE);
    }

    @Override
    protected void showEmptyView() {
        super.showEmptyView();
        setHeadViewVisibility(View.VISIBLE);
    }

    OnClickListener onClicker = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v == layoutDate) {
                TimerFilterWindow popupWindow = new TimerFilterWindow(getActivity(), new
                        TimerFilterListener() {
                            @Override
                            public void callBack(long startDate, long endDate) {
                                startTime = startDate;
                                endTime = endDate;
                                initData();
                            }
                        });
                popupWindow.showPopwindow(popStart);
            }
        }
    };

    @Override
    protected BaseListAdapter getNewAdapter() {
        return new HistoryAdapter(this.getActivity(), this);
    }

    @Override
    protected List getGetMoreList(ResultBase result) {
        return getGetDataList(result);
    }

    @Override
    protected List getGetDataList(ResultBase result) {
        ResultOrder item  = (ResultOrder)result;
        timestamp = item.getTimestamp();
        setCountAndPrice(item.getTotalCount(),item.getTotalAmount());
        return item.getItemList();
    }

    @Override
    protected Mode getMode() {
        return Mode.BOTH;
    }

    @Override
    protected void invokeGetDataDeliveryApi(int requestCode) {
        DeliveryApi.getHistoryOrders(getToken(), ordertype.toString(),
                AppContext.PAGE_SIZE, 0, startTime, endTime, getNewHandler(requestCode,ResultOrder.class));
    }

    @Override
    protected void invokeGetMoreDeliveryApi(int requestCode) {
        DeliveryApi.getHistoryOrders(getToken(), ordertype.toString(),
                AppContext.PAGE_SIZE, timestamp, startTime, endTime, getNewHandler(requestCode,ResultOrder.class));

    }

    @Override
    public void onItemClick(Object item, View view, int position) {
        Order order = (Order) item;
        if (ordertype.equals(HistoryOrderType.dispatch)) {
            PublicUtil.showOrderDetailView(getActivity(), order.getOrderId());
        } else if (ordertype.equals(HistoryOrderType.pickup)) {
            Intent intent = new Intent();
            intent.setClass(getActivity(), HistoryOrderDetailActivity.class);
            intent.putExtra("orderId", order.getOrderId());
            startActivity(intent);
        }
    }

    @SuppressLint("InflateParams")
    class HistoryAdapter extends BaseListAdapter<Order> {

        public HistoryAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.order_history_item;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            final Order order = list.get(position);
            TextView txtOrderid = getViewById(R.id.txt_orderid);
            TextView txtAddress = getViewById(R.id.txt_address);
            TextView txtPrice = getViewById(R.id.txt_price);
            TextView txtSignDate = getViewById(R.id.txt_sign_date);
            txtOrderid.setText(order.getOrderId());
            txtPrice.setText(String.format("%s%s", getString(R.string.order_money_sign), order
                    .getTotalPrice()));
            String date = DateUtil.getTimeStringByCustTime(order.getSignTime(), "yyyy-MM-dd " +
                    "HH:mm:ss");
            txtSignDate.setText(getString(R.string.history_order_receipt_date, date));
            if (HistoryFragment.ordertype.equals(HistoryOrderType.dispatch)) {
                txtAddress.setText(order.getAddress());
            } else if (HistoryFragment.ordertype
                    .equals(HistoryOrderType.pickup)) {
                txtSignDate.setVisibility(View.GONE);
                txtAddress.setText(getString(R.string.history_order_scene_receipt_date,
                        LibDateUtil.getTimeStringByCustTime(order.getSignTime(), "yyyy-MM-dd " +
                                "HH:mm:ss")));
            }

            int index = position % 2;
            if (index == 1) {
                convertView.setBackgroundResource(R.drawable.list_item_grep_bg);
            } else {
                convertView.setBackgroundResource(R.drawable.list_item_white_bg);
            }
            setClickEvent(isNew, position, convertView);
        }

    }

    private void setCountAndPrice(int size, String price) {
        String count;
        if (size > 99) {
            count = "99+";
        } else {
            count = String.valueOf(size);
        }
        count = String.format(getString(R.string.order_history_totalcount), count);
        if (StringUtils.isEmpty(price) || "0".equals(price)
                || "0.0".equals(price)) {
            price = "0.00";
        } else if (price.length() > 9) {
            price = "999999.99+";
        }
        price = String.format(getString(R.string.order_history_price), price);
        txtCount.setText(count);
        txtPrice.setText(price);
    }

}

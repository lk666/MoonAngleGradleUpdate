package cn.com.bluemoon.delivery.module.order;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.OrderVo;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.ResultOrderVo;
import cn.com.bluemoon.delivery.entity.OrderType;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.BasePullToRefreshListViewFragment;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib.view.CommonAlertDialog;

public class PendingOrdersFragment extends BasePullToRefreshListViewFragment {
    private long pageFlag;
    private int clickIndex;
    @Override
    protected String getTitleString() {
        return getString(R.string.tab_orders);
    }


    @Override
    protected BaseListAdapter getNewAdapter() {
        return new OrdersAdapter(getActivity());
    }

    @Override
    protected List getGetMoreList(ResultBase result) {
        ResultOrderVo r = (ResultOrderVo) result;
        pageFlag = r.getTimestamp();
        return r.getItemList();
    }

    @Override
    protected List getGetDataList(ResultBase result) {
        ResultOrderVo r = (ResultOrderVo) result;
        pageFlag = r.getTimestamp();
        return r.getItemList();
    }

    @Override
    protected PullToRefreshBase.Mode getMode() {
        return PullToRefreshBase.Mode.BOTH;
    }

    @Override
    protected void initPullToRefreshListView(PullToRefreshListView ptrlv) {
        ptrlv.getRefreshableView().setDivider(null);
        ptrlv.getRefreshableView().setDividerHeight(0);
        ptrlv.getRefreshableView().setCacheColorHint(Color.TRANSPARENT);
    }

    @Override
    protected void invokeGetDataDeliveryApi(int requestCode) {
        setAmount2();
        pageFlag = 0;
        DeliveryApi.getOrdersByTypeByPager(getToken(), pageFlag, OrderType.PENDINGORDERS, getNewHandler(requestCode, ResultOrderVo.class));
    }

    @Override
    protected void invokeGetMoreDeliveryApi(int requestCode) {
        DeliveryApi.getOrdersByTypeByPager(getToken(), pageFlag, OrderType.PENDINGORDERS,
                getNewHandler(requestCode, ResultOrderVo.class));
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        super.onSuccessResponse(requestCode, jsonString, result);
        if (requestCode ==1 || requestCode == 2) {
            hideWaitDialog();
            getList().remove(clickIndex);
            getAdapter().notifyDataSetChanged();
            toast(result.getResponseMsg());
            setAmount2();
            if (getList().isEmpty()) {
                initData();
            }
        }
    }

    @Override
    public void onErrorResponse(int requestCode, ResultBase result) {
        if (requestCode == 2) {
            if (result.getResponseCode() == 4102) {
                new CommonAlertDialog.Builder(getActivity())
                        .setMessage(result.getResponseMsg())
                        .setPositiveButton(R.string.ok,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        setAmount2();
                                        initData();
                                    }
                                }).show();
            } else {
                PublicUtil.showErrorMsg(getActivity(), result);
            }
        }
    }


    @Override
    public void onItemClick(Object item, View view, int position) {

    }


    public class OrdersAdapter extends BaseListAdapter<OrderVo> {

        public OrdersAdapter(Context context) {
            super(context, null);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.order_list_item;
        }

        @Override
        protected void setView(final int position, View convertView, ViewGroup parent, boolean isNew) {
            TextView txtDispatchId = getViewById(R.id.txt_dispatchId);
            LinearLayout layoutDetail = getViewById(R.id.layout_detail);
            TextView txtPaytime = getViewById(R.id.txt_paytime);
            TextView txtAddress = getViewById(R.id.txt_address);
            Button receivingOrdersAction = getViewById(R.id.receiving_orders_action);
            TextView txtCateAmount = getViewById(R.id.txt_cateAmount);
            TextView txtTotalAmount = getViewById(R.id.txt_totalAmount);
            TextView txtTotalPrice = getViewById(R.id.txt_totalPrice);
            TextView txtRefuseOrder = getViewById(R.id.txt_refuse_order);
            final OrderVo order = list.get(position);

            txtCateAmount.setText(getString(R.string.pending_order_total_kinds, order.getCateAmount()));
            txtTotalAmount.setText(getString(R.string.pending_order_total_amount, order.getTotalAmount()));
            txtTotalPrice.setText(getString(R.string.pending_order_total_price, StringUtil.formatPrice(order.getTotalPrice())));
            txtPaytime.setText(String.format(getString(R.string.pending_order_pay_time), order.getPayOrderTime()));
            txtDispatchId.setText(order.getDispatchId());
            txtAddress.setText(order.getAddress());


            txtRefuseOrder.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
            txtRefuseOrder.getPaint().setAntiAlias(true);

            txtRefuseOrder.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    CommonAlertDialog.Builder dialog = new CommonAlertDialog.Builder(getActivity());
                    dialog.setMessage(getString(R.string.pending_order_refuse_alert));
                    dialog.setPositiveButton(R.string.cancel_with_space, null);
                    dialog.setNegativeButton(R.string.confirm_with_space, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            showWaitDialog();
                            clickIndex = position;
                            DeliveryApi.rejectOrder(getToken(), order.getOrderId(), order.getOrderSource(), getNewHandler(1, ResultBase.class));
                        }
                    });
                    dialog.show();
                }
            });


            layoutDetail.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    PublicUtil.showOrderDetailView(getActivity(), order.getOrderId());
                }
            });
            receivingOrdersAction.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickIndex = position;
                    showWaitDialog();
                    DeliveryApi.acceptOrder(getToken(), order.getOrderId(), getNewHandler(2, ResultBase.class));
                }
            });

        }

    }


}

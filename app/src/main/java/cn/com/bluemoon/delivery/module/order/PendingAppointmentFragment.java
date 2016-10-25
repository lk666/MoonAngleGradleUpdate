package cn.com.bluemoon.delivery.module.order;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;

import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.OrderVo;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.ResultOrderVo;
import cn.com.bluemoon.delivery.app.api.model.Storehouse;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.entity.OrderType;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.BasePullToRefreshListViewFragment;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib.view.CommonAlertDialog;

public class PendingAppointmentFragment extends BasePullToRefreshListViewFragment {
    private long pageFlag;
    private int clickIndex;
    private View viewPopStart;
    private Activity mContext;

    @Override
    protected void onBeforeCreateView() {
        super.onBeforeCreateView();
        mContext = getActivity();
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.tab_appointment);
    }

    @Override
    protected BaseListAdapter getNewAdapter() {
        return new OrdersAdapter(mContext);
    }

    @Override
    protected List getGetMoreList(ResultBase result) {
        ResultOrderVo r = (ResultOrderVo) result;
        pageFlag = r.getPageFlag();
        return r.getItemList();
    }

    @Override
    protected List getGetDataList(ResultBase result) {
        ResultOrderVo r = (ResultOrderVo) result;
        pageFlag = r.getPageFlag();
        return r.getItemList();
    }

    @Override
    protected PullToRefreshBase.Mode getMode() {
        return PullToRefreshBase.Mode.PULL_FROM_START;
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
        DeliveryApi.getOrdersByType(getToken(), pageFlag, OrderType.PENDINGAPPOINTMENT, getNewHandler(requestCode, ResultOrderVo.class));
    }

    @Override
    protected void invokeGetMoreDeliveryApi(int requestCode) {
        DeliveryApi.getOrdersByType(getToken(), pageFlag, OrderType.PENDINGAPPOINTMENT,
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
    public void onItemClick(Object item, View view, int position) {

    }

    @Override
    protected int getHeadLayoutId() {
        return R.layout.head_fragment_tab_appointment;
    }

    @Override
    protected void initHeadViewEvent(View headView) {
        super.initHeadViewEvent(headView);
        viewPopStart = headView.findViewById(R.id.view_pop_start);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            Storehouse storehouse = (Storehouse) data.getSerializableExtra("storehouse");
            if (storehouse != null) {
                OrderVo orderVo = (OrderVo)getList().get(clickIndex);
                orderVo.setStorechargeCode(storehouse.getStorechargeCode());
                orderVo.setStorechargeName(storehouse.getStorechargeName());
                orderVo.setStorehouseCode(storehouse.getStorehouseCode());
                orderVo.setStorehouseName(storehouse.getStorehouseName());
                getAdapter().notifyDataSetChanged();
            }
        }
    }

    public class OrdersAdapter extends BaseListAdapter<OrderVo> {
        public OrdersAdapter(Context context) {
            super(context, null);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.order_list_item2;
        }

        @Override
        protected void setView(final int position, View convertView, ViewGroup parent, boolean isNew) {
            TextView txtDispatchId = getViewById(R.id.txt_dispatchId);
            final LinearLayout layoutDetail = getViewById(R.id.layout_detail);
            TextView txtStorehouse = getViewById(R.id.txt_storehouse);
            final LinearLayout layoutStorehouse = getViewById(R.id.layout_storehouse);
            TextView txtPaytime = getViewById(R.id.txt_paytime);
            TextView txtCustomerName = getViewById(R.id.txt_customerName);
            final TextView txtMobilePhone = getViewById(R.id.txt_mobilePhone);
            TextView txtAddress = getViewById(R.id.txt_address);
            final Button appointmentAction = getViewById(R.id.appointment_action);
            TextView txtCateAmount = getViewById(R.id.txt_cateAmount);
            TextView txtTotalAmount = getViewById(R.id.txt_totalAmount);
            TextView txtTotalPrice = getViewById(R.id.txt_totalPrice);
            final TextView txtCancleOrder = getViewById(R.id.txt_cancle_order);
            final OrderVo order = list.get(position);

            txtCancleOrder.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
            txtCancleOrder.getPaint().setAntiAlias(true);
            txtCustomerName.setText(OrdersUtils.formatLongString(
                    order.getCustomerName(), txtCustomerName));
            txtMobilePhone.setText(order.getMobilePhone());
            txtMobilePhone.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
            txtMobilePhone.getPaint().setAntiAlias(true);
            OnClickListener listener = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickIndex = position;
                    if (v == txtMobilePhone) {
                        PublicUtil.showCallPhoneDialog(mContext, order.getMobilePhone());
                    } else if (v == appointmentAction) {
                        ChoiceOrderDatePopupWindow popupWindow = new ChoiceOrderDatePopupWindow(mContext, order,
                                orderChoiceDateListener);
                        popupWindow.showAsDropDown(viewPopStart);
                    } else if (v == layoutStorehouse) {
                        Intent intent = new Intent();
                        intent.setClass(mContext,SelectStoreHouseActivity.class);
                        intent.putExtra("dispatchId", order.getDispatchId());
                        intent.putExtra("code", order.getStorehouseCode());
                        PendingAppointmentFragment.this.startActivityForResult(intent, 0);
                    } else if (v == layoutDetail) {
                        PublicUtil.showOrderDetailView(mContext, order.getOrderId());
                    } else if (v == txtCancleOrder) {
                        new CommonAlertDialog.Builder(mContext)
                                .setMessage(R.string.pending_order_get_or_not)
                                .setNegativeButton(R.string.yes,
                                        new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog,
                                                                int which) {
                                                showWaitDialog();
                                                DeliveryApi.cancelAppointmentOrder(getToken(),order.getOrderId(), getNewHandler(1, ResultBase.class));
                                            }
                                        }).setPositiveButton(R.string.no, null)
                                .show();
                    }

                }
            };
            txtCancleOrder.setOnClickListener(listener);
            txtMobilePhone.setOnClickListener(listener);
            appointmentAction.setOnClickListener(listener);
            txtPaytime.setText(getString(R.string.pending_order_pay_time, order.getPayOrderTime()));
            txtStorehouse.setText(OrdersUtils.getStorehouseString(order, mContext));
            layoutStorehouse.setOnClickListener(listener);

            txtDispatchId.setText(order.getOrderId());
            txtAddress.setText(order.getAddress());
            txtCateAmount.setText(getString(R.string.pending_order_total_kinds, order.getCateAmount()));
            txtTotalAmount.setText(getString(R.string.pending_order_total_amount, order.getTotalAmount()));
            txtTotalPrice.setText(getString(R.string.pending_order_total_price, StringUtil.formatPrice(order.getTotalPrice())));
            layoutDetail.setOnClickListener(listener);
        }
    }



    IOrderChoiceDateListener orderChoiceDateListener = new IOrderChoiceDateListener() {

        @Override
        public void Choise(OrderVo orderVo, long time, String formatTime) {
            showWaitDialog();
            DeliveryApi.updateOrAppointmentDeliveryTime(ClientStateManager.getLoginToken(mContext), orderVo.getOrderId(), time,
                    OrderType.PENDINGAPPOINTMENT.getType(), getNewHandler(2, ResultBase.class));

        }
    };

}

package cn.com.bluemoon.delivery.module.order;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.ResultOrderVo;
import cn.com.bluemoon.delivery.app.api.model.other.OrderState;
import cn.com.bluemoon.delivery.app.api.model.other.OrderVo;
import cn.com.bluemoon.delivery.app.api.model.other.Storehouse;
import cn.com.bluemoon.delivery.entity.OrderType;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.BasePullToRefreshListViewFragment;
import cn.com.bluemoon.delivery.module.event.OrderChangeEvent;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib.view.CommonAlertDialog;

/**
 * 待签收
 */
public class PendingDeliveryFragment extends BasePullToRefreshListViewFragment {
    private long pageFlag;
    private String nameFilter;
    private String addressFilter;
    private int clickIndex;
    private Activity mContext;
    private View viewPopStart;
    private boolean isFilter;

    @Override
    protected void onBeforeCreateView() {
        super.onBeforeCreateView();
        mContext = getActivity();
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.tab_delivery);
    }

    @Override
    protected void setActionBar(CommonActionBar actionBar) {
        setFilterBtn(actionBar);
    }

    @Override
    protected void onActionBarBtnRightClick() {
        FilterWindow popupWindow = new FilterWindow(getActivity(), new FilterWindow.OkListener() {
            @Override
            public void comfireClick(String name, String address) {
                nameFilter = name;
                addressFilter = address;
                isFilter = true;
                initData();
            }
        });
        popupWindow.showAsDropDown(viewPopStart);
    }


    @Override
    protected BaseListAdapter getNewAdapter() {
        return new OrdersAdapter(mContext);
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
        if (!isFilter) {
            nameFilter = "";
            addressFilter = "";
        } else {
            isFilter = false;
        }
        DeliveryApi.getOrdersByTypeByPager(getToken(), pageFlag, nameFilter, addressFilter, OrderType.PENDINGDELIVERY, getNewHandler(requestCode, ResultOrderVo.class));
    }

    @Override
    protected void invokeGetMoreDeliveryApi(int requestCode) {
        DeliveryApi.getOrdersByTypeByPager(getToken(), pageFlag, nameFilter, addressFilter, OrderType.PENDINGDELIVERY,
                getNewHandler(requestCode, ResultOrderVo.class));
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        super.onSuccessResponse(requestCode, jsonString, result);
        if (requestCode == 1) {
            hideWaitDialog();
            toast(result.getResponseMsg());
            removeItem();
        }
    }

    private void removeItem() {
        getList().remove(clickIndex);
        getAdapter().notifyDataSetChanged();
        setAmount2();
        if (getList().isEmpty()) {
            initData();
        }
    }


    @Override
    protected int getHeadLayoutId() {
        return R.layout.head_fragment_tab_order;
    }

    @Override
    protected void initHeadViewEvent(View headView) {
        super.initHeadViewEvent(headView);
        viewPopStart = headView.findViewById(R.id.view_pop_start);
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
            return R.layout.order_list_item3;
        }

        @Override
        protected void setView(final int position, View convertView, ViewGroup parent, boolean isNew) {
            TextView txtDispatchId = getViewById(R.id.txt_dispatchId);
            final LinearLayout layoutDetail = getViewById(R.id.layout_detail);
            TextView txtStorehouse = getViewById(R.id.txt_storehouse);
            final LinearLayout layoutStorehouse = getViewById(R.id.layout_storehouse);
            TextView txtPaytime = getViewById(R.id.txt_paytime);
            TextView txtSubscribeTime = getViewById(R.id.txt_subscribe_time);
            TextView txtCustomerName = getViewById(R.id.txt_customerName);
            final TextView txtMobilePhone = getViewById(R.id.txt_mobilePhone);
            TextView txtAddress = getViewById(R.id.txt_address);
            final Button deliveryAction = getViewById(R.id.delivery_action);
            TextView txtCateAmount = getViewById(R.id.txt_cateAmount);
            TextView txtTotalAmount = getViewById(R.id.txt_totalAmount);

            final OrderVo order = list.get(position);
            ImageView imgReturnMoney = getViewById(R.id.img_return_money);
            ImageView imgSignOvertime = getViewById(R.id.img_sign_over_time);
            if (order.getIsAbnormal().equals("1")) {
                imgReturnMoney.setVisibility(View.VISIBLE);
            } else {
                imgReturnMoney.setVisibility(View.GONE);
            }
            if (order.getIsTimeoutNotSignOrder()) {
                imgSignOvertime.setVisibility(View.VISIBLE);
            } else {
                imgSignOvertime.setVisibility(View.GONE);
            }
            txtCustomerName.setText(OrdersUtils.formatLongString(order.getCustomerName(), txtCustomerName));
            txtPaytime.setText(getString(R.string.pending_order_pay_time, order.getPayOrderTime()));
            txtSubscribeTime.setText(getString(R.string.pending_order_subscribe_time, order.getSubscribeTime()));
            txtMobilePhone.setText(order.getMobilePhone());
            txtStorehouse.setText(OrdersUtils.getStorehouseString(order, mContext));

            txtDispatchId.setText(order.getOrderId());
            txtAddress.setText(getString(R.string.pending_order_address, order.getAddress()));
            txtCateAmount.setText(getString(R.string.pending_order_total_kinds, order.getCateAmount()));
            txtTotalAmount.setText(getString(R.string.pending_order_total_amount, order.getTotalAmount()));

            OnClickListener listener = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickIndex = position;
                    if (v == layoutDetail) {
                        OrderDetailActivity.startAct(mContext, PendingDeliveryFragment.this, order.getOrderId(), OrderState.DELIVERY.toString());
                    } else if (v == deliveryAction) {
                        if (order.getIsAbnormal().equals("1")) {
                            new CommonAlertDialog.Builder(mContext)
                                    .setMessage(String.format(getString(R.string.order_backing_string), getString(R.string.tab_delivery).substring(1)))
                                    .setTitleGravity(Gravity.CENTER)
                                    .setMessageSize(14)
                                    .setPositiveButton(R.string.yes,
                                            new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface dialog,
                                                                    int which) {
                                                    showWaitDialog();
                                                    DeliveryApi.toDelivery(getToken(), order.getOrderId(), getNewHandler(1, ResultBase.class));
                                                }
                                            }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    OrderDetailActivity.startAct(mContext, PendingDeliveryFragment.this, order.getOrderId(), OrderState.DELIVERY.toString());
                                }
                            })
                                    .show();
                        } else {
                            new CommonAlertDialog.Builder(mContext)
                                    .setMessage(R.string.pending_order_delivery_or_not)
                                    .setNegativeButton(R.string.yes,
                                            new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface dialog,
                                                                    int which) {
                                                    showWaitDialog();
                                                    DeliveryApi.toDelivery(getToken(), order.getOrderId(), getNewHandler(1, ResultBase.class));
                                                }
                                            }).setPositiveButton(R.string.no, null)
                                    .show();
                        }
                    } else if (v == layoutStorehouse) {
                        Intent intent = new Intent();
                        intent.setClass(mContext, SelectStoreHouseActivity.class);
                        intent.putExtra("dispatchId", order.getDispatchId());
                        intent.putExtra("code", order.getStorehouseCode());
                        PendingDeliveryFragment.this.startActivityForResult(intent, 0);
                    } else if (v == txtMobilePhone) {
                        PublicUtil.showCallPhoneDialog(mContext, order.getMobilePhone());
                    }
                }
            };
            layoutDetail.setOnClickListener(listener);
            deliveryAction.setOnClickListener(listener);
            layoutStorehouse.setOnClickListener(listener);
            txtMobilePhone.setOnClickListener(listener);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            Storehouse storehouse = (Storehouse) data.getSerializableExtra("storehouse");
            if (storehouse != null) {
                OrderVo orderVo = (OrderVo) getList().get(clickIndex);
                orderVo.setStorechargeCode(storehouse.getStorechargeCode());
                orderVo.setStorechargeName(storehouse.getStorechargeName());
                orderVo.setStorehouseCode(storehouse.getStorehouseCode());
                orderVo.setStorehouseName(storehouse.getStorehouseName());
                getAdapter().notifyDataSetChanged();
            }
        } else if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            removeItem();
        }
    }

    @Override
    protected boolean isUseEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(OrderChangeEvent event) {
        initData();
    }
}
package cn.com.bluemoon.delivery.module.order;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.OrderVo;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.ResultOrderVo;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.entity.OrderType;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.BasePullToRefreshListViewFragment;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.ui.DragView;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib.utils.LibConstants;
import cn.com.bluemoon.lib.view.CommonAlertDialog;

@SuppressWarnings("rawtypes")
public class PendingReceiptFragment extends BasePullToRefreshListViewFragment {

    private long pageFlag;
    private String nameFilter;
    private String addressFilter;
    private int clickIndex;
    private Activity mContext;
    View viewPopStart;
    private String subscribeTime;

    @Override
    protected void onBeforeCreateView() {
        super.onBeforeCreateView();
        mContext = getActivity();
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.tab_receipt);
    }

    @Override
    protected void setActionBar(CommonActionBar actionBar) {
        setFilterBtn(actionBar);
    }

    @Override
    protected void onActionBarBtnRightClick() {
        FilterWindow popupWindow = new FilterWindow(getActivity(),nameFilter, addressFilter,new FilterWindow.OkListener() {
            @Override
            public void comfireClick(String name, String address) {
                nameFilter = name;
                addressFilter = address;
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
        nameFilter = "";
        addressFilter = "";
    }

    @Override
    protected void invokeGetDataDeliveryApi(int requestCode) {
        setAmount2();
        pageFlag = 0;
        DeliveryApi.getOrdersByTypeByPager(getToken(), pageFlag,nameFilter, addressFilter,
                OrderType.PENDINGRECEIPT, getNewHandler(requestCode, ResultOrderVo.class));
    }

    @Override
    protected void invokeGetMoreDeliveryApi(int requestCode) {
        DeliveryApi.getOrdersByTypeByPager(getToken(), pageFlag,nameFilter, addressFilter, OrderType.PENDINGRECEIPT,
                getNewHandler(requestCode, ResultOrderVo.class));
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        super.onSuccessResponse(requestCode, jsonString, result);
        if (requestCode == 1) {
            //签收成功
            hideWaitDialog();
            toast(result.getResponseMsg());
            removeItem();
        } else if (requestCode == 3) {
            //发送验证码
            toast(result.getResponseMsg());
        } else if (requestCode == 4) {
            //修改预约时间
            OrderVo orderVo = (OrderVo)getList().get(clickIndex);
            orderVo.setSubscribeTime(subscribeTime);
            getAdapter().notifyDataSetChanged();
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

    private List<DragView> views;
    public class OrdersAdapter extends BaseListAdapter<OrderVo> {

        public OrdersAdapter(Context context) {
            super(context, null);
            views = new ArrayList<>();
        }
        @Override
        protected int getLayoutId() {
            return R.layout.order_list_item4;
        }

        @Override
        public int getViewTypeCount() {
            return super.getViewTypeCount();
        }

        @Override
        protected void setView(final int position, View convertView, ViewGroup parent, boolean isNew) {
            TextView txtDispatchId = getViewById(R.id.txt_dispatchId);
            final LinearLayout layoutDetail = getViewById(R.id.layout_detail);
            TextView txtStorehouse = getViewById(R.id.txt_storehouse);
            TextView txtPaytime = getViewById(R.id.txt_paytime);
            TextView txtSubscribeTime = getViewById(R.id.txt_subscribe_time);
            TextView txtCustomerName = getViewById(R.id.txt_customerName);
            TextView txtMobilePhone = getViewById(R.id.txt_mobilePhone);
            TextView txtAddress = getViewById(R.id.txt_address);
            final Button deliveryAction = getViewById(R.id.delivery_action);
            final Button deliverySms = getViewById(R.id.delivery_sms);
            TextView txtCateAmount = getViewById(R.id.txt_cateAmount);
            TextView txtTotalAmount = getViewById(R.id.txt_totalAmount);
            TextView txtTotalPrice = getViewById(R.id.txt_totalPrice);
            TextView txtEditAppointmentTime = getViewById(R.id.txt_edit_appointment_time);
            DragView delBtn = getViewById(R.id.drag_view);
            final OrderVo order = list.get(position);

            delBtn.setTag(position);
            if (isNew) {
                views.add(delBtn);
            }
            txtCustomerName.setText(OrdersUtils.formatLongString(
                    order.getCustomerName(), txtCustomerName));
            txtPaytime.setText(getString(R.string.pending_order_pay_time, order.getPayOrderTime()));
            txtSubscribeTime.setText(getString(R.string.pending_order_subscribe_time, order.getSubscribeTime()));
            txtMobilePhone.setText(order.getMobilePhone());
            txtMobilePhone.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
            txtMobilePhone.getPaint().setAntiAlias(true);
            txtStorehouse.setText(OrdersUtils.getStorehouseString(order, mContext));
            txtDispatchId.setText(order.getOrderId());
            txtAddress.setText(order.getAddress());
            txtCateAmount.setText(getString(R.string.pending_order_total_kinds, order.getCateAmount()));
            txtTotalAmount.setText(getString(R.string.pending_order_total_amount, order.getTotalAmount()));
            txtTotalPrice.setText(getString(R.string.pending_order_total_price,
                    StringUtil.formatPrice(order.getTotalPrice())));
            if (delBtn.isOpen()) {
                delBtn.closeAnim();
            }
            txtEditAppointmentTime.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
            txtEditAppointmentTime.getPaint().setAntiAlias(true);
            OnClickListener listener = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickIndex = position;
                    switch (v.getId()) {
                        case R.id.txt_edit_appointment_time :
                            ChoiceOrderDatePopupWindow popupWindow = new ChoiceOrderDatePopupWindow(
                                    mContext, order, orderChoiceDateListener);
                            popupWindow.showAsDropDown(viewPopStart);
                            break;
                        case R.id.txt_mobilePhone :
                            PublicUtil.showCallPhoneDialog(getActivity(), order.getMobilePhone());
                            break;
                        case R.id.delivery_sms :
                            showCallPhoneOrSendSMSDialog(order.getOrderId());
                            break;
                        case R.id.layout_detail :
                            PublicUtil.showOrderDetailView(mContext, order.getOrderId());
                            break;
                        case R.id.delivery_action :
                            PublicUtil.openOrderWithInput(PendingReceiptFragment.this, getString(R.string.pending_order_receive_sign_title),
                                    getString(R.string.pending_order_receive_sign_scan_btn), 4);
                            break;
                    }

                }
            };
            txtEditAppointmentTime.setOnClickListener(listener);
            txtMobilePhone.setOnClickListener(listener);
            deliverySms.setOnClickListener(listener);
            layoutDetail.setOnClickListener(listener);
            deliveryAction.setOnClickListener(listener);

            delBtn.setOnDragStateListener(new DragView.DragStateListener() {
                @Override
                public void onOpened(DragView dragView) {
                    for (int i = 0; i < views.size(); i++){
                        if ((int)views.get(i).getTag() != position) {
                            views.get(i).closeAnim();
                        }
                    }
                }
                @Override
                public void onClosed(DragView dragView) {

                }

                @Override
                public void onForegroundViewClick(DragView dragView , View v) {
                    notifyDataSetChanged();
                }

                @Override
                public void onBackgroundViewClick(DragView dragView , View v) {
                    int pos = (int) dragView.getTag();
                    clickIndex = pos;
                    Intent intent = new Intent(mContext,ReturnOrderActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("order", list.get(pos));
                    intent.putExtras(bundle);
                    PendingReceiptFragment.this.startActivityForResult(intent, 1);
                }
            });



        }
    }


   public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       if (resultCode == Activity.RESULT_OK) {
           if (requestCode == 1)  {
               //退货成功
               PublicUtil.showToast(getString(R.string.pending_order_return_sucessful));
               removeItem();
           } else if (requestCode == 4) {
               //签收扫描返回二维码code
               String resultStr = data.getStringExtra(LibConstants.SCAN_RESULT);
               if (StringUtils.isNotBlank(resultStr)) {
                   OrderVo order = (OrderVo)getList().get(clickIndex);
                   DeliveryApi.orderSign(ClientStateManager.getLoginToken(mContext), order.getOrderId(), "scan", resultStr, getNewHandler(1, ResultBase.class));
               }
           }
       }

    }

    IOrderChoiceDateListener orderChoiceDateListener = new IOrderChoiceDateListener() {
        @Override
        public void Choise(OrderVo orderVo, long time, String formatTime) {
            showWaitDialog();
            subscribeTime = formatTime;
            DeliveryApi.updateOrAppointmentDeliveryTime(
                    ClientStateManager.getLoginToken(mContext),
                    orderVo.getOrderId(), time,
                    OrderType.PENDINGAPPOINTMENT.getType(),
                    getNewHandler(4, ResultBase.class));
        }
    };

    public void showCallPhoneOrSendSMSDialog(final String orderId) {
        CommonAlertDialog.Builder dialog = new CommonAlertDialog.Builder(getActivity());
        dialog.setMessage(getString(R.string.pending_order_receive_sign_sms_desc));
        dialog.setPositiveButton(R.string.btn_cancel_space, null);
        dialog.setNegativeButton(R.string.btn_send_space, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showWaitDialog();
                DeliveryApi.resendReceiveCode(ClientStateManager.getLoginToken(), orderId, getNewHandler(3, ResultBase.class));
            }
        });
        dialog.show();
    }
}

package cn.com.bluemoon.delivery.module.order;

import android.app.Activity;
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
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.entity.OrderType;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.BasePullToRefreshListViewFragment;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib.view.CommonAlertDialog;

@SuppressWarnings("rawtypes")
public class PendingReceiptFragment extends BasePullToRefreshListViewFragment {

    private long pageFlag;
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
        DeliveryApi.getOrdersByType(getToken(), pageFlag, OrderType.PENDINGRECEIPT, getNewHandler(requestCode, ResultOrderVo.class));
    }

    @Override
    protected void invokeGetMoreDeliveryApi(int requestCode) {
        DeliveryApi.getOrdersByType(getToken(), pageFlag, OrderType.PENDINGRECEIPT,
                getNewHandler(requestCode, ResultOrderVo.class));
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        super.onSuccessResponse(requestCode, jsonString, result);
        if (requestCode == 1) {
            hideWaitDialog();
            getList().remove(clickIndex);
            getAdapter().notifyDataSetChanged();
            toast(result.getResponseMsg());
            setAmount2();
            if (getList().isEmpty()) {
                initData();
            }
        } else if (requestCode == 3) {
            toast(result.getResponseMsg());
        } else if (requestCode == 4) {
            OrderVo orderVo = (OrderVo)getList().get(clickIndex);
            orderVo.setSubscribeTime(subscribeTime);
            getAdapter().notifyDataSetChanged();
        }
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
    public void onItemClick(Object item, View view, int position) {

    }
    public class OrdersAdapter extends BaseListAdapter<OrderVo> {
        public OrdersAdapter(Context context) {
            super(context, null);
        }
        @Override
        protected int getLayoutId() {
            return R.layout.order_list_item4;
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
            final OrderVo order = list.get(position);

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
            txtEditAppointmentTime.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
            txtEditAppointmentTime.getPaint().setAntiAlias(true);
            txtEditAppointmentTime.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickIndex = position;
                    ChoiceOrderDatePopupWindow popupWindow = new ChoiceOrderDatePopupWindow(
                            mContext, order, orderChoiceDateListener);
                    popupWindow.showAsDropDown(viewPopStart);

                }
            });

            txtMobilePhone.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    PublicUtil.showCallPhoneDialog(getActivity(), order.getMobilePhone());
                }
            });

            deliverySms.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCallPhoneOrSendSMSDialog(order.getOrderId());
                }
            });

            layoutDetail.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    PublicUtil.showOrderDetailView(mContext, order.getOrderId());
                }
            });

            deliveryAction.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    clickIndex = position;
                    PublicUtil.openScanOrder(getActivity(), PendingReceiptFragment.this,
                            getString(R.string.pending_order_receive_sign_title),
                            getString(R.string.pending_order_receive_sign_scan_btn),
                            Constants.REQUEST_SCAN, 4);
                }
            });


        }
    }

    /*private void initList(boolean isEmpty) {
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem returnGoodsItem = new SwipeMenuItem(mContext);
                returnGoodsItem.setBackground(R.color.line_solid_deep_bg);
                returnGoodsItem.setWidth(OrdersUtils.dp2px(70, mContext));
                returnGoodsItem.setTitle(R.string.pending_order_return_btn);
                returnGoodsItem.setTitleSize(16);
                returnGoodsItem.setTitleColor(Color.WHITE);
                menu.addMenuItem(returnGoodsItem);
            }
        };
        if (!isEmpty) {
            // set creator
            listView.setMenuCreator(creator);

            // step 2. listener item click event
            listView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(int position, SwipeMenu menu,
                                               int index) {
                    switch (index) {
                        case 0:
                            orderClicked = orderList.get(position);

                            Intent intent = new Intent(mContext,
                                    ReturnOrderActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("order", orderClicked);

                            intent.putExtras(bundle);
                            PendingReceiptFragment.this.startActivityForResult(intent,
                                    REDIRECT_TO_RETURN_ACTIVITY);

                            break;
                        case 1:
                            break;
                    }
                    return false;
                }
            });
        } else {
            listView.setMenuCreator(null);
        }

    }*/

   /* public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REDIRECT_TO_RETURN_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {

                PublicUtil.showToast(getString(R.string.pending_order_return_sucessful));
                //mContext.setAmountShow();
                orderList.remove(orderClicked);
                if (orderList != null && orderList.size() > 0) {
                    ordersAdapter.notifyDataSetChanged();
                } else {
                    initList(true);
                    OrdersEmptyAdapter ordersEmptyAdapter = new OrdersEmptyAdapter(mContext);
                    listView.setAdapter(ordersEmptyAdapter);
                }

            }
        } else if (requestCode == Constants.REQUEST_SCAN) {
            if (resultCode == Activity.RESULT_CANCELED) {
                return;
            } else if (resultCode == Activity.RESULT_OK) {

                String resultStr = data.getStringExtra(LibConstants.SCAN_RESULT);
                DeliveryApi.orderSign(ClientStateManager.getLoginToken(mContext), orderClicked.getOrderId(), "scan", resultStr, getNewHandler(1, ResultBase.class));
            } else if (resultCode == 3) {
                PublicUtil.openScanOrder(getActivity(), PendingReceiptFragment.this,
                        getString(R.string.pending_order_receive_sign_title),
                        getString(R.string.pending_order_receive_sign_scan_btn),
                        Constants.REQUEST_SCAN, 4);
            } else if (resultCode == 4) {

                Intent intent = new Intent(getActivity(), SignActivity.class);
                intent.putExtra("orderId", orderClicked.getOrderId());
                PendingReceiptFragment.this.startActivityForResult(intent, Constants.REQUEST_SCAN);
            } else if (resultCode == 2) {
                //mContext.setAmountShow();
                orderList.remove(orderClicked);
                if (orderList != null && orderList.size() > 0) {
                    ordersAdapter.notifyDataSetChanged();
                } else {
                    initList(true);
                    OrdersEmptyAdapter ordersEmptyAdapter = new OrdersEmptyAdapter(mContext);
                    listView.setAdapter(ordersEmptyAdapter);
                }
            }
        }
    }*/

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

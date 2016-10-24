package cn.com.bluemoon.delivery.module.order;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;

import java.util.List;

import butterknife.Bind;
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
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.delivery.utils.ViewHolder;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib.view.CommonAlertDialog;
import cn.com.bluemoon.lib.view.CommonProgressDialog;

public class PendingOrdersFragment extends BasePullToRefreshListViewFragment {
    private String TAG = "PendingOrdersFragment";
    private PullToRefreshListView listView;
    private OrdersAdapter ordersAdapter;
    private List<OrderVo> orderList;
    private OrdersTabActivity mContext;
    private CommonProgressDialog progressDialog;
    private boolean lock;
    private OrderVo orderClicked;
    private long pageFlag;

    ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initCustomActionBar();
        progressDialog = new CommonProgressDialog(mContext);
        View v = inflater.inflate(R.layout.fragment_tab_orders, container, false);
        listView = (PullToRefreshListView) v.findViewById(R.id.listview_orders);
        listView.setOnRefreshListener(new OnRefreshListener<ListView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                mContext.setAmountShow();
                DeliveryApi.getOrdersByType(ClientStateManager.getLoginToken(mContext), OrderType.PENDINGORDERS, getOrdersHandler);
            }
        });
        intiDate();
        return v;
    }

    private void intiDate() {
        progressDialog.show();
        mContext.setAmountShow();
        DeliveryApi.getOrdersByType(ClientStateManager.getLoginToken(mContext), OrderType.PENDINGORDERS, getOrdersHandler);
    }

    AsyncHttpResponseHandler getOrdersHandler = new TextHttpResponseHandler(HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            LogUtils.d("test", "getOrdersHandler result = " + responseString);
            progressDialog.dismiss();
            listView.onRefreshComplete();
            try {
                ResultOrderVo result = JSON.parseObject(responseString, ResultOrderVo.class);
                if (result.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    orderList = result.getItemList();
                    ordersAdapter = new OrdersAdapter(mContext, R.layout.order_list_item);
                    listView.setAdapter(ordersAdapter);
                } else {
                    PublicUtil.showErrorMsg(mContext, result);
                }
            } catch (Exception e) {
                PublicUtil.showToastServerBusy(mContext);
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString,
                              Throwable throwable) {
            LogUtils.d("test", "getOrdersHandler result failed. statusCode=" + statusCode);
            progressDialog.dismiss();
            listView.onRefreshComplete();
            PublicUtil.showToastServerOvertime(mContext);
        }
    };
    AsyncHttpResponseHandler accceptOrderHandler = new TextHttpResponseHandler(HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            LogUtils.d("test", "accceptOrderHandler result = " + responseString);
            progressDialog.dismiss();
            lock = false;
            try {
                ResultOrderVo result = JSON.parseObject(responseString, ResultOrderVo.class);
                if (result.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    mContext.setAmountShow();
                    PublicUtil.showToast(mContext, result.getResponseMsg());
                    orderList.remove(orderClicked);
                    ordersAdapter.notifyDataSetChanged();
                } else if (result.getResponseCode() == 4102) {
                    new CommonAlertDialog.Builder(mContext)
                            .setMessage(result.getResponseMsg())
                            .setPositiveButton(R.string.ok,
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            progressDialog.show();
                                            mContext.setAmountShow();
                                            DeliveryApi.getOrdersByType(ClientStateManager.getLoginToken(mContext), OrderType.PENDINGORDERS, getOrdersHandler);
                                        }
                                    }).show();
                } else {
                    PublicUtil.showErrorMsg(mContext, result);
                }
            } catch (Exception e) {
                PublicUtil.showToastServerBusy(mContext);
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString,
                              Throwable throwable) {
            lock = false;
            progressDialog.dismiss();
            PublicUtil.showToastServerOvertime(mContext);
        }
    };


    AsyncHttpResponseHandler refuseOrderHandler = new TextHttpResponseHandler(HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            LogUtils.d("test", "refuseOrderHandler result = " + responseString);
            progressDialog.dismiss();
            lock = false;
            try {
                ResultOrderVo result = JSON.parseObject(responseString, ResultOrderVo.class);
                if (result.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    intiDate();
                } else {
                    PublicUtil.showErrorMsg(mContext, result);
                }
            } catch (Exception e) {
                PublicUtil.showToastServerBusy(mContext);
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString,
                              Throwable throwable) {
            lock = false;
            progressDialog.dismiss();
            PublicUtil.showToastServerOvertime(mContext);
        }
    };


    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tab_orders;
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.tab_orders);
    }


    @Override
    protected BaseListAdapter getNewAdapter() {
        return null;
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
        DeliveryApi.getOrdersByType(getToken(), pageFlag, OrderType.PENDINGORDERS, getNewHandler(requestCode, ResultOrderVo.class));
    }

    @Override
    protected void invokeGetMoreDeliveryApi(int requestCode) {
        DeliveryApi.getOrdersByType(getToken(), pageFlag, OrderType.PENDINGORDERS,
                getNewHandler(requestCode, ResultOrderVo.class));
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
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            TextView txtDispatchId = getViewById(R.id.txt_dispatchId);
            TextView txtDetail = getViewById(R.id.txt_detail);
            LinearLayout layoutDetail = getViewById(R.id.layout_detail);
            TextView txtPaytime = getViewById(R.id.txt_paytime);
            TextView txtAddress = getViewById(R.id.txt_address);
            Button receivingOrdersAction = getViewById(R.id.receiving_orders_action);
            TextView txtCateAmount = getViewById(R.id.txt_cateAmount);
            TextView txtTotalAmount = getViewById(R.id.txt_totalAmount);
            TextView txtTotalPrice = getViewById(R.id.txt_totalPrice);
            TextView txtRefuseOrder = getViewById(R.id.txt_refuse_order);
            View lineDotted = getViewById(R.id.line_dotted);
            final OrderVo order = list.get(position);

            txtCateAmount.setText(String.format(getString(R.string.pending_order_total_kinds), order.getCateAmount()));
            txtTotalAmount.setText(String.format(getString(R.string.pending_order_total_amount), order.getTotalAmount()));
            txtTotalPrice.setText(String.format(getString(R.string.pending_order_total_price), StringUtil.formatPrice(order.getTotalPrice())));


            txtRefuseOrder.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
            txtRefuseOrder.getPaint().setAntiAlias(true);

            txtRefuseOrder.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    CommonAlertDialog.Builder dialog = new CommonAlertDialog.Builder(getActivity());
                    dialog.setMessage(getString(R.string.pending_order_refuse_alert));
                    dialog.setPositiveButton(R.string.dialog_cancel, null);
                    dialog.setNegativeButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            progressDialog.show();
                            DeliveryApi.rejectOrder(ClientStateManager.getLoginToken(mContext), order.getOrderId(), order.getOrderSource(), refuseOrderHandler);
                        }
                    });
                    dialog.show();
                }
            });


            layoutDetail.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    PublicUtil.showOrderDetailView(mContext, order.getOrderId());
                }
            });
            receivingOrdersAction.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!lock) {
                        lock = true;
                        orderClicked = order;
                        progressDialog.show();
                        DeliveryApi.acceptOrder(ClientStateManager.getLoginToken(mContext),
                                order.getOrderId(), accceptOrderHandler);
                    }
                }
            });

        }

    }


}

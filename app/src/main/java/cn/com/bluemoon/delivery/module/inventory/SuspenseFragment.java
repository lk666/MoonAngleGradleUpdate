package cn.com.bluemoon.delivery.module.inventory;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;

import java.util.ArrayList;
import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.inventory.OrderVo;
import cn.com.bluemoon.delivery.app.api.model.inventory.ResultCustormerService;
import cn.com.bluemoon.delivery.app.api.model.inventory.ResultOrderVo;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.module.base.interf.IActionBarListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib.view.CommonProgressDialog;

public class SuspenseFragment extends Fragment {
    private String TAG = "SuspenseFragment";

    private SuspenseAdapter adapter;
    private InventoryTabActivity main;
    private CommonProgressDialog progressDialog;
    private PullToRefreshListView listView;
    private String type;

    private TextView txtCount;
    private TextView txtPrice;
    private TextView txtTotalBoxes;
    private String customerNum;
    private String customerName;

    private ResultOrderVo item;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        main = (InventoryTabActivity) getActivity();
        try {
            Bundle bundle = getArguments();
            type = bundle.getString("type");

            DeliveryApi.queryOperatorPersons(ClientStateManager.getLoginToken(main), customerHandler);


        } catch (Exception ex) {

        }
        initCustomActionBar();


        View v = inflater.inflate(R.layout.fragment_tab_inventory, container,
                false);

        txtCount = (TextView) v.findViewById(R.id.txt_count);
        txtPrice = (TextView) v.findViewById(R.id.txt_price);
        txtTotalBoxes = (TextView) v.findViewById(R.id.txt_total_boxes);


        setCountAndPrice(0, null, 0);
        listView = (PullToRefreshListView) v
                .findViewById(R.id.listview_main);
        listView.setMode(PullToRefreshBase.Mode.DISABLED);
        progressDialog = new CommonProgressDialog(main);
        adapter = new SuspenseAdapter(main);
        getItem();
        return v;
    }

    private void getItem() {
        String token = ClientStateManager.getLoginToken(main);
        if (progressDialog != null) {
            progressDialog.show();
        }

        if (type.equals(InventoryTabActivity.RECEIVE_MANAGEMENT)) {
            DeliveryApi.getWaitReceiptOrders(token, inventoryOrderHandler);
        } else {
            DeliveryApi.getWaitOutOrders(token, inventoryOrderHandler);
        }
    }

    private void setData(ResultOrderVo result) {
        if (result == null || result.getOrderList() == null || result.getOrderList().size() < 1) {
            adapter.setList(new ArrayList<OrderVo>());
            main.amountTv.setVisibility(View.GONE);

        } else {
            item = result;
            main.amountTv.setText(String.valueOf(result.getOrderList().size()));
            main.amountTv.setVisibility(View.VISIBLE);
            setCountAndPrice(item.getOrderTotalNum(), StringUtil.formatPriceByFen(item.getOrderTotalMoney()), item.getOrderTotalCase());

            adapter.setList(item.getOrderList());
        }
        listView.setAdapter(adapter);

    }


    private void initCustomActionBar() {

        CommonActionBar actionBar = new CommonActionBar(getActivity().getActionBar(),
                new IActionBarListener() {

                    @Override
                    public void onBtnRight(View v) {
                        // TODO Auto-generated method stub
                        // PublicUtil.showMessage(getActivity(),"click customer button");
                        if (StringUtil.isEmpty(customerName) || StringUtil.isEmpty(customerNum)) {
                            PublicUtil.showMessage(main, getString(R.string.dialog_customer_error));
                        } else {
                            View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_content_call_phone, null);
                            TextView txtCustomer = (TextView) view.findViewById(R.id.txt_customer_service);
                            TextView txtPhone = (TextView) view.findViewById(R.id.txt_phone_num);
                            txtCustomer.setText(String.format(getString(R.string.dialog_customer_name), customerName));
                            txtPhone.setText(customerNum);
                            PublicUtil.showCallPhoneDialog(main, view, customerNum);
                        }
                    }

                    @Override
                    public void onBtnLeft(View v) {
                        // TODO Auto-generated method stub
                        getActivity().finish();
                    }

                    @Override
                    public void setTitle(TextView v) {
                        // TODO Auto-generated method stub
                        if (type.equals(InventoryTabActivity.RECEIVE_MANAGEMENT)) {
                            v.setText(R.string.tab_title_receive_text);
                        } else {
                            v.setText(R.string.tab_title_delivery_text);
                        }
                    }
                });

        actionBar.getImgRightView().setImageResource(R.mipmap.ico_customer_service);
        actionBar.getImgRightView().setVisibility(View.VISIBLE);
    }

    @SuppressLint("InflateParams")
    class SuspenseAdapter extends BaseAdapter {

        private Context context;
        private List<OrderVo> lists;

        public SuspenseAdapter(Context context) {
            this.context = context;
        }

        public void setList(List<OrderVo> list) {
            this.lists = list;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return lists.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return lists.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            // TODO Auto-generated method stub
            LayoutInflater inflate = LayoutInflater.from(context);
            if (lists.size() == 0) {
                View viewEmpty = inflate.inflate(R.layout.layout_no_data, null);
                AbsListView.LayoutParams params = new AbsListView.LayoutParams(
                        AbsListView.LayoutParams.MATCH_PARENT, listView.getHeight());
                viewEmpty.setLayoutParams(params);
                return viewEmpty;
            }

            convertView = inflate.inflate(R.layout.order_suspense_item, null);

            TextView txtOrderId = (TextView) convertView
                    .findViewById(R.id.txt_order_id);
            TextView txtDate = (TextView) convertView
                    .findViewById(R.id.txt_date);
            TextView txtPrice = (TextView) convertView
                    .findViewById(R.id.txt_price);
            TextView txtBoxesCount = (TextView) convertView
                    .findViewById(R.id.txt_boxes_count);
            Button btnSuspense = (Button) convertView.findViewById(R.id.btn_suspense);
            txtOrderId.setText(lists.get(position).getOrderCode());
            txtPrice.setText(
                    getString(R.string.order_money_sign) + StringUtil.formatPriceByFen(lists.get(position).getTotalAmountRmb()));

            txtDate.setText(DateUtil.getTime(lists.get(position).getOrderDate(), "yyyy-MM-dd"));

            txtBoxesCount.setText(String.format(getString(R.string.order_boxes_count),
                            StringUtil.formatBoxesNum(lists.get(position).getTotalCase())) +
                            String.format(getString(R.string.order_product_count),
                                    lists.get(position).getTotalNum())
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

            convertView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if (type.equals(InventoryTabActivity.RECEIVE_MANAGEMENT)) {
                        OrderReceiveDetailActivity.actionStart(SuspenseFragment.this, InventoryTabActivity.RECEIVE_MANAGEMENT, lists.get(position).getOrderCode());
                    } else {
                        OrderDeliverDetailActivity.actionStart(SuspenseFragment.this, InventoryTabActivity.DELIVERY_MANAGEMENT, lists.get(position).getOrderCode());
                    }
                }
            });

            btnSuspense.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if (type.equals(InventoryTabActivity.RECEIVE_MANAGEMENT)) {
                        OrderReceiveDetailActivity.actionStart(SuspenseFragment.this,
                                InventoryTabActivity.RECEIVE_MANAGEMENT, lists.get(position).getOrderCode());
                    } else {
                        OrderDeliverDetailActivity.actionStart(SuspenseFragment.this,
                                InventoryTabActivity.DELIVERY_MANAGEMENT,lists.get(position).getOrderCode());
                        //  OrderReceiveDetailActivity.actionStart(SuspenseFragment.this, InventoryTabActivity.DELIVERY_MANAGEMENT, lists.get(position).getOrderCode());
                    }
                }
            });
            return convertView;
        }

    }

    AsyncHttpResponseHandler inventoryOrderHandler = new TextHttpResponseHandler(
            HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                              String responseString) {
            LogUtils.d(TAG, "getInventoryOrder result = " + responseString);
            if (progressDialog != null)
                progressDialog.dismiss();
            listView.onRefreshComplete();
            try {
                ResultOrderVo orderResult = JSON.parseObject(responseString,
                        ResultOrderVo.class);
                if (orderResult.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    setData(orderResult);
                } else {
                    PublicUtil.showErrorMsg(main, orderResult);
                }
            } catch (Exception e) {
                LogUtils.e(TAG, e.getMessage());
                PublicUtil.showToastServerBusy();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers,
                              String responseString, Throwable throwable) {
            LogUtils.e(TAG, throwable.getMessage());
            if (progressDialog != null)
                progressDialog.dismiss();
            PublicUtil.showToastServerOvertime();
        }
    };

    private void setCountAndPrice(int size, String price, double boxes) {
        String count = "0";
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
        txtTotalBoxes.setText(String.format(getString(R.string.order_boxes_num), StringUtil.formatBoxesNum(boxes)));
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    AsyncHttpResponseHandler customerHandler = new TextHttpResponseHandler(
            HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                              String responseString) {
            LogUtils.d(TAG, "customerHandler result = " + responseString);

            try {
                ResultCustormerService custormerResult = JSON.parseObject(responseString,
                        ResultCustormerService.class);
                if (custormerResult.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    customerName = custormerResult.getCustormerServiceName();
                    customerNum = custormerResult.getCustormerServicePhone();
                }
            } catch (Exception e) {
                LogUtils.e(TAG, e.getMessage());
                // PublicUtil.showToastServerBusy();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers,
                              String responseString, Throwable throwable) {
            LogUtils.e(TAG, throwable.getMessage());
        }
    };

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(resultCode == main.RESULT_OK){
//            setCountAndPrice(0, null, 0);
//            getItem();
//        }
//    }
}

package cn.com.bluemoon.delivery.inventory;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.com.bluemoon.delivery.ClientStateManager;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.inventory.OrderVo;
import cn.com.bluemoon.delivery.app.api.model.inventory.ResultOrderVo;
import cn.com.bluemoon.delivery.async.listener.IActionBarListener;
import cn.com.bluemoon.delivery.order.TimerFilterWindow;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib.utils.LibDateUtil;
import cn.com.bluemoon.lib.view.CommonProgressDialog;

public class ProcessedFragment extends Fragment {
    private String TAG = "ProcessedFragment";

    private SuspenseAdapter adapter;
    private FragmentActivity main;
    private CommonProgressDialog progressDialog;
    private PullToRefreshListView listView;
    private String type;

    private TextView txtCount;
    private TextView txtPrice;
    private TextView txtTotalBoxes;
    View popStart;

    private ResultOrderVo item;
    private long startTime = 0;
    private long endTime = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            Bundle bundle = getArguments();
            type = bundle.getString("type");

        }catch (Exception ex){

        }
        initCustomActionBar();
        main = getActivity();


        View v = inflater.inflate(R.layout.fragment_tab_inventory, container,
                false);
        popStart = (View) v.findViewById(R.id.view_pop_start);
        txtCount = (TextView) v.findViewById(R.id.txt_count);
        txtPrice = (TextView) v.findViewById(R.id.txt_price);
        txtTotalBoxes=(TextView)v.findViewById(R.id.txt_total_boxes);


        setCountAndPrice(0, null,0);
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
        if ( progressDialog != null) {
            progressDialog.show();
        }

        if(type.equals(InventoryTabActivity.RECEIVE_MANAGEMENT)) {
            DeliveryApi.getReceiptOrders(token,startTime, endTime, inventoryOrderHandler);
        }else{
            DeliveryApi.getOutOrders(token, startTime, endTime, inventoryOrderHandler);
        }
    }

    private void setData(ResultOrderVo result) {
        if (result == null || result.getOrderList()==null || result.getOrderList().size() < 1) {
            //PublicUtil.showToastErrorData(main);
            adapter.setList(new ArrayList<OrderVo>());
            listView.setAdapter(adapter);
            //return;
        }else
        {
            item = result;
            setCountAndPrice(item.getOrderTotalNum(), StringUtil.formatPriceByFen(item.getOrderTotalMoney()), item.getOrderTotalCase());
            adapter.setList(item.getOrderList());
        }
        listView.setAdapter(adapter);

    }


    private void initCustomActionBar() {

        CommonActionBar actionBar =   new CommonActionBar(getActivity().getActionBar(),
                new IActionBarListener() {

                    @Override
                    public void onBtnRight(View v) {
                        // TODO Auto-generated method stub

                            TimerFilterWindow popupWindow = new TimerFilterWindow(getActivity(), new TimerFilterWindow.TimerFilterListener() {
                                @Override
                                public void callBack(long startDate, long endDate) {
                                    if(startDate>=0 && endDate>=startDate){
                                    startTime = LibDateUtil.getTimeByCustTime(startDate)/1000;
                                    endTime =  LibDateUtil.getTimeByCustTime(endDate)/1000;

                                    Date start = new Date(startTime*1000);
                                    Date end = new Date(endTime*1000);

                                        if(endDate>=startDate
                                                &&(((end.getDate()>=start.getDate()) && ((end.getYear()*12+end.getMonth())-(start.getYear()*12+start.getMonth())<=5) )
                                                || ((end.getDate()<start.getDate()) && ((end.getYear()*12+end.getMonth())-(start.getYear()*12+start.getMonth())<=6)))){
                                            setCountAndPrice(0, null,0);
                                        getItem();
                                    }else{
                                        PublicUtil.showMessage(main,getString(R.string.txt_order_fillter_date_error));
                                    }

                                }}
                            });
                            popupWindow.showPopwindow(popStart);

                    }

                    @Override
                    public void onBtnLeft(View v) {
                        // TODO Auto-generated method stub
                        getActivity().finish();
                    }

                    @Override
                    public void setTitle(TextView v) {
                        // TODO Auto-generated method stub
                        if(type.equals(InventoryTabActivity.RECEIVE_MANAGEMENT)) {
                            v.setText(R.string.tab_title_received_text);
                        }else{
                            v.setText(R.string.tab_title_delivered_text);
                        }
                    }
                });

        actionBar.getTvRightView().setText(R.string.btn_txt_fillter);
        actionBar.getTvRightView().setCompoundDrawablePadding(10);

        Drawable drawableFillter=getResources().getDrawable(R.mipmap.icon_filter);
        drawableFillter.setBounds(0, 0, drawableFillter.getMinimumWidth(), drawableFillter.getMinimumHeight());
        actionBar.getTvRightView().setCompoundDrawables(drawableFillter,null,null,null);
       actionBar.getTvRightView().setVisibility(View.VISIBLE);

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

            convertView = inflate.inflate(R.layout.order_processed_item, null);

            TextView txtOrderId = (TextView) convertView
                    .findViewById(R.id.txt_order_id);
            TextView txtDate = (TextView) convertView
                    .findViewById(R.id.txt_date);
            TextView txtPrice = (TextView) convertView
                    .findViewById(R.id.txt_price);
            TextView txtBoxesCount = (TextView) convertView
                    .findViewById(R.id.txt_boxes_count);

            txtOrderId.setText(lists.get(position).getOrderCode());
            txtPrice.setText(
                    getString(R.string.order_money_sign) + StringUtil.formatPriceByFen(lists.get(position).getTotalAmountRmb()));

            txtDate.setText( DateUtil.getTime(lists.get(position).getOrderDate(), "yyyy-MM-dd"));

            txtBoxesCount.setText(String.format(getString(R.string.order_boxes_count),
                            StringUtil.formatBoxesNum(lists.get(position).getTotalCase()))+
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

            convertView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if(type.equals(InventoryTabActivity.RECEIVE_MANAGEMENT)) {
                        OrderEndReceiveDetailActivity.actionStart(main,
                                InventoryTabActivity.RECEIVE_MANAGEMENT,lists.get(position).getOrderCode());
                    }else{
                        OrderEndDeliverDetailActivity.actionStart(main,
                                InventoryTabActivity.DELIVERY_MANAGEMENT, lists.get(position).getOrderCode());
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

    private void setCountAndPrice(int size,String price,double boxes){
        String count = "0";
        if (size > 99) {
            count = "99+";
        } else {
            count = String.valueOf(size);
        }
        count = String.format(getString(R.string.order_history_totalcount), count);
        if (StringUtil.isEmpty(price)||"0".equals(price)
                ||"0.0".equals(price)) {
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

}

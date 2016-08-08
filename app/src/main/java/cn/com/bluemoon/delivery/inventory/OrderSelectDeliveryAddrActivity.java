/**
 * Description:
 * Copyright: Copyright (c) 2016
 * Company: BLUE MOON
 *
 * @author: wangshanhai
 * @version 3.1.0
 * @date: 2016/3/23
 * @todo: 进销存发货详情
 */
package cn.com.bluemoon.delivery.inventory;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;

import java.util.ArrayList;
import java.util.List;

import cn.com.bluemoon.delivery.ClientStateManager;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.storage.MallStoreRecieverAddress;
import cn.com.bluemoon.delivery.app.api.model.storage.ResultMallStoreRecieverAddress;
import cn.com.bluemoon.delivery.async.listener.IActionBarListener;
import cn.com.bluemoon.delivery.manager.ActivityManager;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.view.CommonProgressDialog;

public class OrderSelectDeliveryAddrActivity extends Activity implements OnClickListener {

    public static String RECEIVE_ADDRESS = "RECEIVE_ADDRESS";
    public static String DELIVERY_ADDRESS = "DELIVERY_ADDRESS";


    private String TAG = "OrderSelectDeliveryAddrActivity";

    private ListView listView;
    private Activity main;
    private SelectDeliverAddrAdapter adapter;
    private List<MallStoreRecieverAddress> list;
    private String type;
    private String storeCode;
    private int storehouseCode;

    private CommonProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.order_select_deliver_address);
        ActivityManager.getInstance().pushOneActivity(this);
        init();
        initView();
    }

    private void init() {
        main = this;
        type = getIntent().getStringExtra("type");
        storeCode = getIntent().getStringExtra("storeCode");
        storehouseCode = getIntent().getIntExtra("storehouseCode", 1);
        progressDialog = new CommonProgressDialog(main);
        list = new ArrayList<MallStoreRecieverAddress>();
        initCustomActionBar();
    }

    private void initView() {
        listView = (ListView) findViewById(R.id.listview_address);
        adapter = new SelectDeliverAddrAdapter(main, list, new OnCallBackListener() {
            @Override
            public void onCallBackListener(int position) {
                /*for (int i = 0; i < list.size(); i++) {
                    list.get(i).setIsCheck(false);
                }
                list.get(position).setIsCheck(true);*/
              //  adapter.notifyDataSetChanged();
                MallStoreRecieverAddress info = list.get(position);
                StringBuilder sb = new StringBuilder("");
          /*      sb.append(info.getProvinceName());
                sb.append(info.getCityName());
                sb.append(info.getCountyName());
                sb.append(info.getTownName());
                sb.append(info.getVillageName());*/
                sb.append(info.getAddress());

                Intent it = new Intent();
                it.putExtra("address", sb.toString());
                it.putExtra("addressId",info.getAddressId());
                setResult(101, it);
                finish();
            }
        });
        listView.setAdapter(adapter);
        getData();

    }

    private void getData() {

        String token = ClientStateManager.getLoginToken(main);
        if (progressDialog != null) {
            progressDialog.show();
        }

        DeliveryApi.queryReceiveAddressByStoreCode(token, storeCode, orderDetailAddressHandler);
    }

    private void initCustomActionBar() {
        new CommonActionBar(getActionBar(), new IActionBarListener() {

            @Override
            public void setTitle(TextView v) {
                // TODO Auto-generated method stub
                if (InventoryTabActivity.DELIVERY_MANAGEMENT.equals(type)) {
                    v.setText(getResources().getString(R.string.text_select_deliver_address));
                } else {
                    v.setText(getResources().getString(R.string.text_select_receive_address));
                }
            }

            @Override
            public void onBtnRight(View v) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onBtnLeft(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });
    }


    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    AsyncHttpResponseHandler orderDetailAddressHandler = new TextHttpResponseHandler(
            HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                              String responseString) {
            LogUtils.d(TAG, "orderDetailAddressHandler result = " + responseString);

            if (progressDialog != null)
                progressDialog.dismiss();
            try {
                ResultMallStoreRecieverAddress addressInfo = JSON.parseObject(responseString,
                        ResultMallStoreRecieverAddress.class);
                if (addressInfo.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    setData(addressInfo);
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

    private void setData(ResultMallStoreRecieverAddress result) {
        if (result == null || result.getAddressList() == null || result.getAddressList().size() < 1) {
            PublicUtil.showToastErrorData(main);
            return;
        }
        List<MallStoreRecieverAddress> lis = result.getAddressList();
        if (lis.size() > 0) {
            list.addAll(lis);
            adapter.notifyDataSetChanged();
        } else {
            PublicUtil.showToastErrorData(main);
        }
    }

    public static void actionStart(Context context, String type, String storeCode, int storehouseCode) {
        Intent intent = new Intent(context, OrderSelectDeliveryAddrActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("storeCode", storeCode);
        intent.putExtra("storehouseCode", storehouseCode);
        context.startActivity(intent);
    }


    class SelectDeliverAddrAdapter extends BaseAdapter {

        LayoutInflater mInflater;

        private List<MallStoreRecieverAddress> list;
        Context context;
        OnCallBackListener onCallBackListener;

        public SelectDeliverAddrAdapter(Context context, List<MallStoreRecieverAddress> data, OnCallBackListener onCallBackListener) {
            this.context = context;
            this.mInflater = LayoutInflater.from(context);
            this.list = data;
            this.onCallBackListener = onCallBackListener;
        }

        public void setList(List<MallStoreRecieverAddress> list) {
            this.list = list;
        }


        @Override
        public int getCount() {

            // TODO Auto-generated method stub、
            return list.size();
        }

        @Override
        public Object getItem(int position) {

            // TODO Auto-generated method stub
            return position;
        }


        @Override
        public long getItemId(int position) {

            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.order_storehouse_select, null);
                holder.layout_storehouse = (LinearLayout) convertView.findViewById(R.id.layout_storehouse);
              //  holder.cb_select = (CheckBox) convertView.findViewById(R.id.cb_select);
                holder.txt_address = (TextView) convertView.findViewById(R.id.txt_address);
                holder.txt_name = (TextView) convertView.findViewById(R.id.txt_name);
                holder.txt_phone = (TextView) convertView.findViewById(R.id.txt_phone);
                holder.txt_commenName_name =(TextView) convertView.findViewById(R.id.txt_commenName_name);
                holder.line_dotted = convertView.findViewById(R.id.line_dotted);
                holder.line_silde = convertView.findViewById(R.id.line_silde);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            MallStoreRecieverAddress info = list.get(position);

            if(OrderSelectDeliveryAddrActivity.DELIVERY_ADDRESS.equals(type)){
                holder.txt_commenName_name.setText(getResources().getString(R.string.text_deliver_person_tip));
            }else{
                holder.txt_commenName_name.setText(getResources().getString(R.string.text_receive_person_tip));
            }


            holder.txt_name.setText(info.getReceiverName());
            holder.txt_phone.setText(info.getReceiverPhone());

            StringBuilder sb = new StringBuilder("");
            sb.append(info.getProvinceName());
            sb.append(info.getCityName());
            sb.append(info.getCountyName());
            sb.append(info.getTownName());
            sb.append(info.getVillageName());
            sb.append(info.getAddress());
            holder.txt_address.setText(sb.toString());

         //   holder.cb_select.setChecked(info.isCheck());

          /*  int addCode = Integer.parseInt(info.getId());
            if( storehouseCode == addCode){
                holder.cb_select.setChecked(true);
            }*/

            holder.layout_storehouse.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCallBackListener.onCallBackListener(position);
                }
            });

            int index = position;
            if (index == list.size() - 1) {
                holder.line_silde.setVisibility(View.VISIBLE);
                holder.line_dotted.setVisibility(View.GONE);
            } else {
                holder.line_silde.setVisibility(View.GONE);
                holder.line_dotted.setVisibility(View.VISIBLE);
            }


            return convertView;
        }

        final class ViewHolder {
            LinearLayout layout_storehouse;
            CheckBox cb_select;
            TextView txt_name;
            TextView txt_phone;
            TextView txt_address;
            View line_dotted;
            View line_silde;
            TextView txt_commenName_name;
        }

    }

    public interface OnCallBackListener {
        void onCallBackListener(int position);
    }

}



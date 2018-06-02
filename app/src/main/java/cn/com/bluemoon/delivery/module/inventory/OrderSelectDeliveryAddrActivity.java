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
package cn.com.bluemoon.delivery.module.inventory;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;
import org.apache.http.protocol.HTTP;

import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.clothing.collect.ActivityInfo;
import cn.com.bluemoon.delivery.app.api.model.storage.MallStoreRecieverAddress;
import cn.com.bluemoon.delivery.app.api.model.storage.ResultMallStoreRecieverAddress;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.BasePullToRefreshListViewActivity;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.delivery.utils.manager.ActivityManager;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;

public class OrderSelectDeliveryAddrActivity extends BasePullToRefreshListViewActivity {

    public static String RECEIVE_ADDRESS = "RECEIVE_ADDRESS";
    public static String DELIVERY_ADDRESS = "DELIVERY_ADDRESS";

    private String type;
    private String storeCode;

    @Override
    protected void onBeforeSetContentLayout() {
        type = getIntent().getStringExtra("type");
        storeCode = getIntent().getStringExtra("storeCode");
    }

    @Override
    protected PullToRefreshBase.Mode getMode() {
        return PullToRefreshBase.Mode.PULL_FROM_START;
    }

    @Override
    protected void invokeGetDataDeliveryApi(int requestCode) {
        DeliveryApi.queryReceiveAddressByStoreCode(ClientStateManager.getLoginToken(), storeCode,
                getNewHandler(requestCode,ResultMallStoreRecieverAddress.class));
    }

    @Override
    protected void invokeGetMoreDeliveryApi(int requestCode) {

    }

    @Override
    protected String getTitleString() {
        if (InventoryTabActivity.DELIVERY_MANAGEMENT.equals(type)) {
            return getString(R.string.text_select_deliver_address);
        } else {
            return getString(R.string.text_select_receive_address);
        }
    }

    public static void actionStart(Activity context,String type, String storeCode,int requestCode) {
        Intent intent = new Intent(context, OrderSelectDeliveryAddrActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("storeCode", storeCode);
        context.startActivityForResult(intent,requestCode);
    }

    @Override
    protected BaseListAdapter getNewAdapter() {
        return new SelectDeliverAddrAdapter(this,this);
    }

    @Override
    protected List getGetMoreList(ResultBase result) {
        return null;
    }

    @Override
    protected List getGetDataList(ResultBase result) {
        return ((ResultMallStoreRecieverAddress)result).getAddressList();
    }

    @Override
    public void onItemClick(Object item, View view, int position) {
        MallStoreRecieverAddress info = (MallStoreRecieverAddress)item;
        Intent it = new Intent();
        it.putExtra("address", info.getAddress());
        it.putExtra("addressId",info.getAddressId());
        setResult(101, it);
        finish();
    }


    class SelectDeliverAddrAdapter extends BaseListAdapter<MallStoreRecieverAddress> {


        public SelectDeliverAddrAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.order_storehouse_select;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            MallStoreRecieverAddress info = list.get(position);
            if(info==null) return;

            TextView txtAddress = getViewById(R.id.txt_address);
            TextView txtName = getViewById(R.id.txt_name);
            TextView txtPhone = getViewById(R.id.txt_phone);
            TextView txtCommenNameName =getViewById(R.id.txt_commenName_name);

            if(DELIVERY_ADDRESS.equals(type)){
                txtCommenNameName.setText(R.string.text_deliver_person_tip);
            }else{
                txtCommenNameName.setText(R.string.text_receive_person_tip);
            }

            txtName.setText(info.getReceiverName());
            txtPhone.setText(info.getReceiverPhone());

            String sb = StringUtil.getStringParamsByFormat("", info.getProvinceName(), info.getCityName(),
                    info.getCountyName(), info.getTownName(), info.getVillageName(), info.getAddress());
            txtAddress.setText(sb);

            setClickEvent(isNew,position,convertView);
        }
    }
}



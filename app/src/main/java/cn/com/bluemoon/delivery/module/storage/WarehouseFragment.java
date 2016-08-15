package cn.com.bluemoon.delivery.module.storage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
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
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.storage.ResultStock;
import cn.com.bluemoon.delivery.app.api.model.storage.ResultStore;
import cn.com.bluemoon.delivery.app.api.model.storage.Store;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.module.base.BaseFragment;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.interf.IActionBarListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib.view.CommonEmptyView;
import cn.com.bluemoon.lib.view.CommonProgressDialog;


public class WarehouseFragment extends BaseFragment {

    @Bind(R.id.listview_main)
    PullToRefreshListView listView;
    private StoreAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tab_strage;
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.tab_bottom_my_warehouse_text);
    }

    @Override
    public void initView() {
        FragmentActivity main = getActivity();
        listView.setMode(PullToRefreshBase.Mode.DISABLED);
        PublicUtil.setEmptyView(listView, getTitleString(), new CommonEmptyView.EmptyListener() {
            @Override
            public void onRefresh() {
                initData();
            }
        });
        adapter = new StoreAdapter(main);
    }


    @Override
    public void initData() {
        showWaitDialog();
        DeliveryApi.queryStoresBycharger(getToken(), getNewHandler(1, ResultStore.class));
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        ResultStore storeResult = (ResultStore)result;
        setData(storeResult);
    }

    private void setData(ResultStore result) {
        if (result == null || result.getStoreList() == null || result.getStoreList().size() < 1) {
            adapter.setList(new ArrayList<Store>());
        } else {
            ResultStore item = result;
            adapter.setList(item.getStoreList());
        }
        listView.setAdapter(adapter);
    }


    @SuppressLint("InflateParams")
    class StoreAdapter extends BaseListAdapter<Store> {
        public StoreAdapter(Context context) {
            super(context, null);

        }

        @Override
        protected int getLayoutId() {
            return R.layout.store_processed_item;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            final Store store = list.get(position);
            TextView txtDetail = getViewById(R.id.txt_detail);
            TextView txtWarehouseId = getViewById(R.id.txt_storehouse_id);
            TextView txtChargePerson = getViewById(R.id.txt_charge_person);
            TextView txtWarehouseName = getViewById(R.id.txt_storehouse_name);
            TextView txtStoreAddress = getViewById(R.id.txt_store_address_name);
            TextView txtReceiver = getViewById(R.id.txt_receiver);


            txtWarehouseId.setText(store.getStoreCode());
            txtWarehouseName.setText(store.getStoreName());
            txtChargePerson.setText(
                    getString(R.string.text_store_charge_person,
                    store.getChargerName() + " " + store.getChargerPhone()));

            txtReceiver.setText(
                    getString(R.string.text_store_receive_person,
                    store.getReceiverName() + " " + store.getReceiverPhone()));

            txtStoreAddress.setText(store.getAddress());

            String addressStr = getString(R.string.text_store_default,
                    String.format("%s%s%s%s%s%s",
                            store.getProvinceName(),
                            store.getCityName(),
                            store.getCountyName(),
                            store.getTownName(),
                            store.getVillageName(),
                            store.getAddress())
            );

            SpannableString ss = new SpannableString(addressStr);
            ss.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.text_red)), 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.text_black_light)), 5, addressStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            txtStoreAddress.setText(ss);

            if (!store.isAllowedEditAddress) {
                txtDetail.setVisibility(View.GONE);
            } else {

                convertView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (store != null) {
                            try {
                                WarehouseAddressActivity.actionStart(WarehouseFragment.this, store.getStoreCode(), store.getStoreName(), store.getAddressId());
                            } catch (Exception e) {
                                longToast(e.getMessage());
                            }

                        }

                    }
                });
            }
        }

    }


    public void onPause() {
        super.onPause();
        hideWaitDialog();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            initData();
        }
    }
}

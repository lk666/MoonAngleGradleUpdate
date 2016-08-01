package cn.com.bluemoon.delivery.module.storage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.storage.ResultStore;
import cn.com.bluemoon.delivery.app.api.model.storage.Store;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.module.base.interf.IActionBarListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib.view.CommonProgressDialog;


public class WarehouseFragment extends Fragment {

    private String TAG = "WarehouseFragment";

    private StoreAdapter adapter;
    private FragmentActivity main;
    private CommonProgressDialog progressDialog;
    private PullToRefreshListView listView;

    private ResultStore item;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        initCustomActionBar();
        main = getActivity();


        View v = inflater.inflate(R.layout.fragment_tab_strage, container,
                false);

        listView = (PullToRefreshListView) v
                .findViewById(R.id.listview_main);
        listView.setMode(PullToRefreshBase.Mode.DISABLED);
        progressDialog = new CommonProgressDialog(main);
        adapter = new StoreAdapter(main);
        getItem();
        return v;
    }

    private void getItem() {
        String token = ClientStateManager.getLoginToken(main);
        if (progressDialog != null) {
            progressDialog.show();
        }

        DeliveryApi.queryStoresBycharger(token, warehouseHandler);
    }

    private void setData(ResultStore result) {
        if (result == null || result.getStoreList() == null || result.getStoreList().size() < 1) {
            adapter.setList(new ArrayList<Store>());
        } else {

            item = result;

            adapter.setList(item.getStoreList());
        }
        listView.setAdapter(adapter);

    }


    private void initCustomActionBar() {

        new CommonActionBar(getActivity().getActionBar(),
                new IActionBarListener() {

                    @Override
                    public void onBtnRight(View v) {
                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void onBtnLeft(View v) {
                        // TODO Auto-generated method stub
                        getActivity().finish();
                    }

                    @Override
                    public void setTitle(TextView v) {
                        // TODO Auto-generated method stub
                        v.setText(R.string.tab_bottom_my_warehouse_text);
                    }
                });

    }

    @SuppressLint("InflateParams")
    class StoreAdapter extends BaseAdapter {

        private Context context;
        private List<Store> lists;

        public StoreAdapter(Context context) {
            this.context = context;
        }

        public void setList(List<Store> list) {
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

            convertView = inflate.inflate(R.layout.store_processed_item, null);

            TextView txtDetail = (TextView) convertView.findViewById(R.id.txt_detail);
            TextView txtWarehouseId = (TextView) convertView
                    .findViewById(R.id.txt_storehouse_id);

            TextView txtChargePerson = (TextView) convertView.findViewById(R.id.txt_charge_person);

            TextView txtWarehouseName = (TextView) convertView
                    .findViewById(R.id.txt_storehouse_name);

            TextView txtStoreAddress = (TextView) convertView
                    .findViewById(R.id.txt_store_address_name);

            TextView txtReceiver = (TextView) convertView
                    .findViewById(R.id.txt_receiver);


            txtWarehouseId.setText(lists.get(position).getStoreCode());
            txtWarehouseName.setText(lists.get(position).getStoreName());
            txtChargePerson.setText(String.format(
                    getString(R.string.text_store_charge_person),
                    lists.get(position).getChargerName() + " " + lists.get(position).getChargerPhone()));

            txtReceiver.setText(String.format(
                    getString(R.string.text_store_receive_person),
                    lists.get(position).getReceiverName() + " " + lists.get(position).getReceiverPhone()));

            txtStoreAddress.setText(lists.get(position).getAddress());

            String addressStr = String.format(getString(R.string.text_store_default),
                    String.format("%s%s%s%s%s%s",
                            lists.get(position).getProvinceName(),
                            lists.get(position).getCityName(),
                            lists.get(position).getCountyName(),
                            lists.get(position).getTownName(),
                            lists.get(position).getVillageName(),
                            lists.get(position).getAddress())
            );

            SpannableString ss = new SpannableString(addressStr);
            ss.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.text_red)), 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.text_black_light)), 5, addressStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            txtStoreAddress.setText(ss);

            convertView.setTag(lists.get(position));

            if (!lists.get(position).isAllowedEditAddress) {
                txtDetail.setVisibility(View.GONE);
            } else {

                convertView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        Store store = (Store) v.getTag();
                        if (null != store) {
                            WarehouseAddressActivity.actionStart(WarehouseFragment.this, store.getStoreCode(), store.getStoreName(), store.getAddressId());
                        }

                    }
                });
            }
            return convertView;
        }

    }

    AsyncHttpResponseHandler warehouseHandler = new TextHttpResponseHandler(
            HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                              String responseString) {
            LogUtils.d(TAG, "warehouseHandler result = " + responseString);
            if (progressDialog != null)
                progressDialog.dismiss();
            try {
                ResultStore storeResult = JSON.parseObject(responseString,
                        ResultStore.class);
                if (storeResult.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    setData(storeResult);
                } else {
                    PublicUtil.showErrorMsg(main, storeResult);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == main.RESULT_OK) {
            getItem();
        }
    }
}

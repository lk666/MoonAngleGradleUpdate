package cn.com.bluemoon.delivery.module.storage;

import android.annotation.SuppressLint;
import android.content.Context;
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
import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.storage.ResultStock;
import cn.com.bluemoon.delivery.app.api.model.storage.Stock;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.module.base.interf.IActionBarListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib.view.CommonProgressDialog;

public class StockFragment extends Fragment {
    private String TAG = "StockFragment";

    private StockAdapter adapter;
    private FragmentActivity main;
    private CommonProgressDialog progressDialog;
    private PullToRefreshListView listView;
    private ResultStock item;


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
        adapter = new StockAdapter(main);
        getItem();
        return v;
    }

    private void getItem() {
        String token = ClientStateManager.getLoginToken(main);
        if ( progressDialog != null) {
            progressDialog.show();
        }

            DeliveryApi.queryStockSummary(token, stockHandler);
    }

    private void setData(ResultStock result) {
        if (result == null || result.getStockList()==null || result.getStockList().size() < 1) {
            adapter.setList(new ArrayList<Stock>());
        }else {

            item = result;

            adapter.setList(item.getStockList());
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
                       v.setText(R.string.tab_bottom_my_stock_text);
                    }
                });

    }

    @SuppressLint("InflateParams")
    class StockAdapter extends BaseAdapter {

        private Context context;
        private List<Stock> lists;

        public StockAdapter(Context context) {
            this.context = context;
        }

        public void setList(List<Stock> list) {
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

            convertView = inflate.inflate(R.layout.stock_processed_item, null);

            TextView txtWarehouseId = (TextView) convertView
                    .findViewById(R.id.txt_storehouse_id);

            TextView txtWarehouseName = (TextView) convertView
                    .findViewById(R.id.txt_storehouse_name);

            TextView txtTotalMoney = (TextView) convertView
                    .findViewById(R.id.txt_total_money);

            TextView txtBoxesCount = (TextView) convertView
                    .findViewById(R.id.txt_total_boxes);

            txtWarehouseId.setText(lists.get(position).getStoreCode());
            txtWarehouseName.setText(lists.get(position).getStoreName());
            txtTotalMoney.setText(String.format(
                    getString(R.string.text_stock_total_money),
                    StringUtil.formatPriceByFen(lists.get(position).getTotalPrice())));

            txtBoxesCount.setText(String.format(getString(R.string.text_stock_total_boxes),
                            StringUtil.formatBoxesNum(lists.get(position).getTotalCase())));

            convertView.setTag(lists.get(position));
            convertView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    try {
                        // TODO Auto-generated method stub
                        Stock stock = (Stock) v.getTag();
                        if (null != stock) {
                            StockDetailActivity.actionStart(main, stock.getStoreCode(), stock.getStoreName());
                        }
                    }catch (Exception ex){
                        LogUtils.e(ex.getMessage());
                    }

                }
            });
            return convertView;
        }

    }

    AsyncHttpResponseHandler stockHandler = new TextHttpResponseHandler(
            HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                              String responseString) {
            LogUtils.d(TAG, "stockHandler result = " + responseString);
            if (progressDialog != null)
                progressDialog.dismiss();
            try {
                ResultStock stockResult = JSON.parseObject(responseString,
                        ResultStock.class);
                if (stockResult.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    setData(stockResult);
                } else {
                    PublicUtil.showErrorMsg(main, stockResult);
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

}

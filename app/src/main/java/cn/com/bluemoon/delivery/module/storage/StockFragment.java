package cn.com.bluemoon.delivery.module.storage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.storage.ResultStock;
import cn.com.bluemoon.delivery.app.api.model.storage.Stock;
import cn.com.bluemoon.delivery.module.base.BaseFragment;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib.view.CommonEmptyView;

public class StockFragment extends BaseFragment {

    @Bind(R.id.listview_main)
    PullToRefreshListView listView;
    private StockAdapter adapter;
    private FragmentActivity main;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tab_strage;
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.tab_bottom_my_stock_text);
    }

    @Override
    public void initView() {
        main = getActivity();
        listView.setMode(PullToRefreshBase.Mode.DISABLED);
        PublicUtil.setEmptyView(listView, getTitleString(), new CommonEmptyView.EmptyListener() {
            @Override
            public void onRefresh() {
                initData();
            }
        });
        adapter = new StockAdapter(main);
    }


    @Override
    public void initData() {
        showWaitDialog();
        DeliveryApi.queryStockSummary(getToken(), getNewHandler(1,ResultStock.class));
    }

    private void setData(ResultStock result) {
        if (result == null || result.getStockList() == null || result.getStockList().size() < 1) {
            adapter.setList(new ArrayList<Stock>());
        } else {
            ResultStock item = result;
            adapter.setList(item.getStockList());
        }
        listView.setAdapter(adapter);
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        ResultStock stockResult = (ResultStock)result;
        setData(stockResult);
    }

    @SuppressLint("InflateParams")
    class StockAdapter extends BaseListAdapter<Stock> {

        public StockAdapter(Context context) {
            super(context, null);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.stock_processed_item;
        }


        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            final Stock stock = list.get(position);
            TextView txtWarehouseId = getViewById(R.id.txt_storehouse_id);
            TextView txtWarehouseName = getViewById(R.id.txt_storehouse_name);
            TextView txtTotalMoney = getViewById(R.id.txt_total_money);
            TextView txtBoxesCount = getViewById(R.id.txt_total_boxes);
            txtWarehouseId.setText(stock.getStoreCode());
            txtWarehouseName.setText(stock.getStoreName());
            txtTotalMoney.setText(getString(R.string.text_stock_total_money, StringUtil.formatPriceByFen(stock.getTotalPrice())));

            txtBoxesCount.setText(String.format(getString(R.string.text_stock_total_boxes),
                    StringUtil.formatBoxesNum(stock.getTotalCase())));

            convertView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    try {
                        if (stock != null) {
                            StockDetailActivity.actionStart(main, stock.getStoreCode(), stock.getStoreName());
                        }
                    } catch (Exception ex) {
                        LogUtils.e(ex.getMessage());
                    }

                }
            });
        }

    }

}

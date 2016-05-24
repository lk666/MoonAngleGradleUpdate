package cn.com.bluemoon.delivery.storage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;

import java.util.ArrayList;
import java.util.List;

import cn.com.bluemoon.delivery.ClientStateManager;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.storage.ProductDetail;
import cn.com.bluemoon.delivery.app.api.model.storage.ResultProductDetail;
import cn.com.bluemoon.delivery.async.listener.IActionBarListener;
import cn.com.bluemoon.delivery.entity.ProductType;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.ui.TabSelector;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib.view.CommonProgressDialog;

public class StockDetailActivity extends Activity {

    private String TAG = "StockDetailActivity";

    private StockDetailAdapter adapter;
    private StockDetailActivity main;
    private CommonProgressDialog progressDialog;
    private PullToRefreshListView listView;
    private ResultProductDetail item;
    private ProductType currentType = ProductType.NORMAL;

    private TextView txtStoreId;
    private TextView txtCategoryCount;
    private TextView txtTotalMoney;
    private TextView txtTotalBoxes;
    private TabSelector tabSelector;


    private String storeId;
    private String storeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);
        main = this;
        storeId = getIntent().getStringExtra("storeId");
        storeName = getIntent().getStringExtra("storeName");
        if (StringUtil.isEmpty(storeId) || StringUtil.isEmpty(storeName)) {
            finish();
        }
        initCustomActionBar();


        currentType = ProductType.NORMAL;

        listView = (PullToRefreshListView) findViewById(R.id.listview_main);
        txtStoreId = (TextView) findViewById(R.id.txt_store_id);
        txtCategoryCount = (TextView) findViewById(R.id.txt_category_count);
        txtTotalBoxes = (TextView) findViewById(R.id.txt_total_boxes);
        txtTotalMoney = (TextView) findViewById(R.id.txt_total_money);
        tabSelector = (TabSelector)findViewById(R.id.linear_tab_selector);
        tabSelector.setOnClickListener(listener);
        txtStoreId.setText(String.format("%s-%s", storeId, storeName));
        listView.setMode(PullToRefreshBase.Mode.DISABLED);
        progressDialog = new CommonProgressDialog(main);
        adapter = new StockDetailAdapter(main);
        getItem();

    }


    private void getItem() {
        String token = ClientStateManager.getLoginToken(main);
        if (progressDialog != null) {
            progressDialog.show();
        }

        DeliveryApi.queryStockDetail(token, storeId, currentType, productDetailHandler);
    }


    private void setData(ResultProductDetail result) {
        if (result == null || result.getProductDetailList() == null || result.getProductDetailList().size() < 1) {
            txtCategoryCount.setText("0");
            txtTotalMoney.setText(
                    getString(R.string.order_money_sign) +
                            StringUtil.formatPriceByFen(0));
            if (currentType.equals(ProductType.BAD)) {
                txtTotalBoxes.setText(String.format(getString(R.string.order_diff_product_count), 0));

            } else {
                txtTotalBoxes.setText(String.format(getString(R.string.order_boxes_count),
                        StringUtil.formatBoxesNum(0)) +
                        String.format(getString(R.string.order_product_count),
                               0));
            }
            adapter.setList(new ArrayList<ProductDetail>());
            adapter.notifyDataSetChanged();
            //PublicUtil.showToastErrorData(main);
            return;
        }

        item = result;
        txtCategoryCount.setText(String.valueOf(item.getTotalCategory()));
        txtTotalMoney.setText(
                getString(R.string.order_money_sign) +
                        StringUtil.formatPriceByFen(item.getTotalPrice()));

        if (currentType.equals(ProductType.BAD)) {
            txtTotalBoxes.setText(String.format(getString(R.string.order_diff_product_count), item.getTotalNum()));

        } else {
            txtTotalBoxes.setText(String.format(getString(R.string.order_boxes_count),
                            StringUtil.formatBoxesNum(item.getTotalCase())) +
                            String.format(getString(R.string.order_product_count),
                                    item.getTotalNum())
            );
        }


        adapter.setList(item.getProductDetailList());
        listView.setAdapter(adapter);

    }


    TabSelector.CallBackListener listener = new TabSelector.CallBackListener () {
        @Override
        public void onClick(int currentTab) {
            switch (currentTab) {
                case 0 :
                    if (currentType.equals(ProductType.BAD)) {
                        currentType = ProductType.NORMAL;
                        getItem();
                    }
                    break;
                case 1:
                    if (currentType.equals(ProductType.NORMAL)) {
                        currentType = ProductType.BAD;
                        getItem();
                    }
                    break;
            }
        }
    };

    private void initCustomActionBar() {

        new CommonActionBar(main.getActionBar(),
                new IActionBarListener() {

                    @Override
                    public void onBtnRight(View v) {
                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void onBtnLeft(View v) {
                        // TODO Auto-generated method stub
                        main.finish();
                    }

                    @Override
                    public void setTitle(TextView v) {
                        // TODO Auto-generated method stub
                        v.setText(R.string.text_stock_detail);
                    }
                });

    }

    @SuppressLint("InflateParams")
    class StockDetailAdapter extends BaseAdapter {

        private Context context;
        private List<ProductDetail> lists;

        public StockDetailAdapter(Context context) {
            this.context = context;
        }

        public void setList(List<ProductDetail> list) {
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

            convertView = inflate.inflate(R.layout.stock_detail_item, null);

            TextView txtProductId = (TextView) convertView
                    .findViewById(R.id.txt_product_id);

            TextView txtProductName = (TextView) convertView
                    .findViewById(R.id.txt_product_name);

            TextView txtBadReason = (TextView) convertView
                    .findViewById(R.id.txt_bad_reason);

            TextView txtMoney = (TextView) convertView
                    .findViewById(R.id.txt_money);

            TextView txBoxes = (TextView) convertView
                    .findViewById(R.id.txt_boxes);

            View viewLine = convertView.findViewById(R.id.view_line);
            txtProductId.setText(lists.get(position).getProductNo());
            txtProductName.setText(lists.get(position).getProductName());
            txtMoney.setText(
                    getString(R.string.order_money_sign) +
                            StringUtil.formatPriceByFen(lists.get(position).getRealPrice()));

            if (currentType.equals(ProductType.BAD)) {
                txBoxes.setText(String.format(getString(R.string.order_diff_product_count), lists.get(position).getRealNum()));
                txtBadReason.setText(lists.get(position).getType());
            } else {
                txBoxes.setText(String.format(getString(R.string.order_boxes_count),
                                StringUtil.formatBoxesNum(lists.get(position).getRealCase())) +
                                String.format(getString(R.string.order_product_count),
                                        lists.get(position).getRealNum())
                );
            }

            if (position + 1 == lists.size()) {
                viewLine.setVisibility(View.INVISIBLE);
            }

            return convertView;
        }

    }

    AsyncHttpResponseHandler productDetailHandler = new TextHttpResponseHandler(
            HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                              String responseString) {
            LogUtils.d(TAG, "productDetailHandler result = " + responseString);
            if (progressDialog != null)
                progressDialog.dismiss();
            try {
                ResultProductDetail productDetailResult = JSON.parseObject(responseString,
                        ResultProductDetail.class);
                if (productDetailResult.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    setData(productDetailResult);
                } else {
                    setData(null);
                    PublicUtil.showErrorMsg(main, productDetailResult);
                }
            } catch (Exception e) {
                setData(null);
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
            setData(null);
            PublicUtil.showToastServerOvertime();
        }
    };

    public static void actionStart(Context context, String storeId, String storeName) {
        Intent intent = new Intent(context, StockDetailActivity.class);
        intent.putExtra("storeId", storeId);
        intent.putExtra("storeName", storeName);
        context.startActivity(intent);
    }

}

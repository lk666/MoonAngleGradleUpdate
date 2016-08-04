package cn.com.bluemoon.delivery.module.storage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;

import butterknife.Bind;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.storage.ProductDetail;
import cn.com.bluemoon.delivery.app.api.model.storage.ResultProductDetail;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.entity.ProductType;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.ui.TabSelector;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib.view.CommonEmptyView;

public class StockDetailActivity extends BaseActivity {

    @Bind(R.id.txt_store_id)
    TextView txtStoreId;
    @Bind(R.id.linear_tab_selector)
    TabSelector tabSelector;
    @Bind(R.id.txt_category_count)
    TextView txtCategoryCount;
    @Bind(R.id.txt_total_money)
    TextView txtTotalMoney;
    @Bind(R.id.txt_total_boxes)
    TextView txtTotalBoxes;
    @Bind(R.id.listview_main)
    PullToRefreshListView listView;

    private StockDetailAdapter adapter;
    private ResultProductDetail item;
    private ProductType currentType = ProductType.NORMAL;


    private String storeId;
    private String storeName;

    TabSelector.CallBackListener listener = new TabSelector.CallBackListener() {
        @Override
        public void onClick(int currentTab) {
            switch (currentTab) {
                case 0:
                    if (currentType.equals(ProductType.BAD)) {
                        currentType = ProductType.NORMAL;
                        initData();
                    }
                    break;
                case 1:
                    if (currentType.equals(ProductType.NORMAL)) {
                        currentType = ProductType.BAD;
                        initData();
                    }
                    break;
            }
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_stock;
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.text_stock_detail);
    }

    @Override
    public void initView() {
        storeId = getIntent().getStringExtra("storeId");
        storeName = getIntent().getStringExtra("storeName");
        if (StringUtil.isEmpty(storeId) || StringUtil.isEmpty(storeName)) {
            finish();
        }
        currentType = ProductType.NORMAL;
        tabSelector.setOnClickListener(listener);
        txtStoreId.setText(String.format("%s-%s", storeId, storeName));
        listView.setMode(PullToRefreshBase.Mode.DISABLED);
        PublicUtil.setEmptyView(listView, getString(R.string.text_stock_detail), new CommonEmptyView.EmptyListener() {
            @Override
            public void onRefresh() {
                initData();
            }
        });
        adapter = new StockDetailAdapter(this);
    }

    @Override
    public void initData() {
        String token = ClientStateManager.getLoginToken(StockDetailActivity.this);
        showWaitDialog();
        DeliveryApi.queryStockDetail(token, storeId, currentType, getNewHandler(1,ResultProductDetail.class));
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        ResultProductDetail productDetailResult = (ResultProductDetail)result;
        setData(productDetailResult);
    }

    @Override
    public void onFailureResponse(int requestCode, Throwable t) {
        super.onFailureResponse(requestCode, t);
        setData(null);
    }

    @Override
    public void onSuccessException(int requestCode, Throwable t) {
        super.onSuccessException(requestCode, t);
        setData(null);
    }

    @Override
    public void onErrorResponse(int requestCode, ResultBase result) {
        super.onErrorResponse(requestCode, result);
        setData(null);
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

    @SuppressLint("InflateParams")
    class StockDetailAdapter extends BaseListAdapter<ProductDetail> {
        public StockDetailAdapter(Context context) {
            super(context, null);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.stock_detail_item;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            final ProductDetail product = list.get(position);
            TextView txtProductId = getViewById(R.id.txt_product_id);
            TextView txtProductName = getViewById(R.id.txt_product_name);
            TextView txtBadReason = getViewById(R.id.txt_bad_reason);
            TextView txtMoney = getViewById(R.id.txt_money);
            TextView txBoxes = getViewById(R.id.txt_boxes);
            View viewLine = getViewById(R.id.view_line);
            txtProductId.setText(product.getProductNo());
            txtProductName.setText(product.getProductName());
            txtMoney.setText(
                    getString(R.string.order_money_sign) +
                            StringUtil.formatPriceByFen(list.get(position).getRealPrice()));

            if (currentType.equals(ProductType.BAD)) {
                txBoxes.setText(String.format(getString(R.string.order_diff_product_count), product.getRealNum()));
                txtBadReason.setText(product.getType());
            } else {
                txBoxes.setText(String.format(getString(R.string.order_boxes_count),
                        StringUtil.formatBoxesNum(product.getRealCase())) +
                        String.format(getString(R.string.order_product_count),
                                product.getRealNum())
                );
            }

            if (position + 1 == list.size()) {
                viewLine.setVisibility(View.INVISIBLE);
            }
        }

    }

    public static void actionStart(Context context, String storeId, String storeName) {
        Intent intent = new Intent(context, StockDetailActivity.class);
        intent.putExtra("storeId", storeId);
        intent.putExtra("storeName", storeName);
        context.startActivity(intent);
    }
}

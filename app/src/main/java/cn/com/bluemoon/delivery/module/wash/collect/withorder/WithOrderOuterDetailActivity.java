package cn.com.bluemoon.delivery.module.wash.collect.withorder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.protocol.HTTP;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.clothing.collect.ClothesInfo;
import cn.com.bluemoon.delivery.app.api.model.clothing.collect.OuterOrderDetail;
import cn.com.bluemoon.delivery.app.api.model.clothing.collect.OuterOrderReceive;
import cn.com.bluemoon.delivery.app.api.model.clothing.collect.ResultOuterOrderInfo;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.module.wash.collect.ClothesDetailActivity;
import cn.com.bluemoon.delivery.module.wash.collect.ClothesInfoAdapter;
import cn.com.bluemoon.delivery.module.oldbase.BaseActionBarActivity;
import cn.com.bluemoon.delivery.ui.NoScrollListView;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.ViewHolder;

/**
 * 服务订单详情页面
 * Created by lk on 2016/6/27.
 */
public class WithOrderOuterDetailActivity extends BaseActionBarActivity implements
        OnListItemClickListener {

    /**
     * 洗衣服务订单号
     */
    public final static String EXTRA_OUTERCODE = "EXTRA_OUTERCODE";

    /**
     * 洗衣服务订单号
     */
    private String outerCode;

    @BindView(R.id.tv_number)
    TextView tvNumber;
    @BindView(R.id.tv_customer_name)
    TextView tvCustomerName;
    @BindView(R.id.tv_customer_phone)
    TextView tvCustomerPhone;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.tv_pay_total)
    TextView tvPayTotal;
    @BindView(R.id.tv_receivable_count)
    TextView tvReceivableCount;
    @BindView(R.id.lv_order_detail)
    NoScrollListView lvOrderDetail;
    @BindView(R.id.lv_order_receive)
    NoScrollListView lvOrderReceive;
    @BindView(R.id.main)
    ScrollView main;

    private OrderDetailAdapter orderDetailAdapter;
    private CollectAdapter collectAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_order_detail);
        ButterKnife.bind(this);

        setIntentData();
        initView();
        getData();
    }

    @Override
    protected int getActionBarTitleRes() {
        return R.string.title_outer_detail;
    }

    private void initView() {
        orderDetailAdapter = new OrderDetailAdapter(this);
        lvOrderDetail.setAdapter(orderDetailAdapter);

        collectAdapter = new CollectAdapter(this, this);
        lvOrderReceive.setAdapter(collectAdapter);

    }

    private void getData() {
        showProgressDialog();
        DeliveryApi.getOuterOrderInfo(outerCode, ClientStateManager.getLoginToken(this),
                startCollectInfoHandler);

    }

    /**
     * 获取订单详情数据返回
     */
    AsyncHttpResponseHandler startCollectInfoHandler = new TextHttpResponseHandler(HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            LogUtils.d(getDefaultTag(), "startCollectInfo result = " + responseString);
            dismissProgressDialog();
            try {
                ResultOuterOrderInfo result = JSON.parseObject(responseString,
                        ResultOuterOrderInfo.class);
                if (result.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    setData(result);
                } else {
                    PublicUtil.showErrorMsg(WithOrderOuterDetailActivity.this, result);
                }
            } catch (Exception e) {
                LogUtils.e(getDefaultTag(), e.getMessage());
                PublicUtil.showToastServerBusy();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers,
                              String responseString, Throwable throwable) {
            LogUtils.e(getDefaultTag(), throwable.getMessage());
            dismissProgressDialog();
            PublicUtil.showToastServerOvertime();
        }
    };

    @SuppressLint("DefaultLocale")
    private void setData(ResultOuterOrderInfo result) {

        tvNumber.setText(result.getOuterCode());
        tvCustomerName.setText(result.getReceiveName());

        tvCustomerPhone.setText(result.getReceivePhone());
        tvCustomerPhone.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        tvCustomerPhone.getPaint().setAntiAlias(true);

        tvAddress.setText(result.getFullAddress());

        tvPayTotal.setText(String.format("%.2f", (result.getPayTotal() / 100.0)));
        tvReceivableCount.setText(String.valueOf(result.getReceivableCount()));

        collectAdapter.setList(result.getOrderReceive());
        collectAdapter.notifyDataSetChanged();

        orderDetailAdapter.setList(result.getOrderDetail());
        orderDetailAdapter.notifyDataSetChanged();

        main.postDelayed(new Runnable() {
            @Override
            public void run() {
                main.scrollTo(0, 0);
            }
        }, 100);
    }

    private void setIntentData() {
        outerCode = getIntent().getStringExtra(EXTRA_OUTERCODE);
        if (outerCode == null) {
            outerCode = "";
        }
    }

    /**
     * 衣物类型Adapter
     */
    class OrderDetailAdapter extends BaseListAdapter<OuterOrderDetail> {
        public OrderDetailAdapter(Context context) {
            super(context, null);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.item_with_order_outer_detail;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            final OuterOrderDetail item = (OuterOrderDetail) getItem(position);
            if (item == null) {
                return;
            }

            TextView tvTypeName = ViewHolder.get(convertView, R.id.tv_type_name);
            TextView tvCount = ViewHolder.get(convertView, R.id.tv_count);

            tvTypeName.setText(item.getTypeName());

            tvCount.setText(String.format("x%s", item.getCount()));
        }
    }

    /**
     * 收衣单列表Adapter
     */
    public class CollectAdapter extends BaseListAdapter<OuterOrderReceive> {
        public CollectAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.item_with_order_outer_receive;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            final OuterOrderReceive item = (OuterOrderReceive) getItem(position);
            if (item == null) {
                return;
            }

            TextView tvCount = ViewHolder.get(convertView, R.id.tv_count);
            TextView tvCollectCode = ViewHolder.get(convertView, R.id.tv_collect_code);
            NoScrollListView lvOrderReceiveClothes = ViewHolder.get(convertView, R.id
                    .lv_order_receive);
            TextView tvUrgent = ViewHolder.get(convertView, R.id.tv_urgent);
            TextView tvBrcode = ViewHolder.get(convertView, R.id.tv_brcode);

            tvCount.setText(String.format(getString(R.string
                    .with_order_collect_order_receive_count_num), item.getActualCount()));
            tvCollectCode.setText(String.format(getString(R.string
                    .with_order_collect_collect_number_text_num), item.getCollectCode()));

            if (item.getIsUrgent() == 1) {
                tvUrgent.setVisibility(View.VISIBLE);
            } else {
                tvUrgent.setVisibility(View.GONE);
            }

            String brcode = item.getCollectBrcode();
            if (TextUtils.isEmpty(brcode)) {
                tvBrcode.setText(String.format("%s%s", getString(R.string.clothing_detail_brcode),
                        getString(R.string.text_empty)));
            } else {
                tvBrcode.setText(String.format(getString(R.string.clothing_detail_brcode_num),
                        brcode));
            }

            if (isNew) {
                ClothesInfoAdapter newAdapter = new ClothesInfoAdapter(context,
                        WithOrderOuterDetailActivity.this);
                lvOrderReceiveClothes.setAdapter(newAdapter);
            }

            ClothesInfoAdapter adapter = (ClothesInfoAdapter) lvOrderReceiveClothes.getAdapter();
            adapter.setList(item.getClothesInfo());
            adapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onItemClick(Object item, View view, int position) {
        // 点击衣物项
        if (item instanceof ClothesInfo) {
            Intent i = new Intent(WithOrderOuterDetailActivity.this, ClothesDetailActivity.class);
            i.putExtra(ClothesDetailActivity.EXTRA_CLOTHES_CODE, ((ClothesInfo) item)
                    .getClothesCode());
            startActivity(i);
        }
    }
}

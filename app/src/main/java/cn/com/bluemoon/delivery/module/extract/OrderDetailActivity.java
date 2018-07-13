/**
 * Description:
 * Copyright: Copyright (c) 2016
 * Company: BLUE MOON
 *
 * @author: Eric Liang
 * @version 1.0
 * @date: 2016/1/26
 */
package cn.com.bluemoon.delivery.module.extract;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.protocol.HTTP;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.other.Product;
import cn.com.bluemoon.delivery.app.api.model.other.ResultOrderInfoPickup;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.module.base.interf.IActionBarListener;
import cn.com.bluemoon.delivery.module.order.OrdersUtils;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.ui.LinearLayoutForListView;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DialogUtil;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.delivery.utils.manager.ActivityManager;
import cn.com.bluemoon.lib.view.CommonAlertDialog;
import cn.com.bluemoon.lib.view.CommonProgressDialog;

public class OrderDetailActivity extends Activity implements OnClickListener, OnListItemClickListener {
    private String TAG = "OrderDetailActivity";
    private View popStart;
    private Button btnSign;
    private LinearLayout layoutStorehouse;
    private OrderDetailActivity mContext;
    private ResultOrderInfoPickup pickupInfo;
    private CommonProgressDialog progressDialog;
    private String signType;
    private boolean lock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extract_order_detail);
        ActivityManager.getInstance().pushOneActivity(this);
        mContext = this;
        initCustomActionBar();
        progressDialog = new CommonProgressDialog(mContext);
        progressDialog.setCancelable(false);
        popStart = findViewById(R.id.view_pop_start);
        LinearLayoutForListView listviewProduct = (LinearLayoutForListView) findViewById(R.id
                .listview_product);
        btnSign = (Button) findViewById(R.id.btn_sign);
        TextView tvOrderNumber = (TextView) findViewById(R.id.tv_order_number);
        TextView tvStorehouse = (TextView) findViewById(R.id.tv_storehouse);
        TextView tvOrderUserName = (TextView) findViewById(R.id.tv_order_username);
        TextView tvOrderUserPhone = (TextView) findViewById(R.id.tv_order_userphone);
        TextView tvOrderPayTime = (TextView) findViewById(R.id.tv_order_pay_time);
        TextView tvTotalPrice = (TextView) findViewById(R.id.tv_total_price);
        layoutStorehouse = (LinearLayout) findViewById(R.id.layout_storehouse);
        btnSign.setOnClickListener(this);
        layoutStorehouse.setOnClickListener(this);
        pickupInfo = (ResultOrderInfoPickup) getIntent().getSerializableExtra("order");
        signType = getIntent().getStringExtra("signType");

        if (null == pickupInfo) {
            finish();
        } else {
            tvTotalPrice.setText(String.format(
                    getString(R.string.extract_order_total_pay),
                    StringUtil.formatPrice(pickupInfo.getTotalPrice())));

            tvStorehouse.setText(OrdersUtils.getShString(pickupInfo));
            tvOrderNumber.setText(pickupInfo.getOrderId());
            tvOrderUserName.setText(pickupInfo.getNickName());
            tvOrderUserPhone.setText(pickupInfo.getMobilePhone());
            tvOrderPayTime.setText(pickupInfo.getOrderCreateTime());
            OrderProductAdapter productAdapter = new OrderProductAdapter(this, this);
            productAdapter.setList(pickupInfo.getProductList());
            listviewProduct.setAdapter(productAdapter);
        }
    }

    private void initCustomActionBar() {
        CommonActionBar mActionbar = new CommonActionBar(getActionBar(), new IActionBarListener() {

            @Override
            public void onBtnRight(View v) {

            }

            @Override
            public void onBtnLeft(View v) {
                CommonAlertDialog.Builder dialog = new CommonAlertDialog.Builder(
                        OrderDetailActivity.this);
                dialog.setMessage(getString(R.string.extract_cacle_comfire_txt));
                dialog.setPositiveButton(R.string.extract_ok_btn,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                OrderDetailActivity.this.finish();
                            }

                        });
                dialog.setNegativeButton(R.string.extract_cancle_btn,
                        null);
                dialog.show();
            }

            @Override
            public void setTitle(TextView v) {
                v.setText(getText(R.string.extract_order_info_title));
            }
        });
    }

    AsyncHttpResponseHandler pickupOrderHandler = new TextHttpResponseHandler(HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                              String responseString) {
            LogUtils.d("test", "pickupOrderHandler result = " + responseString);
            if (progressDialog != null) progressDialog.dismiss();

            try {
                ResultBase result = JSON.parseObject(responseString, ResultBase.class);
                if (result.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    PublicUtil.showCustomToast(mContext, result.getResponseMsg(), true);
                    finish();
                } else {
                    lock = false;
                    PublicUtil.showErrorMsg(mContext, result);
                }
            } catch (Exception e) {
                lock = false;
                PublicUtil.showToastServerBusy(mContext);
            }

        }

        @Override
        public void onFailure(int statusCode, Header[] headers,
                              String responseString, Throwable throwable) {
            if (progressDialog != null) progressDialog.dismiss();
            PublicUtil.showToastServerOvertime(mContext);
            lock = false;
        }
    };

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
        if (PublicUtil.isFastDoubleClick(1000)) return;

        if (v == btnSign) {
            if (null != pickupInfo && !lock) {
                lock = true;
                if (progressDialog != null) progressDialog.show();
                DeliveryApi.pickupOrder(signType, ClientStateManager.getLoginToken(mContext), pickupInfo, pickupOrderHandler);
            }

        } else if (v == layoutStorehouse) {
            StoreHouseDetailWindow popupWindow = new StoreHouseDetailWindow(this, pickupInfo);
            popupWindow.showPopwindow(popStart);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            CommonAlertDialog.Builder dialog = new CommonAlertDialog.Builder(
                    OrderDetailActivity.this);
            dialog.setMessage(getString(R.string.extract_cacle_comfire_txt));
            dialog.setPositiveButton(R.string.extract_ok_btn,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            OrderDetailActivity.this.finish();
                        }

                    });
            dialog.setNegativeButton(R.string.extract_cancle_btn, null);
            dialog.show();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onItemClick(Object item, View view, int position) {
        DialogUtil.showPictureDialog(this, ((Product) item).getImg());
    }

    class OrderProductAdapter extends BaseListAdapter<Product> {

        public OrderProductAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.order_product_item;
        }

        @Override
        protected void setView(final int position, View convertView, ViewGroup parent, boolean isNew) {
            ImageView imgProduct = getViewById(R.id.img_product);
            TextView txtContent = getViewById(R.id.txt_content);
            TextView txtMoney = getViewById(R.id.txt_money);
            TextView txtCount = getViewById(R.id.txt_count);
            View line = getViewById(R.id.line);
            txtMoney.setText(String.format("%s%s", context.getString(R.string.order_money_sign)
                    , PublicUtil.getPriceFrom(list.get(position).getPayPrice())));
            txtContent.setText(list.get(position).getShopProName());
            txtCount.setText(String.format("x%s", list.get(position).getBuyNum()));
            Glide.with(context).load(list.get(position).getImg()).into(imgProduct);

            if (position == list.size() - 1) {
                line.setVisibility(View.GONE);
            } else {
                line.setVisibility(View.VISIBLE);
            }
            setClickEvent(isNew, position, imgProduct);
        }

    }

}

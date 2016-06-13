package cn.com.bluemoon.delivery.module.clothing.collect.withorder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.bluemoon.delivery.ClientStateManager;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.async.listener.IActionBarListener;
import cn.com.bluemoon.delivery.module.base.BaseFragment;
import cn.com.bluemoon.delivery.module.clothing.collect.ClothingTabActivity;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib.utils.LibConstants;

/**
 * 有订单收衣管理
 * Created by luokai on 2016/6/12.
 */
public class WithOrderManageFragment extends BaseFragment {

    private ClothingTabActivity main;

    @Bind(R.id.listview_main)
    PullToRefreshListView listviewMain;

    private String type;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        main = (ClothingTabActivity)getActivity();

        Bundle bundle = getArguments();
        type = bundle.getString(ClothingTabActivity.TYPE);

        initCustomActionBar();

        View v = inflater.inflate(R.layout.fragment_tab_with_order_clothing_collect, container,
                false);

        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listviewMain.setMode(PullToRefreshBase.Mode.DISABLED);
        // TODO: lk 2016/6/13 Adapter
        // adapter = new SuspenseAdapter(main);
        getItem();
    }

    private void getItem() {
        String token = ClientStateManager.getLoginToken(main);

        if (type.equals(ClothingTabActivity.WITH_ORDER_COLLECT_MANAGE)) {
            // TODO: lk 2016/6/13 获取数据
            //  showProgressDialog();
           // DeliveryApi.getWaitReceiptOrders(token, withOrderListHandler);
        }
    }

    AsyncHttpResponseHandler withOrderListHandler = new TextHttpResponseHandler(
            HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                              String responseString) {
            LogUtils.d(getDefaultTag(), "getInventoryOrder result = " + responseString);
            dismissProgressDialog();
            listviewMain.onRefreshComplete();
            try {
                // TODO: lk 2016/6/13 处理数据
//                ResultOrderVo orderResult = JSON.parseObject(responseString,
//                        ResultOrderVo.class);
//                if (orderResult.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
//                    setData(orderResult);
//                } else {
//                    PublicUtil.showErrorMsg(main, orderResult);
//                }
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

//    private void setData(ResultOrderVo result) {
//        if (result == null || result.getOrderList() == null || result.getOrderList().size() < 1) {
//            adapter.setList(new ArrayList<OrderVo>());
//            main.amountTv.setVisibility(View.GONE);
//
//        } else {
//            item = result;
//            main.amountTv.setText(String.valueOf(result.getOrderList().size()));
//            main.amountTv.setVisibility(View.VISIBLE);
//            setCountAndPrice(item.getOrderTotalNum(), StringUtil.formatPriceByFen(item.getOrderTotalMoney()), item.getOrderTotalCase());
//
//            adapter.setList(item.getOrderList());
//        }
//        listView.setAdapter(adapter);
//
//    }

    private void initCustomActionBar() {

        CommonActionBar actionBar = new CommonActionBar(main.getActionBar(),
                new IActionBarListener() {

                    @Override
                    public void onBtnRight(View v) {
                        PublicUtil.openScanCard(main, WithOrderManageFragment.this,
                                getString(R.string.coupons_scan_code_title),
                                Constants.REQUEST_SCAN);
                    }

                    @Override
                    public void onBtnLeft(View v) {
                        main.finish();
                    }

                    @Override
                    public void setTitle(TextView v) {
                        if (type.equals(ClothingTabActivity.WITH_ORDER_COLLECT_MANAGE)) {
                            v.setText(R.string.tab_title_with_order_collect_manage);
                        }
                    }
                });

        actionBar.getImgRightView().setImageResource(R.mipmap.scan_top_nav);
        actionBar.getImgRightView().setVisibility(View.VISIBLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED){
            return;
        }

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Constants.REQUEST_SCAN:
                    String resultStr = data.getStringExtra(LibConstants.SCAN_RESULT);
                    // TODO: lk 2016/6/13 处理 WithOrderManageFragment 扫码返回
                    PublicUtil.showToast(resultStr);
//                    progressDialog.show();
//                    DeliveryApi.getCustomerInfo(ClientStateManager.getLoginToken(mContext), resultStr, getCustomerInfoHandler);
                    break;
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}

package cn.com.bluemoon.delivery.module.clothing.collect.withorder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.bluemoon.delivery.ClientStateManager;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.clothing.collect.ResultWithOrderClothingCollectList;
import cn.com.bluemoon.delivery.app.api.model.clothing.collect.WithOrderClothingCollectOrder;
import cn.com.bluemoon.delivery.async.listener.IActionBarListener;
import cn.com.bluemoon.delivery.module.base.BaseFragment;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.module.clothing.collect.ClothingTabActivity;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.ViewHolder;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib.utils.LibConstants;

/**
 * 有订单收衣管理
 * Created by luokai on 2016/6/12.
 */
public class WithOrderManageFragment extends BaseFragment implements OnListItemClickListener {
    private final static int REQUEST_CODE_WITH_ORDER_COLLECT_BOOK_IN_ACTIVITY = 0x12;

    private ClothingTabActivity main;
    private ResultWithOrderClothingCollectList orderList;
    private OrderAdapter adapter;


    @Bind(R.id.listview_main)
    PullToRefreshListView listviewMain;

    private String type;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        main = (ClothingTabActivity) getActivity();

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
        listviewMain.setMode(PullToRefreshBase.Mode.BOTH);

        adapter = new OrderAdapter(main, this);
        listviewMain.setAdapter(adapter);
        orderList = null;

        listviewMain.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                orderList = null;
                getData();
            }

            @Override
            public void onPullUpToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                getData();
            }

        });
        // TODO: lk 2016/6/13 是否有空白页？
//        listviewMain.setEmptyView();

        getData();
    }

    private void getData() {
        String token = ClientStateManager.getLoginToken(main);

        if (type.equals(ClothingTabActivity.WITH_ORDER_COLLECT_MANAGE)) {
            // TODO: lk 2016/6/13 获取列表数据
            showProgressDialog();
            main.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // TODO: lk 2016/6/14 onRefreshComplete若此时直接放主线程执行会不成功，为何？
                    dismissProgressDialog();
                    listviewMain.onRefreshComplete();
                    ResultWithOrderClothingCollectList s = new ResultWithOrderClothingCollectList();
                    s.setTimestamp(System.currentTimeMillis());

                    List<WithOrderClothingCollectOrder> orderList = new ArrayList<>();
                    orderList.add(new WithOrderClothingCollectOrder());
                    orderList.add(new WithOrderClothingCollectOrder());
                    orderList.add(new WithOrderClothingCollectOrder());
                    orderList.add(new WithOrderClothingCollectOrder());
                    orderList.add(new WithOrderClothingCollectOrder());
                    orderList.add(new WithOrderClothingCollectOrder());
                    orderList.add(new WithOrderClothingCollectOrder());
                    s.setOrderList(orderList);
                    setData(s);
                }
            });

//            DeliveryApi.getOrderList(token, AppContext.PAGE_SIZE,
//                    (null == orderList ? 0 : orderList.getTimestamp()), withOrderListHandler);
        }
    }

    private void setData(ResultWithOrderClothingCollectList resultOrder) {
        if (orderList == null) {
            orderList = resultOrder;
            adapter.setList(orderList.getOrderList());
        } else {
            orderList.getOrderList().addAll(resultOrder.getOrderList());
            orderList.setTimestamp(resultOrder.getTimestamp());
        }

        adapter.notifyDataSetChanged();
    }

    // TODO: lk 2016/6/13 可写个基类专门处理这种基本逻辑一样的fragment，将公共逻辑抽取（一步步来），
    // 可参照dobago的BaseRefreshListFragment
    AsyncHttpResponseHandler withOrderListHandler = new TextHttpResponseHandler(
            HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                              String responseString) {
            LogUtils.d(getDefaultTag(), "getInventoryOrder result = " + responseString);
            dismissProgressDialog();
            listviewMain.onRefreshComplete();
            try {
                ResultWithOrderClothingCollectList orderResult = JSON.parseObject(responseString,
                        ResultWithOrderClothingCollectList.class);
                if (orderResult.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    setData(orderResult);
                } else {
                    PublicUtil.showErrorMsg(main, orderResult);
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
            listviewMain.onRefreshComplete();
            PublicUtil.showToastServerOvertime();
        }
    };

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
listviewMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
});
        // TODO: lk 2016/6/13 扫码图标
        actionBar.getImgRightView().setImageResource(R.mipmap.scan_top_nav);
        actionBar.getImgRightView().setVisibility(View.VISIBLE);
    }

    // TODO: lk 2016/6/13 拨打电话，UI也未定
    private void call(String num) {
        PublicUtil.showCallPhoneDialog(main, num);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED) {
            return;
        }

        switch (requestCode) {
            case Constants.REQUEST_SCAN:
                if (resultCode == Activity.RESULT_OK) {
                    String resultStr = data.getStringExtra(LibConstants.SCAN_RESULT);
                    // TODO: lk 2016/6/13 处理 WithOrderManageFragment 扫码返回
                    PublicUtil.showToast(resultStr);
//                    progressDialog.show();
//                    DeliveryApi.getCustomerInfo(ClientStateManager.getLoginToken(mContext), resultStr, getCustomerInfoHandler);
                }
                break;
            // TODO: lk 2016/6/14  收衣登记返回
            case REQUEST_CODE_WITH_ORDER_COLLECT_BOOK_IN_ACTIVITY:
                if (resultCode == Activity.RESULT_OK) {
                    getData();
                }
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    /**
     * 收衣订单Adapter
     */
    class OrderAdapter extends BaseListAdapter<WithOrderClothingCollectOrder> {

        public OrderAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.item_with_order_clothing_collect_order_list;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            // TODO: lk 2016/6/13 Adapter View
            final TextView tvCollect = ViewHolder.get(convertView, R.id.tv_collect);
            setClickEvent(tvCollect, position);
        }
    }

    @Override
    public void onItemClick(Object item, View view, int position) {
        switch (view.getId()) {
            case R.id.tv_collect:
                // TODO: lk 2016/6/14 开始收衣
                Intent intent = new Intent(main, WithOrderCollectBookInActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(WithOrderCollectBookInActivity.EXTRA_ORDER,
                        ((WithOrderClothingCollectOrder) item).toString());
                intent.putExtras(bundle);
                WithOrderManageFragment.this.startActivityForResult(intent,
                        REQUEST_CODE_WITH_ORDER_COLLECT_BOOK_IN_ACTIVITY);
                break;
        }
    }
}

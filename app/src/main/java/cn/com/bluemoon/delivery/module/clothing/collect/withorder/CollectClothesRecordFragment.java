package cn.com.bluemoon.delivery.module.clothing.collect.withorder;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;

import java.util.Date;
import java.util.List;

import cn.com.bluemoon.delivery.ClientStateManager;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.clothing.ResultCollectInfo;
import cn.com.bluemoon.delivery.app.api.model.clothing.collect.CollectInfo;
import cn.com.bluemoon.delivery.async.listener.IActionBarListener;
import cn.com.bluemoon.delivery.module.base.BaseFragment;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.module.clothing.collect.ClothingTabActivity;
import cn.com.bluemoon.delivery.order.TimerFilterWindow;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.delivery.utils.ViewHolder;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib.utils.LibDateUtil;

public class CollectClothesRecordFragment extends BaseFragment implements OnListItemClickListener {


    private CollectClothesAdapter adapter;
    private FragmentActivity main;
    private PullToRefreshListView listView;
    View popStart;
    private long startTime = 0;
    private long endTime = 0;
    private String manager;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        initCustomActionBar();
        main = getActivity();
        Bundle bundle = getArguments();
        manager = bundle.getString("manager");
        View v = inflater.inflate(R.layout.fragment_tab_clothes, container,
                false);
        popStart = (View) v.findViewById(R.id.view_pop_start);

        listView = (PullToRefreshListView) v
                .findViewById(R.id.listview_main);
        listView.setMode(PullToRefreshBase.Mode.DISABLED);
        getItem();
        return v;
    }

    private void getItem() {
        String token = ClientStateManager.getLoginToken(main);

        showProgressDialog();
        if(manager.equals(ClothingTabActivity.WITH_ORDER_COLLECT_MANAGE)) {
            DeliveryApi.collectInfoRecord(token, startTime, endTime, collectHandler);
        }else{
            DeliveryApi.collectInfoRecord2(token, startTime, endTime, collectHandler);
        }

    }

    private void setData(List<CollectInfo> resultOrder) {
        adapter = new CollectClothesAdapter(main, this);
        adapter.setList(resultOrder);
        listView.setAdapter(adapter);
    }


    private void initCustomActionBar() {

        CommonActionBar actionBar = new CommonActionBar(getActivity().getActionBar(),
                new IActionBarListener() {

                    @Override
                    public void onBtnRight(View v) {
                        // TODO Auto-generated method stub

                        TimerFilterWindow popupWindow = new TimerFilterWindow(getActivity(), new TimerFilterWindow.TimerFilterListener() {
                            @Override
                            public void callBack(long startDate, long endDate) {
                                if (startDate >= 0 && endDate >= startDate) {
                                    startTime = LibDateUtil.getTimeByCustTime(startDate);
                                    endTime = LibDateUtil.getTimeByCustTime(endDate);

                                    Date start = new Date(startTime);
                                    Date end = new Date(endTime);

                                    if (endDate >= startDate
                                            && (((end.getDate() >= start.getDate()) && ((end.getYear() * 12 + end.getMonth()) - (start.getYear() * 12 + start.getMonth()) <= 5))
                                            || ((end.getDate() < start.getDate()) && ((end.getYear() * 12 + end.getMonth()) - (start.getYear() * 12 + start.getMonth()) <= 6)))) {
                                        getItem();
                                    } else {
                                        PublicUtil.showMessage(main, getString(R.string.txt_order_fillter_date_error));
                                    }

                                }
                            }
                        });
                        popupWindow.showPopwindow(popStart);

                    }

                    @Override
                    public void onBtnLeft(View v) {
                        // TODO Auto-generated method stub
                        getActivity().finish();
                    }

                    @Override
                    public void setTitle(TextView v) {
                        // TODO Auto-generated method stub
                        v.setText("收衣记录");

                    }
                });

        actionBar.getTvRightView().setText(R.string.btn_txt_fillter);
        actionBar.getTvRightView().setCompoundDrawablePadding(10);

        Drawable drawableFillter = getResources().getDrawable(R.mipmap.icon_filter);
        drawableFillter.setBounds(0, 0, drawableFillter.getMinimumWidth(), drawableFillter.getMinimumHeight());
        actionBar.getTvRightView().setCompoundDrawables(drawableFillter, null, null, null);
        actionBar.getTvRightView().setVisibility(View.VISIBLE);

    }


    class CollectClothesAdapter extends BaseListAdapter<CollectInfo> {
        public CollectClothesAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.item_clothes_collect;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            final CollectInfo order = (CollectInfo) getItem
                    (position);
            if (order == null) {
                return;
            }
            TextView   txtDispatchId = ViewHolder.get(convertView, R.id.txt_dispatch_id);
            TextView txtCollectNum = ViewHolder.get(convertView, R.id.txt_collect_num);
            TextView txtStatus = ViewHolder.get(convertView, R.id.txt_status);
            TextView txtUserName = ViewHolder.get(convertView, R.id.txt_username);
            TextView txtUserPhone = ViewHolder.get(convertView, R.id.txt_user_phone);
            TextView txtAddress = ViewHolder.get(convertView, R.id.txt_address);
            TextView txtActual = ViewHolder.get(convertView, R.id.txt_actual);
            TextView txtScanBarCode = ViewHolder.get(convertView, R.id.txt_scan_bar_code);
            TextView txtUrgent = ViewHolder.get(convertView, R.id.txt_urgent);
            TextView txtScan = ViewHolder.get(convertView, R.id.txt_scan_code);
            LinearLayout layoutDetail = ViewHolder.get(convertView, R.id.layout_detail);
            if(manager.equals(ClothingTabActivity.WITH_ORDER_COLLECT_MANAGE)) {
                if (StringUtil.isEmpty(order.getCollectBrcode())) {
                    txtScan.setVisibility(View.GONE);
                }
                txtCollectNum.setText(order.getCollectCode());
                txtScanBarCode.setText(order.getCollectBrcode());
                txtUserName.setText(order.getReceiveName());
                txtUserPhone.setText(order.getReceivePhone());
            }else{
                txtDispatchId.setVisibility(View.GONE);
                txtScan.setVisibility(View.GONE);
                txtScanBarCode.setVisibility(View.GONE);
                txtCollectNum.setText(DateUtil.getTime(order.getOpTime(),"yyyy/MM/dd HH:mm"));
                txtUserName.setText(order.getCustomerName());
                txtUserPhone.setText(order.getCustomerPhone());

            }
            txtStatus.setText(Constants.WASH_STATUS_MAP.get(order.getCollectStatus()));
            txtActual.setText(String.valueOf(order.getActualCount()));
            txtUrgent.setVisibility(order.getIsUrgent() == "0" ? View.GONE : View.VISIBLE);

            txtAddress.setText(String.format("%s%s%s%s%s%s", order.getProvince(),
                    order.getCity(),
                    order.getCounty(),
                    order.getVillage(),
                    order.getStreet(),
                    order.getAddress()));
            setClickEvent(isNew, position, layoutDetail);
        }
    }


    public void onPause() {
        super.onPause();
    }


    AsyncHttpResponseHandler collectHandler = new TextHttpResponseHandler(
            HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                              String responseString) {
            LogUtils.d(getDefaultTag(), "collectHandler result = " + responseString);
            dismissProgressDialog();
            try {
                ResultCollectInfo result = JSON.parseObject(responseString,
                        ResultCollectInfo.class);
                if (result.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    setData(result.getCollectInfos());
                } else {
                    PublicUtil.showErrorMsg(main, result);
                }
            } catch (Exception e) {
                LogUtils.e(getDefaultTag(), e.getMessage());
                PublicUtil.showToastServerBusy();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers,
                              String responseString, Throwable throwable) {
            // TODO: lk 2016/6/19  LogUtils要换，tag没卵用
            LogUtils.e(getDefaultTag(), throwable.getMessage());
            dismissProgressDialog();
            PublicUtil.showToastServerOvertime();
        }
    };


    @Override
    public void onItemClick(Object item, View view, int position) {
        CollectInfo order = (CollectInfo) item;
        if (order == null) {
            return;
        }
        switch (view.getId()) {
            case R.id.layout_detail:

                break;
        }
    }

}

package cn.com.bluemoon.delivery.module.wash.collect;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.protocol.HTTP;

import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.clothing.ResultCollectInfo;
import cn.com.bluemoon.delivery.app.api.model.clothing.collect.CollectInfo;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.module.base.interf.IActionBarListener;
import cn.com.bluemoon.delivery.module.oldbase.BaseFragment;
import cn.com.bluemoon.delivery.module.order.TimerFilterWindow;
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

    private FragmentActivity main;
    private PullToRefreshListView listView;
    View popStart;
    private long startTime = 0;
    private long endTime = 0;
    private String manager;

    private TextView tvTime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        initCustomActionBar();
        main = getActivity();
        Bundle bundle = getArguments();
        manager = bundle.getString("manager");
        View v = inflater.inflate(R.layout.fragment_tab_clothes, container,
                false);
        popStart = v.findViewById(R.id.view_pop_start);

        tvTime = (TextView) v.findViewById(R.id.tv_time);
        tvTime.setVisibility(View.GONE);

        listView = (PullToRefreshListView) v
                .findViewById(R.id.listview_main);
        listView.setMode(PullToRefreshBase.Mode.DISABLED);

        View emptyView = LayoutInflater.from(main).inflate(R.layout.layout_no_data, null);
        ((TextView) emptyView.findViewById(R.id.txt_content)).setText(R.string
                .with_order_collect_no_record);
        listView.setEmptyView(emptyView);
        getItem();
        return v;
    }

    private void getItem() {
        String token = ClientStateManager.getLoginToken(main);

        showProgressDialog();
        if (manager.equals(ClothingTabActivity.WITH_ORDER_COLLECT_MANAGE)) {
            DeliveryApi.collectInfoRecord(token, startTime, endTime, collectHandler);
        } else {
            DeliveryApi.collectInfoRecord2(token, startTime, endTime, collectHandler);
        }

    }

    private void setData(List<CollectInfo> resultOrder) {
        CollectClothesAdapter adapter = new CollectClothesAdapter(main, this);
        adapter.setList(resultOrder);
        listView.setAdapter(adapter);
    }


    private void initCustomActionBar() {

        CommonActionBar actionBar = new CommonActionBar(getActivity().getActionBar(),
                new IActionBarListener() {

                    @Override
                    public void onBtnRight(View v) {
                        TimerFilterWindow popupWindow = new TimerFilterWindow(getActivity(), new
                                TimerFilterWindow.TimerFilterListener() {
                                    @Override
                                    public void callBack(long startDate, long endDate) {
                                        if (startDate >= 0 && endDate >= startDate) {
                                            startTime = LibDateUtil.getTimeByCustTime(startDate);
                                            endTime = LibDateUtil.getTimeByCustTime(endDate);

                                            if (DateUtil.getTimeOffsetMonth(startTime, 6) >
                                                    endTime) {
                                                if (startTime == 0 && endTime == 0) {
                                                    return;
                                                }

                                                // 将查询条件显示在上面
                                                tvTime.setVisibility(View.VISIBLE);
                                                tvTime.setText(String.format("%s%s%s",
                                                        DateUtil.getTime(startTime, "yyyy/MM/dd"),
                                                        getString(R.string.text_to),
                                                        DateUtil.getTime(endTime, "yyyy/MM/dd")));
                                                getItem();
                                            } else {
                                                PublicUtil.showMessage(main, getString(R.string
                                                        .txt_order_fillter_date_error));
                                            }

                                        }
                                    }
                                });
                        popupWindow.showPopwindow(popStart);

                    }

                    @Override
                    public void onBtnLeft(View v) {
                        getActivity().finish();
                    }

                    @Override
                    public void setTitle(TextView v) {
                        v.setText(getString(R.string.tab_bottom_with_order_collect_record));

                    }
                });

        actionBar.getTvRightView().setText(R.string.btn_txt_fillter);
        actionBar.getTvRightView().setCompoundDrawablePadding(10);

        Drawable drawableFillter = getResources().getDrawable(R.mipmap.icon_filter);
        assert drawableFillter != null;
        drawableFillter.setBounds(0, 0, drawableFillter.getMinimumWidth(), drawableFillter
                .getMinimumHeight());
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
            TextView txtDispatchId = ViewHolder.get(convertView, R.id.txt_dispatch_id);
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
            LinearLayout layoutFooter = ViewHolder.get(convertView, R.id.layout_footer);
            TextView txtActivityName = ViewHolder.get(convertView, R.id.txt_activity_name);
            View div = ViewHolder.get(convertView, R.id.div);

            if (manager.equals(ClothingTabActivity.WITH_ORDER_COLLECT_MANAGE)) {
                txtScan.setVisibility(View.VISIBLE);
                txtScanBarCode.setVisibility(View.VISIBLE);
                if (StringUtil.isEmpty(order.getCollectBrcode())) {
                    txtScanBarCode.setText(getString(R.string.text_empty));
                } else {
                    txtScanBarCode.setText(order.getCollectBrcode());
                }
                txtActivityName.setVisibility(View.GONE);
                txtCollectNum.setText(order.getCollectCode());
                txtUserName.setText(order.getReceiveName());
                txtUserPhone.setText(order.getReceivePhone());
            } else {
                layoutFooter.setGravity(Gravity.CENTER_VERTICAL | Gravity.END);
                txtDispatchId.setVisibility(View.GONE);
                txtActivityName.setText(order.getActivityName());
                txtScan.setVisibility(View.GONE);
                txtScanBarCode.setVisibility(View.GONE);
                txtCollectNum.setText(DateUtil.getTime(order.getOpTime(), "yyyy/MM/dd HH:mm"));
                txtUserName.setText(order.getCustomerName());
                txtUserPhone.setText(order.getCustomerPhone());

            }
            txtStatus.setText(Constants.WASH_STATUS_MAP.get(order.getCollectStatus()));
            txtActual.setText(String.valueOf(order.getActualCount()));
            txtUrgent.setVisibility(order.getIsUrgent() == 1 ? View.VISIBLE : View.GONE);

            txtAddress.setText(String.format("%s%s%s%s%s%s", order.getProvince(),
                    order.getCity(),
                    order.getCounty(),
                    order.getVillage(),
                    order.getStreet(),
                    order.getAddress()));

            if (position < getCount() - 1) {
                div.setVisibility(View.VISIBLE);
            } else {
                div.setVisibility(View.GONE);
            }
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
        if (null != order) {
            ClothingRecordDetailActivity.actionStart(main, order.getCollectCode(), manager);
        }

    }
}


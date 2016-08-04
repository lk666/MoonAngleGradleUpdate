package cn.com.bluemoon.delivery.module.base.example;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.clothing.ResultCollectInfo;
import cn.com.bluemoon.delivery.app.api.model.clothing.collect.CollectInfo;
import cn.com.bluemoon.delivery.module.base.BaseFragment;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.BasePullToRefreshListViewFragment;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.module.clothing.collect.ClothingRecordDetailActivity;
import cn.com.bluemoon.delivery.module.clothing.collect.ClothingTabActivity;
import cn.com.bluemoon.delivery.module.order.TimerFilterWindow;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.delivery.utils.ViewHolder;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib.utils.LibDateUtil;

/**
 * 测试带头的下拉刷新
 * Created by lk on 2016/7/29.
 */
public class _CollectClothesRecordFragment extends BasePullToRefreshListViewFragment {
    View popStart;
    private long startTime = 0;
    private long endTime = 0;
    private TextView tvTime;
    private String manager;

    @Override
    protected void onBeforeCreateView() {
        Bundle bundle = getArguments();
        manager = (String) bundle.getSerializable(BaseFragment.EXTRA_BUNDLE_DATA);
    }

    @Override
    protected void initPullToRefreshListView(PullToRefreshListView ptrlv) {
        super.initPullToRefreshListView(ptrlv);
    }

    @Override
    protected int getHeadLayoutId() {
        return R.layout.head_fragment_tab_clothes;
    }

    @Override
    protected void initHeadViewEvent(View headView) {
        super.initHeadViewEvent(headView);
        popStart = headView.findViewById(R.id.view_pop_start);

        tvTime = (TextView) headView.findViewById(R.id.tv_time);
        tvTime.setVisibility(View.GONE);
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.tab_bottom_with_order_collect_record);
    }

    @Override
    protected void setActionBar(CommonActionBar actionBar) {
        super.setActionBar(actionBar);
        actionBar.getTvRightView().setText(R.string.btn_txt_fillter);
        actionBar.getTvRightView().setCompoundDrawablePadding(10);

        Drawable drawableFillter = getResources().getDrawable(R.mipmap.icon_filter);
        assert drawableFillter != null;
        drawableFillter.setBounds(0, 0, drawableFillter.getMinimumWidth(), drawableFillter
                .getMinimumHeight());
        actionBar.getTvRightView().setCompoundDrawables(drawableFillter, null, null, null);
        actionBar.getTvRightView().setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActionBarBtnRightClick() {
        super.onActionBarBtnRightClick();
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
                                setHeadViewVisibility(View.VISIBLE);
                                tvTime.setVisibility(View.VISIBLE);
                                tvTime.setText(String.format("%s%s%s",
                                        DateUtil.getTime(startTime, "yyyy/MM/dd"),
                                        getString(R.string.text_to),
                                        DateUtil.getTime(endTime, "yyyy/MM/dd")));
                                getData();
                            } else {
                                PublicUtil.showMessage(getActivity(), getString(R.string
                                        .txt_order_fillter_date_error));
                            }

                        }
                    }
                });
        popupWindow.showPopwindow(popStart);
    }

    @Override
    protected void showEmptyView() {
        super.showEmptyView();
        // 可在此处设置head的可见性
    }

    @Override
    protected void showNetErrorView() {
        super.showNetErrorView();
        // 可在此处设置head的可见性
    }

    @Override
    protected void showRefreshView() {
        super.showRefreshView();
        // 列表数据刷新，如可在此处设置head等
    }

    @Override
    protected CollectClothesAdapter getNewAdapter() {
        return new CollectClothesAdapter(getActivity(), this);
    }

    /**
     * Mode不包含上拉加载时，可这样重写此方法
     *
     * @param result 继承ResultBase的json字符串数据，不为null，也非空数据
     */
    @Override
    protected List<CollectInfo> getGetMoreList(ResultBase result) {
        return null;
    }

    @Override
    protected List<CollectInfo> getGetDataList(ResultBase result) {
        ResultCollectInfo resultObj = (ResultCollectInfo) result;
        return resultObj.getCollectInfos();
    }

    @Override
    protected PullToRefreshBase.Mode getMode() {
        return PullToRefreshBase.Mode.PULL_FROM_START;
    }

    @Override
    protected void invokeGetDataDeliveryApi(int requestCode) {
        if (manager.equals(ClothingTabActivity.WITH_ORDER_COLLECT_MANAGE)) {
            DeliveryApi._collectInfoRecord(getToken(), startTime, endTime, getNewHandler
                    (requestCode, ResultCollectInfo.class));
        } else {
            DeliveryApi._collectInfoRecord2(getToken(), startTime, endTime, getNewHandler
                    (requestCode, ResultCollectInfo.class));
        }
    }

    /**
     * Mode不包含上拉加载时，可这样重写此方法
     */
    @Override
    protected void invokeGetMoreDeliveryApi(int requestCode) {
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase resultBase) {
        super.onSuccessResponse(requestCode, jsonString, resultBase);
        // 其他requestCode可在此处理
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

    @Override
    public void onItemClick(Object item, View view, int position) {
        CollectInfo order = (CollectInfo) item;
        if (null != order) {
            ClothingRecordDetailActivity.actionStart(getActivity(), order.getCollectCode(),
                    manager);
        }
    }
}

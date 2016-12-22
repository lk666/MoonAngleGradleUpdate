package cn.com.bluemoon.delivery.module.wash.appointment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.AppointmentApi;
import cn.com.bluemoon.delivery.app.api.ReturningApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.wash.appointment.ResultAppointmentCollectList;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.BasePullToRefreshListViewFragment;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.module.order.TimerFilterWindow;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib.utils.LibDateUtil;

/**
 * 收衣记录
 */
public class AppointmentHistoryFragment extends BasePullToRefreshListViewFragment {
    private View viewPopStart;
    private TextView tvDate;
    private TextView tvTotal;

    /**
     * 分页标识
     */
    private long timestamp = 0;

    private long startTime = 0;
    private long endTime = 0;
    private int total = 0;

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
        TimerFilterWindow popupWindow = new TimerFilterWindow(getActivity(), new
                TimerFilterWindow.TimerFilterListener() {
                    @Override
                    public void callBack(long startDate, long endDate) {
                        if (startDate >= 0 && endDate >= startDate) {
                            startTime = LibDateUtil.getTimeByCustTime(startDate);
                            endTime = LibDateUtil.getTimeByCustTime(endDate);

                            if (DateUtil.getTimeOffsetMonth(startTime, 6) >
                                    endTime) {
                                setHead(View.VISIBLE);
                                initData();
                            } else {
                                PublicUtil.showMessage(getActivity(), getString(R.string
                                        .txt_order_fillter_date_error));
                            }
                        }
                    }
                });

        popupWindow.showPopwindow(viewPopStart);
    }

    private void setHead(int visibility) {
        if (visibility == View.VISIBLE && startTime < endTime) {
            setHeadViewVisibility(View.VISIBLE);
            tvDate.setText(getString(R.string.start_to_end, DateUtil.getTime(startTime,
                    "yyyy/MM/dd"),
                    DateUtil.getTime(endTime, "yyyy/MM/dd")));
            tvTotal.setText(getString(R.string.card_record_count, total + ""));
        } else {
            setHeadViewVisibility(View.GONE);
        }
    }

    @Override
    protected int getHeadLayoutId() {
        return R.layout.head_fragment_tab_appointment_history;
    }

    @Override
    protected void initHeadViewEvent(View headView) {
        super.initHeadViewEvent(headView);
        viewPopStart = headView.findViewById(R.id.view_pop_start);
        tvDate = (TextView) headView.findViewById(R.id.tv_date);
        tvTotal = (TextView) headView.findViewById(R.id.tv_total);
    }

    protected void initPullToRefreshListView(PullToRefreshListView ptrlv) {
        ptrlv.getRefreshableView().setDivider(null);
        ptrlv.getRefreshableView().setDividerHeight(0);
        startTime = 0;
        endTime = 0;
    }

    @Override
    protected PullToRefreshBase.Mode getMode() {
        return PullToRefreshBase.Mode.BOTH;
    }

    @Override
    protected void invokeGetDataDeliveryApi(int requestCode) {
        timestamp = 0;
        AppointmentApi.appointmentCollectList(endTime, startTime, 0, getToken(), getNewHandler
                (requestCode, ResultAppointmentCollectList.class));
    }

    @Override
    protected List<ResultAppointmentCollectList.CollectInfosBean> getGetDataList(ResultBase result) {
        ResultAppointmentCollectList resultObj = (ResultAppointmentCollectList) result;
        timestamp = resultObj.getTimestamp();
        total = resultObj.getTotalCount();
        return resultObj.getCollectInfos();
    }

    @Override
    protected void invokeGetMoreDeliveryApi(int requestCode) {
        AppointmentApi.appointmentCollectList(endTime, startTime, timestamp, getToken(), getNewHandler
                (requestCode, ResultAppointmentCollectList.class));
    }

    @Override
    protected List<ResultAppointmentCollectList.CollectInfosBean> getGetMoreList(ResultBase result) {
        return getGetDataList(result);
    }

    @Override
    protected void showEmptyView() {
        super.showEmptyView();
        // 可在此处设置head等
        setHead(View.VISIBLE);
    }

    @Override
    protected void showNetErrorView() {
        super.showNetErrorView();
        // 可在此处设置head等
        setHead(View.GONE);
    }

    @Override
    protected void showRefreshView() {
        super.showRefreshView();
        // 列表数据刷新，如可在此处设置head等
        setHead(View.VISIBLE);
    }

    protected ItemAdapter getNewAdapter() {
        return new ItemAdapter(getContext(), this);
    }

    class ItemAdapter extends BaseListAdapter<ResultAppointmentCollectList.CollectInfosBean> {


        public ItemAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.item_appointment_history;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            ResultAppointmentCollectList.CollectInfosBean item =
                    (ResultAppointmentCollectList.CollectInfosBean) getItem(position);

            TextView txtCollectNum = getViewById(R.id.txt_collect_num);
            TextView txtStatus = getViewById(R.id.txt_status);
            TextView txtUsername = getViewById(R.id.txt_username);
            TextView txtUserPhone = getViewById(R.id.txt_user_phone);
            TextView txtAddress = getViewById(R.id.txt_address);
            TextView txtTime = getViewById(R.id.txt_time);
            TextView txtActual = getViewById(R.id.txt_actual);

            txtCollectNum.setText(getString(R.string.with_order_collect_collect_number_text_num,
                    item.getCollectCode()));
            txtStatus.setText(Constants.WASH_STATUS_MAP.get(item.getCollectStatus()));

            txtUsername.setText(item.getCustomerName());
            txtUserPhone.setText(item.getCustomerPhone());
            txtAddress.setText(String.format("%s%s%s%s%s%s", item.getProvince(),
                    item.getCity(),
                    item.getCounty(),
                    item.getVillage(),
                    item.getStreet(),
                    item.getAddress()));

            txtTime.setText(getString(R.string.collect_detail_time, DateUtil.getTime(item
                    .getCreateTime(), "yyyy/MM/dd HH:mm")));
            txtActual.setText(getString(R.string.with_order_collect_order_receive_count_num,
                    item.getActualCount() + ""));

            setClickEvent(isNew, position, convertView);
        }
    }

    @Override
    public void onItemClick(Object obj, View view, int position) {
        ResultAppointmentCollectList.CollectInfosBean item =
                (ResultAppointmentCollectList.CollectInfosBean) obj;
        if (null != item) {
            // TODO: lk 2016/12/22
//            AppointmentDetailActivity.actionStart(getContext(), item.getCollectCode());
        }
    }
}

package cn.com.bluemoon.delivery.module.wash.appointment;

import android.content.Context;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.AppointmentApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.wash.appointment.ResultAppointmentQueryList;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.BasePullToRefreshListViewFragment;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;

/**
 * 预约收衣界面
 * Created by lk on 2016/9/14.
 */
public class AppointmentFragment extends BasePullToRefreshListViewFragment {

    /**
     * 分页标识
     */
    private long timestamp = 0;

    @Override
    protected String getTitleString() {
        return getString(R.string.appointment_title);
    }

    @Override
    protected void initPullToRefreshListView(PullToRefreshListView ptrlv) {
        ptrlv.getRefreshableView().setDivider(null);
        ptrlv.getRefreshableView().setDividerHeight(0);
    }

    @Override
    protected PullToRefreshBase.Mode getMode() {
        return PullToRefreshBase.Mode.BOTH;
    }

    @Override
    protected void invokeGetDataDeliveryApi(int requestCode) {
        timestamp = 0;
        AppointmentApi.appointmentQueryList(0, getToken(),
                getNewHandler(requestCode, ResultAppointmentQueryList.class));
        setAmount();
    }

    @Override
    protected List<ResultAppointmentQueryList.AppointmentListBean> getGetDataList(
            ResultBase result) {
        ResultAppointmentQueryList resultObj = (ResultAppointmentQueryList) result;
        timestamp = resultObj.getTimestamp();
        return resultObj.getAppointmentList();
    }

    @Override
    protected void invokeGetMoreDeliveryApi(int requestCode) {
        AppointmentApi.appointmentQueryList(timestamp, getToken(),
                getNewHandler(requestCode, ResultAppointmentQueryList.class));
    }

    @Override
    protected List<ResultAppointmentQueryList.AppointmentListBean> getGetMoreList(
            ResultBase result) {
        return getGetDataList(result);
    }

    @Override
    protected ItemAdapter getNewAdapter() {
        return new ItemAdapter(getActivity(), this);
    }

    class ItemAdapter extends BaseListAdapter<ResultAppointmentQueryList.AppointmentListBean> {


        public ItemAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.item_appointment;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            ResultAppointmentQueryList.AppointmentListBean item =
                    (ResultAppointmentQueryList.AppointmentListBean) getItem(position);

            if (item == null) {
                return;
            }

            TextView tvNumber = getViewById(R.id.tv_number);
            Button btnRightAction = getViewById(R.id.btn_right_action);
            TextView tvCustomerName = getViewById(R.id.tv_customer_name);
            TextView tvCustomerPhone = getViewById(R.id.tv_customer_phone);
            TextView tvAddress = getViewById(R.id.tv_address);
            TextView tvTime = getViewById(R.id.tv_time);
            View divRemark = getViewById(R.id.div_remark);
            TextView tvRemark = getViewById(R.id.tv_remark);

            // 预约单号
            tvNumber.setText(getString(R.string.appointment_code, item.getAppointmentCode()));

            // 名称
            tvCustomerName.setText(item.getCustomerName());

            //电话
            tvCustomerPhone.setText(item.getCustomerPhone());

            // 地址
            StringBuilder address = new StringBuilder(item.getProvince()).append(item.getCity())
                    .append(item.getCounty()).append(item.getStreet()).append(item.getVillage
                            ()).append(item.getAddress());
            tvAddress.setText(address);

            // 预约时间
            tvTime.setText(getString(R.string.appointment_time, DateUtil.getTime(item
                    .getCreateTime(), "yyyy/MM/dd HH:mm")));

            // 备注
            if (TextUtils.isEmpty(item.getRemark())) {
                divRemark.setVisibility(View.GONE);
                tvRemark.setVisibility(View.GONE);
            } else {
                divRemark.setVisibility(View.VISIBLE);
                tvRemark.setVisibility(View.VISIBLE);
                tvRemark.setText(getString(R.string.manage_remark, item.getRemark()));
            }

            // 右边按钮
            switch (item.getAppointmentStatus()) {
                // 待接单
                case ResultAppointmentQueryList.AppointmentListBean.APPOINTMENT_WAIT_ORDERS:
                    btnRightAction.setVisibility(View.VISIBLE);
                    btnRightAction.setText(getString(R.string.appointment_accept));
                    break;

                // 已接单
                case ResultAppointmentQueryList.AppointmentListBean.APPOINTMENT_ALREADY_ORDERS:
                    btnRightAction.setVisibility(View.VISIBLE);
                    btnRightAction.setText(
                            getString(R.string.with_order_collect_btn_start_collect));
                    break;

                default:
                    btnRightAction.setVisibility(View.GONE);
                    break;
            }

            if (isNew) {
                tvCustomerPhone.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
                tvCustomerPhone.getPaint().setAntiAlias(true);
            }

            setClickEvent(isNew, position, btnRightAction);
        }
    }

    @Override
    public void onItemClick(Object obj, View view, int position) {
        ResultAppointmentQueryList.AppointmentListBean item =
                (ResultAppointmentQueryList.AppointmentListBean) obj;
        if (null != item) {
            // 点击按钮
            // 右边按钮
            switch (item.getAppointmentStatus()) {
                // 待接单
                case ResultAppointmentQueryList.AppointmentListBean.APPOINTMENT_WAIT_ORDERS:
                    // TODO: lk 2016/12/22
                    toast("接单");
                    break;

                // 已接单
                case ResultAppointmentQueryList.AppointmentListBean.APPOINTMENT_ALREADY_ORDERS:
                    // TODO: lk 2016/12/22
                    toast("开始收衣");
                    break;
            }
        }
    }
}
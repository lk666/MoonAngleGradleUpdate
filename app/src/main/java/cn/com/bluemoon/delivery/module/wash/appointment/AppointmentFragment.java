package cn.com.bluemoon.delivery.module.wash.appointment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import cn.com.bluemoon.delivery.app.api.model.wash.appointment.ResultAppointmentOrderScan;
import cn.com.bluemoon.delivery.app.api.model.wash.appointment.ResultAppointmentQueryList;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.BasePullToRefreshListViewFragment;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.module.wash.collect.withorder.ManualInputCodeActivity;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib.utils.LibConstants;

/**
 * 预约收衣界面
 * Created by lk on 2016/9/14.
 */
public class AppointmentFragment extends BasePullToRefreshListViewFragment {

    private static final int REQUEST_CODE_RECEIVE = 0x777;
    private static final int REQUEST_CODE_CREATE_COLLECT = 0x77;
    private static final int REQUEST_CODE_MANUAL = 0x66;
    private static final int REQUEST_CODE_SCAN_CREATE_COLLECT = 0x55;
    private static final int REQUEST_CODE_QUERY = 0x666;
    /**
     * 分页标识
     */
    private long timestamp = 0;

    @Override
    protected String getTitleString() {
        return getString(R.string.appointment_title);
    }

    @Override
    protected void setActionBar(CommonActionBar titleBar) {
        super.setActionBar(titleBar);
        titleBar.getImgRightView().setVisibility(View.VISIBLE);
        titleBar.getImgRightView().setImageResource(R.mipmap.ewmtxm);
    }

    @Override
    protected void onActionBarBtnRightClick() {
        goScanCode();
    }

    /**
     * 打开扫码界面
     */
    private void goScanCode() {
        PublicUtil.openClothScan(getActivity(), AppointmentFragment.this,
                getString(R.string.coupons_scan_code_title),
                getString(R.string.with_order_collect_manual_input_code_btn),
                Constants.REQUEST_SCAN);
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

    private int acceptPosition;

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
                    showWaitDialog();
                    acceptPosition = position;
                    AppointmentApi.appointmentReceived(item.getAppointmentCode(), getToken(),
                            getNewHandler(REQUEST_CODE_RECEIVE, ResultBase.class));
                    break;

                // 已接单
                case ResultAppointmentQueryList.AppointmentListBean.APPOINTMENT_ALREADY_ORDERS:
                    CreateAppointmentCollectOrderActivity.actionStart(this, item,
                            REQUEST_CODE_CREATE_COLLECT);
                    break;
            }
        }
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        super.onSuccessResponse(requestCode, jsonString, result);
        switch (requestCode) {
            // 接单
            case REQUEST_CODE_RECEIVE:
                toast(getString(R.string.appoint_receive_success));
                ResultAppointmentQueryList.AppointmentListBean item =
                        (ResultAppointmentQueryList.AppointmentListBean) getList().get
                                (acceptPosition);
                item.setAppointmentStatus(ResultAppointmentQueryList.AppointmentListBean
                        .APPOINTMENT_ALREADY_ORDERS);
                getAdapter().notifyDataSetChanged();
                break;
            // 扫一扫预约单成功
            case REQUEST_CODE_QUERY:
                ResultAppointmentOrderScan data = (ResultAppointmentOrderScan) result;
                if (data.appointmentInfo != null) {
                    CreateAppointmentCollectOrderActivity.actionStart(this, data.appointmentInfo,
                            REQUEST_CODE_SCAN_CREATE_COLLECT);
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SCAN_CREATE_COLLECT) {
            initData();
        }

        if (resultCode == Activity.RESULT_CANCELED) {
            return;
        }

        switch (requestCode) {
            // 已接单,创建收衣单成功的返回
            case REQUEST_CODE_CREATE_COLLECT:
                if (resultCode == Activity.RESULT_OK) {
                    initData();
                }
                break;
            case Constants.REQUEST_SCAN:
                // 扫码返回
                if (resultCode == Activity.RESULT_OK) {
                    String resultStr = data.getStringExtra(LibConstants.SCAN_RESULT);
                    handleScanCodeBack(resultStr);
                }
                //   跳转到手动输入
                else if (resultCode == Constants.RESULT_SCAN) {
                    Intent intent = new Intent(getActivity(), ManualInputCodeActivity.class);
                    AppointmentFragment.this.startActivityForResult(intent,
                            REQUEST_CODE_MANUAL);
                }
                break;

            // 手动输入返回
            case REQUEST_CODE_MANUAL:
                // 数字码返回
                if (resultCode == Activity.RESULT_OK) {
                    String resultStr = data.getStringExtra(ManualInputCodeActivity
                            .RESULT_EXTRA_CODE);
                    handleScanCodeBack(resultStr);
                }
                //   跳转到扫码输入
                else if (resultCode == ManualInputCodeActivity.RESULT_CODE_SCANE_CODE) {
                    goScanCode();
                }
                break;
        }
    }


    /**
     * 处理扫码、手动输入数字码返回
     */
    private void handleScanCodeBack(String code) {
        showWaitDialog();
        AppointmentApi.appointmentOrderScan(code, getToken(),
                getNewHandler(REQUEST_CODE_QUERY, ResultAppointmentOrderScan.class));
    }
}
package cn.com.bluemoon.delivery.module.wash.appointment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
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
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.wash.appointment.ResultAppointmentOrderScan;
import cn.com.bluemoon.delivery.app.api.model.wash.appointment.ResultAppointmentQueryList;
import cn.com.bluemoon.delivery.app.api.model.wash.appointment.ResultAppointmentQueryList.AppointmentListBean;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.BasePullToRefreshListViewFragment;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.module.wash.collect.withorder.ManualInputCodeActivity;
import cn.com.bluemoon.delivery.module.wash.collect.withorder.TransferOrderActivity;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.DialogUtil;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.ViewHolder;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib.utils.LibConstants;
import cn.com.bluemoon.lib.view.CommonAlertDialog;

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
    private static final int REQUEST_APPOINTMENT_REFUSAL = 0x6666;
    private static final int REQUEST_CODE_TRAMSFER = 0x46;

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
        return PullToRefreshBase.Mode.PULL_FROM_START;
    }

    @Override
    protected void invokeGetDataDeliveryApi(int requestCode) {
        AppointmentApi.appointmentQueryList(getToken(),
                getNewHandler(requestCode, ResultAppointmentQueryList.class));
        setAmount();
    }

    @Override
    protected List<AppointmentListBean> getGetDataList(
            ResultBase result) {
        ResultAppointmentQueryList resultObj = (ResultAppointmentQueryList) result;
        return resultObj.getAppointmentList();
    }

    @Override
    protected void invokeGetMoreDeliveryApi(int requestCode) {
        AppointmentApi.appointmentQueryList(getToken(),
                getNewHandler(requestCode, ResultAppointmentQueryList.class));
    }

    @Override
    protected List<AppointmentListBean> getGetMoreList(
            ResultBase result) {
        return getGetDataList(result);
    }

    @Override
    protected ItemAdapter getNewAdapter() {
        return new ItemAdapter(getActivity(), this);
    }

    class ItemAdapter extends BaseListAdapter<AppointmentListBean> {
        private int colorTxtBtnBlue;
        private int colorTxtBtnGray;

        public ItemAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
            colorTxtBtnBlue = getResources().getColor(R.color.btn_blue);
            colorTxtBtnGray = getResources().getColor(R.color.text_black_light);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.item_appointment;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            AppointmentListBean item =
                    (AppointmentListBean) getItem(position);

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
            TextView tvRightAction = ViewHolder.get(convertView, R.id.tv_right_action);

            TextView tvAdminDipach = ViewHolder.get(convertView, R.id.tv_admin_dipach);
            TextView tvAngelTransfer = ViewHolder.get(convertView, R.id.tv_angel_transfer);
            tvAdminDipach.setVisibility(View.GONE);
            tvAngelTransfer.setVisibility(View.GONE);
            //添加指派转派标签
            if (item.dispachInfo != null
                    && AppointmentListBean.APPOINTMENT_WAIT_ORDERS.equals(item.getAppointmentStatus())) {
                if ("ANGEL_TRANSFER".equals(item.dispachInfo.getDispachType())) {
                    tvAngelTransfer.setVisibility(View.VISIBLE);
                } else if ("ADMIN_DISPACH".equals(item.dispachInfo.getDispachType())) {
                    tvAdminDipach.setVisibility(View.VISIBLE);
                }
            }

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

            tvRightAction.setVisibility(View.GONE);
            // 右边按钮
            switch (item.getAppointmentStatus()) {
                // 待接单
                case AppointmentListBean.APPOINTMENT_WAIT_ORDERS:
                    btnRightAction.setVisibility(View.VISIBLE);
                    btnRightAction.setText(getString(R.string.appointment_accept));
                    if (item.dispachInfo != null
                            && ("ANGEL_TRANSFER".equals(item.dispachInfo.getDispachType())
                            || "ADMIN_DISPACH".equals(item.dispachInfo.getDispachType()))) {
                        //右边文本按钮
                        tvRightAction.setVisibility(View.VISIBLE);
                        tvRightAction.setText(getString(R.string.with_order_collect_txt_cancle_accept2));
                        tvRightAction.setTextColor(colorTxtBtnGray);

                    }
                    break;
                case AppointmentListBean.APPOINTMENT_WAIT_DISPATCH:
                    btnRightAction.setVisibility(View.VISIBLE);
                    btnRightAction.setText(getString(R.string.appointment_accept));
                    break;
                // 已接单
                case AppointmentListBean.APPOINTMENT_ALREADY_ORDERS:
                    btnRightAction.setVisibility(View.VISIBLE);
                    btnRightAction.setText(
                            getString(R.string.with_order_collect_btn_start_collect));
                    tvRightAction.setVisibility(View.VISIBLE);
                    tvRightAction.setText(getString(R.string.transfer_other));
                    tvRightAction.setTextColor(colorTxtBtnBlue);
                    break;

                default:
                    btnRightAction.setVisibility(View.GONE);
                    break;
            }

            if (isNew) {
                tvCustomerPhone.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
                tvCustomerPhone.getPaint().setAntiAlias(true);
                tvRightAction.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
                tvRightAction.getPaint().setAntiAlias(true);
            }

            setClickEvent(isNew, position, btnRightAction, tvAngelTransfer, tvRightAction);
        }
    }

    private int acceptPosition;

    @SuppressLint("StringFormatInvalid")
    @Override
    public void onItemClick(Object obj, View view, int position) {
        AppointmentListBean item =
                (AppointmentListBean) obj;
        if (null != item) {
            switch (view.getId()) {
                case R.id.tv_angel_transfer:
                    DialogUtil.showTransferDialog(getActivity(), item.dispachInfo);
                    break;
                case R.id.btn_right_action:
                    // 点击按钮
                    // 右边按钮
                    switch (item.getAppointmentStatus()) {
                        // 待接单
                        case AppointmentListBean.APPOINTMENT_WAIT_DISPATCH:
                            receivedOrder(position, item);
                            break;
                        case AppointmentListBean.APPOINTMENT_WAIT_ORDERS:
                            receivedOrder(position, item);
                            break;

                        // 已接单
                        case AppointmentListBean.APPOINTMENT_ALREADY_ORDERS:
                            CreateAppointmentCollectOrderActivity.actionStart(this, item,
                                    REQUEST_CODE_CREATE_COLLECT);
                            break;
                    }
                    break;
                case R.id.tv_right_action:
                    switch (item.getAppointmentStatus()) {
                        //取消
                        case AppointmentListBean.APPOINTMENT_WAIT_ORDERS:
                            cancelOrder(item.getAppointmentCode());
                            break;
                        //转派
                        case AppointmentListBean.APPOINTMENT_ALREADY_ORDERS:
                            TransferOrderActivity.startAct(this, item.getAppointmentCode(),
                                    TransferOrderActivity.APPOINTMENT_TYPE, REQUEST_CODE_TRAMSFER);
                            break;
                    }
                    break;
            }


        }
    }

    /**
     * 接单
     * @param position
     * @param item
     */
    private void receivedOrder(int position, AppointmentListBean item) {
        showWaitDialog();
        acceptPosition = position;
        AppointmentApi.appointmentReceived(item.getAppointmentCode(), getToken(),
                getNewHandler(REQUEST_CODE_RECEIVE, ResultBase.class));
    }

    private void cancelOrder(final String appointmentCode) {
        Context context = getActivity();
        new CommonAlertDialog.Builder(context)
                .setCancelable(true)
                .setMessage(context.getString(R.string.pending_order_get_or_not))
                .setPositiveButton(R.string.btn_cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                            }
                        })
                .setNegativeButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        showWaitDialog();
                        DeliveryApi.appointmentRefusal(getToken(), appointmentCode, getNewHandler(REQUEST_APPOINTMENT_REFUSAL, ResultBase.class));

                    }
                })
                .show();


    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        super.onSuccessResponse(requestCode, jsonString, result);
        switch (requestCode) {
            // 接单
            case REQUEST_CODE_RECEIVE:
                toast(getString(R.string.appoint_receive_success));
                AppointmentListBean item =
                        (AppointmentListBean) getList().get
                                (acceptPosition);
                item.setAppointmentStatus(AppointmentListBean
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
            //取消成功
            case REQUEST_APPOINTMENT_REFUSAL:
                getData();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SCAN_CREATE_COLLECT) {
            initData();
        }

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_TRAMSFER) {
            getData();//转派他人后刷新列表
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
package cn.com.bluemoon.delivery.module.wash.appointment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.AppointmentApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.clothing.collect.ClothesInfo;
import cn.com.bluemoon.delivery.app.api.model.clothing.collect.LaundryLog;
import cn.com.bluemoon.delivery.app.api.model.wash.appointment.ResultAppointmentCollectDetail;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.module.wash.collect.ClothesInfoAdapter;
import cn.com.bluemoon.delivery.module.wash.collect.DeliverLogAdapter;
import cn.com.bluemoon.delivery.ui.NoScrollListView;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;

/**
 * 收衣订单详情页面
 */
public class AppointmentDetailActivity extends BaseActivity implements OnListItemClickListener {

    private static final int REQUEST_CODE_GET_DATA = 0x777;

    /**
     * 洗衣中心名称，状态
     */
    @Bind(R.id.txt_log)
    TextView txtLog;
    /**
     * 日期
     */
    @Bind(R.id.txt_time)
    TextView txtTime;
    /**
     * 洗衣中心名称，状态/n日期 收起、展开
     */
    @Bind(R.id.txt_log_open)
    TextView txtLogOpen;
    /**
     * 动作历史layout
     */
    @Bind(R.id.layout_logs)
    LinearLayout layoutLogs;
    /**
     * 动作历史layout分割线
     */
    @Bind(R.id.line_logs)
    View lineLogs;
    /**
     * 动作历史列表
     */
    @Bind(R.id.list_view_log)
    NoScrollListView listViewLog;
    /**
     * 洗衣订单号
     */
    @Bind(R.id.txt_outer_code)
    TextView txtOuterCode;
    /**
     * 洗衣订单详情分割线
     */
    @Bind(R.id.div_outer_code)
    View divOuterCode;
    /**
     * 洗衣订单详情layout
     */
    @Bind(R.id.ll_outer_detail)
    LinearLayout llOuterDetail;
    /**
     * 洗衣订单标题
     */
    @Bind(R.id.ll_outer_code)
    LinearLayout llOuterCode;
    /**
     * 洗衣订单号 收起、展开
     */
    @Bind(R.id.txt_type_open)
    TextView txtOuterOpen;
    /**
     * 预约单号
     */
    @Bind(R.id.tv_appointment_code)
    TextView tvAppointmentCode;
    /**
     * 消费者姓名
     */
    @Bind(R.id.txt_username)
    TextView txtUsername;
    /**
     * 消费者电话
     */
    @Bind(R.id.txt_user_phone)
    TextView txtUserPhone;
    /**
     * 消费者地址
     */
    @Bind(R.id.txt_address)
    TextView txtAddress;
    /**
     * 收衣单号
     */
    @Bind(R.id.txt_collect_code)
    TextView txtCollectCode;
    /**
     * 收衣列表分割线
     */
    @Bind(R.id.div_listview_info)
    View divListviewInfo;
    /**
     * 收衣列表
     */
    @Bind(R.id.listview_info)
    NoScrollListView listviewInfo;
    /**
     * 实收数量
     */
    @Bind(R.id.tv_actual_sum)
    TextView tvActualSum;
    /**
     * 订单总价
     */
    @Bind(R.id.tv_fee_total)
    TextView tvFeeTotal;

    @Bind(R.id.sc_main)
    ScrollView scMain;
    private String collectCode;

    private List<ClothesInfo> clothes;
    private DeliverLogAdapter deliveryAdapter;
    private ClothesInfoAdapter clothingInfoAdapter;

    public static void actionStart(Context context, String collectCode) {
        Intent intent = new Intent(context, AppointmentDetailActivity.class);
        intent.putExtra("collectCode", collectCode);
        context.startActivity(intent);
    }

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
        collectCode = getIntent().getStringExtra("collectCode");
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.clothing_record_detail);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_appointment_details;
    }

    @Override
    public void initView() {
        clothes = new ArrayList<>();

        deliveryAdapter = new DeliverLogAdapter(this, this);
        clothingInfoAdapter = new ClothesInfoAdapter(this, this);

        clothingInfoAdapter.setList(clothes);

        listViewLog.setAdapter(deliveryAdapter);
        listviewInfo.setAdapter(clothingInfoAdapter);

        closeLog();
        closeOuter();
    }

    @Override
    public void initData() {
        showWaitDialog();
        AppointmentApi.appointmentCollectDetail(collectCode, getToken(),
                getNewHandler(REQUEST_CODE_GET_DATA, ResultAppointmentCollectDetail.class));
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        switch (requestCode) {
            case REQUEST_CODE_GET_DATA:
                ResultAppointmentCollectDetail resultObj = (ResultAppointmentCollectDetail) result;
                setData(resultObj);
                break;
        }
    }

    private void setData(ResultAppointmentCollectDetail resultObj) {
        // 历史
        txtLogOpen.setVisibility(View.GONE);
        layoutLogs.setEnabled(false);
        closeLog();
        if (null != resultObj.getLaundryLog()) {
            List<LaundryLog> logs = resultObj.getLaundryLog();
            if (logs.size() > 0) {
                txtLog.setText(String.format("%s，%s，%s", logs.get(0).getNodeName(),
                        logs.get(0).getPhone(), logs.get(0).getAction())
                );
                txtTime.setText(DateUtil.getTime(logs.get(0).getOpTime(), "yyyy-MM-dd HH:mm"));
            }
            if (logs.size() > 1) {
                logs.remove(0);
                txtLogOpen.setVisibility(View.VISIBLE);
                layoutLogs.setEnabled(true);
                deliveryAdapter.setList(logs);
                deliveryAdapter.notifyDataSetChanged();
            }

        }

        // 洗衣订单
        txtOuterCode.setText(getString(R.string.outer_code, resultObj.getOuterCode()));
        tvAppointmentCode.setText(getString(R.string.appointment_code,
                resultObj.getAppointmentCode()));
        txtUsername.setText(resultObj.getCustomerName());
        txtUserPhone.setText(resultObj.getCustomerPhone());
        txtAddress.setText(String.format("%s%s%s%s%s%s", resultObj.getProvince(),
                resultObj.getCity(),
                resultObj.getCounty(),
                resultObj.getVillage(),
                resultObj.getStreet(),
                resultObj.getAddress()));

        //收衣单
        txtCollectCode.setText(getString(R.string.with_order_collect_collect_number_text_num,
                resultObj.getCollectCode()));
        tvActualSum.setText(getString(R.string.with_order_collect_order_receive_count_num,
                resultObj.getReceivableTotal() + ""));
        tvFeeTotal.setText(getString(R.string.pay_total,
                StringUtil.formatPriceByFen(resultObj.getPayTotal())));

        clothes.clear();
        if (null != resultObj.getCollectOrderDetail()
                && resultObj.getCollectOrderDetail().size() > 0) {
            clothes.addAll(resultObj.getCollectOrderDetail());
            clothingInfoAdapter.notifyDataSetChanged();
            divListviewInfo.setVisibility(View.VISIBLE);
            listviewInfo.setVisibility(View.VISIBLE);
        } else {
            divListviewInfo.setVisibility(View.GONE);
            listviewInfo.setVisibility(View.GONE);
        }

        scMain.postDelayed(new Runnable() {
            @Override
            public void run() {
                scMain.scrollTo(0, 0);
            }
        }, 100);
    }

    private void openLog() {
        lineLogs.setVisibility(View.VISIBLE);
        if (deliveryAdapter.getCount() < 1) {
            listViewLog.setVisibility(View.GONE);
        } else {
            listViewLog.setVisibility(View.VISIBLE);
        }
        txtLogOpen.setText(getString(R.string.txt_close));
        Drawable drawable = getResources().getDrawable(R.mipmap.ic_up);
        assert drawable != null;
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable
                .getMinimumHeight());
        txtLogOpen.setCompoundDrawables(null, null, drawable, null);
    }

    private void closeLog() {
        lineLogs.setVisibility(View.GONE);
        listViewLog.setVisibility(View.GONE);
        txtLogOpen.setText(getString(R.string.txt_open));
        Drawable drawable = getResources().getDrawable(R.mipmap.ic_down);
        assert drawable != null;
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable
                .getMinimumHeight());
        txtLogOpen.setCompoundDrawables(null, null, drawable, null);
    }

    private void openOuter() {
        divOuterCode.setVisibility(View.VISIBLE);
        llOuterDetail.setVisibility(View.VISIBLE);
        txtOuterOpen.setText(getString(R.string.txt_close));
        Drawable drawable = getResources().getDrawable(R.mipmap.ic_up);
        assert drawable != null;
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable
                .getMinimumHeight());
        txtOuterOpen.setCompoundDrawables(null, null, drawable, null);
    }

    private void closeOuter() {
        divOuterCode.setVisibility(View.GONE);
        llOuterDetail.setVisibility(View.GONE);
        txtOuterOpen.setText(getString(R.string.txt_open));
        Drawable drawable = getResources().getDrawable(R.mipmap.ic_down);
        assert drawable != null;
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable
                .getMinimumHeight());
        txtOuterOpen.setCompoundDrawables(null, null, drawable, null);
    }

    @OnClick({R.id.layout_logs, R.id.ll_outer_code})
    public void onClick(View view) {
        switch (view.getId()) {
            // 收起展开历史
            case R.id.layout_logs:
                if (lineLogs.getVisibility() == View.GONE) {
                    openLog();
                } else {
                    closeLog();
                }
                break;
            // 收起洗衣订单
            case R.id.ll_outer_code:
                if (divOuterCode.getVisibility() == View.GONE) {
                    openOuter();
                } else {
                    closeOuter();
                }
                break;
        }
    }

    @Override
    public void onItemClick(Object item, View view, int position) {
        //        if (item instanceof ClothesInfo) {
        //            ClothesInfo info = (ClothesInfo) item;
        //            ClothesDetailActivity.actionStart(this, info.getClothesCode());
        //        }
    }
}

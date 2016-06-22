package cn.com.bluemoon.delivery.module.clothing.collect.withorder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.ClientStateManager;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.clothing.collect.ClothesInfo;
import cn.com.bluemoon.delivery.app.api.model.clothing.collect.OrderDetail;
import cn.com.bluemoon.delivery.app.api.model.clothing.collect.OrderReceiveItem;
import cn.com.bluemoon.delivery.app.api.model.clothing.collect.ResultStartCollectInfos;
import cn.com.bluemoon.delivery.module.base.BaseActionBarActivity;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.module.clothing.collect.ClothingBookInActivity;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.ImageLoaderUtil;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.ViewHolder;
import cn.com.bluemoon.lib.utils.LibConstants;
import cn.com.bluemoon.lib.view.switchbutton.SwitchButton;

/**
 * 收衣登记
 * Created by luokai on 2016/6/12.
 */
public class WithOrderCollectBookInActivity extends BaseActionBarActivity implements
        OnListItemClickListener {
    private static final int RESULT_CODE_MANUAL = 0x23;
    private static final int REQUEST_CODE_MANUAL = 0x43;

    private final static int REQUEST_CODE_CLOTHING_BOOK_IN_ACTIVITY = 0x13;
    /**
     * 洗衣服务订单号
     */
    public final static String EXTRA_OUTERCODE = "EXTRA_OUTERCODE";
    /**
     * 收衣单号
     */
    public final static String EXTRA_COLLECTCODE = "EXTRA_COLLECTCODE";
    @Bind(R.id.tv_number)
    TextView tvNumber;
    @Bind(R.id.tv_customer_name)
    TextView tvCustomerName;
    @Bind(R.id.tv_customer_phone)
    TextView tvCustomerPhone;
    @Bind(R.id.tv_address)
    TextView tvAddress;
    @Bind(R.id.tv_pay_total)
    TextView tvPayTotal;
    @Bind(R.id.tv_receivable_count)
    TextView tvReceivableCount;
    @Bind(R.id.tv_actual_count)
    TextView tvActualCount;
    @Bind(R.id.tv_collect_code)
    TextView tvCollectCode;
    @Bind(R.id.sb_urgent)
    SwitchButton sbUrgent;
    @Bind(R.id.tv_appoint_back_time)
    TextView tvAppointBackTime;
    @Bind(R.id.v_div_appoint_back_time)
    View vDivAppointBackTime;
    @Bind(R.id.ll_appoint_back_time)
    LinearLayout llAppointBackTime;
    @Bind(R.id.lv_order_detail)
    ListView lvOrderDetail;
    @Bind(R.id.tv_actual_collect_count)
    TextView tvActualCollectCount;
    @Bind(R.id.lv_order_receive)
    ListView lvOrderReceive;

    /**
     * 洗衣服务订单号
     */
    private String outerCode;
    /**
     * 收衣单号
     */
    private String collectCode;

    private ClothesInfoAdapter clothesInfoAdapter;
    private OrderDetailAdapter orderDetailAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_order_collect_book_in);
        ButterKnife.bind(this);

        outerCode = getIntent().getStringExtra(EXTRA_OUTERCODE);
        collectCode = getIntent().getStringExtra(EXTRA_COLLECTCODE);

        sbUrgent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    llAppointBackTime.setVisibility(View.VISIBLE);
                    vDivAppointBackTime.setVisibility(View.VISIBLE);
                } else {
                    llAppointBackTime.setVisibility(View.VISIBLE);
                    vDivAppointBackTime.setVisibility(View.VISIBLE);
                }
                // TODO: lk 2016/6/21 发送加急更改
            }
        });

        clothesInfoAdapter = new ClothesInfoAdapter(this, this);
        lvOrderReceive.setAdapter(clothesInfoAdapter);

        orderDetailAdapter = new OrderDetailAdapter(this, this);
        lvOrderDetail.setAdapter(orderDetailAdapter);

        getData();
    }

    private void getData() {
        String token = ClientStateManager.getLoginToken(this);
        showProgressDialog();
        DeliveryApi.startCollectInfo(token, outerCode, collectCode, startCollectInfoHandler);
    }

    /**
     * 获取收衣登记界面数据返回
     */
    AsyncHttpResponseHandler startCollectInfoHandler = new TextHttpResponseHandler(
            HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            // TODO: lk 2016/6/21 待测试
            LogUtils.d(getDefaultTag(), "startCollectInfo result = " + responseString);
            dismissProgressDialog();
            try {
                ResultStartCollectInfos result = JSON.parseObject(responseString,
                        ResultStartCollectInfos.class);
                if (result.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    setStartCollectInfo(result);
                } else {
                    PublicUtil.showErrorMsg(WithOrderCollectBookInActivity.this, result);
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
            PublicUtil.showToastServerOvertime();
        }
    };

    /**
     * 设置数据
     *
     * @param result
     */
    private void setStartCollectInfo(ResultStartCollectInfos result) {
        collectCode = result.getOrderReceive().getCollectCode();
        outerCode = result.getOuterCode();

        tvNumber.setText(result.getOuterCode());
        tvCustomerName.setText(result.getCustomerName());

        tvCustomerPhone.setText(result.getCustomerPhone());
        tvCustomerPhone.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        tvCustomerPhone.getPaint().setAntiAlias(true);

        StringBuilder address = new StringBuilder(result.getProvince()).append(result.getCity())
                .append(result.getCounty()).append(result.getStreet()).append(result.getVillage()
                ).append(result.getAddress());
        tvAddress.setText(address);

        tvPayTotal.setText(result.getPayTotal() / 100.0 + "");
        tvReceivableCount.setText(result.getReceivableCount() + "");
        tvActualCount.setText(result.getActualCount() + "");

        // 包装袋码（收衣单条码）
        tvCollectCode.setEnabled(true);

        sbUrgent.setEnabled(true);
        sbUrgent.setChecked(result.getIsUrgent() == 1);

        tvAppointBackTime.setText(result.getAppointBackTime() <= 0 ? "" : DateUtil.getTime(result
                .getAppointBackTime(), "YYYY-MM-DD hh:mm"));

        tvActualCollectCount.setText(getString(R.string.with_order_collect_order_receive_count) +
                " " + result.getOrderReceive().getCollectCount());

        clothesInfoAdapter.setList(result.getOrderReceive().getClothesInfo());
        clothesInfoAdapter.notifyDataSetInvalidated();

        orderDetailAdapter.setList(result.getOrderDetail());
        orderDetailAdapter.notifyDataSetInvalidated();
    }

    @Override
    protected int getActionBarTitleRes() {
        return R.string.title_with_order_collect_book_in;
    }

    @OnClick({R.id.tv_customer_phone, R.id.tv_collect_code, R.id.tv_appoint_back_time})
    public void onClick(View view) {
        switch (view.getId()) {
            // 打电话
            case R.id.tv_customer_phone:
                PublicUtil.callPhone(WithOrderCollectBookInActivity.this, tvCustomerPhone.getText
                        ().toString());
                break;

            // 扫收衣单条码
            case R.id.tv_collect_code:
                goScanCode();
                break;

            // TODO: lk 2016/6/21 预约时间
            case R.id.tv_appoint_back_time:
                break;
        }
    }

    /**
     * 打开扫码界面
     */
    private void goScanCode() {
        PublicUtil.openScanOrder(this, null, getString(R.string.coupons_scan_code_title),
                getString(R.string.with_order_collect_manual_input_code_btn),
                Constants.REQUEST_SCAN, RESULT_CODE_MANUAL);
    }

    /**
     * 处理扫码、手动输入数字码返回
     *
     * @param code
     */
    private void handleScaneCodeBack(String code) {
        // TODO: lk  2016/6/20 处理扫码、手动输入数字码返回
        PublicUtil.showToast("处理扫码、手动输入数字码返回" + code);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED) {
            return;
        }

        switch (requestCode) {
            // TODO: lk 2016/6/14  衣物登记返回
            case REQUEST_CODE_CLOTHING_BOOK_IN_ACTIVITY:
                if (resultCode == Activity.RESULT_OK) {

                }
                break;

            case Constants.REQUEST_SCAN:
                // 扫码返回
                if (resultCode == Activity.RESULT_OK) {
                    String resultStr = data.getStringExtra(LibConstants.SCAN_RESULT);
                    handleScaneCodeBack(resultStr);
                }
                //   跳转到手动输入
                else if (resultCode == RESULT_CODE_MANUAL) {
                    Intent intent = new Intent(this, ManualInputCodeActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_MANUAL);
                }
                break;

            // 手动输入返回
            case REQUEST_CODE_MANUAL:
                // 数字码返回
                if (resultCode == Activity.RESULT_OK) {
                    String resultStr = data.getStringExtra(ManualInputCodeActivity
                            .RESULT_EXTRA_CODE);
                    handleScaneCodeBack(resultStr);
                }
                //  跳转到扫码输入
                else if (resultCode == ManualInputCodeActivity.RESULT_CODE_SCANE_CODE) {
                    goScanCode();
                }
                break;

            default:
                break;
        }
    }

    /**
     * 收衣明细Adapter
     */
    class ClothesInfoAdapter extends BaseListAdapter<ClothesInfo> {
        public ClothesInfoAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.item_with_order_clothes_info;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            final ClothesInfo item = (ClothesInfo) getItem(position);
            if (item == null) {
                return;
            }

            ImageView ivClothImg = ViewHolder.get(convertView, R.id.iv_cloth_img);
            TextView tvClothesCode = ViewHolder.get(convertView, R.id.tv_clothes_code);
            TextView tvTypeName = ViewHolder.get(convertView, R.id.tv_type_name);
            TextView tvClothesName = ViewHolder.get(convertView, R.id.tv_clothes_name);

            tvClothesCode.setText(item.getClothesCode());
            tvTypeName.setText(item.getTypeName());
            tvClothesName.setText(item.getClothesName());

            ImageLoaderUtil.displayImage(context, item.getImgPath(), ivClothImg);

            setClickEvent(isNew, position, convertView);
        }
    }

    /**
     * 衣物类型Adapter
     */
    class OrderDetailAdapter extends BaseListAdapter<OrderDetail> {
        public OrderDetailAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.item_with_order_detail;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            final OrderDetail item = (OrderDetail) getItem(position);
            if (item == null) {
                return;
            }

            View vAdd = ViewHolder.get(convertView, R.id.v_add);
            TextView tvTypeName = ViewHolder.get(convertView, R.id.tv_type_name);
            TextView tvReceivableCount = ViewHolder.get(convertView, R.id.tv_receivable_count);
            TextView tvActualCount = ViewHolder.get(convertView, R.id.tv_actual_count);

            tvTypeName.setText(item.getTypeName());

            int receivableCount = item.getReceivableCount();
            tvReceivableCount.setText(getString(R.string
                    .with_order_collect_appoint_receivable_count) + receivableCount);

            int actualCount = item.getActualCount();
            tvActualCount.setText(getString(R.string
                    .with_order_collect_appoint_actual_count) + actualCount);

            // 可点击
            if (receivableCount > actualCount) {
                vAdd.setEnabled(true);
            }
            // 不可点击
            else {
                vAdd.setEnabled(false);
            }

            setClickEvent(isNew, position, vAdd);
        }
    }

    @Override
    public void onItemClick(Object item, View view, int position) {
        // 点击收衣明细项
        if (item instanceof ClothesInfo) {
            ClothesInfo info = (ClothesInfo) item;
            // TODO: lk 2016/6/21  点击收衣明细项
        }

        // 点击洗衣类型项的加号
        else if (item instanceof OrderDetail) {
            OrderDetail type = (OrderDetail) item;
            //  添加衣物
            Intent intent = new Intent(WithOrderCollectBookInActivity.this,
                    ClothingBookInActivity.class);
            intent.putExtra(ClothingBookInActivity.EXTRA_COLLECT_CODE, collectCode);
            intent.putExtra(ClothingBookInActivity.EXTRA_OUTER_CODE, outerCode);
            intent.putExtra(ClothingBookInActivity.EXTRA_TYPE_CODE, type.getTypeCode());
            WithOrderCollectBookInActivity.this.startActivityForResult(intent,
                    REQUEST_CODE_CLOTHING_BOOK_IN_ACTIVITY);
        }
    }
}

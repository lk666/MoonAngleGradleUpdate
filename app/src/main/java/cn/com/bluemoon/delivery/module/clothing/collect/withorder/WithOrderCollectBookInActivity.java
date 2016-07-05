package cn.com.bluemoon.delivery.module.clothing.collect.withorder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
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
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.clothing.collect.ClothesInfo;
import cn.com.bluemoon.delivery.app.api.model.clothing.collect.OrderDetail;
import cn.com.bluemoon.delivery.app.api.model.clothing.collect.ResultStartCollectInfos;
import cn.com.bluemoon.delivery.module.base.BaseActionBarActivity;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.module.clothing.collect.ClothesInfoAdapter;
import cn.com.bluemoon.delivery.module.clothing.collect.ClothingBookInActivity;
import cn.com.bluemoon.delivery.ui.DateTimePickDialogUtil;
import cn.com.bluemoon.delivery.ui.NoScrollListView;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DateUtil;
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
    /**
     * 扫描二维码跳转到手动输入的返回码
     */
    private static final int RESULT_CODE_TO_MANUAL = 0x23;
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
    @Bind(R.id.tv_collect_brcode)
    TextView tvCollectBrcode;
    @Bind(R.id.sb_urgent)
    SwitchButton sbUrgent;
    @Bind(R.id.tv_appoint_back_time)
    TextView tvAppointBackTime;
    @Bind(R.id.v_div_appoint_back_time)
    View vDivAppointBackTime;
    @Bind(R.id.ll_appoint_back_time)
    LinearLayout llAppointBackTime;
    @Bind(R.id.lv_order_detail)
    NoScrollListView lvOrderDetail;
    @Bind(R.id.tv_actual_collect_count)
    TextView tvActualCollectCount;
    @Bind(R.id.lv_order_receive)
    NoScrollListView lvOrderReceive;
    @Bind(R.id.v_div_isurgent)
    View vDivIsurgent;
    @Bind(R.id.ll_isurgent)
    LinearLayout llIsurgent;

    /**
     * 实际预约时间
     */
    private long appointBackTime = 0;

    /**
     * 洗衣服务订单号
     */
    private String outerCode;

    /**
     * 初始化/刷新界面数据是否已完成
     */
    private boolean isInitFinished = false;

    /**
     * 初始化的收衣单条码+加急+预约时间字符串，用于是否在新增或修改衣物信息后发送更新预约等信息的请求
     */
    private String initInfoString;

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
                if (!isInitFinished) {
                    return;
                }

                if (isChecked) {
                    vDivAppointBackTime.setVisibility(View.VISIBLE);
                    llAppointBackTime.setVisibility(View.VISIBLE);
                    tvAppointBackTime.setText("");
                    appointBackTime = 0;
                    // 时间选择
                    getAppointBackTime();
                } else {
                    vDivAppointBackTime.setVisibility(View.GONE);
                    llAppointBackTime.setVisibility(View.GONE);
                    tvAppointBackTime.setText("");
                    appointBackTime = 0;
                }
            }
        });

        vDivIsurgent.setVisibility(View.GONE);
        llIsurgent.setVisibility(View.GONE);

        clothesInfoAdapter = new ClothesInfoAdapter(this, this);
        lvOrderReceive.setAdapter(clothesInfoAdapter);

        orderDetailAdapter = new OrderDetailAdapter(this, this);
        lvOrderDetail.setAdapter(orderDetailAdapter);

        getData();
    }

    /**
     * 加急改为true时弹出预约时间选择框
     */
    private void getAppointBackTime() {
        // 至少48小时后的时间
        final long minTime = System.currentTimeMillis() / 1000 + 49 * 3600;
        DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(this, minTime,
                new DateTimePickDialogUtil.OnDetailClickLister() {
                    @Override
                    public void btnClickLister(long time, String datetime) {
                        if (datetime.equals("time")) {
                            // 只能选择48小时之后的时间
                            if (time + 3600 < minTime) {
                                PublicUtil.showToast(getString(R.string
                                        .with_order_collect_appoint_back_time_later_hint));
                            } else {
                                // 预约时间更改
                                appointBackTime = time * 1000;
                                tvAppointBackTime.setText(DateUtil.getTime(appointBackTime,
                                        "yyyy-MM-dd " + "HH:mm"));
                                return;
                            }
                        }

                        sbUrgent.setChecked(false);
                    }
                });
        dateTimePicKDialog.dateTimePicKDialog();
    }

    private void getData() {
        isInitFinished = false;
        initInfoString = "";
        String token = ClientStateManager.getLoginToken(this);
        showProgressDialog();
        DeliveryApi.startCollectInfo(token, outerCode, collectCode,
                createResponseHandler(new IHttpResponseHandler() {
                    @Override
                    public void onResponseSuccess(String responseString) {
                        ResultStartCollectInfos result = JSON.parseObject(responseString,
                                ResultStartCollectInfos.class);
                        // TODO: lk 2016/7/2 待测试
                        setStartCollectInfo(result);
                    }
                }));
    }

    /**
     * 设置数据
     *
     * @param result
     */
    private void setStartCollectInfo(ResultStartCollectInfos result) {
        collectCode = result.getCollectCode();
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

        tvPayTotal.setText(String.format("%.2f", (result.getPayTotal() / 100.0)));
        tvReceivableCount.setText(result.getReceivableCount() + "");
        tvActualCount.setText(result.getActualCount() + "");

        // 包装袋码（收衣单条码）
        tvCollectBrcode.setEnabled(true);
        tvCollectBrcode.setText(result.getCollectBrcode());

        appointBackTime = result.getAppointBackTime();
        if (result.getIsUrgent() == 1) {
            sbUrgent.setChecked(true);
            llAppointBackTime.setVisibility(View.VISIBLE);
            vDivAppointBackTime.setVisibility(View.VISIBLE);
            tvAppointBackTime.setText(DateUtil.getTime(appointBackTime, "yyyy-MM-dd HH:mm"));
        } else {
            llAppointBackTime.setVisibility(View.GONE);
            vDivAppointBackTime.setVisibility(View.GONE);
            sbUrgent.setChecked(false);
        }

        tvActualCollectCount.setText(getString(R.string.with_order_collect_order_receive_count) +
                " " + result.getOrderReceive().getCollectCount());

        if (TextUtils.isEmpty(collectCode)) {
            vDivIsurgent.setVisibility(View.GONE);
            llIsurgent.setVisibility(View.GONE);
        } else {
            vDivIsurgent.setVisibility(View.VISIBLE);
            llIsurgent.setVisibility(View.VISIBLE);
        }

        clothesInfoAdapter.setList(result.getOrderReceive().getClothesInfo());
        clothesInfoAdapter.notifyDataSetInvalidated();

        orderDetailAdapter.setList(result.getOrderDetail());
        orderDetailAdapter.notifyDataSetInvalidated();

        findViewById(R.id.main).scrollTo(0, 0);

        initInfoString = getInfoString(result.getCollectBrcode(), appointBackTime, result
                .getIsUrgent());
        isInitFinished = true;
    }

    private String getInfoString(String collectBrcode, long appointBackTime, int isUrgent) {
        return (collectBrcode + appointBackTime) + isUrgent;
    }

    @Override
    protected int getActionBarTitleRes() {
        return R.string.title_with_order_collect_book_in;
    }

    @OnClick({R.id.tv_collect_brcode, R.id.tv_appoint_back_time})
    public void onClick(View view) {
        switch (view.getId()) {
            // 扫收衣单条码
            case R.id.tv_collect_brcode:
                goScanCode();
                break;

            // 预约时间
            case R.id.tv_appoint_back_time:
                selectAppointBackTime();
                break;
        }
    }

    /**
     * 改变预约时间弹出预约时间选择框
     */
    private void selectAppointBackTime() {
        // 至少48小时后的时间
        final long minTime = System.currentTimeMillis() / 1000 + 49 * 3600;
        DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(this, minTime, new
                DateTimePickDialogUtil.OnDetailClickLister() {
                    @Override
                    public void btnClickLister(long time, String datetime) {
                        if (datetime.equals("time")) {
                            // 只能选择48小时之后的时间
                            if (time + 3600 < minTime) {
                                PublicUtil.showToast(getString(R.string
                                        .with_order_collect_appoint_back_time_later_hint));
                            } else {
                                // 预约时间更改
                                appointBackTime = time * 1000;
                                tvAppointBackTime.setText(DateUtil.getTime(appointBackTime,
                                        "yyyy-MM-dd " + "HH:mm"));
                            }
                        }
                    }
                });
        dateTimePicKDialog.dateTimePicKDialog();
    }

    /**
     * 打开扫码界面
     */
    private void goScanCode() {
        PublicUtil.openScan(this, getString(R.string.coupons_scan_code_title),
                getString(R.string.with_order_collect_manual_input_code_btn),
                Constants.REQUEST_SCAN, RESULT_CODE_TO_MANUAL);
    }

    /**
     * 处理扫码、手动输入数字码返回
     *
     * @param code
     */
    private void handleScaneCodeBack(String code) {
        // TODO: lk 2016/7/2 应加个接口验证收衣单条码合法性
        tvCollectBrcode.setText(code);
    }

//    /**
//     * 改变收衣单条码返回
//     */
//    AsyncHttpResponseHandler collectBrcodeChangeHandler = new TextHttpResponseHandler(
//            HTTP.UTF_8) {
//
//        @Override
//        public void onSuccess(int statusCode, Header[] headers, String responseString) {
//            LogUtils.d(getDefaultTag(), " 改变收衣单条码返回 result = " + responseString);
//            dismissProgressDialog();
//            try {
//                ResultBase result = JSON.parseObject(responseString,
//                        ResultBase.class);
//                if (result.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
//                    tvCollectBrcode.setText(tmpCollectBrcode);
//                } else {
//                    PublicUtil.showErrorMsg(WithOrderCollectBookInActivity.this, result);
//                }
//            } catch (Exception e) {
//                LogUtils.e(getDefaultTag(), e.getMessage());
//                PublicUtil.showToastServerBusy();
//            }
//        }
//
//        @Override
//        public void onFailure(int statusCode, Header[] headers,
//                              String responseString, Throwable throwable) {
//            LogUtils.e(getDefaultTag(), throwable.getMessage());
//            dismissProgressDialog();
//            PublicUtil.showToastServerOvertime();
//        }
//    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED) {
            return;
        }

        switch (requestCode) {
            case REQUEST_CODE_CLOTHING_BOOK_IN_ACTIVITY:

                // TODO: lk 2016/7/5 利用返回新增的clothescode
                // 保存成功
                if (resultCode == ClothingBookInActivity.RESULT_CODE_SAVE_CLOTHES_SUCCESS) {
                    checkInfo();
                }
                // 删除成功，不需要发送修改信息
                else if (resultCode == ClothingBookInActivity.RESULT_CODE_DELETE_CLOTHES_SUCCESS) {
                    getData();
                }
                break;

            case Constants.REQUEST_SCAN:
                // 扫码返回
                if (resultCode == Activity.RESULT_OK) {
                    String resultStr = data.getStringExtra(LibConstants.SCAN_RESULT);
                    handleScaneCodeBack(resultStr);
                }
                //   跳转到手动输入
                else if (resultCode == RESULT_CODE_TO_MANUAL) {
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
     * 检查{@link #initInfoString}，先对比预约时间等的3个的信息是否变更，在发送update预约，再getData()
     */
    private void checkInfo() {
        String brCode = tvCollectBrcode.getText().toString();
        int isUrgent = sbUrgent.isChecked() ? 1 : 0;
        if (getInfoString(brCode, appointBackTime, isUrgent).equals(initInfoString)) {
            getData();
        } else {
            updateCollectInfoParam(appointBackTime, brCode, isUrgent);
        }

    }

    /**
     * 更改收衣登记预约时间等信息
     */
    private void updateCollectInfoParam(long appointBackTime, String collectBrcode,
                                        int isUrgent) {
        if (collectBrcode == null) {
            collectBrcode = "";
        }

        showProgressDialog();
        DeliveryApi.updateCollectInfoParam(ClientStateManager.getLoginToken(this),
                appointBackTime, collectBrcode, collectCode, isUrgent,
                updateCollectInfoParamHandler);
    }

    /**
     * 更改收衣登记回调
     */
    protected AsyncHttpResponseHandler updateCollectInfoParamHandler = new TextHttpResponseHandler(
            HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            LogUtils.d(getDefaultTag(), "更改收衣登记回调 result = " + responseString);
            dismissProgressDialog();
            try {
                ResultBase result = JSON.parseObject(responseString,
                        ResultBase.class);
                if (result.getResponseCode() != Constants.RESPONSE_RESULT_SUCCESS) {
                    PublicUtil.showErrorMsg(WithOrderCollectBookInActivity.this, result);
                }
            } catch (Exception e) {
                LogUtils.e(getDefaultTag(), e.getMessage());
                PublicUtil.showToastServerBusy();
            }
            getData();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers,
                              String responseString, Throwable throwable) {
            LogUtils.e(getDefaultTag(), throwable.getMessage());
            dismissProgressDialog();
            PublicUtil.showToastServerOvertime();
            getData();
        }
    };

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

            Button vAdd = ViewHolder.get(convertView, R.id.btn_add);
            TextView tvTypeName = ViewHolder.get(convertView, R.id.tv_type_name);
            TextView tvReceivableCount = ViewHolder.get(convertView, R.id.tv_receivable_count);
            TextView tvActualCount = ViewHolder.get(convertView, R.id.tv_actual_count);

            tvTypeName.setText(item.getTypeName());

            int receivableCount = item.getDetailReceivableCount();
            tvReceivableCount.setText(getString(R.string
                    .with_order_collect_appoint_receivable_count) + receivableCount);

            int actualCount = item.getDetailActualCount();
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
            // 修改衣物
            ClothingBookInActivity.actionStart(this, collectCode, outerCode, info.getTypeName()
                    , info.getTypeCode(), true, info.getClothesCode(),
                    REQUEST_CODE_CLOTHING_BOOK_IN_ACTIVITY);
        }

        // 点击洗衣类型项的加号, 添加衣物
        else if (item instanceof OrderDetail) {
            // 先校验是否已经扫描收衣条码，没有扫描提示：还未扫描收衣条码，请扫描后继续操作。
            if (TextUtils.isEmpty(tvCollectBrcode.getText().toString())) {
                PublicUtil.showToast(getString(R.string.notice_add_clothes_no_brcode));
            } else {
                OrderDetail type = (OrderDetail) item;
                ClothingBookInActivity.actionStart(this, collectCode, outerCode, type.getTypeName()
                        , type.getTypeCode(), false, null, REQUEST_CODE_CLOTHING_BOOK_IN_ACTIVITY);
            }
        }
    }
}
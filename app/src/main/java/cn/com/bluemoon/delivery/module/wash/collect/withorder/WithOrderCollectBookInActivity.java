package cn.com.bluemoon.delivery.module.wash.collect.withorder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.protocol.HTTP;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.clothing.collect.ClothesInfo;
import cn.com.bluemoon.delivery.app.api.model.clothing.collect.OrderDetail;
import cn.com.bluemoon.delivery.app.api.model.clothing.collect.ResultStartCollectInfos;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.module.oldbase.BaseActionBarActivity;
import cn.com.bluemoon.delivery.module.wash.collect.ClothesDetailActivity;
import cn.com.bluemoon.delivery.module.wash.collect.ClothesInfoAdapter;
import cn.com.bluemoon.delivery.ui.DateTimePickDialogUtil;
import cn.com.bluemoon.delivery.ui.NoScrollListView;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.ViewHolder;
import cn.com.bluemoon.lib.utils.LibConstants;
import cn.com.bluemoon.lib.view.switchbutton.SwitchButton;
import cz.msebera.android.httpclient.Header;

/**
 * 收衣登记
 * Created by luokai on 2016/6/12.
 */
public class WithOrderCollectBookInActivity extends BaseActionBarActivity implements
        OnListItemClickListener {
    /**
     * 扫描二维码跳转到手动输入的返回码
     */
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
    @BindView(R.id.tv_number)
    TextView tvNumber;
    @BindView(R.id.tv_customer_name)
    TextView tvCustomerName;
    @BindView(R.id.tv_customer_phone)
    TextView tvCustomerPhone;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.tv_pay_total)
    TextView tvPayTotal;
    @BindView(R.id.tv_receivable_count)
    TextView tvReceivableCount;
    @BindView(R.id.tv_actual_count)
    TextView tvActualCount;
    @BindView(R.id.tv_collect_brcode)
    TextView tvCollectBrcode;
    @BindView(R.id.sb_urgent)
    SwitchButton sbUrgent;
    @BindView(R.id.tv_appoint_back_time)
    TextView tvAppointBackTime;
    @BindView(R.id.v_div_appoint_back_time)
    View vDivAppointBackTime;
    @BindView(R.id.ll_appoint_back_time)
    LinearLayout llAppointBackTime;
    @BindView(R.id.lv_order_detail)
    NoScrollListView lvOrderDetail;
    @BindView(R.id.tv_actual_collect_count)
    TextView tvActualCollectCount;
    @BindView(R.id.lv_order_receive)
    NoScrollListView lvOrderReceive;
    @BindView(R.id.main)
    ScrollView main;

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

    private List<ClothesInfo> initClothes;

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
                        setStartCollectInfo(result);
                    }
                }));
    }

    /**
     * 设置数据
     */
    @SuppressLint("DefaultLocale")
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
        tvReceivableCount.setText(String.valueOf(result.getReceivableCount()));
        tvActualCount.setText(String.valueOf(result.getActualCount()));

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

        tvActualCollectCount.setText(
                String.format(getString(R.string.with_order_collect_order_receive_count_num),
                        result.getOrderReceive().getActualCount() + ""));

        if (initClothes == null) {
            initClothes = new ArrayList<>();
            initClothes.addAll(result.getOrderReceive().getClothesInfo());
        }
        clothesInfoAdapter.setList(result.getOrderReceive().getClothesInfo());
        clothesInfoAdapter.notifyDataSetInvalidated();

        orderDetailAdapter.setList(result.getOrderDetail());
        orderDetailAdapter.notifyDataSetInvalidated();

        findViewById(R.id.main).scrollTo(0, 0);

        initInfoString = getInfoString(result.getCollectBrcode(), appointBackTime, result
                .getIsUrgent());
        isInitFinished = true;

        main.postDelayed(new Runnable() {
            @Override
            public void run() {
                main.scrollTo(0, 0);
            }
        }, 100);
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
        PublicUtil.openClothScan(this, getString(R.string.coupons_scan_code_title),
                getString(R.string.with_order_collect_manual_input_code_btn), Constants
                        .REQUEST_SCAN);
    }

    /**
     * 处理扫码、手动输入数字码返回
     */
    private void handleScaneCodeBack(String code) {
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

        if (requestCode == REQUEST_CODE_CLOTHING_BOOK_IN_ACTIVITY) {
            // 利用返回新增的clothescode
            // 保存成功
            if (resultCode == ClothingBookInActivity.RESULT_CODE_SAVE_CLOTHES_SUCCESS) {
                if (TextUtils.isEmpty(collectCode)) {
                    collectCode = data.getStringExtra(ClothingBookInActivity
                            .RESULT_COLLECT_CODE);
                }
                checkInfo(collectCode);
            }
            // 删除成功，或其他情况（包括未操作，因为删除已保存图片再点击后退也需要刷新），直接刷新数据
            else {
                getData();
            }
            return;
        }

        if (resultCode == Activity.RESULT_CANCELED) {
            return;
        }

        switch (requestCode) {
            case Constants.REQUEST_SCAN:
                // 扫码返回
                if (resultCode == Activity.RESULT_OK) {
                    String resultStr = data.getStringExtra(LibConstants.SCAN_RESULT);
                    handleScaneCodeBack(resultStr);
                }
                //   跳转到手动输入
                else if (resultCode == Constants.RESULT_SCAN) {
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
    private void checkInfo(String collectCode) {
        String brCode = tvCollectBrcode.getText().toString();
        int isUrgent = sbUrgent.isChecked() ? 1 : 0;
        if (getInfoString(brCode, appointBackTime, isUrgent).equals(initInfoString)) {
            getData();
        } else {
            updateCollectInfoParam(collectCode, appointBackTime, brCode, isUrgent);
        }

    }

    /**
     * 更改收衣登记预约时间等信息
     */
    private void updateCollectInfoParam(String collectCode, long appointBackTime,
                                        String collectBrcode, int isUrgent) {
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

            ImageButton vAdd = ViewHolder.get(convertView, R.id.btn_add);
            TextView tvTypeName = ViewHolder.get(convertView, R.id.tv_type_name);
            TextView tvReceivableCount = ViewHolder.get(convertView, R.id.tv_receivable_count);
            TextView tvActualCount = ViewHolder.get(convertView, R.id.tv_actual_count);

            tvTypeName.setText(item.getTypeName());

            int receivableCount = item.getDetailReceivableCount();
            tvReceivableCount.setText(String.format(getString(R.string
                    .with_order_collect_appoint_receivable_count_num), receivableCount + ""));

            int actualCount = item.getDetailActualCount();
            tvActualCount.setText(
                    String.format(getString(R.string.create_collect_dialog_actual_receive_num),
                            actualCount + ""));

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

            // 查看详情
            if (initClothes != null && initClothes.contains(info) &&
                    !ClientStateManager.getUserName().equals(((ClothesInfo) item)
                            .getReceiveCode())) {
                ClothesDetailActivity.actionStart(this, info.getClothesCode());
            } else {
                // 修改衣物
                ClothingBookInActivity.actionStart(this, collectCode, outerCode, info.getTypeName()
                        , info.getTypeCode(), true, info.getClothesCode(),
                        REQUEST_CODE_CLOTHING_BOOK_IN_ACTIVITY);
            }
        }

        // 点击洗衣类型项的加号, 添加衣物
        else if (item instanceof OrderDetail) {
            //            // 先校验是否已经扫描收衣条码，没有扫描提示：还未扫描收衣条码，请扫描后继续操作。
            //            if (TextUtils.isEmpty(tvCollectBrcode.getText().toString())) {
            //                PublicUtil.showToast(getString(R.string
            // .notice_add_clothes_no_brcode));
            //            } else {
            OrderDetail type = (OrderDetail) item;
            ClothingBookInActivity.actionStart(this, collectCode, outerCode, type.getTypeName()
                    , type.getTypeCode(), false, null, REQUEST_CODE_CLOTHING_BOOK_IN_ACTIVITY);
            //            }
        }
    }
}
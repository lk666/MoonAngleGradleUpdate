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
     * 是否加急
     */
    private boolean isUrgent = false;

    /**
     * 记录的预约时间
     */
    private long tmpAppointBackTime = 0;

    /**
     * 实际预约时间
     */
    private long appointBackTime = 0;

    /**
     * 洗衣服务订单号
     */
    private String outerCode;

    /**
     * 临时记录的扫码返回的收衣单号
     */
    private String tmpCollectBrcode;

    /**
     * 收衣单条码号
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
                // 否->是
                if (isChecked && !isUrgent) {
                    isUrgent = true;
                    getAppointBackTime(isUrgentChangeHandler);
                }
                // 是->否
                else if (!isChecked && isUrgent) {
                    isUrgent = false;
                    updateCollectInfoParam(0, tvCollectBrcode.getText().toString(), false,
                            isUrgentChangeHandler);
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
     * 改变加急返回
     */
    AsyncHttpResponseHandler isUrgentChangeHandler = new TextHttpResponseHandler(
            HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            // TODO: lk 2016/6/21 待测试
            LogUtils.d(getDefaultTag(), "改变加急返回 result = " + responseString);
            dismissProgressDialog();
            try {
                ResultBase result = JSON.parseObject(responseString,
                        ResultBase.class);
                if (result.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    // 否->是
                    if (isUrgent) {
                        llAppointBackTime.setVisibility(View.VISIBLE);
                        vDivAppointBackTime.setVisibility(View.VISIBLE);
                        appointBackTime = tmpAppointBackTime;
                        tvAppointBackTime.setText(DateUtil.getTime(appointBackTime, "yyyy-MM-dd " +
                                "HH:mm"));
                    }
                    // 是->否
                    else {
                        llAppointBackTime.setVisibility(View.GONE);
                        vDivAppointBackTime.setVisibility(View.GONE);
                        appointBackTime = 0;
                        tvAppointBackTime.setText("");
                    }
                    return;
                } else {
                    PublicUtil.showErrorMsg(WithOrderCollectBookInActivity.this, result);
                }
            } catch (Exception e) {
                LogUtils.e(getDefaultTag(), e.getMessage());
                PublicUtil.showToastServerBusy();
            }

            // 否->是
            if (isUrgent) {
                isUrgent = false;
                sbUrgent.setChecked(false);
            }
            // 是->否
            else {
                isUrgent = true;
                sbUrgent.setChecked(true);
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers,
                              String responseString, Throwable throwable) {
            LogUtils.e(getDefaultTag(), throwable.getMessage());
            dismissProgressDialog();
            // 否->是
            if (isUrgent) {
                isUrgent = false;
                sbUrgent.setChecked(false);
            }
            // 是->否
            else {
                isUrgent = true;
                sbUrgent.setChecked(true);
            }
            PublicUtil.showToastServerOvertime();
        }
    };

    /**
     * 加急改为true时弹出预约时间选择框
     */
    private void getAppointBackTime(final AsyncHttpResponseHandler handler) {
        // 至少48小时后的时间
        final long maxTime = System.currentTimeMillis() / 1000 + 48 * 3600;
        DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
                WithOrderCollectBookInActivity.this, maxTime, new
                DateTimePickDialogUtil.OnDetailClickLister() {
                    @Override
                    public void btnClickLister(long time, String datetime) {
                        if (datetime.equals("time")) {
                            // 只能选择48小时之后的时间
                            if (time < maxTime) {
                                PublicUtil.showToast(getString(R.string
                                        .with_order_collect_appoint_back_time_later_hint));
                            } else {
                                // 发送加急更改
                                updateCollectInfoParam(time * 1000, tvCollectBrcode.getText()
                                        .toString(), true, handler);
                                return;
                            }
                        }

                        isUrgent = false;
                        sbUrgent.setChecked(false);
                        llAppointBackTime.setVisibility(View.GONE);
                        vDivAppointBackTime.setVisibility(View.GONE);
                    }
                });
        dateTimePicKDialog.dateTimePicKDialog();
    }

    /**
     * 更改收衣登记
     */
    private void updateCollectInfoParam(long appointBackTime, String collectBrcode, boolean
            isUrgent, AsyncHttpResponseHandler updateCollectInfoParamHandler) {
        this.tmpAppointBackTime = appointBackTime;
        if (collectBrcode == null) {
            collectBrcode = "";
        }

        showProgressDialog();
        DeliveryApi.updateCollectInfoParam(ClientStateManager.getLoginToken(this),
                appointBackTime, collectBrcode,
                collectCode, isUrgent ? 1 : 0, updateCollectInfoParamHandler);
    }

    private void getData() {
        String token = ClientStateManager.getLoginToken(this);
        showProgressDialog();
        DeliveryApi.startCollectInfo(token, outerCode, collectCode, startCollectInfoHandler);
    }

    /**
     * 获取收衣登记界面数据返回
     */
    AsyncHttpResponseHandler startCollectInfoHandler = new TextHttpResponseHandler(HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
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
        tvCustomerName.setText(result.getReceiveName());

        tvCustomerPhone.setText(result.getReceivePhone());
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
            isUrgent = true;
            sbUrgent.setChecked(true);
            llAppointBackTime.setVisibility(View.VISIBLE);
            vDivAppointBackTime.setVisibility(View.VISIBLE);
            tvAppointBackTime.setText(DateUtil.getTime(appointBackTime, "yyyy-MM-dd HH:mm"));
        } else {
            llAppointBackTime.setVisibility(View.GONE);
            vDivAppointBackTime.setVisibility(View.GONE);
            isUrgent = false;
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
    }

    @Override
    protected int getActionBarTitleRes() {
        return R.string.title_with_order_collect_book_in;
    }

    @OnClick({R.id.tv_customer_phone, R.id.tv_collect_brcode, R.id.tv_appoint_back_time})
    public void onClick(View view) {
        switch (view.getId()) {
            // 打电话
            case R.id.tv_customer_phone:
                PublicUtil.callPhone(WithOrderCollectBookInActivity.this, tvCustomerPhone.getText
                        ().toString());
                break;

            // 扫收衣单条码
            case R.id.tv_collect_brcode:
                goScanCode();
                break;

            // 预约时间
            case R.id.tv_appoint_back_time:
                selectAppointBackTime(appointBackTimeChangeHandler);
                break;
        }
    }


    /**
     * 改变预约时间弹出预约时间选择框
     */
    private void selectAppointBackTime(final AsyncHttpResponseHandler handler) {
        // 至少48小时后的时间
        final long maxTime = System.currentTimeMillis() / 1000 + 48 * 3600;
        DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
                WithOrderCollectBookInActivity.this, maxTime, new
                DateTimePickDialogUtil.OnDetailClickLister() {
                    @Override
                    public void btnClickLister(long time, String datetime) {
                        if (datetime.equals("time")) {
                            // 只能选择48小时之后的时间
                            if (time < maxTime) {
                                PublicUtil.showToast(getString(R.string
                                        .with_order_collect_appoint_back_time_later_hint));
                            } else {
                                // 发送预约时间更改
                                updateCollectInfoParam(time * 1000, tvCollectBrcode.getText()
                                        .toString(), true, handler);
                            }
                        }
                    }
                });
        dateTimePicKDialog.dateTimePicKDialog();
    }

    /**
     * 改变预约时间返回
     */
    AsyncHttpResponseHandler appointBackTimeChangeHandler = new TextHttpResponseHandler(
            HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            // TODO: lk 2016/6/21 待测试
            LogUtils.d(getDefaultTag(), " 改变预约时间返回 result = " + responseString);
            dismissProgressDialog();
            try {
                ResultBase result = JSON.parseObject(responseString,
                        ResultBase.class);
                if (result.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    appointBackTime = tmpAppointBackTime;
                    tvAppointBackTime.setText(DateUtil.getTime(appointBackTime, "yyyy-MM-dd " +
                            "HH:mm"));
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
     * 打开扫码界面
     */
    private void goScanCode() {
        PublicUtil.openScan(this, getString(R.string.coupons_scan_code_title),
                getString(R.string.with_order_collect_manual_input_code_btn),
                Constants.REQUEST_SCAN, RESULT_CODE_MANUAL);
    }

    /**
     * 处理扫码、手动输入数字码返回
     *
     * @param code
     */
    private void handleScaneCodeBack(String code) {
        tmpCollectBrcode = code;
        updateCollectInfoParam(tmpAppointBackTime, code, sbUrgent.isChecked(),
                collectBrcodeChangeHandler);
    }

    /**
     * 改变收衣单条码返回
     */
    AsyncHttpResponseHandler collectBrcodeChangeHandler = new TextHttpResponseHandler(
            HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            // TODO: lk 2016/6/21 待测试
            LogUtils.d(getDefaultTag(), " 改变收衣单条码返回 result = " + responseString);
            dismissProgressDialog();
            try {
                ResultBase result = JSON.parseObject(responseString,
                        ResultBase.class);
                if (result.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    getData();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED) {
            return;
        }

        switch (requestCode) {
            case REQUEST_CODE_CLOTHING_BOOK_IN_ACTIVITY:
                // 保存成功
                if (resultCode == ClothingBookInActivity.RESULT_CODE_SAVE_CLOTHES_SUCCESS) {
                    getData();
                }
                // 删除成功
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
            Intent intent = new Intent(WithOrderCollectBookInActivity.this,
                    ClothingBookInActivity.class);
            intent.putExtra(ClothingBookInActivity.EXTRA_COLLECT_CODE, collectCode);
            intent.putExtra(ClothingBookInActivity.EXTRA_OUTER_CODE, outerCode);
            intent.putExtra(ClothingBookInActivity.EXTRA_TYPE_NAME, info.getTypeName());
            intent.putExtra(ClothingBookInActivity.EXTRA_TYPE_CODE, info.getTypeCode());
            intent.putExtra(ClothingBookInActivity.EXTRA_MODE, ClothingBookInActivity.MODE_MODIFY);
            intent.putExtra(ClothingBookInActivity.EXTRA_CLOTHES_CODE, info.getClothesCode());
            WithOrderCollectBookInActivity.this.startActivityForResult(intent,
                    REQUEST_CODE_CLOTHING_BOOK_IN_ACTIVITY);
        }

        // 点击洗衣类型项的加号
        else if (item instanceof OrderDetail) {
            OrderDetail type = (OrderDetail) item;
            //  添加衣物
            Intent intent = new Intent(WithOrderCollectBookInActivity.this,
                    ClothingBookInActivity.class);
            intent.putExtra(ClothingBookInActivity.EXTRA_COLLECT_CODE, collectCode);
            intent.putExtra(ClothingBookInActivity.EXTRA_OUTER_CODE, outerCode);
            intent.putExtra(ClothingBookInActivity.EXTRA_TYPE_NAME, type.getTypeName());
            intent.putExtra(ClothingBookInActivity.EXTRA_TYPE_CODE, type.getTypeCode());
            intent.putExtra(ClothingBookInActivity.EXTRA_MODE, ClothingBookInActivity.MODE_ADD);
            WithOrderCollectBookInActivity.this.startActivityForResult(intent,
                    REQUEST_CODE_CLOTHING_BOOK_IN_ACTIVITY);
        }
    }
}
// TODO: lk 2016/6/24 代码过多，拆 
package cn.com.bluemoon.delivery.module.wash.appointment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.AppointmentApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.wash.appointment.ModifyUploadAppointClothesInfo;
import cn.com.bluemoon.delivery.app.api.model.wash.appointment.ResultAppointmentCollectSave;
import cn.com.bluemoon.delivery.app.api.model.wash.appointment.ResultAppointmentQueryList;
import cn.com.bluemoon.delivery.app.api.model.wash.appointment.UploadAppointClothesInfo;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.module.wash.appointment.clothesinfo.CreateClothesInfoActivity;
import cn.com.bluemoon.delivery.module.wash.appointment.clothesinfo.ModifyClothesInfoActivity;
import cn.com.bluemoon.delivery.module.wash.collect.withorder.ManualInputCodeActivity;
import cn.com.bluemoon.delivery.ui.DateTimePickDialogUtil;
import cn.com.bluemoon.delivery.ui.NoScrollListView;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.utils.LibConstants;
import cn.com.bluemoon.lib.view.CommonAlertDialog;
import cn.com.bluemoon.lib.view.NoDoubleClickListener;

/**
 * 创建收衣单
 */
public class CreateAppointmentCollectOrderActivity extends BaseActivity implements
        OnListItemClickListener {

    private static final int REQUEST_CODE_MANUAL = 0x77;
    private static final int REQUEST_CODE_SAVE = 0x777;
    private static final int REQUEST_CODE_ADD_CLOTHES_INFO = 0x66;
    private static final int REQUEST_CODE_MODIFY_CLOTHES_INFO = 0x55;

    @Bind(R.id.tv_number)
    TextView tvNumber;
    @Bind(R.id.iv_customer)
    ImageView ivCustomer;
    @Bind(R.id.tv_customer_name)
    TextView tvCustomerName;
    @Bind(R.id.tv_customer_phone)
    TextView tvCustomerPhone;
    @Bind(R.id.iv_address)
    ImageView ivAddress;
    @Bind(R.id.tv_address)
    TextView tvAddress;
    @Bind(R.id.tv_collect_brcode)
    TextView tvCollectBrcode;
    @Bind(R.id.iv_collect_brcode)
    ImageView ivCollectBrcode;
    @Bind(R.id.rb_not_urgent)
    RadioButton rbNotUrgent;
    @Bind(R.id.rg_urgent)
    RadioGroup rgUrgent;
    @Bind(R.id.v_div_appoint_back_time)
    View vDivAppointBackTime;
    @Bind(R.id.tv_appoint_back_time)
    TextView tvAppointBackTime;
    @Bind(R.id.ll_appoint_back_time)
    LinearLayout llAppointBackTime;
    @Bind(R.id.tv_actual_collect_count)
    TextView tvActualCollectCount;
    @Bind(R.id.btn_add)
    ImageButton btnAdd;
    @Bind(R.id.lv_order_receive)
    NoScrollListView lvOrderReceive;
    @Bind(R.id.btn_finish)
    Button btnFinish;
    @Bind(R.id.div)
    View div;

    private ResultAppointmentQueryList.AppointmentListBean appointmentListBean;
    private AppointmentUploadClothesInfoAdapter adapter;
    /**
     * 预约单信息
     */
    private final static String EXTRA_APPOINTMENT_LIST_BEAN = "EXTRA_APPOINTMENT_LIST_BEAN";
    private List<UploadAppointClothesInfo> clothesInfo;
    private long appointBackTime;

    public static void actionStart(Fragment fragment,
                                   ResultAppointmentQueryList.AppointmentListBean bean,
                                   int requestCode) {
        Intent intent = new Intent(fragment.getContext(), CreateAppointmentCollectOrderActivity
                .class);
        intent.putExtra(EXTRA_APPOINTMENT_LIST_BEAN, bean);
        fragment.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
        appointmentListBean = (ResultAppointmentQueryList.AppointmentListBean) getIntent()
                .getSerializableExtra(EXTRA_APPOINTMENT_LIST_BEAN);
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.appointment_create_collect_title);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_appointment_create_collect;
    }

    @Override
    public void initView() {
        btnAdd.setEnabled(false);

        if (appointmentListBean == null) {
            return;
        }

        // 预约信息
        tvNumber.setText(appointmentListBean.getAppointmentCode());
        tvCustomerName.setText(appointmentListBean.getCustomerName());
        tvCustomerPhone.setText(appointmentListBean.getCustomerPhone());
        tvAddress.setText(String.format("%s%s%s%s%s%s", appointmentListBean.getProvince(),
                appointmentListBean.getCity(),
                appointmentListBean.getCounty(),
                appointmentListBean.getVillage(),
                appointmentListBean.getStreet(),
                appointmentListBean.getAddress()));

        // 收衣条码
        tvCollectBrcode.setText("");

        // 预约时间
        vDivAppointBackTime.setVisibility(View.GONE);
        llAppointBackTime.setVisibility(View.GONE);
        tvAppointBackTime.setText("");

        // 实收数量
        tvActualCollectCount.setText(getString(R.string
                .with_order_collect_order_receive_count_num, "0"));

        // 衣物列表
        clothesInfo = new ArrayList<>();
        adapter = new AppointmentUploadClothesInfoAdapter(this, this);
        adapter.setList(clothesInfo);
        lvOrderReceive.setAdapter(adapter);
        div.setVisibility(View.GONE);

        // 加急
        rgUrgent.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_urgent) {
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
        //        rbUrgent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        //            @Override
        //            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        //                if (isChecked) {
        //                    vDivAppointBackTime.setVisibility(View.VISIBLE);
        //                    llAppointBackTime.setVisibility(View.VISIBLE);
        //                    tvAppointBackTime.setText("");
        //                    appointBackTime = 0;
        //                    // 时间选择
        //                    getAppointBackTime();
        //                } else {
        //                    vDivAppointBackTime.setVisibility(View.GONE);
        //                    llAppointBackTime.setVisibility(View.GONE);
        //                    tvAppointBackTime.setText("");
        //                    appointBackTime = 0;
        //                }
        //            }
        //        });

        btnFinish.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                // 完成
                checkFinish();
            }
        });
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

                        rbNotUrgent.setChecked(true);
                    }
                });
        dateTimePicKDialog.dateTimePicKDialog();
    }

    @Override
    public void initData() {
        btnAdd.setEnabled(true);
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        switch (requestCode) {
            // 完成收衣返回成功，弹出显示信息窗口
            case REQUEST_CODE_SAVE:
                ResultAppointmentCollectSave info = (ResultAppointmentCollectSave) result;
                setResult(RESULT_OK);
                if (info.getOrderInfo() != null) {
                    showFinishDialog(info.getOrderInfo());
                }
                break;
        }
    }

    private AlertDialog finishDialog;

    private void showFinishDialog(ResultAppointmentCollectSave.OrderInfoBean info) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(
                R.layout.dialog_create_appointment_collect_finish,
                null);

        TextView tvCollectCode = (TextView) view.findViewById(R.id.tv_collect_code);
        TextView tvOuterCode = (TextView) view.findViewById(R.id.tv_outer_code);
        TextView tvName = (TextView) view.findViewById(R.id.tv_name);
        TextView tvPhone = (TextView) view.findViewById(R.id.tv_phone);
        TextView tvAddress = (TextView) view.findViewById(R.id.tv_address);
        TextView tvActualReceive = (TextView) view.findViewById(R.id.tv_actual_receive);
        Button btnClose = (Button) view.findViewById(R.id.btn_close);

        tvCollectCode.setText(info.getCollectCode());
        tvOuterCode.setText(info.getOuterCode());

        tvName.setText(info.getCustomerName());
        tvPhone.setText(info.getCustomerPhone());
        tvAddress.setText(String.format("%s%s%s%s%s%s", info.getProvince(),
                info.getCity(),
                info.getCounty(),
                info.getVillage(),
                info.getStreet(),
                info.getAddress()));

        tvActualReceive.setText(
                String.format(getString(R.string.create_collect_dialog_actual_receive_num_unit),
                        info.getActualCount() + ""));

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishDialog.dismiss();
                finish();
            }
        });

        dialog.setView(view);
        dialog.setCancelable(false);
        finishDialog = dialog.show();
    }

    @OnClick({R.id.tv_collect_brcode, R.id.iv_collect_brcode, R.id.btn_add,
            R.id.tv_appoint_back_time})
    public void onClick(View view) {
        switch (view.getId()) {
            // 收衣单条码扫描/输入
            case R.id.tv_collect_brcode:
            case R.id.iv_collect_brcode:
                goScanCode();
                break;
            // 添加衣物
            case R.id.btn_add:
                CreateClothesInfoActivity.actionStart(this, REQUEST_CODE_ADD_CLOTHES_INFO);
                break;
            // 预约时间选择
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

    private void checkFinish() {
        if (checkBtnFinish()) {
            showWaitDialog();
            long bachTime = 0;
            int isUrgent = rgUrgent.getCheckedRadioButtonId() == R.id.rg_urgent ? 1 : 0;
            if (isUrgent == 1) {
                bachTime = appointBackTime;
            }
            AppointmentApi.appointmentCollectSave(bachTime,
                    appointmentListBean.getAppointmentCode(), clothesInfo,
                    tvCollectBrcode.getText().toString(), 0, isUrgent, getToken(),
                    getNewHandler(REQUEST_CODE_SAVE, ResultAppointmentCollectSave.class));
        }
    }

    /**
     * 设置完成按钮是否校验通过
     */
    private boolean checkBtnFinish() {
        String errStr = null;
        if (clothesInfo == null || clothesInfo.isEmpty()) {
            errStr = getString(R.string.btn_check_err_collect_empty);
        }

        if (TextUtils.isEmpty(errStr)) {
            return true;
        } else {
            toast(errStr);
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        onActionBarBtnLeftClick();
    }

    @Override
    protected void onActionBarBtnLeftClick() {
        new CommonAlertDialog.Builder(this)
                .setCancelable(true)
                .setMessage(getString(R.string.cancel_alarm_message))
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
                        setResult(Activity.RESULT_CANCELED);
                        finish();
                    }
                })
                .show();
    }

    /**
     * 打开扫码界面
     */
    private void goScanCode() {
        PublicUtil.openClothScan(this, getString(R.string.scan_brcode),
                getString(R.string.with_order_collect_manual_input_code_btn),
                Constants.REQUEST_SCAN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED) {
            return;
        }

        switch (requestCode) {
            //   添加衣物返回
            case REQUEST_CODE_ADD_CLOTHES_INFO:
                if (resultCode == RESULT_OK) {
                    UploadAppointClothesInfo info = (UploadAppointClothesInfo) data
                            .getSerializableExtra(CreateClothesInfoActivity
                                    .RESULT_UPLOAD_CLOTHES_INFO);
                    clothesInfo.add(info);
                    adapter.notifyDataSetChanged();
                    div.setVisibility(View.VISIBLE);
                    setActualReceive();
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
                    handleScanCodeBack(resultStr);
                }
                //  跳转到扫码输入
                else if (resultCode == ManualInputCodeActivity.RESULT_CODE_SCANE_CODE) {
                    goScanCode();
                }
                break;

            //  修改衣物
            case REQUEST_CODE_MODIFY_CLOTHES_INFO:
                // 保存成功
                if (resultCode == RESULT_OK) {
                    ModifyUploadAppointClothesInfo info = (ModifyUploadAppointClothesInfo) data
                            .getSerializableExtra(ModifyClothesInfoActivity
                                    .RESULT_UPLOAD_CLOTHES_INFO);

                    int count = clothesInfo.size();
                    for (int i = 0; i < count; i++) {
                        UploadAppointClothesInfo item = clothesInfo.get(i);
                        if (item.getClothesCode().equals(info.getInitClothesCode())) {
                            clothesInfo.remove(i);
                            break;
                        }
                    }

                    clothesInfo.add(info);
                    div.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();
                    setActualReceive();
                }

                // 删除成功
                else if (resultCode == ModifyClothesInfoActivity
                        .RESULT_CODE_DELETE_CLOTHES_SUCCESS) {
                    String deleteClothesCode = data.getStringExtra
                            (ModifyClothesInfoActivity.RESULT_DELETE_CLOTHES_CODE);
                    if (deleteClothesCode != null) {
                        int count = clothesInfo.size();
                        for (int i = 0; i < count; i++) {
                            UploadAppointClothesInfo item = clothesInfo.get(i);
                            if (deleteClothesCode.equals(item.getClothesCode())) {
                                clothesInfo.remove(i);
                                break;
                            }
                        }

                        adapter.notifyDataSetChanged();
                        if (clothesInfo.size() < 1) {
                            div.setVisibility(View.GONE);
                        }
                        setActualReceive();
                    }
                }

                //  删除过衣物图片，并直接退出
                else if (resultCode == ModifyClothesInfoActivity
                        .RESULT_CODE_DELETE_CLOTHES_IMG) {
                    ModifyUploadAppointClothesInfo info = (ModifyUploadAppointClothesInfo) data
                            .getSerializableExtra(ModifyClothesInfoActivity
                                    .RESULT_UPLOAD_CLOTHES_INFO);
                    int count = clothesInfo.size();
                    for (int i = 0; i < count; i++) {
                        UploadAppointClothesInfo item = clothesInfo.get(i);
                        if (item.getClothesCode().equals(info.getInitClothesCode())) {
                            item.setImgPath(info.getImgPath());
                            item.setClothingPics(info.getClothingPics());
                            break;
                        }
                    }

                    adapter.notifyDataSetChanged();
                    setActualReceive();
                }
                break;

            default:
                break;
        }
    }

    /**
     * 修改实收数量
     */
    private void setActualReceive() {
        int size = clothesInfo.size();
        tvActualCollectCount.setText(
                String.format(getString(R.string.create_collect_dialog_actual_receive_num), size
                        + ""));
    }

    /**
     * 处理扫码、手动输入数字码返回
     */
    private void handleScanCodeBack(String code) {
        tvCollectBrcode.setText(code);
    }

    @Override
    public void onItemClick(Object item, View view, int position) {
        // 点击衣物信息，修改衣物
        ModifyClothesInfoActivity.actionStart(this, (UploadAppointClothesInfo) item,
                REQUEST_CODE_MODIFY_CLOTHES_INFO);
    }
}
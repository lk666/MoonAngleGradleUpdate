package cn.com.bluemoon.delivery.module.clothing.collect.withoutorder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import org.apache.commons.lang3.text.StrBuilder;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.ClientStateManager;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.address.SelectAddressByDepthActivity;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.address.Area;
import cn.com.bluemoon.delivery.app.api.model.clothing.ResultQueryActivityLimitNum;
import cn.com.bluemoon.delivery.app.api.model.clothing.ResultRegisterCreateCollectInfo;
import cn.com.bluemoon.delivery.app.api.model.clothing.collect.UploadClothesInfo;
import cn.com.bluemoon.delivery.module.base.BaseActionBarActivity;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.module.clothing.collect.withorder.ManualInputCodeActivity;
import cn.com.bluemoon.delivery.module.clothing.collect.withoutorder.clothesinfo
        .CreateClothesInfoActivity;
import cn.com.bluemoon.delivery.module.clothing.collect.withoutorder.clothesinfo
        .ModifyClothesInfoActivity;
import cn.com.bluemoon.delivery.ui.DateTimePickDialogUtil;
import cn.com.bluemoon.delivery.ui.NoScrollListView;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.ImageLoaderUtil;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.ViewHolder;
import cn.com.bluemoon.lib.utils.LibConstants;
import cn.com.bluemoon.lib.view.CommonAlertDialog;
import cn.com.bluemoon.lib.view.switchbutton.SwitchButton;

/**
 * 创建收衣订单
 * Created by luokai on 2016/6/28.
 */
public class CreateCollectOrderActivity extends BaseActionBarActivity implements
        OnListItemClickListener {
    /**
     * 扫描二维码跳转到手动输入的返回码
     */
    private static final int RESULT_CODE_TO_MANUAL = 0x23;
    private static final int REQUEST_CODE_MANUAL = 0x43;

    /**
     * 修改衣物
     */
    public static final int REQUEST_CODE_MODIFY_CLOTHES_INFO = 0x92;
    /**
     * 新增衣物
     */
    public static final int REQUEST_CODE_ADD_CLOTHES_INFO = 0x91;

    /**
     * 选择省市区
     */
    public static final int REQUEST_CODE_SELECT_PROVINCE_CITY_COUNTRY = 0x80;
    /**
     * 选择乡镇街道
     */
    public static final int REQUEST_CODE_SELECT_STREET_VILLAGE = 0x81;
    /**
     * 创建收衣订单成功
     */
    public static final int RESULT_COLLECT_SCUUESS = 0x77;

    /**
     * 活动编码
     */
    private final static String EXTRA_ACTIVITY_CODE = "EXTRA_ACTIVITY_CODE";

    @Bind(R.id.et_name)
    EditText etName;
    @Bind(R.id.et_phone)
    EditText etPhone;

    @Bind(R.id.tv_province_city_country)
    TextView tvProvinceCityCountry;

    @Bind(R.id.tv_street_village)
    TextView tvStreetVillage;
    @Bind(R.id.v_div_street_village)
    View vDivStreetVillage;
    @Bind(R.id.ll_street_village)
    LinearLayout llStreetVillage;

    @Bind(R.id.et_address)
    EditText etAddress;

    @Bind(R.id.tv_collect_brcode)
    TextView tvCollectBrcode;

    @Bind(R.id.sb_is_urgent)
    SwitchButton sbIsUrgent;
    @Bind(R.id.tv_appoint_back_time)
    TextView tvAppointBackTime;
    @Bind(R.id.v_div_appoint_back_time)
    View vDivAppointBackTime;
    @Bind(R.id.ll_appoint_back_time)
    LinearLayout llAppointBackTime;

    @Bind(R.id.tv_actual_collect_count)
    TextView tvActualCollectCount;
    @Bind(R.id.btn_add)
    Button btnAdd;

    @Bind(R.id.v_div_order_receive)
    View vDivOrderReceive;
    @Bind(R.id.lv_order_receive)
    NoScrollListView lvOrderReceive;

    @Bind(R.id.btn_finish)
    Button btnFinish;

    @Bind(R.id.sb_is_fee)
    SwitchButton sbIsFee;

    /**
     * 活动编号
     */
    private String activityCode;

    /**
     * 最多衣物件数
     */
    private int limitNum;

    /**
     * 记录的待提交衣物
     */
    private List<UploadClothesInfo> clothesInfo;

    private UploadClothesInfoAdapter clothesInfoAdapter;

    long appointBackTime;

    /**
     * 省
     */
    private String province;
    /**
     * 市
     */
    private String city;
    /**
     * 区/县
     */
    private String county;
    /**
     * 区/县，用于定位乡镇/街道
     */
    private Area countyArea;
    /**
     * 乡镇/街道
     */
    private String street;
    /**
     * 村/社区
     */
    private String village;
    /**
     * 详细地址
     */
    private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_collect_order);
        ButterKnife.bind(this);

        setIntentData();
        initView();
        getData();
    }

    private void setIntentData() {
        activityCode = getIntent().getStringExtra(EXTRA_ACTIVITY_CODE);
        if (activityCode == null) {
            activityCode = "";
        }
    }

    private void initView() {
        appointBackTime = 0;
        province = "";
        city = "";
        county = "";
        street = "";
        village = "";
        address = "";

        vDivStreetVillage.setVisibility(View.GONE);
        llStreetVillage.setVisibility(View.GONE);

        vDivAppointBackTime.setVisibility(View.GONE);
        llAppointBackTime.setVisibility(View.GONE);

        btnAdd.setEnabled(false);

        vDivOrderReceive.setVisibility(View.GONE);
        clothesInfo = new ArrayList<>();
        clothesInfoAdapter = new UploadClothesInfoAdapter(this, this);
        lvOrderReceive.setAdapter(clothesInfoAdapter);

        sbIsUrgent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
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

                        sbIsUrgent.setChecked(false);
                    }
                });
        dateTimePicKDialog.dateTimePicKDialog();
    }

    /**
     * 设置完成按钮是否校验通过
     */
    private boolean checkBtnFinish() {
        String errStr = null;
        if (TextUtils.isEmpty(etName.getText().toString())) {
            errStr = getString(R.string.btn_check_err_name_empty);
        } else if (TextUtils.isEmpty(etPhone.getText().toString())) {
            errStr = getString(R.string.btn_check_err_phone_empty);
        } else if (TextUtils.isEmpty(province) || TextUtils.isEmpty(city) || TextUtils.isEmpty
                (county)) {
            errStr = getString(R.string.btn_check_err_province_etc_empty);
        } else if (TextUtils.isEmpty(etAddress.getText().toString())) {
            errStr = getString(R.string.btn_check_err_address_empty);
        } else if (clothesInfo == null || clothesInfo.isEmpty()) {
            errStr = getString(R.string.btn_check_err_collect_empty);
        }

        if (TextUtils.isEmpty(errStr)) {
            return true;
        } else {
            PublicUtil.showToast(errStr);
            return false;
        }
    }

    private void getData() {
        showProgressDialog();
        DeliveryApi.queryActivityLimitNum(activityCode, ClientStateManager.getLoginToken(this),
                createResponseHandler(new IHttpResponseHandler() {
                    @Override
                    public void onResponseSuccess(String responseString) {
                        // TODO: lk 2016/6/30 待测试
                        // 初始化时查询活动收衣上限返回
                        ResultQueryActivityLimitNum result = JSON.parseObject(responseString,
                                ResultQueryActivityLimitNum.class);
                        setData(result);
                    }
                }));

    }

    private void setData(ResultQueryActivityLimitNum result) {
        limitNum = result.getLimitNum();

        if (limitNum > 0) {
            btnAdd.setEnabled(true);
        } else {
            btnAdd.setEnabled(false);
            btnAdd.setEnabled(false);
        }
    }

    @Override
    protected int getActionBarTitleRes() {
        return R.string.title_activity_dec;
    }

    private void showFinishDialog(String collectCode) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_create_wash_order_finish,
                null);

        TextView tvCode = (TextView) view.findViewById(R.id.tv_code);
        TextView tvName = (TextView) view.findViewById(R.id.tv_name);
        TextView tvPhone = (TextView) view.findViewById(R.id.tv_phone);
        TextView tvAddress = (TextView) view.findViewById(R.id.tv_address);
        TextView tvActualReceive = (TextView) view.findViewById(R.id.tv_actual_receive);
        Button btnClose = (Button) view.findViewById(R.id.btn_close);

        tvCode.setText(collectCode);
        tvName.setText(etName.getText().toString());
        tvPhone.setText(etPhone.getText().toString());
        tvAddress.setText((new StrBuilder(province)).append(city).append(county).append(street)
                .append(village).append(address).toString());
        tvActualReceive.setText(getString(R.string.create_collect_dialog_actual_receive) +
                clothesInfo.size() + getString(R.string.unit_clothes));

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        dialog.setView(view);
        dialog.setCancelable(false);
        dialog.show();
    }

    @OnClick({R.id.tv_province_city_country, R.id.tv_street_village, R.id.tv_collect_brcode,
            R.id.tv_appoint_back_time, R.id.btn_add, R.id.btn_finish})
    public void onClick(View view) {
        switch (view.getId()) {
            // 省市选择
            case R.id.tv_province_city_country:
                SelectAddressByDepthActivity.actionStart(CreateCollectOrderActivity.this, null,
                        null,
                        3, REQUEST_CODE_SELECT_PROVINCE_CITY_COUNTRY);
                break;
            //  街道选择
            case R.id.tv_street_village:
                SelectAddressByDepthActivity.actionStart(CreateCollectOrderActivity.this,
                        countyArea.getDcode(), countyArea.getChildType(), 2,
                        REQUEST_CODE_SELECT_STREET_VILLAGE);
                break;
            // 收衣单条码扫描/输入
            case R.id.tv_collect_brcode:
                goScanCode();
                break;
            // 预约时间选择
            case R.id.tv_appoint_back_time:
                selectAppointBackTime();
                break;
            // 添加衣物
            case R.id.btn_add:
//                // 先校验是否已经扫描收衣条码，没有扫描提示：还未扫描收衣条码，请扫描后继续操作。
//                if (TextUtils.isEmpty(tvCollectBrcode.getText().toString())) {
//                    PublicUtil.showToast(getString(R.string.notice_add_clothes_no_brcode));
//                } else {
                CreateClothesInfoActivity.actionStart(this, activityCode,
                        REQUEST_CODE_ADD_CLOTHES_INFO);
//                }
                break;
            //  完成收衣
            case R.id.btn_finish:
                if (checkBtnFinish()) {
                    showProgressDialog();
                    address = etAddress.getText().toString();
                    DeliveryApi.registerCreatedCollectInfo(activityCode, address, appointBackTime,
                            city, clothesInfo, tvCollectBrcode.getText().toString(), county,
                            etName.getText().toString(), etPhone.getText().toString(),
                            sbIsFee.isChecked() ? 1 : 0, sbIsUrgent.isChecked() ? 1 : 0, province,
                            street, ClientStateManager.getLoginToken(this), village,
                            createResponseHandler(new IHttpResponseHandler() {
                                @Override
                                public void onResponseSuccess(String responseString) {
                                    // TODO: lk 2016/6/30 待测试
                                    // 完成收衣返回成功，弹出显示信息窗口，服务器未返回收衣单号
                                    ResultRegisterCreateCollectInfo info = JSON.parseObject
                                            (responseString,
                                                    ResultRegisterCreateCollectInfo.class);
                                    setResult(RESULT_COLLECT_SCUUESS);
                                    showFinishDialog(info.getCollectCode());
                                }
                            }));
                }
                break;
            default:
                break;
        }
    }

    /**
     * 打开扫码界面
     */
    private void goScanCode() {
        PublicUtil.openNewScan(this, getString(R.string.coupons_scan_code_title),
                getString(R.string.with_order_collect_manual_input_code_btn),
                Constants.REQUEST_SCAN, RESULT_CODE_TO_MANUAL);
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

    public static void actionStart(Activity activity, int requestCode, String activityCode) {
        Intent intent = new Intent(activity, CreateCollectOrderActivity.class);
        intent.putExtra(EXTRA_ACTIVITY_CODE, activityCode);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    public void onItemClick(Object item, View view, int position) {
        // 点击衣物信息，修改衣物
        ModifyClothesInfoActivity.actionStart(this, activityCode, (UploadClothesInfo) item,
                REQUEST_CODE_MODIFY_CLOTHES_INFO);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED) {
            return;
        }

        switch (requestCode) {
            // 选择省市
            case REQUEST_CODE_SELECT_PROVINCE_CITY_COUNTRY:
                if (resultCode == RESULT_OK) {
                    List<Area> regions = (List<Area>) data.getSerializableExtra("subRegionList");
                    String tmpProvince = regions.get(0).getDname();
                    String tmpCity = regions.get(1).getDname();
                    String tmpCountry = regions.get(2).getDname();

                    if (!province.equals(tmpProvince) || !city.equals(tmpCity) || !county.equals
                            (tmpCity)) {
                        province = tmpProvince;
                        city = tmpCity;
                        county = tmpCountry;
                        countyArea = regions.get(2);

                        tvProvinceCityCountry.setText(province + city + county);
                        vDivStreetVillage.setVisibility(View.VISIBLE);
                        llStreetVillage.setVisibility(View.VISIBLE);
                        tvStreetVillage.setText("");
                        street = "";
                        village = "";
                    }
                }
                break;

            // 选择乡镇/街道 + 村/社区
            case REQUEST_CODE_SELECT_STREET_VILLAGE:
                if (resultCode == RESULT_OK) {
                    List<Area> regions = (List<Area>) data.getSerializableExtra("subRegionList");
                    String tmpStreet = regions.get(0).getDname();
                    String tmpVillage = regions.get(1).getDname();

                    if (!street.equals(tmpStreet) || !village.equals(tmpVillage)) {
                        street = tmpStreet;
                        village = tmpVillage;

                        tvStreetVillage.setText(street + village);
                    }
                }
                break;

            //   添加衣物返回
            case REQUEST_CODE_ADD_CLOTHES_INFO:
                if (resultCode == RESULT_OK) {
                    UploadClothesInfo info = (UploadClothesInfo) data.getSerializableExtra
                            (CreateClothesInfoActivity.RESULT_UPLOAD_CLOTHES_INFO);
                    clothesInfo.add(info);
                    clothesInfoAdapter.setList(clothesInfo);
                    clothesInfoAdapter.notifyDataSetChanged();
                    setActualReceive();
                }
                break;

            case REQUEST_CODE_MODIFY_CLOTHES_INFO:
                // 保存成功
                if (resultCode == RESULT_OK) {
                    UploadClothesInfo info = (UploadClothesInfo) data.getSerializableExtra
                            (ModifyClothesInfoActivity.RESULT_UPLOAD_CLOTHES_INFO);
                    clothesInfo.remove(info);
                    clothesInfo.add(info);
                    clothesInfoAdapter.setList(clothesInfo);
                    clothesInfoAdapter.notifyDataSetChanged();
                    setActualReceive();
                }
                // 删除成功
                else if (resultCode == ModifyClothesInfoActivity
                        .RESULT_CODE_DELETE_CLOTHES_SUCCESS) {
                    String deleteClothesCode = data.getStringExtra(ModifyClothesInfoActivity
                            .RESULT_DELETE_CLOTHES_CODE);
                    if (deleteClothesCode != null) {
                        for (UploadClothesInfo info : clothesInfo) {
                            if (deleteClothesCode.equals(info.getClothesCode())) {
                                clothesInfo.remove(info);
                                break;
                            }
                        }
                        clothesInfoAdapter.setList(clothesInfo);
                        clothesInfoAdapter.notifyDataSetChanged();
                        setActualReceive();
                    }
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
     * 修改实收数量
     */
    private void setActualReceive() {
        int size = clothesInfo.size();
        tvActualCollectCount.setText(getString(R.string.create_collect_dialog_actual_receive) +
                size);

        if (limitNum > size) {
            btnAdd.setEnabled(true);
        } else {
            btnAdd.setEnabled(false);
        }

        if (size > 0) {
            vDivOrderReceive.setVisibility(View.VISIBLE);
        } else {
            vDivOrderReceive.setVisibility(View.GONE);
        }
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

    /**
     * 收衣明细Adapter
     * Created by lk on 2016/7/7.
     */
    public class UploadClothesInfoAdapter extends BaseListAdapter<UploadClothesInfo> {
        public UploadClothesInfoAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.item_with_order_clothes_info;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            final UploadClothesInfo item = (UploadClothesInfo) getItem(position);
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


    @Override
    protected void ontActionBarBtnLeftClick() {


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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
             ontActionBarBtnLeftClick();
            return true;
        }
        return false;
    }
}

package cn.com.bluemoon.delivery.module.wash.enterprise.createorder;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.EnterpriseApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.wash.enterprise.Employee;
import cn.com.bluemoon.delivery.app.api.model.wash.enterprise.RequestEnterpriseOrderInfo;
import cn.com.bluemoon.delivery.app.api.model.wash.enterprise.ResultGetWashEnterpriseScan;
import cn.com.bluemoon.delivery.app.api.model.wash.enterprise.ResultSaveWashEnterpriseOrder;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.wash.collect.withorder.ManualInputCodeActivity;
import cn.com.bluemoon.delivery.module.wash.enterprise.event.CreateOrderEvent;
import cn.com.bluemoon.delivery.module.wash.enterprise.event.SaveOrderEvent;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.ui.selectordialog.SingleOptionSelectDialog;
import cn.com.bluemoon.delivery.ui.selectordialog.TextSingleOptionSelectDialog;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.lib.utils.LibConstants;
import cn.com.bluemoon.lib.view.CommonAlertDialog;

/**
 * 创建订单页面
 */
public class CreateOrderActivity extends BaseActivity {

    private static final String EXTRA_INFO = "EXTRA_INFO";
    private static final String EXTRA_EMPLOYEE = "EXTRA_EMPLOYEE";
    private static final int REQUEST_CODE_SCAN = 0x777;
    private static final int REQUEST_CODE_SAVE = 0x666;
    private static final int REQUEST_CODE_MANUAL = 0x77;
    private static final int REQUEST_CODE_ADD = 0x555;
    @Bind(R.id.tv_employee_name)
    TextView tvEmployeeName;
    @Bind(R.id.tv_employee_phone)
    TextView tvEmployeePhone;
    @Bind(R.id.et_employee_extension)
    EditText etEmployeeExtension;
    @Bind(R.id.tv_return_address)
    TextView tvReturnAddress;
    @Bind(R.id.ll_branch_code)
    LinearLayout llBranchCode;
    @Bind(R.id.tv_collect_brcode)
    TextView tvCollectBrcode;
    @Bind(R.id.ll_collect_brcode)
    LinearLayout llCollectBrcode;
    @Bind(R.id.et_backup)
    EditText etBackup;
    @Bind(R.id.tv_balance)
    TextView tvBalance;
    @Bind(R.id.tv_point)
    TextView tvPoint;
    @Bind(R.id.btn_send)
    Button btnAddCloth;

    private ResultGetWashEnterpriseScan info;
    private Employee employee;

    /**
     * 扫一扫入口
     */
    public static void actionStart(Context context, ResultGetWashEnterpriseScan info) {
        Intent intent = new Intent(context, CreateOrderActivity.class);
        intent.putExtra(EXTRA_INFO, info);
        context.startActivity(intent);
    }

    /**
     * 手动搜索入口
     */
    public static void actionStart(Context context, Employee employee) {
        Intent intent = new Intent(context, CreateOrderActivity.class);
        intent.putExtra(EXTRA_EMPLOYEE, employee);
        context.startActivity(intent);
    }

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
        info = (ResultGetWashEnterpriseScan) getIntent().getSerializableExtra(EXTRA_INFO);
        employee = (Employee) getIntent().getSerializableExtra(EXTRA_EMPLOYEE);
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.title_create_order);
    }

    @Override
    protected void setActionBar(CommonActionBar titleBar) {
        super.setActionBar(titleBar);
        titleBar.getTvRightView().setVisibility(View.VISIBLE);
        titleBar.getTvRightView().setText(R.string.btn_save);
    }

    @Override
    protected void onActionBarBtnRightClick() {
        if (check()) {
            showWaitDialog();
            RequestEnterpriseOrderInfo sendInfo = new RequestEnterpriseOrderInfo(branchCode,
                    tvCollectBrcode.getText().toString(), info.employeeInfo.employeeCode,
                    etEmployeeExtension.getText().toString(), etBackup.getText().toString());
            EnterpriseApi.saveWashEnterpriseOrder(sendInfo, getToken(),
                    getNewHandler(REQUEST_CODE_SAVE, ResultSaveWashEnterpriseOrder.class));
        }
    }

    private boolean check() {
        if (TextUtils.isEmpty(etEmployeeExtension.getText().toString())) {
            toast(R.string.hint_employee_extension);
            return false;
        } else if (TextUtils.isEmpty(branchCode)) {
            toast(R.string.hint_return_address);
            return false;
        } else if (TextUtils.isEmpty(tvCollectBrcode.getText())) {
            toast(R.string.hint_collect_brcode);
            return false;
        }
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_create_order;
    }

    @Override
    public void initView() {
        tvCollectBrcode.setText("");
        etBackup.setText("");
        etBackup.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (etBackup.getLineCount() > 1) {
                    etBackup.setGravity(Gravity.START);
                } else {
                    etBackup.setGravity(Gravity.END);
                }
            }
        });

        if (info != null) {
            setData(info);
        } else if (employee != null) {
            setData(employee);
        }
    }

    private String branchCode;

    private void setData(Employee employee) {
        if (employee == null) {
            return;
        }
        tvEmployeeName.setText(employee.employeeName);
        tvEmployeePhone.setText(employee.employeePhone);
        etEmployeeExtension.setText(employee.employeeExtension);
        tvReturnAddress.setText(employee.branchName);
        branchCode = employee.branchCode;
    }

    private void setData(ResultGetWashEnterpriseScan info) {
        if (info == null || info.amountInfo == null || info.employeeInfo == null) {
            return;
        }
        tvEmployeeName.setText(info.employeeInfo.employeeName);
        tvEmployeePhone.setText(info.employeeInfo.employeePhone);

        etEmployeeExtension.setText(info.employeeInfo.employeeExtension);

        tvReturnAddress.setText(info.employeeInfo.branchName);
        branchCode = info.employeeInfo.branchCode;

        tvBalance.setText(getString(R.string.order_money,
                StringUtil.formatPriceByFen(info.amountInfo.accountBalance)));

        tvPoint.setText(String.valueOf(info.amountInfo.integralBalance));
    }

    @Override
    public void initData() {
        // 手动搜索入口进来的要再调用一下扫一扫
        if (info == null && employee != null) {
            showWaitDialog();
            EnterpriseApi.getWashEnterpriseScan(employee.employeePhone, getToken(),
                    getNewHandler(REQUEST_CODE_SCAN, ResultGetWashEnterpriseScan.class));
        }
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        switch (requestCode) {
            // 手动搜索入口进来的,再调用一下扫一扫返回
            case REQUEST_CODE_SCAN:
                info = (ResultGetWashEnterpriseScan) result;
                setData(info);
                break;
            // 点击保存
            case REQUEST_CODE_SAVE:
                ResultSaveWashEnterpriseOrder order0 = (ResultSaveWashEnterpriseOrder) result;
                EventBus.getDefault().post(new SaveOrderEvent(order0.outerCode));
                finish(false);
                break;
            // 点击添加衣物
            case REQUEST_CODE_ADD:
                ResultSaveWashEnterpriseOrder order1 = (ResultSaveWashEnterpriseOrder) result;
                EventBus.getDefault().post(new CreateOrderEvent(order1.outerCode));
                AddClothesActivity.actionStart(this, order1.outerCode);
                finish(false);
                break;

        }
    }

    @Override
    protected void onActionBarBtnLeftClick() {
        setResult(Activity.RESULT_CANCELED);
        finish(true);
    }

    private void finish(boolean isFinishManual) {
        // 手动后退，确认
        if (isFinishManual) {
            CommonAlertDialog.Builder dialog = new CommonAlertDialog.Builder(this);
            dialog.setMessage(getString(R.string.cancel_create_order));
            dialog.setNegativeButton(R.string.btn_ok,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }

                    });
            dialog.setPositiveButton(R.string.btn_cancel, null);
            dialog.show();
        } else {
            finish();
        }
    }


    @Override
    public void onBackPressed() {
        finish(true);
    }

    private TextSingleOptionSelectDialog dialog;

    /**
     * 选择还衣地址
     */
    private void showSelectReturn() {
        if (info == null || info.branchList == null || info.branchList.isEmpty()) {
            return;
        }
        if (dialog == null) {
            ArrayList<String> list = new ArrayList<>();
            int index = 0;
            int size = info.branchList.size();
            for (int i = 0; i < size; i++) {
                ResultGetWashEnterpriseScan.BranchListBean branch = info.branchList.get(i);
                list.add(branch.branchName);
                if (branch.branchCode.equals(branchCode)) {
                    index = i;
                }
            }

            dialog = new TextSingleOptionSelectDialog(this, "",
                    list, index, new SingleOptionSelectDialog.OnButtonClickListener() {
                @Override
                public void onOKButtonClick(int index, String text) {
                    tvReturnAddress.setText(text);
                    branchCode = info.branchList.get(index).branchCode;
                }

                @Override
                public void onCancelButtonClick() {

                }
            });
        }
        dialog.show();
    }

    @OnClick({R.id.ll_branch_code, R.id.ll_collect_brcode, R.id.btn_send})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            // 还衣地点
            case R.id.ll_branch_code:
                showSelectReturn();
                break;
            // 收衣袋
            case R.id.ll_collect_brcode:
                goScanCode();
                break;
            // 添加衣物
            case R.id.btn_send:
                if (check()) {
                    showWaitDialog();
                    RequestEnterpriseOrderInfo sendInfo = new RequestEnterpriseOrderInfo(branchCode,
                            tvCollectBrcode.getText().toString(), info.employeeInfo.employeeCode,
                            etEmployeeExtension.getText().toString(), etBackup.getText().toString
                            ());
                    EnterpriseApi.saveWashEnterpriseOrder(sendInfo, getToken(),
                            getNewHandler(REQUEST_CODE_ADD, ResultSaveWashEnterpriseOrder.class));
                }
                break;
        }
    }

    /**
     * 打开扫码界面
     */
    private void goScanCode() {
        PublicUtil.openClothScan(this, getString(R.string.coupons_scan_code_title),
                getString(R.string.with_order_collect_manual_input_code_btn),
                Constants.REQUEST_SCAN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED) {
            return;
        }

        switch (requestCode) {
            case Constants.REQUEST_SCAN:
                // 扫码返回
                if (resultCode == Activity.RESULT_OK) {
                    String resultStr = data.getStringExtra(LibConstants.SCAN_RESULT);
                    handleScanCodeBack(resultStr);
                }
                //   跳转到手动输入
                else if (resultCode == Constants.RESULT_SCAN) {
                    Intent intent = new Intent(this, ManualInputCodeActivity.class);
                    intent.putExtra(ManualInputCodeActivity.EXTRA_MAX_LENGTH, 32);
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

            default:
                break;
        }
    }

    /**
     * 新增模式下处理扫码、手动输入数字码返回
     */
    private void handleScanCodeBack(String code) {
        tvCollectBrcode.setText(code);
    }
}

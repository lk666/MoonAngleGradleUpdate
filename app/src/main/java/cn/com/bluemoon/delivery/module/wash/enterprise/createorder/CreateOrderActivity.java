package cn.com.bluemoon.delivery.module.wash.enterprise.createorder;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.EnterpriseApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.wash.enterprise.Employee;
import cn.com.bluemoon.delivery.app.api.model.wash.enterprise.ResultGetWashEnterpriseScan;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.ui.selectordialog.SingleOptionSelectDialog;
import cn.com.bluemoon.delivery.ui.selectordialog.TextSingleOptionSelectDialog;
import cn.com.bluemoon.delivery.utils.StringUtil;

/**
 * 创建订单页面
 */
public class CreateOrderActivity extends BaseActivity {

    private static final String EXTRA_INFO = "EXTRA_INFO";
    private static final String EXTRA_EMPLOYEE = "EXTRA_EMPLOYEE";
    private static final int REQUEST_CODE_SCAN = 0x777;
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
    @Bind(R.id.btn_add_cloth)
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
        // TODO: lk 2017/5/5
        toast("保存");
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
            EnterpriseApi.getWashEnterpriseScan(employee.employeeCode, getToken(),
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
        }
    }

    private TextSingleOptionSelectDialog dialog;

    /**
     * 选择还衣地址
     */
    private void showSelectReturn() {
        if (info == null || info.branchList == null) {
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

    // TODO: lk 2017/5/5
    @OnClick({R.id.ll_branch_code, R.id.ll_collect_brcode, R.id.btn_add_cloth})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            // 还衣地点
            case R.id.ll_branch_code:
                showSelectReturn();
                break;
            // 收衣袋
            case R.id.ll_collect_brcode:
                toast("收衣袋");
                break;
            case R.id.btn_add_cloth:
                toast("添加衣物");
                break;
        }
    }


}

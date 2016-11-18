package cn.com.bluemoon.delivery.module.evidencecash;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.EvidenceCashApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.evidencecash.ResultBankModule;
import cn.com.bluemoon.delivery.app.api.model.evidencecash.ResultCash;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.ui.UpDownTextView;

/**
 * Created by ljl on 2016/11/16.
 */
public class EvidenceCashActivity extends BaseActivity {

    @Bind(R.id.txt_cooperate_code)
    TextView txtCooperateCode;
    @Bind(R.id.txt_name)
    TextView txtName;
    @Bind(R.id.txt_shop_amount)
    TextView txtShopAmount;
    @Bind(R.id.txt_total_amount)
    TextView txtTotalAmount;
    @Bind(R.id.up_down)
    UpDownTextView upDown;
    @Bind(R.id.layout_bank)
    LinearLayout layoutBank;
    @Bind(R.id.txt_company)
    TextView txtCompany;
    @Bind(R.id.txt_number)
    TextView txtNumber;
    @Bind(R.id.txt_bank_name)
    TextView txtbankname;
    @Bind(R.id.txt_remark)
    TextView txtRemark;

    @Override
    protected String getTitleString() {
        return getString(R.string.evidence_cash_title);
    }

    @Override
    protected void setActionBar(CommonActionBar titleBar) {
        TextView textView = titleBar.getTvRightView();
        textView.setText(R.string.detail_right_txt);
        textView.setVisibility(View.VISIBLE);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EvidenceCashActivity.this, TransferHistoryActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_evidence_cash;
    }

    @Override
    public void onResume() {
        super.onResume();
        showWaitDialog();
        EvidenceCashApi.cash(getToken(),getNewHandler(1, ResultCash.class));
    }

    @Override
    public void initView() {
        upDown.setListener(new UpDownTextView.ClickListener() {
            @Override
            public void onClick(boolean clicked, int index) {
                if (clicked) {
                    layoutBank.setVisibility(View.VISIBLE);
                    EvidenceCashApi.module(getToken(), getNewHandler(2, ResultBankModule.class));
                } else {
                    layoutBank.setVisibility(View.GONE);
                }
            }
        }, 1);
    }

    @Override
    public void initData() {

    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        if (requestCode == 1) {
            hideWaitDialog();
            ResultCash resultCash = (ResultCash)result;
            txtCooperateCode.setText(resultCash.getUserCode());
            txtName.setText(resultCash.getUserName());
            txtShopAmount.setText(getString(R.string.shop_count, resultCash.getStoreCount()));
            txtTotalAmount.setText(formatMoney(resultCash.getTotalAmount()));
        } else if (requestCode == 2) {
            ResultBankModule resultBankModule = (ResultBankModule)result;
            txtCompany.setText(resultBankModule.getModuleName());
            txtNumber.setText(resultBankModule.getModuleAccount());
            txtbankname.setText(resultBankModule.getModuleBank());
            txtRemark.setText(resultBankModule.getModuleRemark());
        }
    }

    @OnClick({R.id.layout_pay_online,R.id.layout_pay_bank,R.id.btn_ok})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_pay_bank :
                upDown.changeStatus();
                break;
            case R.id.layout_pay_online :
                Intent intent = new Intent(this, PayOnlineActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_ok :
                intent = new Intent(this, TransferVoucherActivity.class);
                startActivity(intent);
                break;
        }
    }

    private String formatMoney(long money) {
        StringBuilder strBuff = new StringBuilder( String.valueOf(money));
        int length = strBuff.toString().length();
        if (length == 1) {
            strBuff.insert(0, "0.0");
        } else if (length == 2){
            strBuff.insert(0, "0.");
        } else if (length > 8) {
            strBuff.insert(length - 8, ",");
            strBuff.insert(length - 4, ",");
            strBuff.insert(length, ".");
        } else if (length > 5) {
            strBuff.insert(length - 5, ",");
            strBuff.insert(length - 1, ".");
        } else {
            strBuff.insert(length - 2, ".");
        }
        return strBuff.toString();
    }
}

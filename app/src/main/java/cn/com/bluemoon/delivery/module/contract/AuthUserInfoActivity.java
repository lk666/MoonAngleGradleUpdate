package cn.com.bluemoon.delivery.module.contract;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.Bind;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.ContractApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.contract.ResultCheckPersonReal;
import cn.com.bluemoon.delivery.app.api.model.contract.ResultDoRealNameCheck;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.WithContextTextHttpResponseHandler;
import cn.com.bluemoon.delivery.utils.FileUtil;
import cn.com.bluemoon.delivery.utils.ViewUtil;
import cn.com.bluemoon.lib.view.CommonAlertDialog;
import cn.com.bluemoon.lib_widget.module.form.BMAngleBtn1View;
import cn.com.bluemoon.lib_widget.module.form.BmCellTextView;

public class AuthUserInfoActivity extends BaseActivity {

    @Bind(R.id.txt_name)
    BmCellTextView txtName;
    @Bind(R.id.txt_card_id)
    BmCellTextView txtCardId;
    @Bind(R.id.txt_phone)
    BmCellTextView txtPhone;
    @Bind(R.id.cb_tnc)
    CheckBox cbTnc;
    @Bind(R.id.btn_confirm)
    BMAngleBtn1View btnConfirm;
    @Bind(R.id.lv_remark)
    ListView lvRemark;
    private static final String DATA_RESULT = "dataResult";
    private static final String CONTRACT_ID = "contractId";

    public static void startAct(Activity context, String contractId, ResultCheckPersonReal result,
                                int requestCode) {
        Intent intent = new Intent(context, AuthUserInfoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(DATA_RESULT, result);
        bundle.putString(CONTRACT_ID, contractId);
        intent.putExtras(bundle);
        context.startActivityForResult(intent, requestCode);
    }


    ResultCheckPersonReal resultCheckPersonReal;
    String contractId;

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
        resultCheckPersonReal = (ResultCheckPersonReal) getIntent().getSerializableExtra
                (DATA_RESULT);
        contractId = getIntent().getStringExtra(CONTRACT_ID);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_auth_user_info;
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.auth_user_info_title);
    }

    @Override
    public void initView() {
        cbTnc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                btnConfirm.setEnabled(b);
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWaitDialog();
                ContractApi.doRealNameCheck(getToken(), contractId,
                        (WithContextTextHttpResponseHandler) getNewHandler(1,
                                ResultDoRealNameCheck.class));

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK && data != null) {
                setResult(RESULT_OK, data);
            }
            finish();
        }
    }
    @Override
    public void initData() {
        if (resultCheckPersonReal == null) {
            return;
        }
        txtName.setContentText(resultCheckPersonReal.empName);
        txtCardId.setContentText(resultCheckPersonReal.idCard);
        txtPhone.setContentText(resultCheckPersonReal.mobileNo);
        RemarkAdapter adapter = new RemarkAdapter(this);
        adapter.setList(resultCheckPersonReal.remarkText);
        lvRemark.setAdapter(adapter);
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        if (requestCode == 1) {
            ResultDoRealNameCheck obj = (ResultDoRealNameCheck) result;

            if (obj.isSendSms) {
                // 要发短信
                sendSms();
            } else {
                // 不需要发短信
                toast(result.getResponseMsg());
                SignatureActivity.startAct(AuthUserInfoActivity.this, FileUtil.getPathTemp(), 1);
            }
        } else if (requestCode == 4) {
            // 发送短信后，进行银行四要素认证
            toast(result.getResponseMsg());
            pwdDialog.dismiss();
            SignatureActivity.startAct(AuthUserInfoActivity.this, FileUtil.getPathTemp(), 1);
        }
    }

    @Override
    public void onErrorResponse(int requestCode, ResultBase result) {
        super.onErrorResponse(requestCode, result);
        // 三次失败过后不弹窗了
        if (requestCode == 4 && result.getResponseCode() == 13020) {
            pwdDialog.dismiss();
        }
    }

    private CommonAlertDialog pwdDialog;

    /**
     * 弹出验证码输入框
     */
    private void sendSms() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_send_msg_content, null);
        TextView txtMsg = (TextView) view.findViewById(R.id.txt_msg);
        txtMsg.setText(getString(R.string.send_message, resultCheckPersonReal.mobileNo));
        final EditText etCode = (EditText) view.findViewById(R.id.et_psw);

        pwdDialog = new CommonAlertDialog.Builder(this)
                .setTitle(getString(R.string.title_tips))
                .setCancelable(false)
                .setView(view)
                .setDismissable(false)
                .setNegativeButton(R.string.btn_ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                String pwd = etCode.getText().toString();
                                if (TextUtils.isEmpty(pwd)) {
                                    toast(R.string.err_valid_code_empty);
                                } else if (pwd.length() != 6) {
                                    toast(R.string.err_valid_code_empty_6);
                                } else {
                                    showWaitDialog(false);
                                    ContractApi.doBankCardCheck(getToken(), contractId, pwd,
                                            (WithContextTextHttpResponseHandler) getNewHandler(4,
                                                    ResultBase.class));
                                    ViewUtil.hideKeyboard(etCode);
                                }
                            }
                        })
                .setPositiveButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ViewUtil.hideKeyboard(etCode);
                        dialog.dismiss();
                    }
                }).create();
        pwdDialog.show();
        etCode.post(new Runnable() {
            @Override
            public void run() {
                ViewUtil.showKeyboard(etCode);
            }
        });
    }

    class RemarkAdapter extends BaseListAdapter<String> {

        public RemarkAdapter(Context context) {
            super(context, null);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.item_auth_info_remark;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            String remark = list.get(position);
            TextView txtTips = getViewById(R.id.txt_remark);
            txtTips.setText(remark);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pwdDialog != null) {
            pwdDialog.dismiss();
        }
    }
}

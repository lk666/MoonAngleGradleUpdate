package cn.com.bluemoon.delivery.module.wash.collect.withorder;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;

import butterknife.Bind;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.clothing.ResultUserInfo;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.lib.utils.LibViewUtil;
import cn.com.bluemoon.lib_widget.module.form.BMFieldBtn1View;
import cn.com.bluemoon.lib_widget.module.form.BMFieldParagraphView;
import cn.com.bluemoon.lib_widget.module.form.BMFieldStatusView;
import cn.com.bluemoon.lib_widget.module.form.BmCellTextView;

/**
 * Created by liangjiangli on 2018/5/3.
 * 转派他人
 */

public class TransferOrderActivity extends BaseActivity {

    @Bind(R.id.person_opcode)
    BMFieldStatusView personOpcode;
    @Bind(R.id.btn_ok)
    Button btnOk;
    @Bind(R.id.cell_name)
    BmCellTextView cellName;
    @Bind(R.id.cell_mobile)
    BmCellTextView cellMobile;
    @Bind(R.id.field_remark)
    BMFieldParagraphView fieldRemark;
    public static final int WASH_TYPE = 0;
    public static final int APPOINTMENT_TYPE = 1;

    private String code;
    private int type;

    public static void startAct(Fragment fragment, String code, int type, int requestCode) {
        Intent intent = new Intent(fragment.getActivity(), TransferOrderActivity.class);
        intent.putExtra("code", code);
        intent.putExtra("type", type);
        fragment.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
        code = getIntent().getStringExtra("code");
        type = getIntent().getIntExtra("type", 0);
    }

    @Override
    protected String getTitleString() {
        LibViewUtil.hideIM(null);
        return getString(R.string.transfer_other);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_transfer_order;
    }

    @Override
    public void initView() {
        btnOk.setEnabled(false);
        personOpcode.setInputType(EditorInfo.TYPE_CLASS_NUMBER | EditorInfo.TYPE_NUMBER_FLAG_DECIMAL);
        personOpcode.setListener(new BMFieldBtn1View.FieldButtonListener() {
            @Override
            public void onClickLayout(View view) {

            }

            @Override
            public void onClickRight(View view) {
                if (TextUtils.isEmpty(personOpcode.getContent())) {
                    toast(getString(R.string.transfer_code_input));
                } else {
                    DeliveryApi.getEmp(getToken(), personOpcode.getContent(), getNewHandler(1, ResultUserInfo.class));
                }
            }

            @Override
            public void afterTextChanged(BMFieldBtn1View bmFieldBtn1View, String s) {
                if (TextUtils.isEmpty(s)) {
                    btnOk.setEnabled(false);
                }
            }
        });
    }

    @OnClick({R.id.btn_cancel, R.id.btn_ok})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok:
                String receiverCode = personOpcode.getContent();
                String receiverName = cellName.getContentText();
                String receiverPhone = cellMobile.getContentText();
                String remark = fieldRemark.getContent();
                showWaitDialog();
                if (type == WASH_TYPE) {
                    DeliveryApi.washTransfer(getToken(), code, receiverCode,
                            receiverName, receiverPhone, remark, getNewHandler(2, ResultBase.class));
                } else {
                    DeliveryApi.appointmentTransfer(getToken(), code, receiverCode,
                            receiverName, receiverPhone, remark, getNewHandler(2, ResultBase.class));
                }
                break;
            case R.id.btn_cancel:
                finish();
                break;
        }
    }

    @Override
    public void onSuccessException(int requestCode, Throwable t) {
        super.onSuccessException(requestCode, t);
        if (requestCode == 1) {
            cellName.setContentText("");
            cellMobile.setContentText("");
            personOpcode.setFail();
        }

    }

    @Override
    public void onErrorResponse(int requestCode, ResultBase result) {
        super.onErrorResponse(requestCode, result);
        if (requestCode == 1) {
            cellName.setContentText("");
            cellMobile.setContentText("");
            personOpcode.setFail();
        }
    }

    @Override
    public void initData() {

    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        if (requestCode == 1) {
            ResultUserInfo info = (ResultUserInfo) result;
            cellName.setContentText(info.getEmpName());
            cellMobile.setContentText(info.getPhone());
            personOpcode.setSuccess();
            btnOk.setEnabled(true);
        } else if (requestCode == 2) {
            toast(getString(R.string.order_transfer_success));
            setResult(RESULT_OK);
            finish();
        }
    }
}

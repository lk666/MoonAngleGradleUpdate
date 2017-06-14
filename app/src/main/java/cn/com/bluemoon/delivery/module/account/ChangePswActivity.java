package cn.com.bluemoon.delivery.module.account;

import android.widget.Button;
import android.widget.TextView;

import org.kymjs.kjframe.utils.StringUtils;

import butterknife.Bind;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.utils.ViewUtil;
import cn.com.bluemoon.lib.view.ClearEditText;

public class ChangePswActivity extends BaseActivity {

    @Bind(R.id.ed_old_psw)
    ClearEditText edOldPsw;
    @Bind(R.id.ed_new_psw)
    ClearEditText edNewPsw;
    @Bind(R.id.ed_new_con_psw)
    ClearEditText edNewConPsw;
    @Bind(R.id.txt_toast)
    TextView txtToast;
    @Bind(R.id.btn_submit)
    Button btnSubmit;

    @Override
    protected int getLayoutId() {
        return R.layout.account_set_change_psw;
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.user_change_pwd);
    }

    @Override
    public void initView() {
        edOldPsw.setMaxLength(18);
        edNewPsw.setMaxLength(18);
        edNewConPsw.setMaxLength(18);
    }

    @Override
    public void initData() {

    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        toast(R.string.change_pwd_success);
        finish();
    }

    @Override
    public void onErrorResponse(int requestCode, ResultBase result) {
        txtToast.setText(result.getResponseMsg());
        ViewUtil.showSubmitAmin(btnSubmit,txtToast);
    }

    public void submit() {
        String cuPsw = edOldPsw.getText().toString();
        String nePsw = edNewPsw.getText().toString();
        String coPsw = edNewConPsw.getText().toString();
        boolean isTrue = false;
        if (StringUtils.isEmpty(cuPsw)) {
            txtToast.setText(R.string.login_old_psw_empty);
        } else if (StringUtils.isEmpty(nePsw)) {
            txtToast.setText(R.string.login_new_psw_empty);
        } else if (!coPsw.equals(nePsw)) {
            txtToast.setText(R.string.login_same_psw);
        } else {
            isTrue = true;
            showWaitDialog();
            DeliveryApi.updatePassword(ClientStateManager.getLoginToken(), cuPsw, nePsw, getNewHandler(0, ResultBase.class));
        }
        if (!isTrue) {
            ViewUtil.showSubmitAmin(btnSubmit, txtToast);
        }

    }

    @OnClick(R.id.btn_submit)
    public void onClick() {
        submit();
    }
}

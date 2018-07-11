package cn.com.bluemoon.delivery;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.kymjs.kjframe.utils.StringUtils;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.HRApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.ResultUser;
import cn.com.bluemoon.delivery.app.api.model.User;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.module.account.ChangePswActivity;
import cn.com.bluemoon.delivery.module.account.LoginActivity;
import cn.com.bluemoon.delivery.module.account.SettingActivity;
import cn.com.bluemoon.delivery.module.base.BaseFragment;
import cn.com.bluemoon.delivery.module.base.WithContextTextHttpResponseHandler;
import cn.com.bluemoon.delivery.module.hr.personinfo.PersonInfoActivity;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.delivery.utils.ViewUtil;
import cn.com.bluemoon.lib.view.CommonAlertDialog;


/**
 * 侧滑栏
 * Created by liangjiangli on 2016/5/5.
 */
public class MenuFragment extends BaseFragment implements View.OnClickListener {
    @BindView(R.id.txt_userid)
    TextView txtUserid;
    @BindView(R.id.txt_username)
    TextView txtUsername;
    @BindView(R.id.txt_phone)
    TextView txtPhone;
    @BindView(R.id.btn_user_info)
    Button btnUserInfo;
    @BindView(R.id.main_left_fragment)
    LinearLayout topHead;
    public static User user;

    @Override
    protected int getLayoutId() {
        return R.layout.main_left_fragment;
    }

    @Override
    public void initView() {
        ViewUtil.setTopHead(topHead, false);
        setUserInfo();
    }

    @Override
    public void initData() {

    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        if (requestCode == 1) {
            ResultUser userResult = (ResultUser) result;
            user = userResult.getUser();
            if (user != null) {
                txtUserid.setText(user.getAccount());
                txtUsername.setText(user.getRealName());
                txtPhone.setText(user.getMobileNo());
                setBtnUserInfo();
            }
        } else if (requestCode == 2) {
            loginOut();
        } else if (requestCode == 3) {
            // 校验成功，跳到个人信息页
            pwdDialog.dismiss();
            PersonInfoActivity.actionStart(getActivity());
        }
    }

    @Override
    public void onErrorResponse(int requestCode, ResultBase result) {
        int responseCode = result.getResponseCode();
        if (requestCode == 2 && (responseCode == Constants.RESPONSE_RESULT_TOKEN_EXPIRED
                || responseCode == Constants.RESPONSE_RESULT_TOKEN_NOT_EXIST)) {
            loginOut();
        } else {
            super.onErrorResponse(requestCode, result);
        }

    }

    private void setBtnUserInfo() {
        if (user == null) {
            return;
        }

        if (user.getEmpType().equals("emp")) {
            btnUserInfo.setVisibility(View.VISIBLE);
        }
    }

    public void setUserInfo() {
        txtUserid.setText(ClientStateManager.getUserName());
        if (user != null) {
            txtUserid.setText(user.getAccount());
            txtUsername.setText(user.getRealName());
            txtPhone.setText(user.getMobileNo());
            setBtnUserInfo();
        } else {
            if (!StringUtil.isEmpty(getToken())) {
                DeliveryApi.getUserInfo(getToken(), getNewHandler(1, ResultUser.class));
            }
        }

    }

    private void loginOut() {
        toast(R.string.loginout_success);
        Intent i = new Intent();
        i.setClass(getActivity(), LoginActivity.class);
        startActivity(i);
        getActivity().finish();
    }


    private void close() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ((MainActivity) getActivity()).toggle();
            }
        }, 500);
    }

    private CommonAlertDialog pwdDialog;

    private void gotoUserInfo() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout
                .dialog_user_info_content, null);
        final EditText etPsw = (EditText) view.findViewById(R.id.et_psw);

        // 输入密码提示
        pwdDialog = new CommonAlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.title_tips))
                .setMessage(getString(R.string.hint_input_login_psw))
                .setCancelable(false)
                .setView(view)
                .setDismissable(false)
                .setNegativeButton(R.string.btn_ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                String pwd = etPsw.getText().toString();
                                if (TextUtils.isEmpty(pwd)) {
                                    toast(R.string.err_login_psw_empty);
                                } else {
                                    showWaitDialog();
                                    // 验证密码
                                    HRApi.checkPassword(pwd, getToken(),
                                            (WithContextTextHttpResponseHandler) getNewHandler(3, ResultBase.class));
                                }
                                ViewUtil.hideKeyboard(etPsw);
                            }
                        })
                .setPositiveButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ViewUtil.hideKeyboard(etPsw);
                        pwdDialog.dismiss();
                    }
                }).create();
        pwdDialog.show();
        etPsw.post(new Runnable() {
            @Override
            public void run() {
                ViewUtil.showKeyboard(etPsw);
            }
        });
    }

    @OnClick({R.id.btn_change_pwd, R.id.layout_empty, R.id.layout_setting,
            R.id.layout_exit, R.id.btn_user_info, R.id.img_qcode})
    public void onClick(View v) {
        String token = getToken();
        MainActivity act = (MainActivity) getActivity();
        switch (v.getId()) {
            //个人信息
            case R.id.btn_user_info:
                gotoUserInfo();
                break;
            case R.id.btn_change_pwd:
                Intent intent = new Intent(act, ChangePswActivity.class);
                startActivity(intent);
                close();
                break;
            case R.id.layout_exit:
                if (StringUtils.isEmpty(token)) {
                    loginOut();
                } else {
                    DeliveryApi.logout(token, getNewHandler(2, ResultUser.class));
                }
                break;
            case R.id.layout_setting:
                Intent settingIntent = new Intent(act, SettingActivity.class);
                startActivity(settingIntent);
                close();
                break;
            case R.id.img_qcode:
                act.openQrCode();
                break;
            case R.id.layout_empty:
                act.toggle();
                break;

        }
    }

}

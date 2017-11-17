package cn.com.bluemoon.delivery;


import android.app.Activity;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;
import org.kymjs.kjframe.utils.StringUtils;

import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.HRApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.ResultUser;
import cn.com.bluemoon.delivery.app.api.model.User;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.module.account.ChangePswActivity;
import cn.com.bluemoon.delivery.module.account.LoginActivity;
import cn.com.bluemoon.delivery.module.account.SettingActivity;
import cn.com.bluemoon.delivery.module.base.WithContextTextHttpResponseHandler;
import cn.com.bluemoon.delivery.module.hr.personinfo.PersonInfoActivity;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.delivery.utils.ViewUtil;
import cn.com.bluemoon.lib.view.CommonAlertDialog;
import cn.com.bluemoon.liblog.NetLogUtils;


/**
 * 侧滑栏
 * Created by liangjiangli on 2016/5/5.
 */
public class MenuFragment extends Fragment implements View.OnClickListener {

    private String TAG = "MenuFragment";
    private TextView txtUserid;
    private TextView txtUsername;
    private TextView txtPhone;
    private Button btnUserInfo;
    public static User user;
    private MainActivity mContext;

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mContext = (MainActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_left_fragment, container, false);
        ImageView imgQcode = (ImageView) view.findViewById(R.id.img_qcode);
        txtUserid = (TextView) view.findViewById(R.id.txt_userid);
        txtUsername = (TextView) view.findViewById(R.id.txt_username);
        txtPhone = (TextView) view.findViewById(R.id.txt_phone);
        btnUserInfo = (Button) view.findViewById(R.id.btn_user_info);
        btnUserInfo.setVisibility(View.GONE);
        LinearLayout layoutExit = (LinearLayout) view.findViewById(R.id.layout_exit);
        LinearLayout layoutSetting = (LinearLayout) view.findViewById(R.id.layout_setting);
        LinearLayout layoutEmpty = (LinearLayout) view.findViewById(R.id.layout_empty);

        layoutEmpty.setOnClickListener(this);
        view.findViewById(R.id.btn_change_pwd).setOnClickListener(this);
        btnUserInfo.setOnClickListener(this);
        layoutExit.setOnClickListener(this);
        layoutSetting.setOnClickListener(this);
        imgQcode.setOnClickListener(this);

        LinearLayout topHead = (LinearLayout) view.findViewById(R.id.main_left_fragment);
        ViewUtil.setTopHead(topHead,false);

        setUserInfo();
        return view;
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
            String token = ClientStateManager.getLoginToken(mContext);
            if (!StringUtil.isEmpty(token)) {
                DeliveryApi.getUserInfo(token, userInfoHandler);
            }
        }

    }

    private void loginout() {
        PublicUtil.showToast(mContext, getString(R.string.loginout_success));
        Intent i = new Intent();
        i.setClass(mContext, LoginActivity.class);
        startActivity(i);
        mContext.finish();
    }

    AsyncHttpResponseHandler userInfoHandler = new TextHttpResponseHandler(HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            LogUtils.d(TAG, "userInfoHandler result = " + responseString);
            try {
                ResultUser userResult = JSON.parseObject(responseString, ResultUser.class);
                if (userResult.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    user = userResult.getUser();
                    if (user != null) {
                        txtUserid.setText(user.getAccount());
                        txtUsername.setText(user.getRealName());
                        txtPhone.setText(user.getMobileNo());
                        setBtnUserInfo();
                    }
                } else {
                    PublicUtil.showErrorMsg(mContext, userResult);
                }
            } catch (Exception e) {
                LogUtils.e(TAG, e.getMessage());
                PublicUtil.showToastServerBusy();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString,
                              Throwable throwable) {
            LogUtils.e(TAG, throwable.getMessage());
            PublicUtil.showToastServerOvertime();
        }
    };

    AsyncHttpResponseHandler loginoutHandler = new TextHttpResponseHandler(HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            LogUtils.d(TAG, "logout result = " + responseString);
            try {
                ResultBase baseResult = JSON.parseObject(responseString, ResultUser.class);
                if (baseResult.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS
                        || baseResult.getResponseCode() == Constants.RESPONSE_RESULT_TOKEN_EXPIRED
                        || baseResult.getResponseCode() == Constants
                        .RESPONSE_RESULT_TOKEN_NOT_EXIST) {
                    loginout();
                } else {
                    PublicUtil.showErrorMsg(mContext, baseResult);
                }
            } catch (Exception e) {
                LogUtils.e(TAG, e.getMessage());
                PublicUtil.showToastServerBusy();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString,
                              Throwable throwable) {
            LogUtils.e(TAG, throwable.getMessage());

            PublicUtil.showToastServerOvertime();
        }
    };

    WithContextTextHttpResponseHandler checkPwdHandler = new WithContextTextHttpResponseHandler(
            HTTP.UTF_8, mContext, 0x777, ResultBase.class) {

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            try {
                Object resultObj;
                resultObj = JSON.parseObject(responseString, getClazz());
                if (resultObj instanceof ResultBase) {
                    ResultBase resultBase = (ResultBase) resultObj;
                    if (resultBase.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                        NetLogUtils.dNetResponse(Constants.TAG_HTTP_RESPONSE_SUCCESS, getUuid(),
                                System.currentTimeMillis(), responseString);
                        // 校验成功，跳到个人信息页
                        pwdDialog.dismiss();
                        PersonInfoActivity.actionStart(getActivity());
                    } else {
                        NetLogUtils.eNetResponse(Constants.TAG_HTTP_RESPONSE_NOT_SUCCESS,
                                getUuid(), System
                                        .currentTimeMillis(), responseString, new Exception
                                        ("resultBase.getResponseCode() = " + resultBase
                                                .getResponseCode() + "-->" + responseString));
                        PublicUtil.showErrorMsg(mContext, resultBase);
                    }
                } else {
                    throw new Exception
                            ("转换ResultBase失败-->" + responseString);
                }
            } catch (Exception e) {
                NetLogUtils.eNetResponse(Constants.TAG_HTTP_RESPONSE_EXCEPTION, getUuid(),
                        System.currentTimeMillis(), responseString, e);
                PublicUtil.showToastServerBusy();
            }
            mContext.hideWaitDialog();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers,
                              String responseString, Throwable throwable) {
            NetLogUtils.eNetResponse(Constants.TAG_HTTP_RESPONSE_FAILURE, getUuid(), System
                    .currentTimeMillis(), responseString, throwable);
            PublicUtil.showToastServerOvertime();
            mContext.hideWaitDialog();
        }
    };

    private void close() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mContext.toggle();
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
                                    Toast.makeText(getActivity(), R.string.err_login_psw_empty,
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    mContext.showWaitDialog();
                                    // 验证密码
                                    HRApi.checkPassword(pwd, ClientStateManager.getLoginToken(),
                                            checkPwdHandler);
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

    @Override
    public void onClick(View v) {
        String token = ClientStateManager.getLoginToken(mContext);
        switch (v.getId()) {
            //个人信息
            case R.id.btn_user_info:
                //PersonInfoActivity.actionStart(getActivity());
                gotoUserInfo();
                break;
            case R.id.btn_change_pwd:
                Intent intent = new Intent(mContext, ChangePswActivity.class);
                startActivity(intent);
                close();
                break;
            case R.id.layout_exit:
                if (StringUtils.isEmpty(token)) {
                    loginout();
                } else {
                    DeliveryApi.logout(token, loginoutHandler);
                }
                break;
            case R.id.layout_setting:
                Intent settingIntent = new Intent(mContext,
                        SettingActivity.class);
                startActivity(settingIntent);
                close();
                break;
            case R.id.img_qcode:
                mContext.openQrCode();
                break;
            case R.id.layout_empty:
                mContext.toggle();
                break;

        }
    }

}

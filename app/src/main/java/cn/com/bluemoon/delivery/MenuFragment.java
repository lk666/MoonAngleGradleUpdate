package cn.com.bluemoon.delivery;


import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;
import org.kymjs.kjframe.utils.StringUtils;

import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.ResultUser;
import cn.com.bluemoon.delivery.app.api.model.User;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.module.account.view.ChangePswActivity;
import cn.com.bluemoon.delivery.module.account.view.LoginActivity;
import cn.com.bluemoon.delivery.module.account.view.SettingActivity;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;


/**
 * Created by liangjiangli on 2016/5/5.
 */
public class MenuFragment extends Fragment implements View.OnClickListener{

    private String TAG = "MenuFragment";
    private TextView txtUserid;
    private TextView txtUsername;
    private TextView txtPhone;
    private LinearLayout layoutChangePwd;
    private LinearLayout layoutExit;
    private LinearLayout layoutSetting;
    private LinearLayout layoutEmpty;
    public static User user;
    private MainActivity mContext;
    private ImageView imgQcode;

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mContext = (MainActivity) activity;
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_left_fragment, container, false);
        imgQcode = (ImageView) view.findViewById(R.id.img_qcode);
        txtUserid = (TextView)view.findViewById(R.id.txt_userid);
        txtUsername = (TextView)view.findViewById(R.id.txt_username);
        txtPhone = (TextView)view.findViewById(R.id.txt_phone);
        layoutChangePwd = (LinearLayout) view.findViewById(R.id.layout_changepwd);
        layoutExit = (LinearLayout) view.findViewById(R.id.layout_exit);
        layoutSetting = (LinearLayout) view.findViewById(R.id.layout_setting);
        layoutEmpty = (LinearLayout) view.findViewById(R.id.layout_empty);

        View line = view.findViewById(R.id.view_line);
        line.getBackground().setAlpha(255 * 3 / 10);
        layoutEmpty.setOnClickListener(this);
        layoutChangePwd.setOnClickListener(this);
        layoutExit.setOnClickListener(this);
        layoutSetting.setOnClickListener(this);
        imgQcode.setOnClickListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int statusbarHeight = getStatusBarHeight();
            LinearLayout top_head = (LinearLayout) view.findViewById(R.id.main_left_fragment);
            top_head.setPadding(0, statusbarHeight, 0, 0);
        }
        setUserInfo();
        return view;
    }
    public void setUserInfo() {
        txtUserid.setText(ClientStateManager.getUserName(mContext));
        if (user != null) {
            txtUserid.setText(user.getAccount());
            txtUsername.setText(user.getRealName());
            txtPhone.setText(user.getMobileNo());
        } else {
            String token = ClientStateManager.getLoginToken(mContext);
            if(!StringUtil.isEmpty(token)){
                DeliveryApi.getUserInfo(token, userInfoHandler);
            }
        }

    }
    private void loginout(){
        PublicUtil.showToast(mContext, getString(R.string.loginout_success));
        Intent i = new Intent();
        i.setClass(mContext, LoginActivity.class);
        startActivity(i);
        mContext.finish();
    }

    AsyncHttpResponseHandler userInfoHandler = new TextHttpResponseHandler(HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            LogUtils.d(TAG,"getVerifyCode result = " + responseString);
            try {
                ResultUser userResult = JSON.parseObject(responseString,ResultUser.class);
                if(userResult.getResponseCode()== Constants.RESPONSE_RESULT_SUCCESS){
                    user = userResult.getUser();
                    if (user != null){
                        txtUserid.setText(user.getAccount());
                        txtUsername.setText(user.getRealName());
                        txtPhone.setText(user.getMobileNo());
                    }
                }else{
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
            LogUtils.d(TAG,"logout result = " + responseString);
            try {
                ResultBase baseResult = JSON.parseObject(responseString,ResultUser.class);
                if(baseResult.getResponseCode()==Constants.RESPONSE_RESULT_SUCCESS
                        ||baseResult.getResponseCode()==Constants.RESPONSE_RESULT_TOKEN_EXPIRED
                        ||baseResult.getResponseCode()==Constants.RESPONSE_RESULT_TOKEN_NOT_EXIST){
                    loginout();
                }else{
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


    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void close(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mContext.CloseMenu();
            }
        },500);
    }

    @Override
    public void onClick(View v) {
        String token = ClientStateManager.getLoginToken(mContext);
        switch (v.getId()) {
            case R.id.layout_changepwd:
                Intent intent = new Intent(mContext, ChangePswActivity.class);
                startActivity(intent);
                close();
                break;
            case R.id.layout_exit:
                if(StringUtils.isEmpty(token)){
                    loginout();
                }else{
                    DeliveryApi.logout(token, loginoutHandler);
                }
                break;
            case  R.id.layout_setting:
                Intent settingIntent = new Intent(mContext,
                        SettingActivity.class);
                startActivity(settingIntent);
                close();
                break;
            case  R.id.img_qcode:
                mContext.openQcode();
                break;
            case R.id.layout_empty:
                mContext.CloseMenu();
                break;

        }
    }

}

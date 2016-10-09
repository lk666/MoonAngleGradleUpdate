package cn.com.bluemoon.delivery.module.account;

import android.view.View;

import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.utils.Constants;

public class SettingActivity extends BaseActivity {

    @Override
    protected String getTitleString() {
        return getString(R.string.user_settings);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.account_set_main;
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {

    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {

    }

    @OnClick({R.id.re_general, R.id.re_about})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.re_general:
                SettingInfoActivity.actStart(this,Constants.MODE_GENERAL);
                break;
            case R.id.re_about:
                SettingInfoActivity.actStart(this,Constants.MODE_CHECK);
                break;
        }
    }
}

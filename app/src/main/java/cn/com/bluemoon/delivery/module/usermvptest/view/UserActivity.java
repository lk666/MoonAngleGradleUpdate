package cn.com.bluemoon.delivery.module.usermvptest.view;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;

import butterknife.Bind;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.account.LoginActivity;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.usermvptest.presenter.UserPresenter;

public class UserActivity extends BaseActivity implements IUserView {


    @Bind(R.id.et_id)
    EditText etId;
    @Bind(R.id.et_name)
    EditText etName;
    private UserPresenter userPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_test_user;
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.app_name);
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        userPresenter.success(requestCode, result);
    }

    @Override
    public void initView() {
        userPresenter = new UserPresenter(this);
    }

    @Override
    public void initData() {

    }

    @Override
    public void setUserName(String name) {
        etName.setText(name);
    }

    @Override
    public void setUserId(String id) {
        etId.setText(id);
    }

    @Override
    public void toLoginView() {
        LoginActivity.actStart(this);
    }

    public static void actStart(Context context) {
        Intent intent = new Intent(context, UserActivity.class);
        context.startActivity(intent);
    }

    @OnClick({R.id.btn_get, R.id.btn_exit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_get:
                userPresenter.getUser();
                break;
            case R.id.btn_exit:
                userPresenter.logout();
                break;
        }
    }
}

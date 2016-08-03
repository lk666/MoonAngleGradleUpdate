package cn.com.bluemoon.delivery.module.mvptest.view;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;

import butterknife.Bind;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.mvptest.presenter.UserPresenter;

public class UserActivity extends BaseActivity implements IUserView{


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
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {

    }

    @Override
    public void initView() {
        userPresenter = new UserPresenter(this);
    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.btn_get, R.id.btn_upload})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_get:
                userPresenter.setUser();
                break;
            case R.id.btn_upload:
                userPresenter.upload(getUserId(),getUserName());
                break;
        }
    }

    @Override
    public String getUserName() {
        return etName.getText().toString();
    }

    @Override
    public String getUserId() {
        return etId.getText().toString();
    }

    @Override
    public void setUserName(String name) {
        etName.setText(name);
    }

    @Override
    public void setUserId(String id) {
        etId.setText(id);
    }

    public static void actStart(Context context){
        Intent intent = new Intent(context,UserActivity.class);
        context.startActivity(intent);
    }
}

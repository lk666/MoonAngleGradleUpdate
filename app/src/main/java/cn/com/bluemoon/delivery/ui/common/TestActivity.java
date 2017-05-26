package cn.com.bluemoon.delivery.ui.common;

import butterknife.Bind;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.base.BaseActivity;

public class TestActivity extends BaseActivity {

    @Bind(R.id.view_bottom)
    BMListPaginationView viewBottom;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_test;
    }

    @Override
    protected String getTitleString() {
        return "通用组件测试";
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

}

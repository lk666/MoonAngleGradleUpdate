package cn.com.bluemoon.delivery.module.hr.personinfo;

import android.support.v4.app.Fragment;
import android.view.View;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.newbase.BaseFragment;
import cn.com.bluemoon.delivery.module.newbase.view.CommonActionBar;

/**
 * 个人信息
 * Created by lk on 2017/6/14.
 */

public class PersonInfoFragment extends BaseFragment<CommonActionBar> {
    public static Fragment newInstance() {
        PersonInfoFragment fragment = new PersonInfoFragment();
        return fragment;
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.user_info);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_person_info;
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initContentView(View mainView) {

    }
}

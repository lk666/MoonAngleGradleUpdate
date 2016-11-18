package cn.com.bluemoon.delivery.module.evidencecash;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.base.BaseActivity;

/**
 * Created by ljl on 2016/11/18.
 */
public class TransferHistoryActivity extends BaseActivity{

    @Override
    protected String getTitleString() {
        return getString(R.string.transfer_history_title);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_transfer_history;
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

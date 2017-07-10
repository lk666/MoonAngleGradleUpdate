package cn.com.bluemoon.delivery.module.wash.returning.manager;


import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.base.BaseFragment;

/**
 * 查询结果空数据页
 * Created by tangqiwei on 2017/7/10.
 */

public class SearchResultNullDataFragment extends BaseFragment {

    @Override
    protected String getTitleString() {
        return getString(R.string.returning_queries_result_title);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_queries_result_null_data;
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

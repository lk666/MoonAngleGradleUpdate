package cn.com.bluemoon.delivery.module.wash.returning.manager;

import android.view.View;

import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.BasePullToRefreshListViewFragment;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;

/**
 * Created by ljl on 2016/9/19.
 */
public class ReturnFragment extends BasePullToRefreshListViewFragment{

    @Override
    protected String getTitleString() {
        return getString(R.string.manger_tab_2);
    }
    @Override
    protected BaseListAdapter getNewAdapter() {
        return null;
    }

    @Override
    protected List getGetMoreList(ResultBase result) {
        return null;
    }

    @Override
    protected List getGetDataList(ResultBase result) {
        return null;
    }

    @Override
    protected PullToRefreshBase.Mode getMode() {
        return null;
    }

    @Override
    protected void invokeGetDataDeliveryApi(int requestCode) {

    }

    @Override
    protected void invokeGetMoreDeliveryApi(int requestCode) {

    }

    @Override
    public void onItemClick(Object item, View view, int position) {

    }
}

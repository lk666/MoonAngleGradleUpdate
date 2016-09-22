package cn.com.bluemoon.delivery.module.wash.returning.driver;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.ReturningApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.wash.driver.DriverCarriage;
import cn.com.bluemoon.delivery.app.api.model.wash.driver.ResultWaitLoadList;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.BasePullToRefreshListViewFragment;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;


public class WaitLoadFragment extends BasePullToRefreshListViewFragment {

    private long timesamp;

    @Override
    protected String getTitleString() {
        return getString(R.string.driver_tab_wait_load);
    }

    @Override
    protected void initPullToRefreshListView(PullToRefreshListView ptrlv) {
        ptrlv.getRefreshableView().setDivider(null);
        ptrlv.getRefreshableView().setDividerHeight(getResources().getDimensionPixelSize(R.dimen
                .div_height_10));
    }

    @Override
    protected BaseListAdapter getNewAdapter() {
        return new WaitLoadAdapter(getActivity(),this);
    }

    @Override
    protected List getGetMoreList(ResultBase result) {
        ResultWaitLoadList loadList = (ResultWaitLoadList)result;
        timesamp = loadList.getPageFlag();
        return loadList.getCarriageList();
    }

    @Override
    protected List getGetDataList(ResultBase result) {
        ResultWaitLoadList loadList = (ResultWaitLoadList)result;
        timesamp = loadList.getPageFlag();
        return loadList.getCarriageList();
    }

    @Override
    protected PullToRefreshBase.Mode getMode() {
        return PullToRefreshBase.Mode.BOTH;
    }

    @Override
    protected void invokeGetDataDeliveryApi(int requestCode) {
        ReturningApi.queryWaitLoadList(0,getToken(),getNewHandler(requestCode,ResultWaitLoadList.class));
    }

    @Override
    protected void invokeGetMoreDeliveryApi(int requestCode) {
        ReturningApi.queryWaitLoadList(timesamp,getToken(),getNewHandler(requestCode,ResultWaitLoadList.class));
    }

    @Override
    public void onItemClick(Object item, View view, int position) {
        DriverCarriage carriage = (DriverCarriage)item;
        switch (view.getId()){
            case R.id.txt_transport_code:
                CarriageDetailActivity.actStart(getActivity(),carriage.getCarriageCode());
                break;
            case R.id.btn_load:
                TransportListActivity.actStart(this, carriage.getCarriageCode());
                break;
        }

    }


    class WaitLoadAdapter extends BaseListAdapter<DriverCarriage>{

        public WaitLoadAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.item_driver_carriage;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            DriverCarriage item = list.get(position);
            TextView txtTransportCode = getViewById(R.id.txt_transport_code);
            TextView txtBoxNum = getViewById(R.id.txt_box_num);
            TextView txtLoadAddress = getViewById(R.id.txt_load_address);
            Button btnLoad = getViewById(R.id.btn_load);
            txtTransportCode.setText(getString(R.string.driver_transport_code, item.getCarriageCode()));
            txtBoxNum.setText(getString(R.string.driver_box_num,item.getBoxNum()));
            txtLoadAddress.setText(getString(R.string.driver_load_address, item.getCenterName()));

            setClickEvent(isNew, position, txtTransportCode, btnLoad);
        }
    }
}

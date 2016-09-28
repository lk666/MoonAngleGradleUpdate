package cn.com.bluemoon.delivery.module.wash.returning.driver;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.ReturningApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.wash.driver.DriverCarriage;
import cn.com.bluemoon.delivery.app.api.model.wash.driver.ResultCarriageHistory;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.BasePullToRefreshListViewFragment;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.module.wash.returning.closebox.SingleTimerFilterWindow;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;


public class CarriageHistoryFragment extends BasePullToRefreshListViewFragment {

    private long pageFlag = 0;
    private long chooseTime = 0;
    private View viewPopStart;
    private TextView txtTime;
    private TextView txtCount;

    @Override
    protected String getTitleString() {
        return getString(R.string.driver_carriage_history_title);
    }

    @Override
    protected void initPullToRefreshListView(PullToRefreshListView ptrlv) {
        ptrlv.getRefreshableView().setDivider(null);
        ptrlv.getRefreshableView().setDividerHeight(0);
    }

    @Override
    protected int getHeadLayoutId() {
        return R.layout.head_fragment_tab_close_box;
    }

    @Override
    protected void initHeadViewEvent(View headView) {
        super.initHeadViewEvent(headView);
        viewPopStart = headView.findViewById(R.id.view_pop_start);
        txtTime = (TextView) headView.findViewById(R.id.txt_count);
        txtCount = (TextView) headView.findViewById(R.id.txt_pending_box);
        setHeadViewVisibility(View.GONE);
        setEmptyViewMsg(String.format(getString(R.string.current_no_some_data), getTitleString()));
    }

    @Override
    protected void setActionBar(CommonActionBar titleBar) {
        super.setActionBar(titleBar);
        titleBar.getTvRightView().setText(R.string.btn_txt_fillter);
        titleBar.getTvRightView().setCompoundDrawablePadding(10);

        Drawable drawableFillter = getResources().getDrawable(R.mipmap.icon_filter);
        drawableFillter.setBounds(0, 0, drawableFillter.getMinimumWidth(), drawableFillter.getMinimumHeight());
        titleBar.getTvRightView().setCompoundDrawables(drawableFillter, null, null, null);
        titleBar.getTvRightView().setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActionBarBtnRightClick() {
        super.onActionBarBtnRightClick();
        SingleTimerFilterWindow popupWindow = new SingleTimerFilterWindow(getActivity(), chooseTime, new
                SingleTimerFilterWindow.FilterListener() {
                    @Override
                    public void onOkClick(long time) {
                        chooseTime = time;
                        if (time > 0) {
                            setHeadViewVisibility(View.VISIBLE);
                            txtTime.setText(DateUtil.getTime(time, "yyyy/MM/dd"));
                        } else {
                            setHeadViewVisibility(View.GONE);
                        }
                        initData();
                    }
                });
        popupWindow.showPopwindow(viewPopStart);
    }

    @Override
    protected BaseListAdapter getNewAdapter() {
        return new CarriageHistoryAdapter(getActivity(), this);
    }

    @Override
    protected List getGetMoreList(ResultBase result) {
        ResultCarriageHistory carriageHistory = (ResultCarriageHistory) result;
        pageFlag = carriageHistory.getPageFlag();
        txtCount.setText(getString(R.string.driver_order_num, carriageHistory.getCarriageSum()));
        return carriageHistory.getCarriageList();
    }

    @Override
    protected List getGetDataList(ResultBase result) {
        ResultCarriageHistory carriageHistory = (ResultCarriageHistory) result;
        pageFlag = carriageHistory.getPageFlag();
        txtCount.setText(getString(R.string.driver_order_num, carriageHistory.getCarriageSum()));
        return carriageHistory.getCarriageList();
    }

    @Override
    protected PullToRefreshBase.Mode getMode() {
        return PullToRefreshBase.Mode.BOTH;
    }

    @Override
    protected void invokeGetDataDeliveryApi(int requestCode) {
        pageFlag = 0;
        ReturningApi.queryCarriageHistoryList(0, chooseTime, getToken(), getNewHandler(requestCode, ResultCarriageHistory.class));
    }

    @Override
    protected void invokeGetMoreDeliveryApi(int requestCode) {
        ReturningApi.queryCarriageHistoryList(pageFlag, chooseTime, getToken(), getNewHandler(requestCode, ResultCarriageHistory.class));
    }

    @Override
    public void onItemClick(Object item, View view, int position) {
        HistoryDetailActivity.actStart(this,(DriverCarriage)item);
    }


    class CarriageHistoryAdapter extends BaseListAdapter<DriverCarriage> {

        public CarriageHistoryAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.item_carriage_history;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            DriverCarriage item = list.get(position);
            TextView txtTransportCode = getViewById(R.id.txt_transport_code);
            TextView txtBoxNum = getViewById(R.id.txt_box_num);
            TextView txtRealBoxNum = getViewById(R.id.txt_real_box_num);
            TextView txtCarriageTime = getViewById(R.id.txt_carriage_time);
            txtTransportCode.setText(getString(R.string.driver_transport_code, item.getCarriageCode()));
            txtBoxNum.setText(getString(R.string.driver_box_num, item.getBoxNum()));
            txtRealBoxNum.setText(getString(R.string.driver_real_box_num, item.getActualNum()));
            txtCarriageTime.setText(getString(R.string.driver_carriage_time,
                    DateUtil.getTime(item.getReceiverSignTime(), "yyyy-MM-dd HH:mm:ss")));
            setClickEvent(isNew, position, convertView);
        }
    }
}

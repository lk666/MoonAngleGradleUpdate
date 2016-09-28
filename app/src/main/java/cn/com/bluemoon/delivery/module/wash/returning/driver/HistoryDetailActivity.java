package cn.com.bluemoon.delivery.module.wash.returning.driver;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.ReturningApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.wash.driver.DriverCarriage;
import cn.com.bluemoon.delivery.app.api.model.wash.driver.ResultCarriageHistoryDetail;
import cn.com.bluemoon.delivery.app.api.model.wash.driver.TagBoxHistory;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.BasePullToRefreshListViewActivity;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;

public class HistoryDetailActivity extends BasePullToRefreshListViewActivity {

    private DriverCarriage carriage;

    public static void actStart(Fragment fragment,DriverCarriage item){
        Intent intent = new Intent(fragment.getActivity(),HistoryDetailActivity.class);
        intent.putExtra("item",item);
        fragment.startActivity(intent);
    }

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
        carriage = (DriverCarriage)getIntent().getSerializableExtra("item");
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.driver_carriage_history_detail_title);
    }

    @Override
    protected void initPullToRefreshListView(PullToRefreshListView ptrlv) {
        ptrlv.getRefreshableView().setDivider(null);
        ptrlv.getRefreshableView().setDividerHeight(getResources().getDimensionPixelOffset(R.dimen.div_height_8));
    }

    @Override
    protected int getHeadLayoutId() {
        return R.layout.head_driver_carriage_detail;
    }

    @Override
    protected void initHeadViewEvent(View headView) {
        super.initHeadViewEvent(headView);
        TextView txtTransportCode = (TextView)headView.findViewById(R.id.txt_transport_code);
        TextView txtBoxNum = (TextView) headView.findViewById(R.id.txt_box_num);
        TextView txtRealBoxNum = (TextView) headView.findViewById(R.id.txt_real_box_num);
        txtTransportCode.setText(getString(R.string.driver_transport_code,carriage.getCarriageCode()));
        txtBoxNum.setText(getString(R.string.driver_box_num, carriage.getBoxNum()));
        txtRealBoxNum.setText(getString(R.string.driver_real_box_num,carriage.getActualNum()));
        setEmptyViewMsg(String.format(getString(R.string.current_no_some_data), getTitleString()));

    }

    @Override
    protected void showEmptyView() {
        super.showEmptyView();
        // 可在此处设置head等
        setHeadViewVisibility(View.VISIBLE);
    }

    @Override
    protected void showNetErrorView() {
        super.showNetErrorView();
        // 可在此处设置head等
        setHeadViewVisibility(View.GONE);
    }

    @Override
    protected void showRefreshView() {
        super.showRefreshView();
        // 列表数据刷新，如可在此处设置head等
        setHeadViewVisibility(View.VISIBLE);
    }

    @Override
    protected BaseListAdapter getNewAdapter() {
        return new HistoryDetailAdapter(this,this);
    }

    @Override
    protected List getGetMoreList(ResultBase result) {
        return null;
    }

    @Override
    protected List getGetDataList(ResultBase result) {
        ResultCarriageHistoryDetail detail = (ResultCarriageHistoryDetail) result;
        return detail.getTagList();
    }

    @Override
    protected PullToRefreshBase.Mode getMode() {
        return PullToRefreshBase.Mode.DISABLED;
    }

    @Override
    protected void invokeGetDataDeliveryApi(int requestCode) {
        showWaitDialog();
        ReturningApi.queryCarriageHistoryDetail(carriage.getCarriageCode(), getToken(), getNewHandler(requestCode, ResultCarriageHistoryDetail.class));
    }

    @Override
    protected void invokeGetMoreDeliveryApi(int requestCode) {

    }

    @Override
    public void onItemClick(Object item, View view, int position) {

    }

    class HistoryDetailAdapter extends BaseListAdapter<TagBoxHistory>{

        public HistoryDetailAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.item_driver_history_detail;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            TagBoxHistory item = list.get(position);
            TextView txtBoxCode = getViewById(R.id.txt_box_code);
            TextView txtCenterName = getViewById(R.id.txt_center_name);
            TextView txtReceiver = getViewById(R.id.txt_receiver);
            TextView txtTime = getViewById(R.id.txt_time);
            txtBoxCode.setText(getString(R.string.driver_box_code3, item.getTagCode()));
            txtCenterName.setText(item.getCenterName());
            txtReceiver.setText(getString(R.string.driver_receiver, item.getReceiver()));
            txtTime.setText(DateUtil.getTime(item.getReceiverSignTime(), "yyyy-MM-dd HH:mm:ss"));
        }
    }
}

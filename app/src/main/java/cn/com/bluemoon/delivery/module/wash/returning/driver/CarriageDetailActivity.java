package cn.com.bluemoon.delivery.module.wash.returning.driver;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.ReturningApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.wash.driver.DriverBox;
import cn.com.bluemoon.delivery.app.api.model.wash.driver.ResultCarriageDetail;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib.utils.LibViewUtil;
import cn.com.bluemoon.lib.view.CommonEmptyView;

public class CarriageDetailActivity extends BaseActivity {

    @Bind(R.id.txt_transport_code)
    TextView txtTransportCode;
    @Bind(R.id.txt_box_num)
    TextView txtBoxNum;
    @Bind(R.id.ptrlv)
    PullToRefreshListView ptrlv;
    @Bind(R.id.layout_title)
    RelativeLayout layoutTitle;
    @Bind(R.id.layout_empty)
    CommonEmptyView layoutEmpty;
    private String carriageCode;
    private CarriageDetailAdapter adapter;

    public static void actStart(Context context, String carriageCode) {
        Intent intent = new Intent(context, CarriageDetailActivity.class);
        intent.putExtra("code", carriageCode);
        context.startActivity(intent);
    }

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
        carriageCode = getIntent().getStringExtra("code");
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.driver_carriage_detail_title);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_carriage_detail;
    }

    @Override
    public void initView() {
        layoutEmpty.setContentText(getString(R.string.empty_hint3, getTitleString()));
        layoutEmpty.setEmptyListener(new CommonEmptyView.EmptyListener() {
            @Override
            public void onRefresh() {
                initData();
            }
        });
    }

    @Override
    public void initData() {
        showWaitDialog();
        ReturningApi.queryCarriageDetail(carriageCode, getToken(), getNewHandler(0, ResultCarriageDetail.class));
    }

    private void setAdapter(List<DriverBox> list) {
        if (adapter == null) {
            adapter = new CarriageDetailAdapter(this, null);
        }
        adapter.setList(list);
        ptrlv.setAdapter(adapter);
    }

    private void showEmptyView(){
        LibViewUtil.setViewVisibility(ptrlv, View.GONE);
        LibViewUtil.setViewVisibility(layoutEmpty, View.VISIBLE);
    }

    private void hideEmptyView(){
        LibViewUtil.setViewVisibility(ptrlv, View.VISIBLE);
        LibViewUtil.setViewVisibility(layoutEmpty, View.GONE);
    }

    private void showEmptyViewNoTitle(){
        LibViewUtil.setViewVisibility(layoutTitle, View.GONE);
        showEmptyView();
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        ResultCarriageDetail carriageDetail = (ResultCarriageDetail) result;
        LibViewUtil.setViewVisibility(layoutTitle, View.VISIBLE);
        txtTransportCode.setText(getString(R.string.driver_transport_code, carriageCode));
        txtBoxNum.setText(getString(R.string.driver_box_num, carriageDetail.getBoxNum()));
        if(carriageDetail.getBoxList().size()==0){
            showEmptyView();
        }else{
            hideEmptyView();
        }
        setAdapter(carriageDetail.getBoxList());
    }

    @Override
    public void onSuccessException(int requestCode, Throwable t) {
        super.onSuccessException(requestCode, t);
        showEmptyViewNoTitle();
    }

    @Override
    public void onErrorResponse(int requestCode, ResultBase result) {
        super.onErrorResponse(requestCode, result);
        showEmptyViewNoTitle();
    }

    @Override
    public void onFailureResponse(int requestCode, Throwable t) {
        super.onFailureResponse(requestCode, t);
        showEmptyViewNoTitle();
    }

    class CarriageDetailAdapter extends BaseListAdapter<DriverBox> {

        public CarriageDetailAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.item_driver_carriage_detail;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            DriverBox item = list.get(position);
            TextView txtBoxCode = getViewById(R.id.txt_box_code);
            TextView txtCenterNum = getViewById(R.id.txt_center_num);
            txtBoxCode.setText(getString(R.string.driver_box_code, item.getBoxCode()));
            txtCenterNum.setText(getString(R.string.driver_center_num, item.getCenterNum()));
        }
    }

}

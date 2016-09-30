package cn.com.bluemoon.delivery.module.wash.returning.driver;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.ReturningApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.wash.driver.DriverBox;
import cn.com.bluemoon.delivery.app.api.model.wash.driver.ResultCarriageDetail;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.module.wash.returning.cupboard.CupboardScanActivity;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib.utils.LibViewUtil;
import cn.com.bluemoon.lib.view.CommonEmptyView;

public class TransportListActivity extends BaseActivity {

    @Bind(R.id.txt_transport_code)
    TextView txtTransportCode;
    @Bind(R.id.txt_box_num)
    TextView txtBoxNum;
    @Bind(R.id.txt_real_box_num)
    TextView txtRealBoxNum;
    @Bind(R.id.btn_load_finish)
    Button btnLoadFinish;
    @Bind(R.id.ptrlv)
    PullToRefreshListView ptrlv;
    @Bind(R.id.layout_title)
    RelativeLayout layoutTitle;
    @Bind(R.id.layout_empty)
    CommonEmptyView layoutEmpty;
    private String carriageCode;
    private CarriageDetailAdapter adapter;
    private ResultCarriageDetail carriageDetail;

    public static void actStart(Fragment fragment, String carriageCode) {
        Intent intent = new Intent(fragment.getActivity(), TransportListActivity.class);
        intent.putExtra("code", carriageCode);
        fragment.startActivityForResult(intent, 0);
    }

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
        carriageCode = getIntent().getStringExtra("code");
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.driver_carriage_confirm_title);
    }

    @Override
    protected void setActionBar(CommonActionBar titleBar) {
        super.setActionBar(titleBar);
        titleBar.getImgRightView().setImageResource(R.mipmap.scan_top_nav);
        titleBar.getImgRightView().setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActionBarBtnRightClick() {
        super.onActionBarBtnRightClick();
        CupboardScanActivity.actStart(this, carriageDetail);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_carriage_detail;
    }

    @Override
    public void initView() {
        LibViewUtil.setViewVisibility(txtRealBoxNum, View.VISIBLE);
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
        ReturningApi.queryTransportList(carriageCode, getToken(), getNewHandler(0, ResultCarriageDetail.class));
    }

    /*private void changeList() {
        for (int i = 0; i < carriageDetail.getBoxList().size(); i++) {
            carriageDetail.getBoxList().get(i).setBoxCode("no" + i);
        }
    }*/

    /**
     * 获取已扫描的衣物箱号列表
     *
     * @param list
     * @return
     */
    private List<String> getCheckList(List<DriverBox> list) {
        List<String> checkList = new ArrayList<>();
        if (list != null) {
            for (DriverBox item : list) {
                if (item.isCheck) {
                    checkList.add(item.getBoxCode());
                }
            }
        }
        return checkList;
    }

    @OnClick(R.id.btn_load_finish)
    public void onClick() {
        showWaitDialog();
        ReturningApi.loadComplete(getCheckList(carriageDetail.getBoxList()), carriageCode, getToken(), getNewHandler(1, ResultBase.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK && data != null) {
            carriageDetail = (ResultCarriageDetail) data.getSerializableExtra("item");
            adapter.setList(carriageDetail.getBoxList());
            adapter.notifyDataSetChanged();
        }
    }

    private void setAdapter(List<DriverBox> list) {
        if (adapter == null) {
            adapter = new CarriageDetailAdapter(this, null);
        }
        adapter.setList(list);
        ptrlv.setAdapter(adapter);
    }

    private void showEmptyView(){
        LibViewUtil.setViewVisibility(btnLoadFinish, View.GONE);
        LibViewUtil.setViewVisibility(ptrlv, View.GONE);
        LibViewUtil.setViewVisibility(layoutEmpty, View.VISIBLE);
    }

    private void hideEmptyView(){
        LibViewUtil.setViewVisibility(btnLoadFinish, View.VISIBLE);
        LibViewUtil.setViewVisibility(ptrlv, View.VISIBLE);
        LibViewUtil.setViewVisibility(layoutEmpty, View.GONE);
    }

    private void showEmptyViewNoTitle(){
        LibViewUtil.setViewVisibility(layoutTitle, View.GONE);
        showEmptyView();
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        if (requestCode == 0) {
            carriageDetail = (ResultCarriageDetail) result;
            LibViewUtil.setViewVisibility(layoutTitle, View.VISIBLE);
            txtTransportCode.setText(getString(R.string.driver_transport_code, carriageCode));
            txtBoxNum.setText(getString(R.string.driver_box_num, carriageDetail.getBoxList().size()));
            txtRealBoxNum.setText(getString(R.string.driver_real_box_num, 0));
            if(carriageDetail.getBoxList().size()==0){
                showEmptyView();
            }else{
                hideEmptyView();
            }
//            changeList();
            setAdapter(carriageDetail.getBoxList());
        } else if (requestCode == 1) {
//            toast(result.getResponseMsg());
            finish();
            toast(R.string.driver_btn_load_finish);
        }
    }

    @Override
    public void onSuccessException(int requestCode, Throwable t) {
        super.onSuccessException(requestCode, t);
        if(requestCode==0){
            showEmptyViewNoTitle();
        }
    }

    @Override
    public void onErrorResponse(int requestCode, ResultBase result) {
        super.onErrorResponse(requestCode, result);
        if(requestCode==0){
            showEmptyViewNoTitle();
        }
    }

    @Override
    public void onFailureResponse(int requestCode, Throwable t) {
        super.onFailureResponse(requestCode, t);
        if(requestCode==0){
            showEmptyViewNoTitle();
        }
    }


    class CarriageDetailAdapter extends BaseListAdapter<DriverBox> {

        public CarriageDetailAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        public void setList(List<DriverBox> list) {
            super.setList(list);
            txtRealBoxNum.setText(getString(R.string.driver_real_box_num, list == null ? 0 : getCheckList(list).size()));
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
            ImageView imgScaned = getViewById(R.id.img_scaned);
            txtBoxCode.setText(getString(R.string.driver_box_code, item.getBoxCode()));
            txtCenterNum.setText(getString(R.string.driver_center_num, item.getCenterNum()));
            LibViewUtil.setViewVisibility(imgScaned, item.isCheck ? View.VISIBLE : View.GONE);
        }
    }

}

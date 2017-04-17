package cn.com.bluemoon.delivery.module.wash.returning.driver;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.ReturningApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.wash.driver.ClothCenter;
import cn.com.bluemoon.delivery.app.api.model.wash.driver.ResultSendList;
import cn.com.bluemoon.delivery.app.api.model.wash.driver.TagBox;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.BasePullToRefreshListViewFragment;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.module.wash.returning.cupboard.CupboardScanActivity;
import cn.com.bluemoon.delivery.ui.NoScrollListView;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;


public class WaitSendFragment extends BasePullToRefreshListViewFragment {

    private long pageFlag = 0;

    @Override
    protected String getTitleString() {
        return getString(R.string.driver_tab_wait_send);
    }

    @Override
    protected void initPullToRefreshListView(PullToRefreshListView ptrlv) {
        ptrlv.getRefreshableView().setDivider(null);
        ptrlv.getRefreshableView().setDividerHeight(0);
    }

    @Override
    protected BaseListAdapter getNewAdapter() {
        return new WaitSendAdapter(getActivity(), this);
    }

    @Override
    protected List getGetMoreList(ResultBase result) {
        ResultSendList sendList = (ResultSendList) result;
        pageFlag = sendList.getPageFlag();
        return sendList.getCenterList();
    }

    @Override
    protected List getGetDataList(ResultBase result) {
        ResultSendList sendList = (ResultSendList) result;
        pageFlag = sendList.getPageFlag();
        return sendList.getCenterList();
    }

    @Override
    protected PullToRefreshBase.Mode getMode() {
        return PullToRefreshBase.Mode.BOTH;
    }

    @Override
    protected void invokeGetDataDeliveryApi(int requestCode) {
        ReturningApi.queryWaitSendList(0, getToken(), getNewHandler(requestCode, ResultSendList.class));
        setAmount();
    }

    @Override
    protected void invokeGetMoreDeliveryApi(int requestCode) {
        ReturningApi.queryWaitSendList(pageFlag, getToken(), getNewHandler(requestCode, ResultSendList.class));
    }

    private ArrayList<String> getTagBoxList(List<TagBox> list) {
        ArrayList<String> tags = new ArrayList<>();
        for (TagBox tag : list) {
            tags.add(tag.getTagCode());
        }
        return tags;
    }

    @Override
    public void onItemClick(Object item, View view, int position) {
        ClothCenter clothCenter = (ClothCenter) item;
        CupboardScanActivity.actStart(this, clothCenter.getCarriageAddressId(), getTagBoxList(clothCenter.getTagList()));
    }


    class WaitSendAdapter extends BaseListAdapter<ClothCenter> {

        public WaitSendAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.item_driver_send;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            ClothCenter item = list.get(position);
            TextView txtName = getViewById(R.id.txt_name);
            TextView txtPhone = getViewById(R.id.txt_phone);
            TextView txtBoxNum = getViewById(R.id.txt_box_num);
            TextView txtAddress = getViewById(R.id.txt_address);
            Button btnReceiver = getViewById(R.id.btn_receiver);
            NoScrollListView listViewBox = getViewById(R.id.listView_box);
            txtName.setText(item.getCenterName());
            txtPhone.setText(item.getReceiverPhone());
            txtBoxNum.setText(getString(R.string.driver_box_num_all, item.getTagList().size()));
            txtAddress.setText(StringUtil.getStringParamsByFormat("",
                    item.getProvince(), item.getCity(), item.getCounty(), item.getStreet(),
                    item.getVillage(), item.getAddress()));
            if (isNew) {
                TagBoxAdapter tagBoxAdapter = new TagBoxAdapter(context, null);
                listViewBox.setAdapter(tagBoxAdapter);
            }
            TagBoxAdapter tagBoxAdapter = (TagBoxAdapter) listViewBox.getAdapter();
            tagBoxAdapter.setList(item.getTagList());
            tagBoxAdapter.notifyDataSetChanged();
            setClickEvent(isNew, position, btnReceiver);
        }
    }

    class TagBoxAdapter extends BaseListAdapter<TagBox> {

        public TagBoxAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.item_driver_send_box;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            TagBox item = list.get(position);
            TextView txtTagCode = getViewById(R.id.txt_tag_code);
            TextView txtBoxCode = getViewById(R.id.txt_box_code);
            TextView txtClothNum = getViewById(R.id.txt_cloth_num);
            txtTagCode.setText(getString(R.string.driver_tag_code, item.getTagCode()));
            txtBoxCode.setText(getString(R.string.driver_box_code2, item.getBoxCode()));
            txtClothNum.setText(getString(R.string.driver_cloth_num, item.getBackOrderNum()));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0 && resultCode == Activity.RESULT_OK){
            initData();
        }
    }
}

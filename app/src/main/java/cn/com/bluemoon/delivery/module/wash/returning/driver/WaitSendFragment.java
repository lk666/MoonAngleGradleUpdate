package cn.com.bluemoon.delivery.module.wash.returning.driver;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
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
import cn.com.bluemoon.delivery.module.wash.returning.incabinet.CabinetScanActivity;
import cn.com.bluemoon.delivery.ui.LinearLayoutForListView;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.delivery.utils.ViewUtil;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;


public class WaitSendFragment extends BasePullToRefreshListViewFragment {

    private long timesamp;

    @Override
    protected String getTitleString() {
        return getString(R.string.driver_send_title);
    }

    @Override
    protected void initPullToRefreshListView(PullToRefreshListView ptrlv) {
        ptrlv.getRefreshableView().setDivider(null);
        ptrlv.getRefreshableView().setDividerHeight(getResources().getDimensionPixelSize(R.dimen
                .div_height_10));
    }

    @Override
    protected BaseListAdapter getNewAdapter() {
        return new WaitSendAdapter(getActivity(), this);
    }

    @Override
    protected List getGetMoreList(ResultBase result) {
        ResultSendList sendList = (ResultSendList) result;
        timesamp = sendList.getPageFlag();
        return sendList.getCenterList();
    }

    @Override
    protected List getGetDataList(ResultBase result) {
        ResultSendList sendList = (ResultSendList) result;
        timesamp = sendList.getPageFlag();
        return sendList.getCenterList();
    }

    @Override
    protected PullToRefreshBase.Mode getMode() {
        return PullToRefreshBase.Mode.BOTH;
    }

    @Override
    protected void invokeGetDataDeliveryApi(int requestCode) {
        ReturningApi.queryWaitSendList(0, getToken(), getNewHandler(requestCode, ResultSendList.class));
        ((DriverTabActivity)getActivity()).getAmount();
    }

    @Override
    protected void invokeGetMoreDeliveryApi(int requestCode) {
        ReturningApi.queryWaitSendList(timesamp, getToken(), getNewHandler(requestCode, ResultSendList.class));
        ((DriverTabActivity)getActivity()).getAmount();
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
        CabinetScanActivity.actStart(this, clothCenter.getCarriageAddressId(), getTagBoxList(clothCenter.getTagList()));
    }


    class WaitSendAdapter extends BaseListAdapter<ClothCenter> {
        TagBoxAdapter tagBoxAdapter;

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
            TextView txtCenterAddress = getViewById(R.id.txt_center_address);
            TextView txtBoxNum = getViewById(R.id.txt_box_num);
            TextView txtAddress = getViewById(R.id.txt_address);
            Button btnReceiver = getViewById(R.id.btn_receiver);
            ListView listViewBox = getViewById(R.id.listView_box);
            txtCenterAddress.setText(item.getCenterName());
            txtBoxNum.setText(getString(R.string.driver_box_num_all, item.getTagList().size()));
            txtAddress.setText(StringUtil.getStringParamsByFormat("",
                    item.getProvince(),item.getCity(),item.getCounty(),
                    item.getVillage(),item.getStreet(),item.getAddress()));
            if(isNew){
                tagBoxAdapter = new TagBoxAdapter(context, null);
            }
            tagBoxAdapter.setList(item.getTagList());
            listViewBox.setAdapter(tagBoxAdapter);
            ViewUtil.setListViewHeight2(listViewBox);
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
}

package cn.com.bluemoon.delivery.module.wash.returning.transportreceive;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.ReturningApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.wash.transportreceive.Carriage;
import cn.com.bluemoon.delivery.app.api.model.wash.transportreceive.CarriageTag;
import cn.com.bluemoon.delivery.app.api.model.wash.transportreceive.ResultWaitSignList;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.BasePullToRefreshListViewFragment;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.ui.NoScrollListView;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;

/**
 * 待签收界面
 * Created by lk on 2016/9/14.
 */
public class WaitSignFragment extends BasePullToRefreshListViewFragment {
    private static final int REQUEST_CODE_SCANE = 0x444;
    /**
     * 分页标识
     */
    private long pageFlag = 0;

    @Override
    protected String getTitleString() {
        return getString(R.string.wait_sign_title);
    }

    @Override
    protected void initPullToRefreshListView(PullToRefreshListView ptrlv) {
        ptrlv.getRefreshableView().setDivider(null);
        ptrlv.getRefreshableView().setDividerHeight(0);
    }

    @Override
    protected PullToRefreshBase.Mode getMode() {
        return PullToRefreshBase.Mode.BOTH;
    }

    @Override
    protected void invokeGetDataDeliveryApi(int requestCode) {
        isFirstTimeLoad = true;
        pageFlag = 0;
        ReturningApi.queryWaitSignList(0, getToken(), getNewHandler
                (requestCode, ResultWaitSignList.class));
        setAmount();
    }

    @Override
    protected List<Carriage> getGetDataList(ResultBase result) {
        ResultWaitSignList resultObj = (ResultWaitSignList) result;
        pageFlag = resultObj.getPageFlag();
        return resultObj.getCarriageList();
    }

    @Override
    protected void invokeGetMoreDeliveryApi(int requestCode) {
        ReturningApi.queryWaitSignList(pageFlag, getToken(), getNewHandler
                (requestCode, ResultWaitSignList.class));
    }

    /**
     * Mode不包含上拉加载时，可这样重写此方法
     *
     * @param result 继承ResultBase的json字符串数据，不为null，也非空数据
     */
    @Override
    protected List<Carriage> getGetMoreList(ResultBase result) {
        ResultWaitSignList resultObj = (ResultWaitSignList) result;
        pageFlag = resultObj.getPageFlag();
        return resultObj.getCarriageList();
    }

    /**
     * 当再次回到此界面，且在此此回到此界面，setUserVisibleHint之前没有调用initData时，刷新数据
     */
    private boolean isFirstTimeLoad = true;

    @Override
    public void onResume() {
        super.onResume();
        if (!isFirstTimeLoad) {
            initData();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isFirstTimeLoad = false;
    }

    @Override
    protected ItemAdapter getNewAdapter() {
        return new ItemAdapter(getActivity(), this);
    }

    class ItemAdapter extends BaseListAdapter<Carriage> {
        public ItemAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.item_wait_sign;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            Carriage item = (Carriage) getItem(position);
            if (item == null) {
                return;
            }

            TextView tvCarriageCode = getViewById(R.id.tv_carriage_code);
            Button btnSign = getViewById(R.id.btn_sign);
            TextView tvCarriagePeopleName = getViewById(R.id.tv_carriage_people_name);
            TextView tvCarriagePhone = getViewById(R.id.tv_carriage_phone);
            NoScrollListView lv = getViewById(R.id.lv);
            TextView tvSealTags = getViewById(R.id.tv_seal_tags);
            TextView tvSign = getViewById(R.id.tv_sign);

            tvCarriageCode.setText(String.format(getString(R.string
                    .wait_sign_carriage_code), item.getCarriageCode()));

            tvCarriagePeopleName.setText(item.getCarriagePeopleName());
            tvCarriagePhone.setText(item.getCarriagePhone());
            tvSealTags.setText(String.format(getString(R.string
                    .wait_sign_seal_tags), item.getSealTags()));

            if (item.isSign()) {
                btnSign.setVisibility(View.VISIBLE);
                tvSign.setVisibility(View.VISIBLE);
            } else {
                btnSign.setVisibility(View.GONE);
                tvSign.setVisibility(View.GONE);
            }

            if (isNew) {
                TagAdapter newAdapter = new TagAdapter(context, WaitSignFragment.this);
                lv.setAdapter(newAdapter);
            }

            TagAdapter adapter = (TagAdapter) lv.getAdapter();
            adapter.setList(item.getTagList());
            adapter.notifyDataSetChanged();

            setClickEvent(isNew, position, btnSign);
        }
    }


    class TagAdapter extends BaseListAdapter<CarriageTag> {
        public TagAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.item_wait_sign_tag;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            final CarriageTag item = (CarriageTag) getItem(position);
            if (item == null) {
                return;
            }

            ImageView ivSigned = getViewById(R.id.iv_signed);
            TextView tvSigned = getViewById(R.id.tv_signed);
            TextView tvTagCode = getViewById(R.id.tv_tag_code);
            TextView tvBackOrderNum = getViewById(R.id.tv_back_order_num);

            tvTagCode.setText(String.format(getString(R.string.wait_sign_tag_code),
                    item.getTagCode()));
            tvBackOrderNum.setText(String.format(getString(R.string.wait_sign_back_orderNum),
                    item.getBackOrderNum()));

            if (item.isSign()) {
                ivSigned.setVisibility(View.VISIBLE);
                tvSigned.setVisibility(View.VISIBLE);
            } else {
                ivSigned.setVisibility(View.GONE);
                tvSigned.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onItemClick(Object item, View view, int position) {
        // 点击签收
        if (item instanceof Carriage) {
            Carriage c = (Carriage) item;
            ScanReceiveSignActivity.actionStart(getActivity(), this, REQUEST_CODE_SCANE,
                    c.getCarriageCode(), ((Carriage) item).getTagList());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //  扫码返回
        if (requestCode == REQUEST_CODE_SCANE) {
            initData();
        }
    }
}
package cn.com.bluemoon.delivery.module.wash.returning.transportreceive;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.ReturningApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.wash.closebox.ReceiveCarriage;
import cn.com.bluemoon.delivery.app.api.model.wash.closebox.ReceiveCarriageTag;
import cn.com.bluemoon.delivery.app.api.model.wash.closebox.ResultReceiveHistoryList;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.BasePullToRefreshListViewFragment;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.module.wash.returning.closebox.SingleTimerFilterWindow;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.ui.NoScrollListView;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;

/**
 * 签收历史
 * Created by lk on 2016/9/14.
 */
public class SignHistoryFragment extends BasePullToRefreshListViewFragment {
    private View viewPopStart;
    private TextView tvDate;
    private TextView tvTotal;

    /**
     * 分页标识
     */
    private long pageFlag = 0;

    /**
     * 筛选时间
     */
    private long opTime = 0;

    @Override
    protected String getTitleString() {
        return getString(R.string.sign_history_title);
    }

    @Override
    protected void setActionBar(CommonActionBar actionBar) {
        super.setActionBar(actionBar);

        actionBar.getTvRightView().setText(R.string.btn_txt_fillter);
        actionBar.getTvRightView().setCompoundDrawablePadding(10);

        Drawable drawableFillter = getResources().getDrawable(R.mipmap.icon_filter);
        assert drawableFillter != null;
        drawableFillter.setBounds(0, 0, drawableFillter.getMinimumWidth(), drawableFillter
                .getMinimumHeight());
        actionBar.getTvRightView().setCompoundDrawables(drawableFillter, null, null, null);
        actionBar.getTvRightView().setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActionBarBtnRightClick() {
        SingleTimerFilterWindow popupWindow = new SingleTimerFilterWindow(getActivity(), opTime,
                new SingleTimerFilterWindow.FilterListener() {
                    @Override
                    public void onOkClick(long time) {
                        opTime = time;
                        if (time > 0) {
                            setHeadViewVisibility(View.VISIBLE);
                            tvDate.setText(DateUtil.getTime(time, "yyyy/MM/dd"));
                        } else {
                            setHeadViewVisibility(View.GONE);
                        }
                        initData();
                    }
                });
        popupWindow.showPopwindow(viewPopStart);
    }

    @Override
    protected int getHeadLayoutId() {
        return R.layout.head_fragment_tab_sign_history;
    }

    @Override
    protected void initHeadViewEvent(View headView) {
        super.initHeadViewEvent(headView);
        viewPopStart = headView.findViewById(R.id.view_pop_start);
        tvDate = (TextView) headView.findViewById(R.id.tv_date);
        tvTotal = (TextView) headView.findViewById(R.id.tv_total);
        setEmptyViewMsg(String.format(getString(R.string.current_no_some_data), getTitleString()));
    }

    protected void initPullToRefreshListView(PullToRefreshListView ptrlv) {
        ptrlv.getRefreshableView().setDivider(null);
        ptrlv.getRefreshableView().setDividerHeight(0);
        opTime = 0;
    }

    @Override
    protected PullToRefreshBase.Mode getMode() {
        return PullToRefreshBase.Mode.BOTH;
    }

    @Override
    protected void invokeGetDataDeliveryApi(int requestCode) {
        pageFlag = 0;
        ReturningApi.queryReceiveHistoryList(pageFlag, opTime, getToken(), getNewHandler
                (requestCode, ResultReceiveHistoryList.class));
    }

    @Override
    protected List<ReceiveCarriage> getGetDataList(ResultBase result) {
        ResultReceiveHistoryList resultObj = (ResultReceiveHistoryList) result;
        pageFlag = resultObj.getPageFlag();
        tvTotal.setText(getString(R.string.driver_order_num, resultObj.getCarriageSum()));
        return resultObj.getCarriageList();
    }

    @Override
    protected void invokeGetMoreDeliveryApi(int requestCode) {
        ReturningApi.queryReceiveHistoryList(pageFlag, opTime, getToken(), getNewHandler
                (requestCode, ResultReceiveHistoryList.class));
    }

    @Override
    protected List<ReceiveCarriage> getGetMoreList(ResultBase result) {
        ResultReceiveHistoryList resultObj = (ResultReceiveHistoryList) result;
        pageFlag = resultObj.getPageFlag();
        tvTotal.setText(getString(R.string.driver_order_num, resultObj.getCarriageSum()));
        return resultObj.getCarriageList();
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
        if (opTime == 0) {
            setHeadViewVisibility(View.GONE);
        } else {
            setHeadViewVisibility(View.VISIBLE);
        }
    }

    @Override
    protected ItemAdapter getNewAdapter() {
        return new ItemAdapter(getActivity(), this);
    }

    class ItemAdapter extends BaseListAdapter<ReceiveCarriage> {
        public ItemAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.item_sign_history;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            ReceiveCarriage item = (ReceiveCarriage) getItem(position);
            if (item == null) {
                return;
            }

            TextView tvCarriageCode = getViewById(R.id.tv_carriage_code);
            TextView tvTotal = getViewById(R.id.tv_total);
            NoScrollListView lv = getViewById(R.id.lv);

            tvCarriageCode.setText(String.format(getString(R.string.wait_sign_carriage_code),
                    item.getCarriageCode()));

            tvTotal.setText(String.format(getString(R.string.sign_history_total),
                    item.getTagList().size()));

            if (isNew) {
                TagAdapter newAdapter = new TagAdapter(context, SignHistoryFragment.this);
                lv.setAdapter(newAdapter);
            }

            TagAdapter adapter = (TagAdapter) lv.getAdapter();
            adapter.setList(item.getTagList());
            adapter.notifyDataSetChanged();
        }
    }


    class TagAdapter extends BaseListAdapter<ReceiveCarriageTag> {
        public TagAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.item_sign_history_tag;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            final ReceiveCarriageTag item = (ReceiveCarriageTag) getItem(position);
            if (item == null) {
                return;
            }

            TextView tvBackOrderNum = getViewById(R.id.tv_back_order_num);
            TextView tvTagCode = getViewById(R.id.tv_tag_code);
            TextView tvReceiverSignTime = getViewById(R.id.tv_receiver_sign_time);
            TextView tvReceiver = getViewById(R.id.tv_receiver);


            tvBackOrderNum.setText(String.valueOf(item.getBackOrderNum()));
            tvTagCode.setText(String.format(getString(R.string.wait_sign_tag_code),
                    item.getTagCode()));

            tvReceiver.setText(String.format(getString(R.string.sign_history_receiver),
                    item.getReceiver()));
            tvReceiverSignTime.setText(DateUtil.getTime(item.getReceiverSignTime(),
                    "yyyy-MM-dd " + "HH:mm:ss"));
        }
    }

    @Override
    public void onItemClick(Object item, View view, int position) {

    }

}

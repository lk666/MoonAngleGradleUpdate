package cn.com.bluemoon.delivery.module.wash.returning.pack;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.ReturningApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.wash.pack.BackOrder;
import cn.com.bluemoon.delivery.app.api.model.wash.pack.ResultInboxHistory;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.BasePullToRefreshListViewFragment;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.module.wash.returning.closebox.SingleTimerFilterWindow;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;

/**
 * Created by allenli on 2016/10/8.
 */
public class PackHistoryFragment extends BasePullToRefreshListViewFragment {
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
        return getString(R.string.title_history);
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
        SingleTimerFilterWindow popupWindow = new SingleTimerFilterWindow(getActivity(), opTime, new
                SingleTimerFilterWindow.FilterListener() {
                    @Override
                    public void onOkClick(long time) {
                        opTime = time;
                        setHead(View.VISIBLE);
                        initData();
                    }
                });
        popupWindow.showPopwindow(viewPopStart);
    }

    private void setHead(int visibility) {
        if (visibility == View.VISIBLE && opTime > 0) {
            setHeadViewVisibility(View.VISIBLE);
            tvDate.setText(DateUtil.getTime(opTime, "yyyy/MM/dd"));
        } else {
            setHeadViewVisibility(View.GONE);
            tvTotal.setText(getString(R.string.pack_order_num, 0));
        }
    }

    @Override
    protected int getHeadLayoutId() {
        return R.layout.head_fragment_tab_pack_history;
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
        ReturningApi.queryInboxHistoryList(opTime, pageFlag, getToken(), getNewHandler
                (requestCode, ResultInboxHistory.class));
    }

    @Override
    protected List<BackOrder> getGetDataList(ResultBase result) {
        ResultInboxHistory resultObj = (ResultInboxHistory) result;
        pageFlag = resultObj.getPageFlag();
        tvTotal.setText(getString(R.string.pack_order_num, resultObj.getBackOrderSum()));
        return resultObj.getBackOrderList();
    }

    @Override
    protected void invokeGetMoreDeliveryApi(int requestCode) {
        ReturningApi.queryInboxHistoryList(opTime, pageFlag, getToken(), getNewHandler
                (requestCode, ResultInboxHistory.class));
    }

    @Override
    protected List<BackOrder> getGetMoreList(ResultBase result) {
        ResultInboxHistory resultObj = (ResultInboxHistory) result;
        pageFlag = resultObj.getPageFlag();
        tvTotal.setText(getString(R.string.pack_order_num, resultObj.getBackOrderSum()));
        return resultObj.getBackOrderList();
    }

    @Override
    protected void showEmptyView() {
        super.showEmptyView();
        // 可在此处设置head等
        setHead(View.VISIBLE);
    }

    @Override
    protected void showNetErrorView() {
        super.showNetErrorView();
        // 可在此处设置head等
        setHead(View.GONE);
    }

    @Override
    protected void showRefreshView() {
        super.showRefreshView();
        // 列表数据刷新，如可在此处设置head等
        setHead(View.VISIBLE);
    }

    protected BackOrderAdapter getNewAdapter() {
        return new BackOrderAdapter(getActivity(), this);
    }

    class BackOrderAdapter extends BaseListAdapter<BackOrder> {

        public BackOrderAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.item_pack_history;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            BackOrder item = (BackOrder) getItem(position);

            TextView tvBoxTagCode = getViewById(R.id.tv_box_tag_code);
            TextView tvBackOrderNum = getViewById(R.id.tv_back_order_num);
            TextView tvClothesNum = getViewById(R.id.tv_clothes_num);

            tvBoxTagCode.setText(String.format(getString(R.string.pack_back_order_code), item
                    .getBackOrderCode()));
            tvBackOrderNum.setText(item.getBoxCode());
            tvClothesNum.setText(String.valueOf(item.getClothesNum()));

            setClickEvent(isNew, position, convertView);
        }
    }

    @Override
    public void onItemClick(Object obj, View view, int position) {
        BackOrder item = (BackOrder) obj;
        if (null != item) {
            PackDetailActivity.actionStart(getContext(), item.getBackOrderCode());
        }
    }
}

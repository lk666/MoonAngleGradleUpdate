package cn.com.bluemoon.delivery.module.wash.returning.clothescheck;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.ReturningApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.wash.closebox.CheckLog;
import cn.com.bluemoon.delivery.app.api.model.wash.closebox.ResultCheckHistoryList;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.BasePullToRefreshListViewFragment;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.module.order.TimerFilterWindow;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib.utils.LibDateUtil;

/**
 * 衣物清点历史
 */
public class ClothesCheckHistoryFragment extends BasePullToRefreshListViewFragment {
    private View viewPopStart;
    private TextView tvDate;

    /**
     * 分页标识
     */
    private long pageFlag = 0;

    private long startTime = 0;
    private long endTime = 0;

    @Override
    protected String getTitleString() {
        return getString(R.string.clothes_check_history_title);
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
        TimerFilterWindow popupWindow = new TimerFilterWindow(getActivity(), new
                TimerFilterWindow.TimerFilterListener() {
                    @Override
                    public void callBack(long startDate, long endDate) {
                        if (startDate >= 0 && endDate >= startDate) {
                            startTime = LibDateUtil.getTimeByCustTime(startDate);
                            endTime = LibDateUtil.getTimeByCustTime(endDate);

                            if (DateUtil.getTimeOffsetMonth(startTime, 6) >
                                    endTime) {
                                if (startTime == 0 && endTime == 0) {
                                    return;
                                }

                                setHead(View.VISIBLE);
                                initData();
                            } else {
                                PublicUtil.showMessage(getActivity(), getString(R.string
                                        .txt_order_fillter_date_error));
                            }
                        }
                    }
                });

        popupWindow.showPopwindow(viewPopStart);
    }

    private void setHead(int visibility) {
        if (visibility == View.VISIBLE && startTime < endTime) {
            setHeadViewVisibility(View.VISIBLE);
            tvDate.setText(String.format("%s至%s", DateUtil.getTime(startTime, "yyyy/MM/dd"),
                    DateUtil.getTime(endTime, "yyyy/MM/dd")));
        } else {
            setHeadViewVisibility(View.GONE);
        }
    }

    @Override
    protected int getHeadLayoutId() {
        return R.layout.head_fragment_tab_clothes_check_history;
    }

    @Override
    protected void initHeadViewEvent(View headView) {
        super.initHeadViewEvent(headView);
        viewPopStart = headView.findViewById(R.id.view_pop_start);
        tvDate = (TextView) headView.findViewById(R.id.tv_date);
        setEmptyViewMsg(String.format(getString(R.string.current_no_some_data), getTitleString()));
    }

    protected void initPullToRefreshListView(PullToRefreshListView ptrlv) {
        ptrlv.getRefreshableView().setDivider(null);
        ptrlv.getRefreshableView().setDividerHeight(0);
        startTime = 0;
        endTime = 0;
    }

    @Override
    protected PullToRefreshBase.Mode getMode() {
        return PullToRefreshBase.Mode.BOTH;
    }

    @Override
    protected void invokeGetDataDeliveryApi(int requestCode) {
        pageFlag = 0;
        ReturningApi.checkHistoryList(startTime, endTime, 0, getToken(), getNewHandler
                (requestCode, ResultCheckHistoryList.class));
    }

    @Override
    protected List<CheckLog> getGetDataList(ResultBase result) {
        ResultCheckHistoryList resultObj = (ResultCheckHistoryList) result;
        pageFlag = resultObj.getPageFlag();
        return resultObj.getCheckLogList();
    }

    @Override
    protected void invokeGetMoreDeliveryApi(int requestCode) {
        ReturningApi.checkHistoryList(startTime, endTime, pageFlag, getToken(), getNewHandler
                (requestCode, ResultCheckHistoryList.class));
    }

    @Override
    protected List<CheckLog> getGetMoreList(ResultBase result) {
        return getGetDataList(result);
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

    protected ItemAdapter getNewAdapter() {
        return new ItemAdapter(getContext(), this);
    }

    class ItemAdapter extends BaseListAdapter<CheckLog> {

        public ItemAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.item_clothes_history;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            CheckLog item = (CheckLog) getItem(position);

            TextView tvType = getViewById(R.id.tv_type);
            TextView tvBoxTagCode = getViewById(R.id.tv_box_tag_code);
            TextView tvAbnormal = getViewById(R.id.tv_abnormal);
            TextView tvNum = getViewById(R.id.tv_num);
            TextView tvDate = getViewById(R.id.tv_date);

            // 还衣单清点
            if (item.getSourceType().equals(CheckLog.INVENTORY_SEALED_BOX)) {
                tvType.setText(getString(R.string.clothes_check_history_type_back_order));
                tvBoxTagCode.setText(String.format(getString(R.string.driver_tag_code), item.getSourceCode()));
                tvNum.setText(String.format(getString(R.string.clothes_check_history_back_order_num), item.getSourceDetailNum()));
            }
            // 衣物清点
            else {
                tvType.setText(getString(R.string.clothes_check_title));
                tvBoxTagCode.setText(String.format(getString(R.string.back_order_code), item.getSourceCode()));
                tvNum.setText(String.format(getString(R.string.clothes_check_history_clothes_num), item.getSourceDetailNum()));
            }

            if (item.isAbnormal()) {
                tvAbnormal.setVisibility(View.VISIBLE);
            } else {
                tvAbnormal.setVisibility(View.GONE);
            }

            tvDate.setText(DateUtil.getTime(item.getOpTime(), "yyyy-MM-dd HH:mm:ss"));
            setClickEvent(isNew, position, convertView);
        }
    }

    @Override
    public void onItemClick(Object obj, View view, int position) {
        CheckLog item = (CheckLog) obj;
        if (null != item) {
            // todo 还衣单清点
            if (item.getSourceType().equals(CheckLog.INVENTORY_SEALED_BOX)) {

            }
            // 衣物清点
            else {
                ClothesCheckHistoryDetailActivity.actionStart(getActivity(), this, item.getCheckLogId(), item.getSourceCode());
            }
        }
    }
}

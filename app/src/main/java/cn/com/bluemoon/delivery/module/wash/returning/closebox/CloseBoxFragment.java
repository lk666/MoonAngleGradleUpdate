package cn.com.bluemoon.delivery.module.wash.returning.closebox;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.ReturningApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.wash.closebox.BoxItem;
import cn.com.bluemoon.delivery.app.api.model.wash.closebox.ResultWaitCloseBoxList;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.BasePullToRefreshListViewFragment;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;

/**
 * 待封箱界面
 * Created by lk on 2016/9/14.
 */
public class CloseBoxFragment extends BasePullToRefreshListViewFragment {
    private static final int REQUEST_CODE_SCANE_BOX_CODE = 0x777;
    private View viewPopStart;
    private TextView txtCount;
    private TextView txtPendingBox;
    /**
     * 是否显示待封箱
     */
    private boolean waitInbox = true;

    /**
     * 待装箱数
     */
    private int waitInboxCount;
    /**
     * 总箱数
     */
    private int totalCount;

    /**
     * 分页标识
     */
    private long pageFlag = 0;

    @Override
    protected String getTitleString() {
        return getString(R.string.close_box_title);
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
        WaitCloseBoxFilterWindow popupWindow = new WaitCloseBoxFilterWindow(getActivity(),
                waitInbox, new WaitCloseBoxFilterWindow.FilterListener() {

            @Override
            public void onOkClick(boolean flag) {
                waitInbox = flag;
                initData();
            }
        });
        popupWindow.showPopwindow(viewPopStart);
    }

    @Override
    protected int getHeadLayoutId() {
        return R.layout.head_fragment_tab_close_box;
    }

    @Override
    protected void initHeadViewEvent(View headView) {
        super.initHeadViewEvent(headView);

        viewPopStart = headView.findViewById(R.id.view_pop_start);
        txtCount = (TextView) headView.findViewById(R.id.txt_count);
        txtPendingBox = (TextView) headView.findViewById(R.id.txt_pending_box);
        waitInbox = true;
        waitInboxCount = 0;
        totalCount = 0;
        setHeadCOntent(0, true, 0);
        setEmptyViewMsg(String.format(getString(R.string.current_no_some_data), getTitleString()));
    }

    /**
     * 设置头部
     */
    private void setHeadCOntent(int count, boolean showPending, int pending) {
        txtCount.setText(String.format(getString(R.string.order_boxes_num), count));
        if (showPending) {
            txtPendingBox.setText(String.format(getString(R.string.close_box_pending_box),
                    pending));
        } else {
            txtPendingBox.setText("");
        }
    }

    @Override
    protected void initPullToRefreshListView(PullToRefreshListView ptrlv) {
        ptrlv.getRefreshableView().setDivider(null);
        ptrlv.getRefreshableView().setDividerHeight(0);
        // ptrlv.getRefreshableView().setHeaderDividersEnabled(false); 无效
        // ptrlv.getRefreshableView().setFooterDividersEnabled(false);
    }

    @Override
    protected PullToRefreshBase.Mode getMode() {
        return PullToRefreshBase.Mode.BOTH;
    }

    @Override
    protected void invokeGetDataDeliveryApi(int requestCode) {
        isFirstTimeLoad = true;
        pageFlag = 0;
        ReturningApi.queryWaitCloseBoxList(0, getToken(), waitInbox, getNewHandler
                (requestCode, ResultWaitCloseBoxList.class));
    }

    @Override
    protected List<BoxItem> getGetDataList(ResultBase result) {
        ResultWaitCloseBoxList resultObj = (ResultWaitCloseBoxList) result;
        waitInboxCount = resultObj.getWaitInboxCount();
        totalCount = resultObj.getInboxSum();
        pageFlag = resultObj.getPageFlag();
        return resultObj.getInboxList();
    }

    @Override
    protected void showEmptyView() {
        super.showEmptyView();
        // 可在此处设置head等
        setHeadViewVisibility(View.VISIBLE);
        getBaseTabActivity().setAmount(0, 0);
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
        setHeadCOntent(totalCount, waitInbox, waitInboxCount);
        getBaseTabActivity().setAmount(0, waitInboxCount);
    }

    @Override
    protected BoxItemAdapter getNewAdapter() {
        return new BoxItemAdapter(getActivity(), this);
    }

    class BoxItemAdapter extends BaseListAdapter<BoxItem> {

        public BoxItemAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.item_close_box_pending;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            BoxItem item = (BoxItem) getItem(position);

            TextView tvBoxCode = getViewById(R.id.tv_box_tag_code);
            Button btnCloseBox = getViewById(R.id.btn_close_box);
            TextView tvTotal = getViewById(R.id.tv_back_order_num);
            TextView tvFinish = getViewById(R.id.tv_clothes_num);

            tvBoxCode.setText(item.getBoxCode());
            tvTotal.setText(String.valueOf(item.getBackOrderNum()));
            tvFinish.setText(String.valueOf(item.getBackOrderIntoNum()));
            if (item.getBackOrderIntoNum() != item.getBackOrderNum()) {
                btnCloseBox.setVisibility(View.GONE);
            } else {
            btnCloseBox.setVisibility(View.VISIBLE);
            }

            setClickEvent(isNew, position, btnCloseBox);
        }
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
    public void onItemClick(Object obj, View view, int position) {
        BoxItem item = (BoxItem) obj;
        if (null != item) {
            ScanBoxCodeActivity.actStart(getActivity(), this, getString(R.string
                            .close_box_scan_box_code_title), getString(R.string
                            .with_order_collect_manual_input_code_btn), item.getBoxCode(),
                    ScanBoxCodeActivity.class, REQUEST_CODE_SCANE_BOX_CODE);
        }
    }

    @Override
    protected void invokeGetMoreDeliveryApi(int requestCode) {
        ReturningApi.queryWaitCloseBoxList(pageFlag, getToken(), waitInbox, getNewHandler
                (requestCode, ResultWaitCloseBoxList.class));
    }

    /**
     * Mode不包含上拉加载时，可这样重写此方法
     *
     * @param result 继承ResultBase的json字符串数据，不为null，也非空数据
     */
    @Override
    protected List<BoxItem> getGetMoreList(ResultBase result) {
        ResultWaitCloseBoxList resultObj = (ResultWaitCloseBoxList) result;
        waitInboxCount = resultObj.getWaitInboxCount();
        totalCount = resultObj.getInboxSum();
        pageFlag = resultObj.getPageFlag();
        return resultObj.getInboxList();
    }
}
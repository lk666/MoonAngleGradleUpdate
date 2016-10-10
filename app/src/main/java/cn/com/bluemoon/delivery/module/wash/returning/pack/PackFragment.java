package cn.com.bluemoon.delivery.module.wash.returning.pack;

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
import cn.com.bluemoon.delivery.app.api.model.wash.pack.CabinetItem;
import cn.com.bluemoon.delivery.app.api.model.wash.pack.ResultWaitPackage;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.BasePullToRefreshListViewFragment;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.module.wash.returning.closebox.WaitCloseBoxFilterWindow;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;


public class PackFragment extends BasePullToRefreshListViewFragment {
    private final String orderStatus ="WAIT_PACKED";
    private static final int REQUEST_CODE_SCANE_BOX_CODE = 0x777;
    private View viewPopStart;
    private TextView txtCount;
    private TextView txtPendingPack;

    /**
     * 是否显示待封箱
     */
    private boolean waitPack = true;

    /**
     * 待装箱数
     */
    private int waitPackCount;
    /**
     * 总箱数
     */
    private int totalCount;

    /**
     * 分页标识
     */
    private long pageFlag = 0;

    private String region="";

    @Override
    protected String getTitleString() {
        return getString(R.string.title_pack);
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
                waitPack, new WaitCloseBoxFilterWindow.FilterListener() {

            @Override
            public void onOkClick(boolean flag) {
                waitPack = flag;
                initData();
            }
        });
        popupWindow.showPopwindow(viewPopStart);
    }

    @Override
    protected int getHeadLayoutId() {
        return R.layout.head_fragment_tab_pack;
    }

    @Override
    protected void initHeadViewEvent(View headView) {
        super.initHeadViewEvent(headView);

        viewPopStart = headView.findViewById(R.id.view_pop_start);
        txtCount = (TextView) headView.findViewById(R.id.txt_count);
        txtPendingPack = (TextView) headView.findViewById(R.id.txt_pending_pack);
        waitPack = true;
        waitPackCount = 0;
        totalCount = 0;
        setHeadContent(0, true, 0);
        setEmptyViewMsg(String.format(getString(R.string.current_no_some_data), getTitleString()));
    }

    /**
     * 设置头部
     */
    private void setHeadContent(int count, boolean showPending, int pending) {
        txtCount.setText(String.format(getString(R.string.pack_order_num), count));
        if (showPending) {
            txtPendingPack.setText(String.format(getString(R.string.pack_pending_num),
                    pending));
        } else {
            txtPendingPack.setText("");
        }
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
        region="";
        ReturningApi.queryWaitPackageList(orderStatus, pageFlag,region ,getToken(), getNewHandler
                (requestCode, ResultWaitPackage.class));
        setAmount();
    }

    @Override
    protected List<CabinetItem> getGetDataList(ResultBase result) {
        ResultWaitPackage resultObj = (ResultWaitPackage) result;
        waitPackCount = resultObj.getWaitPackCount();
        totalCount = resultObj.getCabinetCount();
        pageFlag = resultObj.getPageFlag();
        return resultObj.getCabinetList();
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
        setHeadContent(totalCount, waitPack, waitPackCount);
    }

    @Override
    protected CabinetItemAdapter getNewAdapter() {
        return new CabinetItemAdapter(getActivity(), this);
    }

    class CabinetItemAdapter extends BaseListAdapter<CabinetItem> {

        public CabinetItemAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.item_pack_pending;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            CabinetItem item = (CabinetItem) getItem(position);

            TextView tvPackCode = getViewById(R.id.tv_pack_code);
            Button btnPackBox = getViewById(R.id.btn_pack);
            TextView tvTotal = getViewById(R.id.tv_pack_order_num);
            TextView tvFinish = getViewById(R.id.tv_clothes_num);

            tvPackCode.setText(item.getCupboardCode());
            tvTotal.setText(String.valueOf(item.getCapacity()));
            tvFinish.setText(String.valueOf(item.getActInNum()));
            if (item.getCapacity() != item.getActInNum()) {
                btnPackBox.setVisibility(View.GONE);
            } else {
                btnPackBox.setVisibility(View.VISIBLE);
            }

            setClickEvent(isNew, position, btnPackBox);
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
        CabinetItem item = (CabinetItem) obj;
        if (null != item) {
            ScanPackActivity.actStart(this,item.getCupboardCode(),item.getRegion());
        }
    }

    @Override
    protected void invokeGetMoreDeliveryApi(int requestCode) {
        ReturningApi.queryWaitPackageList(orderStatus, pageFlag,region ,getToken(), getNewHandler
                (requestCode, ResultWaitPackage.class));
    }

    /**
     * Mode不包含上拉加载时，可这样重写此方法
     *
     * @param result 继承ResultBase的json字符串数据，不为null，也非空数据
     */
    @Override
    protected List<CabinetItem> getGetMoreList(ResultBase result) {
        ResultWaitPackage resultObj = (ResultWaitPackage) result;
        waitPackCount = resultObj.getWaitPackCount();
        totalCount = resultObj.getCabinetCount();
        pageFlag = resultObj.getPageFlag();
        return resultObj.getCabinetList();
    }
}
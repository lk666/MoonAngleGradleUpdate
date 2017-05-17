package cn.com.bluemoon.delivery.module.wash.enterprise;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.EnterpriseApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.wash.enterprise.QueryInfo;
import cn.com.bluemoon.delivery.app.api.model.wash.enterprise.ResultEnterpriseList;
import cn.com.bluemoon.delivery.app.api.model.wash.enterprise.ResultEnterpriseList.EnterpriseOrderListBean;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.BasePullToRefreshListViewFragment;
import cn.com.bluemoon.delivery.module.wash.enterprise.event.ConfirmEvent;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib.utils.LibDateUtil;

/**
 * Created by ljl on 2017/5/4.
 */
public class EnterpriseHistoryFragment extends BasePullToRefreshListViewFragment {

    private long timestamp = 0;
    private int count = 0;
    private long startTime = 0;
    private long endTime = 0;
    private String branchCode = "";    //机构编码
    private String enterpriseCode = "";    //企业编码
    private String queryCode = "";
    private View viewPopStart;
    private TextView tvDate;
    private TextView tvTotal;

    @Override
    protected String getTitleString() {
        return getString(R.string.card_search_history);
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
        QueryFilterWindow popupWindow = new QueryFilterWindow(getActivity(), new
                QueryFilterWindow.TimerFilterListener() {
                    @Override
                    public void callBack(long startDate, long endDate, String code1, String code2, String code3) {
                        if (startDate >= 0 && endDate >= startDate) {
                            startTime = LibDateUtil.getTimeByCustTime(startDate);
                            endTime = LibDateUtil.getTimeByCustTime(endDate);
                            enterpriseCode = code1;
                            branchCode = code2;
                            queryCode = code3;
                            if (startTime == 0 && endTime == 0) {
                                return;
                            }
                            initData();
                        }
                    }
                });

        popupWindow.showPopwindow(viewPopStart);
    }

    @Override
    protected int getHeadLayoutId() {
        return R.layout.head_fragment_tab_close_box_history;
    }

    private void setHead(int visibility) {
        if (visibility == View.VISIBLE) {
            setHeadViewVisibility(View.VISIBLE);
            tvDate.setText(getString(R.string.start_to_end, DateUtil.getTime(startTime, "yyyy/MM/dd"),
                    DateUtil.getTime(endTime, "yyyy/MM/dd")));
            tvTotal.setText(getString(R.string.history_count, count));
        } else {
            setHeadViewVisibility(View.GONE);
        }
    }

    @Override
    protected void initHeadViewEvent(View headView) {
        super.initHeadViewEvent(headView);
        viewPopStart = headView.findViewById(R.id.view_pop_start);
        tvDate = (TextView) headView.findViewById(R.id.tv_date);
        tvTotal = (TextView) headView.findViewById(R.id.tv_total);
    }


    @Override
    protected BaseListAdapter getNewAdapter() {
        return new ItemAdapter(getActivity(), this);
    }

    @Override
    protected PullToRefreshBase.Mode getMode() {
        return PullToRefreshBase.Mode.BOTH;
    }

    @Override
    protected void initPullToRefreshListView(PullToRefreshListView ptrlv) {
        ptrlv.getRefreshableView().setDivider(null);
        ptrlv.getRefreshableView().setDividerHeight(0);
        ptrlv.getRefreshableView().setCacheColorHint(Color.TRANSPARENT);
        startTime = 0;
        endTime = 0;
    }

    @Override
    protected void invokeGetDataDeliveryApi(int requestCode) {
        timestamp = 0;
        invokeGetMoreDeliveryApi(requestCode);
    }

    @Override
    protected List<EnterpriseOrderListBean> getGetDataList(
            ResultBase result) {
        return getGetMoreList(result);
    }

    @Override
    protected List<EnterpriseOrderListBean> getGetMoreList(
            ResultBase result) {
        ResultEnterpriseList resultObj = (ResultEnterpriseList) result;
        timestamp = resultObj.timestamp;
        count = resultObj.count;
        return resultObj.enterpriseOrderList;
    }

    @Override
    protected void invokeGetMoreDeliveryApi(int requestCode) {
        EnterpriseApi.getWashEnterpriseRecordList(getToken(), timestamp,
                new QueryInfo(startTime, endTime, enterpriseCode, branchCode, queryCode),
                getNewHandler(requestCode, ResultEnterpriseList.class));
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        super.onSuccessResponse(requestCode, jsonString, result);
        if (startTime > 0) {
            setHead(View.VISIBLE);
        }
    }

    @Override
    public void onItemClick(Object item, View view, int position) {
        EnterpriseOrderListBean bean = (EnterpriseOrderListBean) item;
        switch (view.getId()) {
            case R.id.layout_detail:
                EnterpriseOrderDetailActivity.startAct(getActivity(), bean.outerCode, bean.state, true);
                break;
        }
    }
}

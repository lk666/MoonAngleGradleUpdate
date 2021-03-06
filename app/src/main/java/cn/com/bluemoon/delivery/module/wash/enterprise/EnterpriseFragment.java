package cn.com.bluemoon.delivery.module.wash.enterprise;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.view.View;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.EnterpriseApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.wash.enterprise.ResultEnterpriseList;
import cn.com.bluemoon.delivery.app.api.model.wash.enterprise.ResultEnterpriseList
        .EnterpriseOrderListBean;
import cn.com.bluemoon.delivery.module.base.BasePullToRefreshListViewFragment;
import cn.com.bluemoon.delivery.module.event.EnterpriseListChangeEvent;
import cn.com.bluemoon.delivery.module.wash.enterprise.createorder.AddClothesActivity;
import cn.com.bluemoon.delivery.module.wash.enterprise.event.ClothesChangedEvent;
import cn.com.bluemoon.delivery.module.wash.enterprise.event.ConfirmEvent;
import cn.com.bluemoon.delivery.module.wash.enterprise.event.CreateOrderEvent;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib.view.CommonAlertDialog;

/**
 * 企业收衣界面
 * Created by ljl on 2017/4/28.
 */
public class EnterpriseFragment extends BasePullToRefreshListViewFragment {

    private static final int REQUEST_CODE_CANCEL = 0x777;

    /**
     * 分页标识
     */
    private int currentPage = 1;

    @Override
    protected String getTitleString() {
        return getString(R.string.tab_enterprise_txt);
    }

    @Override
    protected void setActionBar(CommonActionBar actionBar) {
        super.setActionBar(actionBar);

        actionBar.getTvRightView().setText(R.string.add_collect_wash);
        actionBar.getTvRightView().setCompoundDrawablePadding(10);

        Drawable drawableAdd = getResources().getDrawable(R.mipmap.add_white);
        assert drawableAdd != null;
        drawableAdd.setBounds(0, 0, drawableAdd.getMinimumWidth(), drawableAdd
                .getMinimumHeight());
        actionBar.getTvRightView().setCompoundDrawables(drawableAdd, null, null, null);
        actionBar.getTvRightView().setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActionBarBtnRightClick() {
        EnterpriseScanInputActivity.actStart(getActivity(), getString(R.string.hand_query_with_space));
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
        currentPage = 1;
        invokeGetMoreDeliveryApi(requestCode);
    }

    @Override
    protected List<EnterpriseOrderListBean> getGetDataList(
            ResultBase result) {
        return getGetMoreList(result);
    }

    @Override
    protected void invokeGetMoreDeliveryApi(int requestCode) {
        EnterpriseApi.getWashEnterpriseList(currentPage, getToken(),
                getNewHandler(requestCode, ResultEnterpriseList.class));
    }

    @Override
    protected List<EnterpriseOrderListBean> getGetMoreList(
            ResultBase result) {
        currentPage++;
        ResultEnterpriseList resultObj = (ResultEnterpriseList) result;
        return resultObj.enterpriseOrderList;
    }

    @Override
    protected ItemAdapter getNewAdapter() {
        return new ItemAdapter(getActivity(), this);
    }


    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        super.onSuccessResponse(requestCode, jsonString, result);
        if (requestCode == REQUEST_CODE_CANCEL) {
            initData();
        }
    }

    @Override
    public void onItemClick(Object obj, View view, int position) {
        final EnterpriseOrderListBean bean = (EnterpriseOrderListBean) obj;
        switch (view.getId()) {
            case R.id.layout_detail:
                goToDetail(bean);
                break;
            case R.id.tv_cancel_order:
                new CommonAlertDialog.Builder(getActivity()).setMessage(R.string.enterprise_cancel).setNegativeButton(R.string.btn_ok_with_space, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showWaitDialog();
                        EnterpriseApi.cancelWashEnterpriseOrder(bean.outerCode, getToken(),
                                getNewHandler(REQUEST_CODE_CANCEL, ResultBase.class));
                    }
                }).setPositiveButton(R.string.cancel_with_space, null).show();

                break;
        }
    }

    private void goToDetail(EnterpriseOrderListBean bean) {
        if (Constants.OUTER_ACCEPT_CLOTHES.equals(bean.state)) {
            AddClothesActivity.actionStart(getActivity(), bean.outerCode);
        } else {
            EnterpriseOrderDetailActivity.startAct(getActivity(), bean.outerCode, bean
                    .state, false);
        }
    }

    @Override
    protected boolean isUseEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EnterpriseListChangeEvent event) {
        initData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ConfirmEvent event) {
        initData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(CreateOrderEvent event) {
        initData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ClothesChangedEvent event) {
        initData();
    }
}
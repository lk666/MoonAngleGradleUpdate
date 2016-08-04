package cn.com.bluemoon.delivery.module.base.example;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.clothing.ResultActivityInfo;
import cn.com.bluemoon.delivery.app.api.model.clothing.collect.ActivityInfo;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.BasePullToRefreshListViewFragment;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.module.clothing.collect.withoutorder.ActivityDescActivity;
import cn.com.bluemoon.delivery.module.clothing.collect.withoutorder.ActivityDetailActivity;
import cn.com.bluemoon.delivery.utils.ViewHolder;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;

/**
 * 测试下拉刷新列表fragment
 * Created by lk on 2016/7/29.
 */
public class _WithoutOrderManageFragment extends
        BasePullToRefreshListViewFragment<_WithoutOrderManageFragment.ActivityAdapter, ActivityInfo>
        implements OnListItemClickListener {

    private static final int REQUEST_CODE_ACTIVITY_DESC = 0x55;

    @Override
    protected String getTitleString() {
        return getString(R.string.tab_title_without_order_collect_manage);
    }

    @Override
    protected void onBeforeCreateView() {
//        Bundle bundle = getArguments();
//        manager = bundle.getString("manager");
    }

    @Override
    protected ActivityAdapter getNewAdapter() {
        return new ActivityAdapter(getActivity(), this);
    }

    /**
     * Mode不包含上拉加载时，可这样重写此方法
     */
    @Override
    protected List<ActivityInfo> getGetMoreList(ResultBase result) {
        return null;
    }

    @Override
    protected List<ActivityInfo> getGetDataList(ResultBase result) {
        ResultActivityInfo resultObj = (ResultActivityInfo) result;
        return resultObj.getActivityInfos();
    }

    @Override
    protected PullToRefreshBase.Mode getMode() {
        return PullToRefreshBase.Mode.PULL_FROM_START;
    }

    @Override
    protected void invokeGetDataDeliveryApi(int requestCode) {
        DeliveryApi._getActivityInfos(ClientStateManager.getLoginToken(getActivity())
                , getNewHandler(requestCode, ResultActivityInfo.class));
    }

    /**
     * Mode不包含上拉加载时，可这样重写此方法
     */
    @Override
    protected void invokeGetMoreDeliveryApi(int requestCode) {
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase resultBase) {
        super.onSuccessResponse(requestCode, jsonString, resultBase);
        // 其他requestCode可在此处理
    }

    /**
     * 活动Adapter
     */
    class ActivityAdapter extends BaseListAdapter<ActivityInfo> {
        public ActivityAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.item_clothing_without_order;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            final ActivityInfo order = (ActivityInfo) getItem
                    (position);
            if (order == null) {
                return;
            }

            TextView txtActivityName = ViewHolder.get(convertView, R.id.txt_activity_name);
            TextView txtAddress = ViewHolder.get(convertView, R.id.txt_address_detail);
            Button btnStart = ViewHolder.get(convertView, R.id.btn_start);
            LinearLayout layoutDetail = ViewHolder.get(convertView, R.id.layout_detail);

            txtActivityName.setText(order.getActivityName());
            txtAddress.setText(String.format("%s%s%s%s%s%s", order.getProvince(),
                    order.getCity(),
                    order.getCounty(),
                    order.getVillage(),
                    order.getStreet(),
                    order.getAddress()));

            setClickEvent(isNew, position, btnStart, layoutDetail);
        }
    }

    @Override
    public void onItemClick(Object item, View view, int position) {
        ActivityInfo order = (ActivityInfo) item;
        if (order == null) {
            return;
        }
        switch (view.getId()) {
            case R.id.layout_detail:
                ActivityDetailActivity.actionStart(getActivity(), order.getActivityCode());
                break;
            // 开始收衣
            case R.id.btn_start:
                ActivityDescActivity.actionStart(getActivity(), REQUEST_CODE_ACTIVITY_DESC, order
                        .getActivityCode());

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED) {
            return;
        }

        switch (requestCode) {
            // 开始收衣返回
            case REQUEST_CODE_ACTIVITY_DESC:
                if (resultCode == ActivityDescActivity.RESULT_HAS_COLLECT) {
                    initData();
                    return;
                }
                break;
            default:
                break;
        }

    }
}

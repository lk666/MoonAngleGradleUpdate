package cn.com.bluemoon.delivery.module.wash.collect.withoutorder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;
import org.apache.http.protocol.HTTP;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.clothing.ResultActivityInfo;
import cn.com.bluemoon.delivery.app.api.model.clothing.collect.ActivityInfo;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.module.base.interf.IActionBarListener;
import cn.com.bluemoon.delivery.module.wash.collect.ClothingTabActivity;
import cn.com.bluemoon.delivery.module.oldbase.BaseFragment;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.ViewHolder;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;

public class WithoutOrderManageFragment extends BaseFragment implements OnListItemClickListener {
    private static final int REQUEST_CODE_ACTIVITY_DESC = 0x55;

    private ClothingTabActivity main;
    @Bind(R.id.listview_main)
    PullToRefreshListView listviewMain;
    ActivityAdapter adapter;
    private String manager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        main = (ClothingTabActivity) getActivity();
        Bundle bundle = getArguments();
        manager = bundle.getString("manager");
        initCustomActionBar();

        View v = inflater.inflate(R.layout.fragment_tab_clothing_without_order, container,
                false);

        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listviewMain.setMode(PullToRefreshBase.Mode.DISABLED);

        adapter = new ActivityAdapter(main, this);
        listviewMain.setAdapter(adapter);


        View emptyView = LayoutInflater.from(main).inflate(R.layout.layout_no_data, null);
        ((TextView) emptyView.findViewById(R.id.txt_content)).setText(R.string
                .with_order_collect_no_order);
        listviewMain.setEmptyView(emptyView);

        getData();
    }

    private void getData() {
        showProgressDialog();
        DeliveryApi.getActivityInfos(ClientStateManager.getLoginToken(main), activityHandler);
    }

    private void setData(ResultActivityInfo result) {
        adapter.setList(result.getActivityInfos());
        main.setAmountShow(manager, result.getActivityInfos().size());
        adapter.notifyDataSetChanged();
    }

    AsyncHttpResponseHandler activityHandler = new TextHttpResponseHandler(
            HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                              String responseString) {
            LogUtils.d(getDefaultTag(), "activityHandler result = " + responseString);
            dismissProgressDialog();

            try {
                ResultActivityInfo result = JSON.parseObject(responseString,
                        ResultActivityInfo.class);
                if (result.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    setData(result);
                } else {
                    PublicUtil.showErrorMsg(main, result);
                }
            } catch (Exception e) {
                LogUtils.e(getDefaultTag(), e.getMessage());
                PublicUtil.showToastServerBusy();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers,
                              String responseString, Throwable throwable) {
            LogUtils.e(getDefaultTag(), throwable.getMessage());
            dismissProgressDialog();

            PublicUtil.showToastServerOvertime();
        }
    };

    private void initCustomActionBar() {

        new CommonActionBar(main.getActionBar(),
                new IActionBarListener() {

                    @Override
                    public void onBtnRight(View v) {

                    }

                    @Override
                    public void onBtnLeft(View v) {
                        main.finish();
                    }

                    @Override
                    public void setTitle(TextView v) {
                        v.setText(R.string.tab_title_without_order_collect_manage);
                    }
                });
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
                    getData();
                    return;
                }
                break;
            default:
                break;
        }

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
                ActivityDetailActivity.actionStart(main, order.getActivityCode());
                break;
            // 开始收衣
            case R.id.btn_start:
                ActivityDescActivity.actionStart(main, REQUEST_CODE_ACTIVITY_DESC, order
                        .getActivityCode());

        }
    }


}

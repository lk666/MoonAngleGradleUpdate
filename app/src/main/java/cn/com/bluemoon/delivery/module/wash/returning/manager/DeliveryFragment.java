package cn.com.bluemoon.delivery.module.wash.returning.manager;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.BasePullToRefreshListViewFragment;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.module.wash.returning.manager.model.ResultExpress;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;

/**
 * Created by ljl on 2016/9/19.
 */
public class DeliveryFragment extends BasePullToRefreshListViewFragment {

    @Override
    protected String getTitleString() {
        return getString(R.string.manger_tab_1);
    }

    @Override
    protected void setActionBar(CommonActionBar actionBar) {
        super.setActionBar(actionBar);
        ImageView rightImg = actionBar.getImgRightView();
        rightImg.setImageResource(R.mipmap.scan_top_nav);
        rightImg.setVisibility(View.VISIBLE);
        rightImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                longToast("scan");
            }
        });
    }

    @Override
    protected BaseListAdapter getNewAdapter() {
        return new DeliveryAdapter(getActivity(), null);
    }

    @Override
    protected List getGetMoreList(ResultBase result) {
        return null;
    }

    @Override
    protected List getGetDataList(ResultBase result) {
        ResultExpress r = (ResultExpress) result;
        return r.getExpressList();
    }

    @Override
    protected PullToRefreshBase.Mode getMode() {
        return null;
    }

    @Override
    protected void invokeGetDataDeliveryApi(int requestCode) {
        showWaitDialog();
        DeliveryApi.queryExpressReceiveList(74563, getToken(), getNewHandler(requestCode, ResultExpress.class));
    }

    @Override
    protected void invokeGetMoreDeliveryApi(int requestCode) {

    }

    @Override
    public void onItemClick(Object item, View view, int position) {

    }
    @Override
    protected void initPullToRefreshListView(PullToRefreshListView ptrlv) {
        //ptrlv.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        //ptrlv.setDividerDrawable(getResources().getDrawable(R.drawable.div_left_padding_16));
        ptrlv.getRefreshableView().setDividerHeight(getResources().getDimensionPixelSize(R.dimen.div_height_8));
    }

    class DeliveryAdapter extends BaseListAdapter<ResultExpress.ExpressListBean> {

        public DeliveryAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.list_item_return_express;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            TextView txtNo = getViewById(R.id.txt_no);
            LinearLayout layoutDetail = getViewById(R.id.layout_detail);
            TextView txtNumber = getViewById(R.id.txt_number);
            TextView txt_logistics = getViewById(R.id.txt_logistics);
            Button receivingOrdersAction = getViewById(R.id.receiving_orders_action);
            ResultExpress.ExpressListBean result = list.get(position);
            txtNo.setText(result.getCompanyName() + "：" + result.getExpressCode());
            txtNumber.setText("还衣单数量："+result.getBackOrderNum());
        }
    }
}

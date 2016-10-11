package cn.com.bluemoon.delivery.module.wash.returning.expressclosebox;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.ReturningApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.BasePullToRefreshListViewFragment;
import cn.com.bluemoon.delivery.app.api.model.wash.manager.ResultExpress;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;

/**
 * Created by ljl on 2016/9/28.
 */
public class ExpressHistoryFragment extends BasePullToRefreshListViewFragment {

    private long pageFlag = 0;
    private long closeBoxTime = 0;
    private int index;

    @Override
    protected String getTitleString() {
        return getString(R.string.express_close_box_tab2);
    }

    @Override
    protected void setActionBar(CommonActionBar actionBar) {
        super.setActionBar(actionBar);
    }

    @Override
    protected BaseListAdapter getNewAdapter() {
        return new RecordAdapter(getActivity());
    }

    @Override
    protected List getGetMoreList(ResultBase result) {
        ResultExpress r = (ResultExpress) result;
        pageFlag = r.getPageFlag();
        return r.getExpressList();
    }

    @Override
    protected List getGetDataList(ResultBase result) {
        ResultExpress r = (ResultExpress) result;
        pageFlag = r.getPageFlag();
        return r.getExpressList();
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
    }

    @Override
    protected void invokeGetDataDeliveryApi(int requestCode) {
        pageFlag = 0;
        ReturningApi.queryExpressLog(closeBoxTime, 0, getToken(), getNewHandler(requestCode, ResultExpress.class));
    }

    @Override
    protected void invokeGetMoreDeliveryApi(int requestCode) {
        ReturningApi.queryExpressLog(closeBoxTime, pageFlag, getToken(), getNewHandler(requestCode, ResultExpress.class));
    }

    @Override
    public void onItemClick(Object item, View view, int position) {

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    class RecordAdapter extends BaseListAdapter<ResultExpress.ExpressListBean> {

        public RecordAdapter(Context context) {
            super(context, null);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.list_item_express_record;
        }

        @Override
        protected void setView(final int position, View convertView, ViewGroup parent, boolean isNew) {

            TextView txtDes = getViewById(R.id.txt_des);
            LinearLayout layoutDetail = getViewById(R.id.layout_detail);
            TextView txtName = getViewById(R.id.txt_name);
            TextView txtAmount = getViewById(R.id.txt_amount);


            final ResultExpress.ExpressListBean result = list.get(position);
            txtDes.setText(result.getCompanyName() + "-" + result.getExpressCode());
            txtAmount.setText(String.valueOf(result.getBackOrderNum()));
            txtName.setText(result.getReceiver());

            layoutDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), ExpressDetailActivity.class);
                    intent.putExtra("companyName",result.getCompanyName());
                    intent.putExtra("expressCode",result.getExpressCode());
                    intent.putExtra("receiverCode",result.getReceiverCode());
                    intent.putExtra("receiver",result.getReceiver());
                    intent.putExtra("companyCode",result.getCompanyCode());
                    startActivity(intent);
                }
            });
        }
    }
}

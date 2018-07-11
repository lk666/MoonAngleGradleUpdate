package cn.com.bluemoon.delivery.module.wash.returning.pack;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.ReturningApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.wash.pack.BackOrder;
import cn.com.bluemoon.delivery.app.api.model.wash.pack.ResultWaitInbox;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.BasePullToRefreshListViewFragment;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;


public class WaitInboxFragment extends BasePullToRefreshListViewFragment {

    private long pageFlag = 0;

    @Override
    protected String getTitleString() {
        return getString(R.string.title_box);
    }

    @Override
    protected void initPullToRefreshListView(PullToRefreshListView ptrlv) {
        ptrlv.getRefreshableView().setDivider(null);
        ptrlv.getRefreshableView().setDividerHeight(0);
    }

    @Override
    protected int getHeadLayoutId() {
        return R.layout.head_fragment_tab_close_box;
    }

    @Override
    protected void setActionBar(CommonActionBar titleBar) {
        super.setActionBar(titleBar);
        titleBar.getTvRightView().setText(R.string.pack_inbox);
        titleBar.getTvRightView().setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActionBarBtnRightClick() {
        super.onActionBarBtnRightClick();
        ScanPackActivity.actStart(this);
    }

    @Override
    protected BaseListAdapter getNewAdapter() {
        return new WaitInboxAdapter(getActivity(), this);
    }

    @Override
    protected List getGetMoreList(ResultBase result) {
        return getGetDataList(result);
    }

    @Override
    protected List getGetDataList(ResultBase result) {
        ResultWaitInbox waitInbox = (ResultWaitInbox) result;
        pageFlag = waitInbox.getPageFlag();
        return waitInbox.getBackOrderList();
    }

    @Override
    protected PullToRefreshBase.Mode getMode() {
        return PullToRefreshBase.Mode.BOTH;
    }

    @Override
    protected void invokeGetDataDeliveryApi(int requestCode) {
        pageFlag = 0;
        ReturningApi.queryWaitInboxList(pageFlag, getToken(), getNewHandler(requestCode, ResultWaitInbox.class));
        setAmount();
    }

    @Override
    protected void invokeGetMoreDeliveryApi(int requestCode) {
        ReturningApi.queryWaitInboxList(pageFlag, getToken(), getNewHandler(requestCode,
                ResultWaitInbox.class));
    }

    @Override
    public void onItemClick(Object item, View view, int position) {

    }

    class WaitInboxAdapter extends BaseListAdapter<BackOrder> {

        public WaitInboxAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.item_pack_inbox_list;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            BackOrder item = list.get(position);
            TextView txtBackCode = getViewById(R.id.txt_back_code);
            TextView txtBoxCode = getViewById(R.id.txt_box_code);
            txtBackCode.setText(getString(R.string.pack_inbox_back_code,item.getBackOrderCode()));
            txtBoxCode.setText(TextUtils.isEmpty(item.getBoxCode()) ? getString(R.string.none2) : item.getBoxCode());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0){
            initData();
        }
    }
}

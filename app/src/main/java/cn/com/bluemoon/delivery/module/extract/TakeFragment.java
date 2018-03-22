package cn.com.bluemoon.delivery.module.extract;


import android.content.Context;
import android.graphics.Paint;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.com.bluemoon.delivery.BuildConfig;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.extract.ResultGetAllStores;
import cn.com.bluemoon.delivery.app.api.model.extract.ResultGetAllStores.AllStoresListBean;
import cn.com.bluemoon.delivery.module.base.BaseFragment;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.BasePullHeadToRefreshListViewFragment;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase.Mode;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib.view.CommonEmptyView;
import cn.com.bluemoon.lib.view.CommonEmptyView.EmptyListener;

public class TakeFragment extends BasePullHeadToRefreshListViewFragment implements OnListItemClickListener{

    private boolean auth = false;

    @Override
    protected BaseListAdapter getNewAdapter() {
        return new StoreAdapter(getActivity(), this);
    }

    @Override
    protected List getGetMoreList(ResultBase result) {
        return null;
    }

    @Override
    protected List getGetDataList(ResultBase result) {
        ResultGetAllStores r = (ResultGetAllStores)result;
        return r.getAllStoresList();
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.extract_take_title);
    }

    @Override
    protected void initPullToRefreshListView(PullToRefreshListView ptrlv) {
        ptrlv.getRefreshableView().setDivider(null);
        ptrlv.getRefreshableView().setDividerHeight(0);
    }

    @Override
    protected Mode getMode() {
        return Mode.DISABLED;
    }

    @Override
    protected void invokeGetDataDeliveryApi(int requestCode) {
        DeliveryApi.getAllStoresByUserCode(getToken(), getNewHandler(requestCode, ResultGetAllStores.class));
    }

    @Override
    protected void invokeGetMoreDeliveryApi(int requestCode) {

    }

    @Override
    public void onResume() {
        super.onResume();
        if (auth) {
            initData();
            auth = false;
        }
    }

    @Override
    public void onItemClick(Object item, View view, int position) {
        String url = String.format(BuildConfig.H5_DOMAIN, "FE/angue/storeHouse/authorizationManager");
        if (!TextUtils.isEmpty(url)) {
            if (!TextUtils.isEmpty(getToken())) {
                url = url + "?token=" + getToken()
                        + "&storeCode=" + ((AllStoresListBean)item).getStoreCode();
            }
            switch (view.getId()) {
                case R.id.txt_auth:
                    url = url + "&type=edit";
                    auth = true;
                    break;
                case R.id.txt_detail:
                    url = url + "&type=show";
                    break;

            }
            PublicUtil.openWebView(getActivity(), url, null);
        }
    }

    class StoreAdapter extends BaseListAdapter<AllStoresListBean> {

        public StoreAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.item_storehouse;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            AllStoresListBean bean = list.get(position);
            TextView txtShop = getViewById(R.id.txt_shop);
            TextView txtAddress = getViewById(R.id.txt_address);
            TextView txtPersonNum = getViewById(R.id.txt_person_num);
            TextView txtAuth = getViewById(R.id.txt_auth);
            TextView txtDetail = getViewById(R.id.txt_detail);
            txtDetail.setText(Html.fromHtml("<u>"+getString(R.string.storehouse_detail)+"</u>"));
            txtPersonNum.setText(getString(R.string.auth_person_num, bean.getPersonNum()));
            txtShop.setText(bean.getStoreCode()+"-"+bean.getStoreName());
            txtAddress.setText(bean.getStoreAddress());
            setClickEvent(isNew, position, txtAuth, txtDetail);
        }
    }

}

package cn.com.bluemoon.delivery.jobrecord;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

import cn.com.bluemoon.delivery.ClientStateManager;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.jobrecord.ResultBpList;
import cn.com.bluemoon.delivery.async.listener.IActionBarListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.ViewHolder;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib.view.CommonEmptyView;
import cn.com.bluemoon.lib.view.CommonProgressDialog;
import cn.com.bluemoon.lib.view.CommonSearchView;

/**
 * Created by LIANGJIANGLI on 2016/7/1.
 */
public class CommunitySelectActivity extends Activity implements CommonSearchView.SearchViewListener{

    private String TAG = "CommunitySelectActivity";
    private CommonProgressDialog progressDialog;
    private List<ResultBpList.Item> items = new ArrayList<>();
    private PullToRefreshListView listview;
    private CommunityAdapter adapter;
    private CommonSearchView searchView;
    private long timestamp;
    private boolean isPullUp;
    private boolean isPullDown;
    private String searchKey = "";
    private String bpCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);
        initCustomActionBar();
        listview = (PullToRefreshListView) findViewById(R.id.listview_community);
        adapter = new CommunityAdapter(this);
        listview.setAdapter(adapter);
        PublicUtil.setEmptyView(listview, null, new CommonEmptyView.EmptyListener() {
            @Override
            public void onRefresh() {
                isPullDown = false;
                isPullUp = false;
                getList();
            }
        });
        searchView = (CommonSearchView) findViewById(R.id.search_view);
        Button btnOk = (Button) findViewById(R.id.btn_ok);
        progressDialog = new CommonProgressDialog(this);
        bpCode = getIntent().getStringExtra("bpCode");
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResultBpList.Item itemSelect = null;
                if (items != null && items.size() > 0) {
                    for (ResultBpList.Item item : items) {
                        if (item.isSelect()) {
                            itemSelect = item;
                            break;
                        }
                    }
                }
                if (itemSelect != null) {
                    Intent data = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("community", itemSelect);
                    data.putExtras(bundle);
                    setResult(4, data);
                    finish();
                } else {
                    PublicUtil.showToast(getString(R.string.community_select_txt));
                }

            }
        });

        searchView.setListHistory(ClientStateManager.getHistory(ClientStateManager.COMMUNITY_KEY));
        searchView.setSearchViewListener(this);
        listview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                isPullDown = true;
                isPullUp = false;
                getList();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                isPullDown = false;
                isPullUp = true;
                getList();
            }
        });
        getList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ClientStateManager.setHistory(searchView.getListHistory(),ClientStateManager.COMMUNITY_KEY);
    }

    private void getList(){
        if(!isPullUp){
            timestamp = 0;
        }
        if(!isPullUp&&!isPullDown&&progressDialog!=null){
            progressDialog.show();
        }
        DeliveryApi.getBpList(ClientStateManager.getLoginToken(this), searchKey, timestamp, getBpListHandler);
    }

    private void setData(List<ResultBpList.Item> itemList){

        if(itemList == null||itemList.size()==0){
            if(isPullUp){
                PublicUtil.showToast(R.string.card_no_list_data);
                return;
            } else {
                items.clear();
            }
        }else{
            if(isPullUp){
                items.addAll(itemList);
            }else{
                items.clear();
                items.addAll(itemList);
            }
        }
        isPullUp = false;
        isPullDown = false;
        adapter.notifyDataSetChanged();
    }

    AsyncHttpResponseHandler getBpListHandler = new TextHttpResponseHandler() {

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                              String responseString) {
            LogUtils.d("test", "getBpListHandler result = " + responseString);
            progressDialog.dismiss();
            try {
                ResultBpList result = JSON.parseObject(responseString,
                        ResultBpList.class);
                if (result.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    if (StringUtils.isNotBlank(bpCode) && result.getItemList().size() > 0) {
                        for (int i = 0; i < result.getItemList().size(); i++) {
                            ResultBpList.Item item = result.getItemList().get(i);
                            if (bpCode.equals(item.getBpCode())) {
                                item.setSelect(true);
                                result.getItemList().set(i, item);
                                bpCode = null;
                                break;
                            }
                        }
                    }
                    timestamp = result.getTimestamp();
                    setData(result.getItemList());
                } else {
                    PublicUtil.showErrorMsg(CommunitySelectActivity.this, result);
                }
            } catch (Exception e) {
                PublicUtil.showToastServerBusy();
            } finally {
                listview.onRefreshComplete();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers,
                              String responseString, Throwable throwable) {
            LogUtils.d("test", "getBpListHandler result failed. statusCode="
                    + statusCode);
            progressDialog.dismiss();
            listview.onRefreshComplete();
            PublicUtil.showToastServerOvertime();
        }
    };

    @Override
    public void onSearch(CommonSearchView view, String str) {
        searchKey = str;
        DeliveryApi.getBpList(ClientStateManager.getLoginToken(CommunitySelectActivity.this), searchKey, 0, getBpListHandler);
    }

    @Override
    public void onCancel(CommonSearchView view) {
        searchKey = "";
    }

    class CommunityAdapter extends BaseAdapter {

        private Context context;
        public CommunityAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.list_item_community, null);
            }
            final ResultBpList.Item item = items.get(position);
            TextView txtCommunity = ViewHolder.get(convertView, R.id.txt_community);
            TextView txtCommunity2 = ViewHolder.get(convertView, R.id.txt_community2);
            final CheckBox cb = ViewHolder.get(convertView, R.id.cb_community);
            cb.setChecked(item.isSelect());

            txtCommunity.setText(String.format(getString(R.string.promote_append),item.getBpCode(), item.getBpName()));
            txtCommunity2.setText(String.format(getString(R.string.promote_append),item.getBpCode1(), item.getBpName1()));

            final View line1 = (View) convertView.findViewById(R.id.line_1);
            final View line2 = (View) convertView.findViewById(R.id.line_2);

            if (position != items.size() -1) {
                line1.setVisibility(View.VISIBLE);
                line2.setVisibility(View.GONE);
            } else {
                line1.setVisibility(View.GONE);
                line2.setVisibility(View.VISIBLE);
            }
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v instanceof CheckBox) {
                        if (!cb.isChecked()) {
                            cb.setChecked(true);
                        }
                    }
                    for (int i =0 ; i < items.size(); i++) {
                        ResultBpList.Item item = items.get(i);
                        item.setSelect(i == position);
                        items.set(i,item);
                        adapter.notifyDataSetChanged();
                    }
                }
            };
            convertView.setOnClickListener(listener);
            cb.setOnClickListener(listener);
            return convertView;
        }
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
    }

    public void onPause() {
        super.onPause();

        MobclickAgent.onPageEnd(TAG);
    }

    private void initCustomActionBar() {
        new CommonActionBar(getActionBar(), new IActionBarListener() {

            @Override
            public void setTitle(TextView v) {
                // TODO Auto-generated method stub
                v.setText(getResources().getString(R.string.community_title));
            }

            @Override
            public void onBtnRight(View v) {
            }

            @Override
            public void onBtnLeft(View v) {
                finish();
            }
        });
    }
}

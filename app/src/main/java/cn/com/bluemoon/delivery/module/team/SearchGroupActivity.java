package cn.com.bluemoon.delivery.module.team;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.protocol.HTTP;

import java.util.ArrayList;
import java.util.List;

import cn.com.bluemoon.delivery.AppContext;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.team.ResultGroupList;
import cn.com.bluemoon.delivery.app.api.model.team.TeamGroup;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.module.base.interf.IActionBarListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.delivery.utils.ViewHolder;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib.utils.LibViewUtil;
import cn.com.bluemoon.lib.view.CommonEmptyView;
import cn.com.bluemoon.lib.view.CommonProgressDialog;
import cn.com.bluemoon.lib.view.CommonSearchView;

public class SearchGroupActivity extends Activity implements CommonSearchView.SearchViewListener {

    private String TAG = "SearchGroupActivity";
    private SearchGroupActivity mContext;
    private CommonSearchView searchView;
    private GroupAdapter groupAdapter;
    private CommonProgressDialog progressDialog;
    private PullToRefreshListView listviewGroup;
    private long timestamp = 0;
    private boolean pullUp;
    private boolean pullDown;
    private List<TeamGroup> items;
    private CommonEmptyView emptyView;
    private String content="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initCustomActionBar();
        setContentView(R.layout.activity_search_group);
        mContext = this;
        searchView = (CommonSearchView) findViewById(R.id.searchview_group);
        searchView.setSearchViewListener(this);
        searchView.setListHistory(ClientStateManager.getHistory(ClientStateManager.HISTORY_GROUP));
        listviewGroup = (PullToRefreshListView) findViewById(R.id.listview_group);
        emptyView = PublicUtil.setEmptyView(listviewGroup, null,
                new CommonEmptyView.EmptyListener() {
            @Override
            public void onRefresh() {
                pullDown = false;
                pullUp = false;
                getData();
            }
        });
        progressDialog = new CommonProgressDialog(mContext);
        listviewGroup.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                pullDown = true;
                pullUp = false;
                getData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                pullDown = false;
                pullUp = true;
                getData();
            }
        });
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                searchView.setFocus(true);
                LibViewUtil.showKeyboard(searchView.getSearchEdittext());
                LibViewUtil.setViewVisibility(emptyView, View.VISIBLE);
            }
        }, 100);
    }

    private void getData() {
        if (!pullUp) {
            timestamp = 0;
            content = searchView.getText();
        }
        if (!pullUp && !pullDown && progressDialog != null) {
            progressDialog.show();
        }
        DeliveryApi.getGroupList(ClientStateManager.getLoginToken(mContext), content, AppContext.PAGE_SIZE, timestamp, groupListHandler);
    }

    private void setData(List<TeamGroup> list) {
        if (items == null) {
            items = new ArrayList<>();
        }
        if (pullUp && (list == null || list.size() == 0)) {
            PublicUtil.showToast(R.string.card_no_list_data);
            return;
        } else if (pullUp) {
            items.addAll(list);
        } else {
            items = list;
        }
        if (groupAdapter == null) {
            groupAdapter = new GroupAdapter(R.layout.item_team_group);
        }
        groupAdapter.setList(items);
        if (pullUp) {
            groupAdapter.notifyDataSetChanged();
        } else {
            listviewGroup.setAdapter(groupAdapter);
        }
    }

    AsyncHttpResponseHandler groupListHandler = new TextHttpResponseHandler(HTTP.UTF_8) {
        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            LogUtils.d(TAG, "groupListHandler result = " + responseString);
            if (progressDialog != null)
                progressDialog.dismiss();
            listviewGroup.onRefreshComplete();
            try {
                ResultGroupList groupListResult = JSON.parseObject(responseString, ResultGroupList.class);

                if (groupListResult.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    MyTeamActivity.roleCode = groupListResult.getRoleCode();
                    timestamp = groupListResult.getTimestamp();
                    setData(groupListResult.getItemList());
                } else {
                    PublicUtil.showErrorMsg(mContext, groupListResult);
                }
            } catch (Exception e) {
                LogUtils.e(TAG, e.getMessage());
                PublicUtil.showToastServerBusy();
            }

        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString,
                              Throwable throwable) {
            LogUtils.e(TAG, throwable.getMessage());
            if (progressDialog != null)
                progressDialog.dismiss();
            listviewGroup.onRefreshComplete();
            PublicUtil.showToastServerOvertime();
        }
    };


    private void initCustomActionBar() {
        new CommonActionBar(getActionBar(), new IActionBarListener() {

            @Override
            public void onBtnRight(View v) {

            }

            @Override
            public void onBtnLeft(View v) {
                LibViewUtil.hideIM(v);
                finish();
            }

            @Override
            public void setTitle(TextView v) {
                v.setText(R.string.team_group_title);
            }

        });

    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (searchView != null)
            ClientStateManager.setHistory(searchView.getListHistory(), ClientStateManager.HISTORY_GROUP);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onSearch(CommonSearchView view, String str) {
        pullDown = false;
        pullUp = false;
        getData();
    }

    @Override
    public void onCancel(CommonSearchView view) {

    }

    class GroupAdapter extends BaseAdapter {

        private LayoutInflater mInflater;
        private int layoutID;
        private List<TeamGroup> list;

        public GroupAdapter(int layoutID) {
            this.mInflater = LayoutInflater.from(mContext);
            this.layoutID = layoutID;
        }

        public void setList(List<TeamGroup> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            if (list == null) {
                return 0;
            }
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = mInflater.inflate(layoutID, null);
            }
            TextView txtName = ViewHolder.get(convertView, R.id.txt_name);
            TextView txtNum = ViewHolder.get(convertView, R.id.txt_num);
            final TeamGroup item = list.get(position);
            txtName.setText(StringUtil.getStringParams(item.getBpCode(), item.getBpName(), item.getChargeName()));
            txtNum.setText(String.format(getString(R.string.team_group_membernum),
                    item.getActualPopulation(), item.getPlanPopulation()));
            int index = position % 2;
            if (index == 1) {
                convertView.setBackgroundResource(R.drawable.list_item_grep_bg);
            } else {
                convertView.setBackgroundResource(R.drawable.list_item_white_bg);
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext,GroupDetailActivity.class);
                    intent.putExtra("code",item.getBpCode());
                    startActivity(intent);
                }
            });

            return convertView;
        }
    }
}

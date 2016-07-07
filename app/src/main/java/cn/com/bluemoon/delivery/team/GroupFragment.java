package cn.com.bluemoon.delivery.team;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;

import java.util.ArrayList;
import java.util.List;

import cn.com.bluemoon.delivery.ClientStateManager;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.AppContext;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.team.ResultGroupList;
import cn.com.bluemoon.delivery.app.api.model.team.TeamGroup;
import cn.com.bluemoon.delivery.async.listener.IActionBarListener;
import cn.com.bluemoon.delivery.card.CardUtils;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.ViewHolder;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib.view.CommonProgressDialog;
import cn.com.bluemoon.lib.view.CommonSearchView;

public class GroupFragment extends BackHandledFragment {

    private MyTeamActivity mContext;
    private String TAG = "GroupFragment";
    private TextView txtGroupNum;
    private TextView txtMembernum;
    private PullToRefreshListView listviewGroup;
    private LinearLayout layoutTitle;
    private GroupAdapter groupAdapter;
    private CommonSearchView searchView;
    private CommonProgressDialog progressDialog;
    private View rootView;
    private long timestamp = 0;
    private boolean pullUp;
    private boolean pullDown;
    private List<TeamGroup> items;

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mContext = (MyTeamActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initCustomActionBar();

        if (rootView != null) {
            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_group,
                container, false);
        txtGroupNum = (TextView) rootView.findViewById(R.id.txt_groupnum);
        txtMembernum = (TextView) rootView.findViewById(R.id.txt_membernum);
        listviewGroup = (PullToRefreshListView) rootView.findViewById(R.id.listview_group);
        layoutTitle = (LinearLayout) rootView.findViewById(R.id.layout_title);
        PublicUtil.setEmptyView(listviewGroup, getString(R.string.team_group_empty_group), R.mipmap.team_empty_group);

        searchView = (CommonSearchView) rootView.findViewById(R.id.searchview_group);
        searchView.setSearchViewListener(searchViewListener);
        searchView.setListHistory(ClientStateManager.getHistory(ClientStateManager.HISTORY_GROUP));

        progressDialog = new CommonProgressDialog(mContext);

        listviewGroup.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                pullDown = true;
                pullUp = false;
                getData("");
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                pullDown = false;
                pullUp = true;
                getData("");
            }
        });
        pullUp = false;
        pullDown = false;
        getData("");

        return rootView;
    }

    private void getData(String content) {
        if (!pullUp) {
            timestamp = 0;
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
        if (items == null || items.size() == 0) {
            if (layoutTitle.getVisibility() == View.VISIBLE) {
                layoutTitle.setVisibility(View.GONE);
            }
        } else {
            if (layoutTitle.getVisibility() == View.GONE) {
                layoutTitle.setVisibility(View.VISIBLE);
            }
        }
        if (groupAdapter == null) {
            groupAdapter = new GroupAdapter(mContext, R.layout.item_team_group);
        }
        groupAdapter.setList(items);
        if (pullUp) {
            groupAdapter.notifyDataSetChanged();
        } else {
            listviewGroup.setAdapter(groupAdapter);
        }
    }

    CommonSearchView.SearchViewListener searchViewListener = new CommonSearchView.SearchViewListener() {
        @Override
        public void onSearch(String str) {
            pullUp = false;
            pullDown = false;
            getData(str);
            searchView.setVisibility(View.GONE);
        }

        @Override
        public void onCancel() {
            searchView.setVisibility(View.GONE);
        }

    };

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
                    txtGroupNum.setText(String.format(getString(R.string.team_group_groupnum), groupListResult.getTotalGroup()));
                    txtMembernum.setText(String.format(getString(R.string.team_group_membernum),
                            groupListResult.getActualTotalPopulation(), groupListResult.getPlanTotalPopulation()));
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
        CommonActionBar actionBar = new CommonActionBar(mContext.getActionBar(), new IActionBarListener() {

            @Override
            public void onBtnRight(View v) {
                if (searchView.getVisibility() == View.GONE) {
                    searchView.setVisibility(View.VISIBLE);
                    searchView.setText("");
                    searchView.showHistoryView();
                }
            }

            @Override
            public void onBtnLeft(View v) {
                mContext.finish();
            }

            @Override
            public void setTitle(TextView v) {
                v.setText(getText(R.string.team_group_title));
            }

        });
        actionBar.getImgRightView().setImageResource(R.mipmap.team_search);
        actionBar.getImgRightView().setVisibility(View.VISIBLE);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (searchView != null){
            ClientStateManager.setHistory(searchView.getListHistory(), ClientStateManager.HISTORY_GROUP);
            if(searchView.getVisibility()==View.VISIBLE){
                searchView.setVisibility(View.GONE);
            }
        }
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
    }

    @Override
    protected boolean onBackPressed() {
        if(searchView!=null&&searchView.getVisibility()==View.VISIBLE){
            searchView.setVisibility(View.GONE);
            return true;
        }
        return false;
    }

    class GroupAdapter extends BaseAdapter {

        private LayoutInflater mInflater;
        private Context context;
        private int layoutID;
        private List<TeamGroup> list;

        public GroupAdapter(Context context, int layoutID) {
            this.mInflater = LayoutInflater.from(context);
            this.layoutID = layoutID;
            this.context = context;
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
            txtName.setText(PublicUtil.getStringParams(item.getBpCode(),item.getBpName(),item.getChargeName()));
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

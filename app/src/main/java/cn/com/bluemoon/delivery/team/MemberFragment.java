package cn.com.bluemoon.delivery.team;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import java.util.List;

import cn.com.bluemoon.delivery.ClientStateManager;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.async.listener.IActionBarListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.ViewHolder;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib.view.CommonSearchView;

public class MemberFragment extends BackHandledFragment {

    private MyTeamActivity mContext;
    private String TAG = "MemberFragment";
    private PullToRefreshListView listview;
    private MemberAdapter adapter;
    private CommonSearchView searchView;
    private View rootView;


    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mContext = (MyTeamActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(!"CEO".equals(MyTeamActivity.roleCode)){
            return rootView;
        }
        initCustomActionBar();
        if(rootView!=null){
            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_member,
                container, false);
        listview = (PullToRefreshListView) rootView.findViewById(R.id.listview_member);
        PublicUtil.setEmptyView(listview, getString(R.string.team_group_empty_member),R.mipmap.team_empty_member);
        searchView = (CommonSearchView) rootView.findViewById(R.id.searchview_member);
        searchView.setSearchViewListener(searchViewListener);
        searchView.hideHistoryView();
        searchView.setListHistory(ClientStateManager.getHistory(ClientStateManager.HISTORY_MEMBER));
        setData();
        return rootView;
    }

    private void setData(){
        setAdapter(null);
    }

    private void setAdapter(List<String> list){
        adapter = new MemberAdapter(mContext,R.layout.layout_no_data);
        adapter.setList(list);
        listview.setAdapter(adapter);
    }

    CommonSearchView.SearchViewListener searchViewListener = new CommonSearchView.SearchViewListener() {
        @Override
        public void onSearch(String str) {
            searchView.hideHistoryView();
        }

        @Override
        public void onCancel() {
            searchView.hideHistoryView();
        }

    };

    private void initCustomActionBar() {
        CommonActionBar actionBar = new CommonActionBar(mContext.getActionBar(), new IActionBarListener() {

            @Override
            public void onBtnRight(View v) {

            }

            @Override
            public void onBtnLeft(View v) {
                mContext.finish();
            }

            @Override
            public void setTitle(TextView v) {
                v.setText(getText(R.string.team_member_title));
            }

        });

        actionBar.getImgRightView().setImageResource(R.mipmap.team_add);
        actionBar.getImgRightView().setVisibility(View.VISIBLE);
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
        if(searchView!=null)
        ClientStateManager.setHistory(searchView.getListHistory(),ClientStateManager.HISTORY_MEMBER);
    }

    @Override
    protected boolean onBackPressed() {
        return false;
    }

    class MemberAdapter extends BaseAdapter {

        private LayoutInflater mInflater;
        private Context context;
        private int layoutID;
        private List<String> list;

        public MemberAdapter(Context context, int layoutID){
            this.mInflater = LayoutInflater.from(context);
            this.layoutID = layoutID;
            this.context = context;
        }

        public void setList(List<String> list){
            this.list = list;
        }

        @Override
        public int getCount() {
            if(list==null){
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
            final TextView txtContent = ViewHolder.get(convertView, R.id.txt_content);
            txtContent.setText(list.get(position).toString());

            return convertView;
        }
    }
}

package cn.com.bluemoon.delivery.team;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.async.listener.IActionBarListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.ViewHolder;

public class GroupFragment extends Fragment {

    private MyTeamActivity mContext;
    private String TAG = "GroupFragment";
    private TextView txtGroupNum;
    private TextView txtMembernum;
    private ListView listviewGroup;
    private GroupAdapter groupAdapter;


    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mContext = (MyTeamActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initCustomActionBar();
        View v = inflater.inflate(R.layout.fragment_group,
                container, false);
        txtGroupNum = (TextView) v.findViewById(R.id.txt_groupnum);
        txtMembernum = (TextView) v.findViewById(R.id.txt_membernum);
        listviewGroup = (ListView) v.findViewById(R.id.listview_group);
        PublicUtil.setEmptyView(listviewGroup, getString(R.string.team_group_empty_group));
        setData();
        return v;
    }

    private void setData(){
        List<Object> list = new ArrayList<>();
        list.add("sssssssssssssss");
        list.add("aaaaaaaaaaaaaaaaaa");
        list.add("bbbbbbbbbbbbbbbb");
        setAdapter(list);
    }

    private void setAdapter(List<Object> list){
        groupAdapter = new GroupAdapter(mContext,R.layout.item_team_group);
        groupAdapter.setList(list);
        listviewGroup.setAdapter(groupAdapter);
    }

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
                v.setText(getText(R.string.team_group_title));
            }

        });
        actionBar.getImgRightView().setImageResource(R.mipmap.search_gray);
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

    class GroupAdapter extends BaseAdapter{

        private LayoutInflater mInflater;
        private Context context;
        private int layoutID;
        private List<Object> list;

        public GroupAdapter(Context context, int layoutID){
            this.mInflater = LayoutInflater.from(context);
            this.layoutID = layoutID;
            this.context = context;
        }

        public void setList(List<Object> list){
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
            TextView txtId = ViewHolder.get(convertView, R.id.txt_id);
            TextView txtName = ViewHolder.get(convertView, R.id.txt_name);
            TextView txtNum = ViewHolder.get(convertView, R.id.txt_num);
            TextView txtLeader = ViewHolder.get(convertView, R.id.txt_leader);

            return convertView;
        }
    }
}

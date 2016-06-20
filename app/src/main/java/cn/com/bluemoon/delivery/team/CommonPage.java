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

public class CommonPage extends Fragment {

    private MyTeamActivity mContext;
    private String TAG = "CommonPage";
    private TextView txtLeft;
    private TextView txtRight;
    private ListView listview;
    private CommonAdapter adapter;


    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mContext = (MyTeamActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initCustomActionBar();
        View v = inflater.inflate(R.layout.layout_common,
                container, false);
        txtLeft = (TextView) v.findViewById(R.id.txt_left);
        txtRight = (TextView) v.findViewById(R.id.txt_right);
        listview = (ListView) v.findViewById(R.id.listview);
        PublicUtil.setEmptyView(listview, getString(R.string.none));
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
        adapter = new CommonAdapter(mContext,R.layout.layout_no_data);
        adapter.setList(list);
        listview.setAdapter(adapter);
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

    class CommonAdapter extends BaseAdapter{

        private LayoutInflater mInflater;
        private Context context;
        private int layoutID;
        private List<Object> list;

        public CommonAdapter(Context context,int layoutID){
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
            final TextView txtContent = ViewHolder.get(convertView,R.id.txt_content);
            txtContent.setText(list.get(position).toString());

            return convertView;
        }
    }

}

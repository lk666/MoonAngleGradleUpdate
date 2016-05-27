package cn.com.bluemoon.delivery.notice;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;

import java.util.ArrayList;
import java.util.List;

import cn.com.bluemoon.delivery.ClientStateManager;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.AppContext;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.knowledge.CartItem;
import cn.com.bluemoon.delivery.app.api.model.knowledge.FavoriteItem;
import cn.com.bluemoon.delivery.app.api.model.knowledge.Knowledge;
import cn.com.bluemoon.delivery.app.api.model.knowledge.Paper;
import cn.com.bluemoon.delivery.app.api.model.knowledge.ResultFavorites;
import cn.com.bluemoon.delivery.app.api.model.knowledge.ResultKnowledges;
import cn.com.bluemoon.delivery.async.listener.IActionBarListener;
import cn.com.bluemoon.delivery.manager.ActivityManager;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.KJFUtil;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib.view.CommonProgressDialog;

public class PaperListActivity extends Activity {

    private String TAG="PaperListActivity";
    private PaperListActivity main;
    private CommonProgressDialog progressDialog;
    private ListView listMenu;
    private PullToRefreshListView listCollect;
    private ExpandableListView listTitle;
    private MenuAdapter menuAdapter;
    private TitleFavoriteAdapter titleFavoriteAdapter;
    private ExpandableAdapter expandableAdapter;
    private List<Knowledge> menu;
    private List<FavoriteItem> favorites;
    private long timestamp = 0;
    private boolean pullUp;
    private boolean pullDown;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paper_list);
        main = this;
        ActivityManager.getInstance().pushOneActivity(this);
        progressDialog = new CommonProgressDialog(this);
        initCustomActionBar();
        listMenu = (ListView) findViewById(R.id.listview_menu);
        listTitle = (ExpandableListView) findViewById(R.id.listview_title);
        listCollect = (PullToRefreshListView) findViewById(R.id.listview_collect);

        listCollect.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                pullDown = true;
                pullUp = false;
                getFavoriteData(false);

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                pullDown = false;
                pullUp = true;
                getFavoriteData(false);

            }
        });

        if(progressDialog !=null) progressDialog.show();
        DeliveryApi.getMenuList(ClientStateManager.getLoginToken(main), MenuListHandler);


    }

    private void setData(){

        menuAdapter = new MenuAdapter(main);
        menuAdapter.setList(menu);
        menuAdapter.setSelectItem(0);
        listMenu.setAdapter(menuAdapter);

        getFavoriteData(true);

        listMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                menuAdapter.setSelectItem(position);
                menuAdapter.notifyDataSetInvalidated();
                if (position == 0) {
                    pullUp = false;
                    pullDown = false;
                    getFavoriteData(true);
                } else {
                    setTitleData(position);
                }
            }
        });
    }

    private void getFavoriteData(boolean isFirst){
        if(isFirst){
            if(progressDialog!=null) progressDialog.show();
        }
        if(!pullUp){
            timestamp = 0;
        }
        DeliveryApi.getCollectList(ClientStateManager.getLoginToken(main), AppContext.PAGE_SIZE, timestamp, CollectListHandler);
    }

    private void setFavoriteData(List<FavoriteItem> list){
        listTitle.setVisibility(View.GONE);
        listCollect.setVisibility(View.VISIBLE);
        if(favorites==null){
            favorites = new ArrayList<>();
        }
        if(list==null){
            list = new ArrayList<>();
        }
        if(pullUp){
            favorites.addAll(list);
        }else{
            favorites = list;
        }
        if(titleFavoriteAdapter==null){
            titleFavoriteAdapter = new TitleFavoriteAdapter(main);
            titleFavoriteAdapter.setList(favorites);
            listCollect.setAdapter(titleFavoriteAdapter);
        }else{
            titleFavoriteAdapter.setList(favorites);
            titleFavoriteAdapter.notifyDataSetChanged();
        }



    }

    private void setTitleData(int position){
        final List<CartItem> list = menu.get(position).getCatList();
        listTitle.setVisibility(View.VISIBLE);
        listCollect.setVisibility(View.GONE);
        expandableAdapter = new ExpandableAdapter(list);
        listTitle.setAdapter(expandableAdapter);
        for(int i = 0; i < expandableAdapter.getGroupCount(); i++){
            listTitle.expandGroup(i);
        }
        listTitle.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Intent intent = new Intent(main, NoticeDetailActivity.class);
                intent.putExtra("id",list.get(groupPosition).getPaperList().get(childPosition).getPaperId());
                intent.putExtra("type",Constants.TYPE_KNOWLEDGE);
                main.startActivity(intent);
                return true;
            }
        });
    }

    private void addCollectMenu(){
        if(menu==null){
            menu = new ArrayList<>();
        }
        Knowledge item = new Knowledge();
        item.setCatFirstName(getString(R.string.paper_detail_collect));
        menu.add(0,item);
    }

    AsyncHttpResponseHandler MenuListHandler = new TextHttpResponseHandler(
            HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                              String responseString) {
            LogUtils.d(TAG, "getMenuList result = " + responseString);
            if (progressDialog != null)
                progressDialog.dismiss();
            try {
                ResultKnowledges knowledgesResult = JSON.parseObject(responseString,
                        ResultKnowledges.class);
                if (knowledgesResult.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    menu = knowledgesResult.getList();
                    addCollectMenu();
                    setData();
                } else {
                    PublicUtil.showErrorMsg(main, knowledgesResult);
                }
            } catch (Exception e) {
                LogUtils.e(TAG, e.getMessage());
                PublicUtil.showToastServerBusy();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers,
                              String responseString, Throwable throwable) {
            if (progressDialog != null)
                progressDialog.dismiss();
            LogUtils.e(TAG, throwable.getMessage());
            PublicUtil.showToastServerOvertime();
        }
    };

    AsyncHttpResponseHandler CollectListHandler = new TextHttpResponseHandler(
            HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                              String responseString) {
            LogUtils.d(TAG, "getCollectList result = " + responseString);
            if (progressDialog != null)
                progressDialog.dismiss();
            listCollect.onRefreshComplete();
            try {
                ResultFavorites favoritesResult = JSON.parseObject(responseString,
                        ResultFavorites.class);
                if (favoritesResult.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    timestamp = favoritesResult.getTimestamp();
                    setFavoriteData(favoritesResult.getList());
                } else {
                    PublicUtil.showErrorMsg(main, favoritesResult);
                }
            } catch (Exception e) {
                LogUtils.e(TAG, e.getMessage());
                PublicUtil.showToastServerBusy();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers,
                              String responseString, Throwable throwable) {
            if (progressDialog != null)
                progressDialog.dismiss();
            listCollect.onRefreshComplete();
            LogUtils.e(TAG, throwable.getMessage());
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
                finish();
            }

            @Override
            public void setTitle(TextView v) {
                v.setText("知识库");
            }

        });
    }

    class MenuAdapter extends BaseAdapter {

        private Context context;
        private List<Knowledge> lists;

        public MenuAdapter(Context context) {
            this.context = context;
        }

        public void setList(List<Knowledge> list) {
            this.lists = list;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return lists.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return lists.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            // TODO Auto-generated method stub
            LayoutInflater inflate = LayoutInflater.from(context);

            ViewHolder holder = null;
            if (null == convertView) {
                convertView = inflate
                        .inflate(R.layout.paper_menu_item_view, null);
                holder = new ViewHolder();
                holder.layoutMenu = (LinearLayout) convertView
                        .findViewById(R.id.layout_menu);

                holder.imgIcon = (ImageView) convertView
                        .findViewById(R.id.imageview);
                holder.txtMenu = (TextView) convertView
                        .findViewById(R.id.textview);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (position == selectItem) {
                convertView.setBackgroundColor(getResources().getColor(
                        R.color.text_blue));

                holder.txtMenu.setTextColor(getResources().getColor(
                        R.color.view_bg));

                if(position==0){
                    holder.imgIcon.setBackgroundResource(R.mipmap.collect_white);
                }else{
                    KJFUtil.getUtil()
                            .getKJB()
                            .display(holder.imgIcon,
                                    lists.get(position).getImgOn());
                }

            } else {
                convertView.setBackgroundColor(getResources().getColor(
                        R.color.transparent));

                holder.txtMenu.setTextColor(getResources().getColor(
                        R.color.text_black_light));

                if(position==0){
                    holder.imgIcon.setBackgroundResource(R.mipmap.collect_grep);
                }else{
                    KJFUtil.getUtil()
                            .getKJB()
                            .display(holder.imgIcon,
                                    lists.get(position).getImgOut());
                }


            }

            holder.txtMenu.setText(lists.get(position).getCatFirstName());
            holder.layoutMenu.setTag(lists.get(position));

            return convertView;
        }

        class ViewHolder {
            LinearLayout layoutMenu;
            ImageView imgIcon;
            TextView txtMenu;
        }

        public void setSelectItem(int selectItem) {
            this.selectItem = selectItem;
        }

        private int selectItem = -1;

        public int getSelectItem() {
            return selectItem;
        }

    }

    class TitleFavoriteAdapter extends BaseAdapter {

        private Context context;
        private List<FavoriteItem> lists;

        public TitleFavoriteAdapter(Context context) {
            this.context = context;
        }

        public void setList(List<FavoriteItem> list) {
            this.lists = list;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return lists.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return lists.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            // TODO Auto-generated method stub
            LayoutInflater inflate = LayoutInflater.from(context);

            if (null == convertView) {
                convertView = inflate.inflate(R.layout.paper_title_item_view, null);
            }
            TextView txtTitle = (TextView) convertView
                    .findViewById(R.id.txt_title);
            TextView txtTime = (TextView) convertView
                    .findViewById(R.id.txt_time);

            txtTitle.setText(lists.get(position).getPaperTitle());
            txtTime.setText(DateUtil.getTime(lists.get(position).getCollectTime(), "yyyy-MM-dd HH:mm"));

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(main, NoticeDetailActivity.class);
                    intent.putExtra("id",lists.get(position).getPaperId());
                    intent.putExtra("type",Constants.TYPE_KNOWLEDGE);
                    main.startActivity(intent);
                }
            });

            return convertView;
        }

    }

    class ExpandableAdapter extends BaseExpandableListAdapter {

        private List<CartItem> list;

        public ExpandableAdapter(List<CartItem> list){
            this.list = list;
        }

        //得到子item需要关联的数据
        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return list.get(groupPosition).getPaperList().get(childPosition);
        }

        //得到子item的ID
        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        //设置子item的组件
        @Override
        public View getChildView(int groupPosition, int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
            Paper info = list.get(groupPosition).getPaperList().get(childPosition);
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater)main.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.paper_layout_children, null);
            }
            TextView tv = (TextView) convertView
                    .findViewById(R.id.txt_children);
            tv.setText(info.getPaperTitle());
            return convertView;
        }

        //获取当前父item下的子item的个数
        @Override
        public int getChildrenCount(int groupPosition) {
            return list.get(groupPosition).getPaperList().size();
        }

        //获取当前父item的数据
        @Override
        public Object getGroup(int groupPosition) {
            return list.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return list.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }
        //设置父item组件
        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) main
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.paper_layout_parent, null);
            }
            TextView tv = (TextView) convertView
                    .findViewById(R.id.txt_parent);
            ImageView img = (ImageView) convertView
                    .findViewById(R.id.img_icon);
            tv.setText(list.get(groupPosition).getCatSecondName());
            if(isExpanded){
                img.setImageResource(R.mipmap.paper_fold);
            }else{
                img.setImageResource(R.mipmap.paper_unfold);
            }
            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

    }
}

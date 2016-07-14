package cn.com.bluemoon.delivery.team;

import android.app.Activity;
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
import org.kymjs.kjframe.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import cn.com.bluemoon.delivery.ClientStateManager;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.AppContext;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.team.Emp;
import cn.com.bluemoon.delivery.app.api.model.team.GroupDetail;
import cn.com.bluemoon.delivery.app.api.model.team.ResultEmpList;
import cn.com.bluemoon.delivery.app.api.model.team.ResultGroupDetailInfo;
import cn.com.bluemoon.delivery.async.listener.IActionBarListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.ViewHolder;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib.utils.LibConstants;
import cn.com.bluemoon.lib.view.CommonEmptyView;
import cn.com.bluemoon.lib.view.CommonProgressDialog;
import cn.com.bluemoon.lib.view.CommonSearchView;

public class MemberFragment extends BackHandledFragment {

    private MyTeamActivity mContext;
    private String TAG = "MemberFragment";
    private PullToRefreshListView listview;
    private CommonSearchView searchView;
    private View rootView;
    private boolean pullUp;
    private boolean pullDown;
    private boolean isRefresh;
    private long timestamp;
    private List<GroupDetail> items;
    private adapter adapter;
    private CommonProgressDialog progressDialog;
    private View popStart;
    private String content="";

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
        progressDialog = new CommonProgressDialog(mContext);
        listview = (PullToRefreshListView) rootView.findViewById(R.id.listview_member);
        popStart = rootView.findViewById(R.id.view_pop_start);
        PublicUtil.setEmptyView(listview, String.format(getString(R.string.empty_hint),
                getString(R.string.team_my_member_title)), new CommonEmptyView.EmptyListener() {
            @Override
            public void onRefresh() {
                pullDown = false;
                pullUp = false;
                isRefresh = false;
                getData();
            }
        });
        searchView = (CommonSearchView) rootView.findViewById(R.id.searchview_member);
        searchView.setSearchViewListener(searchViewListener);
        searchView.hideHistoryView();
        searchView.setListHistory(ClientStateManager.getHistory(ClientStateManager.HISTORY_MEMBER));
        listview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                pullDown = true;
                pullUp = false;
                isRefresh = false;
                getData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                pullUp = true;
                pullDown = false;
                isRefresh = true;
                getData();
            }
        });
        pullDown = false;
        pullUp = false;
        isRefresh = false;
        getData();
        return rootView;
    }

    private void getData() {
        if (!pullUp) {
            timestamp = 0;
        }
        if(pullDown){
            content = searchView.getText();
        }
        if (!pullUp && !pullDown && progressDialog != null) {
            progressDialog.show();
        }
        DeliveryApi.getGroupDetailInfo(content, AppContext.PAGE_SIZE, timestamp, ClientStateManager.getLoginToken(mContext), Constants.RELTYPE_COMMUNITY, getGroupDetailInfoHandler);
    }

    private void setData(List<GroupDetail> list) {
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
        if (adapter == null) {
            adapter = new adapter();
        }
        adapter.setList(items);
        if (isRefresh) {
            adapter.notifyDataSetChanged();
        } else {
            listview.setAdapter(adapter);
        }
    }

    CommonSearchView.SearchViewListener searchViewListener = new CommonSearchView.SearchViewListener() {
        @Override
        public void onSearch(String str) {
            content = str;
            searchView.hideHistoryView();
            pullDown = false;
            pullUp = false;
            isRefresh = false;
            getData();
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
                PublicUtil.openScanOrder(mContext, MemberFragment.this, getString(R.string.team_group_add_member),
                        getString(R.string.team_group_add_member_code), Constants.REQUEST_SCAN, 4);
            }

            @Override
            public void onBtnLeft(View v) {
                mContext.finish();
            }

            @Override
            public void setTitle(TextView v) {
                v.setText(getString(R.string.team_my_member_title));
            }

        });

        actionBar.getImgRightView().setImageResource(R.mipmap.team_add);
        actionBar.getImgRightView().setVisibility(View.VISIBLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(Activity.RESULT_CANCELED == resultCode) return;
        if(requestCode == Constants.REQUEST_SCAN){
            switch (resultCode){
                case Activity.RESULT_OK:
                    if(data==null) return;
                    String resultStr = data.getStringExtra(LibConstants.SCAN_RESULT);
                    if(progressDialog!=null) progressDialog.show();
                    DeliveryApi.getEmpList(ClientStateManager.getLoginToken(mContext), resultStr, getEmpListHandler);
                    break;
                case 4:
                    Intent intent2 = new Intent(mContext,SelectEmpActivity.class);
                    MemberFragment.this.startActivityForResult(intent2, 1);
                    break;
            }
        }else if(requestCode == 1&&resultCode == Activity.RESULT_OK
                &&data!=null&&data.hasExtra("emp")){
            Emp emp = (Emp)data.getSerializableExtra("emp");
            openRelationInfo(emp,Constants.TYPE_ADD);
        }else if(requestCode == 2&&resultCode == Activity.RESULT_OK){
            isRefresh = true;
            pullDown = false;
            pullUp = false;
            getData();
        }
    }

    private void openRelationInfo(Emp emp,String type){
        Intent intent = new Intent(mContext,RelationInfoActivity.class);
        intent.putExtra("emp", emp);
        intent.putExtra("type",type);
        intent.putExtra("relType", Constants.RELTYPE_COMMUNITY);
        startActivityForResult(intent, 2);
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
        if(searchView!=null){
            ClientStateManager.setHistory(searchView.getListHistory(),ClientStateManager.HISTORY_MEMBER);
            searchView.hideHistoryView();
        }
    }

    AsyncHttpResponseHandler getEmpListHandler = new TextHttpResponseHandler(HTTP.UTF_8) {
        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            LogUtils.d(TAG, "getEmpListHandler result = " + responseString);
            if (progressDialog != null)
                progressDialog.dismiss();
            try {
                ResultEmpList result = JSON.parseObject(responseString, ResultEmpList.class);
                if (result.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    if(result.getItemList()!=null&&result.getItemList().size()>0){
                        Emp emp = result.getItemList().get(0);
                        if(StringUtils.isEmpty(emp.getBpCode())){
                            openRelationInfo(emp,Constants.TYPE_ADD);
                        }else{
                            PublicUtil.showMessageNoTitle(mContext,getString(R.string.team_member_add_existed));
                        }
                    }else{
                        PublicUtil.showToast(result.getResponseMsg());
                    }
                } else {
                    PublicUtil.showErrorMsg(mContext, result);
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
            PublicUtil.showToastServerOvertime();
        }
    };

    AsyncHttpResponseHandler getGroupDetailInfoHandler = new TextHttpResponseHandler(HTTP.UTF_8) {
        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            LogUtils.d(TAG, "getGroupDetailInfoHandler result = " + responseString);
            if (progressDialog != null)
                progressDialog.dismiss();
            listview.onRefreshComplete();
            try {
                ResultGroupDetailInfo groupDetailInfoResult = JSON.parseObject(responseString, ResultGroupDetailInfo.class);

                if (groupDetailInfoResult.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    timestamp = groupDetailInfoResult.getTimestamp();
                    setData(groupDetailInfoResult.getItemList());
                } else {
                    PublicUtil.showErrorMsg(mContext, groupDetailInfoResult);
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
            listview.onRefreshComplete();
            PublicUtil.showToastServerOvertime();
        }
    };

    AsyncHttpResponseHandler deleteRelationShipHandler = new TextHttpResponseHandler(HTTP.UTF_8) {
        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            LogUtils.d(TAG, "deleteRelationShipHandler result = " + responseString);
            if (progressDialog != null)
                progressDialog.dismiss();
            try {
                ResultBase baseResult = JSON.parseObject(responseString, ResultBase.class);
                if (baseResult.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    PublicUtil.showToast(baseResult.getResponseMsg());
                    isRefresh = true;
                    pullDown = false;
                    pullUp = false;
                    getData();
                } else {
                    PublicUtil.showErrorMsg(mContext, baseResult);
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
            PublicUtil.showToastServerOvertime();
        }
    };

    @Override
    protected boolean onBackPressed() {
        if(searchView!=null&&searchView.getHistoryView().getVisibility()==View.VISIBLE){
            searchView.setFocus(false);
            searchView.hideHistoryView();
            return true;
        }
        return false;
    }

    class adapter extends BaseAdapter {

        private LayoutInflater mInflater;
        private List<GroupDetail> list;

        public adapter() {
            this.mInflater = LayoutInflater.from(mContext);
        }

        public void setList(List<GroupDetail> list) {
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
                convertView = mInflater.inflate(R.layout.item_member_list, null);
            }
            TextView txtName = ViewHolder.get(convertView, R.id.txt_name);
            TextView txtPhone = ViewHolder.get(convertView, R.id.txt_phone);
            TextView txtMsg = ViewHolder.get(convertView, R.id.txt_msg);
            TextView txtCommunity = ViewHolder.get(convertView, R.id.txt_community);
            txtPhone = PublicUtil.getPhoneView(mContext, txtPhone);
            final LinearLayout layoutInfo = ViewHolder.get(convertView,R.id.layout_info);
            final TextView txtUnlock = ViewHolder.get(convertView, R.id.txt_unlock);
            final TextView txtEdit = ViewHolder.get(convertView, R.id.txt_edit);
            final GroupDetail item = list.get(position);
            txtName.setText(PublicUtil.getStringParams(item.getBpCode(), item.getBpName()));
            txtPhone.setText(item.getMobileNo());
            String work;
            if(Constants.WORKTYPE_PART.equals(item.getWorkType())){
                work = getString(R.string.team_work_part);
                if(item.getWorkLength()!=0){
                    work += "，"+item.getWorkLength()+"h";
                }
            }else{
                work = getString(R.string.team_work_full);
            }
            txtMsg.setText(String.format(getString(R.string.team_group_detail_msg),
                    DateUtil.getTime(item.getStartDate(),"yyyy-MM-dd"), work));
            txtCommunity.setText(PublicUtil.getStringParams(item.getCommunityCode(), item.getCommunityName()));

            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(v == txtUnlock){
                        ChooseDateWindow popupWindow = new ChooseDateWindow(mContext,PublicUtil.getStringParams(
                                item.getBpCode(),item.getBpName()), new ChooseDateWindow.ChooseDateListener() {
                            @Override
                            public void callBack(long endTime) {
                                if(progressDialog!=null) progressDialog.show();
                                DeliveryApi.deleteRelationShip(item.getCommunityCode(),item.getBpCode(),endTime,
                                        ClientStateManager.getLoginToken(mContext), Constants.RELTYPE_COMMUNITY,deleteRelationShipHandler);
                            }

                        });
                        popupWindow.showPopwindow(popStart);
                    }else if(v == txtEdit){
                        Emp emp = new Emp();
                        emp.setBpCode(item.getCommunityCode());
                        emp.setEmpCode(item.getBpCode());
                        emp.setEmpName(item.getBpName());
                        emp.setMobileNo(item.getMobileNo());
                        openRelationInfo(emp, Constants.TYPE_UPDATE);
                    }else if(v == layoutInfo){
                        Intent intent = new Intent(mContext,RelationShipDetailActivity.class);
                        intent.putExtra("bpCode",item.getCommunityCode());
                        intent.putExtra("empCode",item.getBpCode());
                        startActivity(intent);
                    }
                }
            };
            txtUnlock.setOnClickListener(onClickListener);
            txtEdit.setOnClickListener(onClickListener);
            layoutInfo.setOnClickListener(onClickListener);

            return convertView;
        }

    }
}

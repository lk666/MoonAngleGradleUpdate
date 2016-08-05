package cn.com.bluemoon.delivery.module.team;

import android.app.Activity;
import android.content.Intent;
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
import org.kymjs.kjframe.KJActivity;
import org.kymjs.kjframe.ui.BindView;
import org.kymjs.kjframe.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import cn.com.bluemoon.delivery.AppContext;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.team.Emp;
import cn.com.bluemoon.delivery.app.api.model.team.EmpEdit;
import cn.com.bluemoon.delivery.app.api.model.team.GroupDetail;
import cn.com.bluemoon.delivery.app.api.model.team.ResultEmpList;
import cn.com.bluemoon.delivery.app.api.model.team.ResultGroupDetailInfo;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.module.base.interf.IActionBarListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.ViewHolder;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib.utils.LibConstants;
import cn.com.bluemoon.lib.utils.LibViewUtil;
import cn.com.bluemoon.lib.view.CommonEmptyView;
import cn.com.bluemoon.lib.view.CommonProgressDialog;

public class GroupDetailActivity extends KJActivity {

    private String TAG = "GroupDetail";
    private EmpEdit empEdit;
    @BindView(id = R.id.txt_title)
    private TextView txtTitle;
    @BindView(id = R.id.txt_total_num)
    private TextView txtTotal;
    @BindView(id = R.id.txt_full)
    private TextView txtFull;
    @BindView(id = R.id.txt_part)
    private TextView txtPart;
    @BindView(id = R.id.listview_group_detail)
    private PullToRefreshListView listviewDetail;
    @BindView(id = R.id.view_pop_start)
    private View popStart;
    private CommonProgressDialog progressDialog;
    private boolean pullUp;
    private boolean pullDown;
    private long timestamp;
    private List<GroupDetail> items;
    private GroupDetailAdapter groupDetailAdapter;
    private ChooseDateWindow popupWindow;
    private CommonEmptyView emptyView;

    @Override
    public void setRootView() {
        initCustomActionBar();
        setContentView(R.layout.activity_group_detail);
    }

    @Override
    public void initWidget() {
        super.initWidget();
        progressDialog = new CommonProgressDialog(aty);
        progressDialog.setCancelable(false);
        empEdit = new EmpEdit();
        empEdit.setRelType(Constants.RELTYPE_GROUP);
        if(getIntent().hasExtra("code")){
            empEdit.setGroupCode(getIntent().getStringExtra("code"));
        }
        emptyView = PublicUtil.setEmptyView(listviewDetail,R.string.team_group_detail_title, new CommonEmptyView.EmptyListener() {
            @Override
            public void onRefresh() {
                pullDown = false;
                pullUp = false;
                getData();
            }
        });
        listviewDetail.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                pullDown = true;
                pullUp = false;
                getData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                pullUp = true;
                pullDown = false;
                getData();
            }
        });
        pullDown = false;
        pullUp = false;
        getData();

    }

    private void getData() {
        if (!pullUp) {
            timestamp = 0;
        }
        if (!pullUp && !pullDown && progressDialog != null) {
            progressDialog.show();
        }
        DeliveryApi.getGroupDetailInfo(empEdit.getGroupCode(), AppContext.PAGE_SIZE, timestamp,
                ClientStateManager.getLoginToken(aty), Constants.RELTYPE_GROUP, getGroupDetailInfoHandler);
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
        if (groupDetailAdapter == null) {
            groupDetailAdapter = new GroupDetailAdapter();
        }
        groupDetailAdapter.setList(items);
        if (pullUp) {
            groupDetailAdapter.notifyDataSetChanged();
        } else {
            listviewDetail.setAdapter(groupDetailAdapter);
        }
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

    private void initCustomActionBar() {
        CommonActionBar actionBar = new CommonActionBar(aty.getActionBar(), new IActionBarListener() {

            @Override
            public void onBtnRight(View v) {
                PublicUtil.openScanOrder(aty,null,getString(R.string.team_group_add_member),
                        getString(R.string.team_group_add_member_code),Constants.REQUEST_SCAN,4);
            }

            @Override
            public void onBtnLeft(View v) {
                finish();
            }

            @Override
            public void setTitle(TextView v) {
                v.setText(getText(R.string.team_group_detail_title));
            }

        });

        actionBar.getImgRightView().setImageResource(R.mipmap.team_add);
        actionBar.getImgRightView().setVisibility(View.VISIBLE);
    }

    AsyncHttpResponseHandler getGroupDetailInfoHandler = new TextHttpResponseHandler(HTTP.UTF_8) {
        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            LogUtils.d(TAG, "getGroupDetailInfoHandler result = " + responseString);
            if (progressDialog != null)
                progressDialog.dismiss();
            listviewDetail.onRefreshComplete();
            try {
                ResultGroupDetailInfo result = JSON.parseObject(responseString, ResultGroupDetailInfo.class);

                if (result.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    empEdit.setGroupCode(result.getBpCode());
                    empEdit.setGroupName(result.getBpName());
                    empEdit.setCommunityCode(result.getCommunityCode());
                    empEdit.setCommunityName(result.getCommunityName());
                    timestamp = result.getTimestamp();
                    txtTitle.setText(PublicUtil.getStringParams(result.getBpCode(), result.getBpName()));
                    txtTotal.setText(getString(R.string.team_group_detail_total_num,result.getActualTotalPopulation(),result.getPlanTotalPopulation()));
                    txtFull.setText(getString(R.string.team_group_detail_full_num, result.getFullTimeNumber()));
                    txtPart.setText(getString(R.string.team_group_detail_part_num, result.getPartTimeNumber()));
                    setData(result.getItemList());
                } else {
                    PublicUtil.showErrorMsg(aty, result);
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
            listviewDetail.onRefreshComplete();
            PublicUtil.showToastServerOvertime();
            LibViewUtil.setViewVisibility(emptyView, View.VISIBLE);
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
                    pullDown = true;
                    pullUp = false;
                    getData();
                } else {
                    PublicUtil.showErrorMsg(aty, baseResult);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Constants.REQUEST_SCAN){
            switch (resultCode){
                case RESULT_CANCELED:
                    break;
                case RESULT_OK:
                    if(data==null) return;
                    String resultStr = data.getStringExtra(LibConstants.SCAN_RESULT);
                    if(progressDialog!=null) progressDialog.show();
                    DeliveryApi.getEmpList(ClientStateManager.getLoginToken(aty), resultStr,Constants.TYPE_SCAN, getEmpListHandler);
                    break;
                case 4:
                    Intent intent2 = new Intent(aty,SelectEmpActivity.class);
                    startActivityForResult(intent2, 1);
                    break;
            }
        }else if(requestCode == 1&&resultCode == RESULT_OK
                &&data!=null&&data.hasExtra("emp")){
            Emp emp = (Emp)data.getSerializableExtra("emp");
            empEdit.setType(Constants.TYPE_ADD);
            openRelationInfo(emp);
        }else if(requestCode == 2&&resultCode == Activity.RESULT_OK){
            pullDown = false;
            pullUp = false;
            getData();
        }
    }

    private void openRelationInfo(Emp emp){
        empEdit.setEmpCode(emp.getEmpCode());
        empEdit.setEmpName(emp.getEmpName());
        empEdit.setMobileNo(emp.getMobileNo());
        Intent intent = new Intent(aty,RelationInfoActivity.class);
        intent.putExtra("empEdit", empEdit);
        startActivityForResult(intent,2);
    }

    AsyncHttpResponseHandler getEmpListHandler = new TextHttpResponseHandler(HTTP.UTF_8) {
        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            LogUtils.d(TAG, "getEmpListHandler result = " + responseString);
            if (progressDialog != null)
                progressDialog.dismiss();
            try {
                ResultEmpList empListResult = JSON.parseObject(responseString, ResultEmpList.class);
                if (empListResult.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    if(empListResult.getItemList()!=null&&empListResult.getItemList().size()>0){
                        Emp emp = empListResult.getItemList().get(0);
                        if(StringUtils.isEmpty(emp.getBpCode())){
                            empEdit.setType(Constants.TYPE_ADD);
                            openRelationInfo(emp);
                        }else{
                            PublicUtil.showMessageNoTitle(aty,getString(R.string.team_member_add_existed));
                        }
                    }
                } else {
                    PublicUtil.showErrorMsg(aty, empListResult);
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

    ChooseDateWindow.ChooseDateListener listener = new ChooseDateWindow.ChooseDateListener() {
        @Override
        public void callBack(String bpCode, String commonityCode, long endTime) {
            if(progressDialog!=null) progressDialog.show();
            DeliveryApi.deleteRelationShip(commonityCode,bpCode,endTime,
                    ClientStateManager.getLoginToken(aty),Constants.RELTYPE_GROUP,deleteRelationShipHandler);
        }

    };

    class GroupDetailAdapter extends BaseAdapter {

        private LayoutInflater mInflater;
        private List<GroupDetail> list;

        public GroupDetailAdapter() {
            this.mInflater = LayoutInflater.from(aty);
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
        public View getView(final int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_group_detail, null);
            }
            TextView txtName = ViewHolder.get(convertView, R.id.txt_name);
            TextView txtJob = ViewHolder.get(convertView, R.id.txt_job);
            TextView txtPhone = ViewHolder.get(convertView, R.id.txt_phone);
            TextView txtMsg = ViewHolder.get(convertView, R.id.txt_msg);
            txtPhone = PublicUtil.getPhoneView(aty,txtPhone);
            final LinearLayout layoutInfo = ViewHolder.get(convertView,R.id.layout_info);
            final TextView txtUnlock = ViewHolder.get(convertView, R.id.txt_unlock);
            final TextView txtMember = ViewHolder.get(convertView, R.id.txt_member);
            final TextView txtEdit = ViewHolder.get(convertView, R.id.txt_edit);
            final GroupDetail item = list.get(position);
            txtName.setText(PublicUtil.getStringParams(item.getBpCode(), item.getBpName()));
            if(!StringUtils.isEmpty(item.getPosiName())){
                txtJob.setText(item.getPosiName());
                LibViewUtil.setViewVisibility(txtJob, View.VISIBLE);
                LibViewUtil.setViewVisibility(txtUnlock, View.GONE);
                LibViewUtil.setViewVisibility(txtEdit, View.GONE);
            }else{
                LibViewUtil.setViewVisibility(txtJob,View.GONE);
                LibViewUtil.setViewVisibility(txtUnlock, View.VISIBLE);
                LibViewUtil.setViewVisibility(txtEdit,View.VISIBLE);
            }
            txtPhone.setText(item.getMobileNo());
            String work = "";
            if(Constants.WORKTYPE_PART.equals(item.getWorkType())){
                work = getString(R.string.team_work_part);
                if(item.getWorkLength()!=0){
                    work += "，"+item.getWorkLength()+"h";
                }
            }else if(Constants.WORKTYPE_FULL.equals(item.getWorkType())){
                work = getString(R.string.team_work_full);
            }
            txtMsg.setText(PublicUtil.getStringParams(DateUtil.getTime(item.getStartDate()), work));

            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(PublicUtil.isFastDoubleClick(1000)) return;
                    if(v == txtUnlock){
                        if(popupWindow == null){
                            popupWindow = new ChooseDateWindow(aty,listener);
                        }
                        popupWindow.setData(item.getBpCode(), item.getBpName(),empEdit.getGroupCode());
                        popupWindow.setMinDate(item.getStartDate());
                        popupWindow.showPopwindow(popStart);
                    }else if(v == txtMember){
                        Intent intent = new Intent(aty,PersonnelAreaActivity.class);
                        intent.putExtra("bpCode",empEdit.getGroupCode());
                        intent.putExtra("empCode",item.getBpCode());
                        startActivity(intent);
                    }else if(v == txtEdit){
                        Emp emp = new Emp();
                        emp.setEmpCode(item.getBpCode());
                        emp.setEmpName(item.getBpName());
                        emp.setMobileNo(item.getMobileNo());
                        empEdit.setType(Constants.TYPE_UPDATE);
                        openRelationInfo(emp);
                    }else if(v == layoutInfo){
                        Intent intent = new Intent(aty,RelationShipDetailActivity.class);
                        intent.putExtra("bpCode",empEdit.getGroupCode());
                        intent.putExtra("empCode",item.getBpCode());
                        startActivity(intent);
                    }
                }
            };
            txtUnlock.setOnClickListener(onClickListener);
            txtMember.setOnClickListener(onClickListener);
            txtEdit.setOnClickListener(onClickListener);
            layoutInfo.setOnClickListener(onClickListener);

            return convertView;
        }

    }

}

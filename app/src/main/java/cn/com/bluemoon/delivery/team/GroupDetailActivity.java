package cn.com.bluemoon.delivery.team;

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
import cn.com.bluemoon.lib.view.CommonProgressDialog;

public class GroupDetailActivity extends KJActivity {

    private String TAG = "GroupDetail";
    private String bpCode;
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
    private boolean isRefresh;
    private long timestamp;
    private List<GroupDetail> items;
    private GroupDetailAdapter groupDetailAdapter;

    @Override
    public void setRootView() {
        initCustomActionBar();
        setContentView(R.layout.activity_group_detail);
    }

    @Override
    public void initWidget() {
        super.initWidget();
        progressDialog = new CommonProgressDialog(aty);
        if(getIntent().hasExtra("code")){
            bpCode = getIntent().getStringExtra("code");
        }
//        PublicUtil.setEmptyView(listviewDetail,getString(R.string.team_group_empty_detail),R.mipmap.team_empty_group);
        listviewDetail.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
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
                isRefresh = true;
                pullDown = false;
                getData();
            }
        });
        pullDown = false;
        pullUp = false;
        isRefresh = false;
        getData();

    }

    private void getData() {
        if (!pullUp) {
            timestamp = 0;
        }
        if (!pullUp && !pullDown && progressDialog != null) {
            progressDialog.show();
        }
        DeliveryApi.getGroupDetailInfo(bpCode, AppContext.PAGE_SIZE, timestamp, ClientStateManager.getLoginToken(aty), Constants.RELTYPE_GROUP, getGroupDetailInfoHandler);
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
        if (isRefresh) {
            groupDetailAdapter.notifyDataSetChanged();
        } else {
            listviewDetail.setAdapter(groupDetailAdapter);
        }
        pullDown = false;
        pullUp = false;
        isRefresh = false;
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
                ResultGroupDetailInfo groupDetailInfoResult = JSON.parseObject(responseString, ResultGroupDetailInfo.class);

                if (groupDetailInfoResult.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    bpCode = groupDetailInfoResult.getBpCode();
                    timestamp = groupDetailInfoResult.getTimestamp();
                    txtTitle.setText(PublicUtil.getStringParams(groupDetailInfoResult.getBpCode(), groupDetailInfoResult.getBpName()));
                    txtTotal.setText(String.format(getString(R.string.team_group_detail_total_num),groupDetailInfoResult.getActualTotalPopulation(),groupDetailInfoResult.getPlanTotalPopulation()));
                    txtFull.setText(String.format(getString(R.string.team_group_detail_full_num), groupDetailInfoResult.getFullTimeNumber()));
                    txtPart.setText(String.format(getString(R.string.team_group_detail_part_num), groupDetailInfoResult.getPartTimeNumber()));
                    setData(groupDetailInfoResult.getItemList());
                } else {
                    PublicUtil.showErrorMsg(aty, groupDetailInfoResult);
                    pullDown = false;
                    pullUp = false;
                    isRefresh = false;
                }
            } catch (Exception e) {
                LogUtils.e(TAG, e.getMessage());
                PublicUtil.showToastServerBusy();
                pullDown = false;
                pullUp = false;
                isRefresh = false;
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
            pullDown = false;
            pullUp = false;
            isRefresh = false;
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

    private void unlock(final GroupDetail item){
        ChooseDateWindow popupWindow = new ChooseDateWindow(aty,PublicUtil.getStringParams(
                item.getBpCode(),item.getBpName()), new ChooseDateWindow.ChooseDateListener() {
            @Override
            public void callBack(long endTime) {
                if(progressDialog!=null) progressDialog.show();
                DeliveryApi.deleteRelationShip(bpCode,item.getBpCode(),endTime,
                        ClientStateManager.getLoginToken(aty),Constants.RELTYPE_GROUP,deleteRelationShipHandler);
            }

        });
        popupWindow.showPopwindow(popStart);
    }

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
                    DeliveryApi.getEmpList(ClientStateManager.getLoginToken(aty), resultStr, getEmpListHandler);
                    break;
                case 4:
                    Intent intent2 = new Intent(aty,SelectEmpActivity.class);
                    startActivityForResult(intent2, 1);
                    break;
            }
        }else if(requestCode == 1&&resultCode == RESULT_OK
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
        Intent intent = new Intent(aty,RelationInfoActivity.class);
        intent.putExtra("emp", emp);
        intent.putExtra("type",type);
        intent.putExtra("relType",Constants.RELTYPE_GROUP);
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
                            openRelationInfo(emp,Constants.TYPE_ADD);
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
        public View getView(int position, View convertView, ViewGroup parent) {

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
                txtJob.setVisibility(View.VISIBLE);
            }else{
                txtJob.setVisibility(View.GONE);
            }
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

            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(PublicUtil.isFastDoubleClick(1000)) return;
                    if(v == txtUnlock){
                        unlock(item);
                    }else if(v == txtMember){
                        Emp emp = new Emp();
                        emp.setBpCode(bpCode);
                        emp.setEmpCode(item.getBpCode());
                        emp.setEmpName(item.getBpName());
                        emp.setMobileNo(item.getMobileNo());
                        Intent intent = new Intent(aty,PersonnelAreaActivity.class);
                        intent.putExtra("emp",emp);
                        startActivity(intent);
                    }else if(v == txtEdit){
                        Emp emp = new Emp();
                        emp.setBpCode(bpCode);
                        emp.setEmpCode(item.getBpCode());
                        emp.setEmpName(item.getBpName());
                        emp.setMobileNo(item.getMobileNo());
                        openRelationInfo(emp, Constants.TYPE_UPDATE);
                    }else if(v == layoutInfo){
                        Intent intent = new Intent(aty,RelationShipDetailActivity.class);
                        intent.putExtra("bpCode",bpCode);
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

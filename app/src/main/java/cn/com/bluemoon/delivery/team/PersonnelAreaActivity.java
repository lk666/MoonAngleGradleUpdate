package cn.com.bluemoon.delivery.team;

import android.content.DialogInterface;
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
import cn.com.bluemoon.delivery.app.api.model.team.PersonnelArea;
import cn.com.bluemoon.delivery.app.api.model.team.ResultPersonnelAreaList;
import cn.com.bluemoon.delivery.async.listener.IActionBarListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.delivery.utils.ViewHolder;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib.utils.LibViewUtil;
import cn.com.bluemoon.lib.view.CommonAlertDialog;
import cn.com.bluemoon.lib.view.CommonEmptyView;
import cn.com.bluemoon.lib.view.CommonProgressDialog;

public class PersonnelAreaActivity extends KJActivity {

    private String TAG = "PersonnelAreaActivity";
    private String groupCode;
    private String empCode;
    @BindView(id = R.id.txt_name)
    private TextView txtName;
    @BindView(id = R.id.txt_num)
    private TextView txtNum;
    @BindView(id = R.id.listview_person_area)
    private PullToRefreshListView listviewArea;
    private CommonProgressDialog progressDialog;
    private boolean pullUp;
    private boolean pullDown;
    private long timestamp;
    private List<PersonnelArea> items;
    private PersonAreaAdapter personAreaAdapter;
    private CommonEmptyView emptyView;

    @Override
    public void setRootView() {
        initCustomActionBar();
        setContentView(R.layout.activity_person_area);
    }

    @Override
    public void initWidget() {
        super.initWidget();
        progressDialog = new CommonProgressDialog(aty);
        progressDialog.setCancelable(false);
        if (getIntent()!=null) {
            groupCode = getIntent().getStringExtra("bpCode");
            empCode = getIntent().getStringExtra("empCode");
        }
        if(StringUtil.isEmpty(groupCode)||StringUtil.isEmpty(empCode)){
            PublicUtil.showToastErrorData();
            finish();
            return;
        }
        emptyView = PublicUtil.setEmptyView(listviewArea, getString(R.string.empty_hint,
                getString(R.string.team_group_detail_member)), new CommonEmptyView.EmptyListener() {
            @Override
            public void onRefresh() {
                pullDown = false;
                pullUp = false;
                getData();
            }
        });
        listviewArea.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
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
        pullUp = false;
        pullDown = false;
        getData();

    }

    private void getData() {
        if (!pullUp) {
            timestamp = 0;
        }
        if (!pullUp && !pullDown && progressDialog != null) {
            progressDialog.show();
        }
        DeliveryApi.getPersonnelAreaList(groupCode,empCode, AppContext.PAGE_SIZE, timestamp,
                ClientStateManager.getLoginToken(aty), getPersonnelAreaListHandler);
    }

    private void setData(List<PersonnelArea> list) {
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
        if (personAreaAdapter == null) {
            personAreaAdapter = new PersonAreaAdapter();
            personAreaAdapter.setList(items);
            listviewArea.setAdapter(personAreaAdapter);
        } else {
            personAreaAdapter.setList(items);
            if (pullDown) {
                listviewArea.setAdapter(personAreaAdapter);
            } else {
                personAreaAdapter.notifyDataSetChanged();
            }
        }

    }

    private void delete(final String bpCode) {
        CommonAlertDialog.Builder dialog = new CommonAlertDialog.Builder(aty);
        dialog.setMessage(getString(R.string.team_area_delete_content));
        dialog.setNegativeButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (progressDialog != null) progressDialog.show();
                DeliveryApi.deletePersonnelArea(ClientStateManager.getLoginToken(aty), bpCode, empCode,groupCode, deletePersonAreaHandler);
            }
        });
        dialog.setPositiveButton(R.string.btn_cancel, null);
        dialog.show();
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
                Intent intent = new Intent(aty, SelectAreaActivity.class);
                intent.putExtra("bpCode", groupCode);
                intent.putExtra("empCode", empCode);
                startActivityForResult(intent, 1);
            }

            @Override
            public void onBtnLeft(View v) {
                finish();
            }

            @Override
            public void setTitle(TextView v) {
                v.setText(getText(R.string.team_group_detail_member));
            }

        });

        actionBar.getImgRightView().setImageResource(R.mipmap.team_add);
        actionBar.getImgRightView().setVisibility(View.VISIBLE);
    }


    AsyncHttpResponseHandler getPersonnelAreaListHandler = new TextHttpResponseHandler(HTTP.UTF_8) {
        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            LogUtils.d(TAG, "getPersonnelAreaListHandler result = " + responseString);
            if (progressDialog != null)
                progressDialog.dismiss();
            listviewArea.onRefreshComplete();
            try {
                ResultPersonnelAreaList result = JSON.parseObject(responseString, ResultPersonnelAreaList.class);

                if (result.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    timestamp = result.getTimestamp();
                    txtName.setText(PublicUtil.getStringParams(result.getEmpCode(), result.getEmpName()));
                    txtNum.setText(getString(R.string.team_area_total_num, result.getTotalFamily()));
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
            listviewArea.onRefreshComplete();
            PublicUtil.showToastServerOvertime();
            LibViewUtil.setViewVisibility(emptyView, View.VISIBLE);
        }
    };

    AsyncHttpResponseHandler deletePersonAreaHandler = new TextHttpResponseHandler(HTTP.UTF_8) {
        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            LogUtils.d(TAG, "deletePersonAreaHandler result = " + responseString);
            if (progressDialog != null)
                progressDialog.dismiss();
            try {
                ResultBase baseResult = JSON.parseObject(responseString, ResultBase.class);
                if (baseResult.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    PublicUtil.showToast(baseResult.getResponseMsg());
                    pullUp = false;
                    pullDown = false;
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

        if (requestCode == 1 && resultCode == RESULT_OK) {
            pullUp = false;
            pullDown = false;
            getData();
        }
    }


    class PersonAreaAdapter extends BaseAdapter {

        private LayoutInflater mInflater;
        private List<PersonnelArea> list;

        public PersonAreaAdapter() {
            this.mInflater = LayoutInflater.from(aty);
        }

        public void setList(List<PersonnelArea> list) {
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
                convertView = mInflater.inflate(R.layout.item_person_area, null);
            }
            LinearLayout layoutArea = ViewHolder.get(convertView, R.id.layout_area);
            TextView txtName = ViewHolder.get(convertView, R.id.txt_name);
            TextView txtNum = ViewHolder.get(convertView, R.id.txt_num);
            TextView txtAreaName = ViewHolder.get(convertView, R.id.txt_area_name);
            TextView txtAddress = ViewHolder.get(convertView, R.id.txt_address);
            TextView txtDate = ViewHolder.get(convertView, R.id.txt_date);
            final TextView txtDelete = ViewHolder.get(convertView, R.id.txt_delete);
            final PersonnelArea item = list.get(position);
            String name = PublicUtil.getStringParams(item.getBpCode(), item.getBpName());
            if (!StringUtils.isEmpty(item.getBpCode1())) {
                layoutArea.setVisibility(View.VISIBLE);
                txtAreaName.setText(PublicUtil.getStringParams(item.getBpCode1(), item.getBpName1()));
                name = PublicUtil.getStringParams(name, item.getYuanGarden(), item.getBalcony());
            } else {
                layoutArea.setVisibility(View.GONE);
            }
            txtNum.setText(getString(R.string.team_area_num, item.getTotalRooms()));
            txtName.setText(name);
            txtAddress.setText(item.getProvinceName() + item.getCityName() + item.getCountyName()
                    + item.getStreetName() + item.getVillageName());
            txtDate.setText(getString(R.string.team_area_add_date,
                    DateUtil.getTime(item.getStartDate())));
            txtDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delete(item.getBpCode());
                }
            });

            return convertView;
        }

    }

}

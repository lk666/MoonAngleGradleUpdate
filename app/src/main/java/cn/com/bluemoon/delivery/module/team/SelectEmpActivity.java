package cn.com.bluemoon.delivery.module.team;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
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

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.team.Emp;
import cn.com.bluemoon.delivery.app.api.model.team.ResultEmpList;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.module.base.interf.IActionBarListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.delivery.utils.ViewHolder;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib.utils.LibViewUtil;
import cn.com.bluemoon.lib.view.CommonEmptyView;
import cn.com.bluemoon.lib.view.CommonProgressDialog;
import cn.com.bluemoon.lib.view.CommonSearchView;

public class SelectEmpActivity extends Activity implements CommonSearchView.SearchViewListener {

    private String TAG = "SelectEmpActivity";
    private SelectEmpActivity aty;
    private CommonSearchView searchView;
    private PullToRefreshListView listview;
    private Button btnOk;
    private SelectMemberAdapter adapter;
    private CommonProgressDialog progressDialog;
    private Emp emp;
    private CommonEmptyView emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initCustomActionBar();
        setContentView(R.layout.activity_select_emp);
        aty = this;
        listview = (PullToRefreshListView) findViewById(R.id.listview_select_member);
        btnOk = (Button) findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(onClickListener);
        progressDialog = new CommonProgressDialog(aty);
        emptyView = PublicUtil.setEmptyView(listview, null, new CommonEmptyView.EmptyListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });
        LibViewUtil.setViewVisibility(emptyView, View.VISIBLE);
        searchView = (CommonSearchView) findViewById(R.id.searchview_select_member);
        searchView.setSearchViewListener(this);
        searchView.setSearchEmpty(false);
        searchView.setListHistory(ClientStateManager.getHistory(ClientStateManager.HISTORY_SELECT_MEMBER));
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (PublicUtil.isFastDoubleClick(1000)) {
                return;
            }
            if (v == btnOk) {
                if (emp != null) {
                    Intent intent = new Intent();
                    intent.putExtra("emp", emp);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    PublicUtil.showToast(getString(R.string.team_group_select_member_tips));
                }
            }
        }
    };

    private void getData() {
        if (StringUtils.isEmpty(searchView.getText())) {
            PublicUtil.showToast(getString(R.string.search_cannot_empty));
            return;
        }
        if (progressDialog != null) progressDialog.show();
        DeliveryApi.getEmpList(ClientStateManager.getLoginToken(aty), searchView.getText(), Constants.TYPE_INPUT, getEmpListHandler);
    }

    private void setData(List<Emp> list) {
        this.emp = null;
        adapter = new SelectMemberAdapter(aty);
        adapter.setList(list);
        listview.setAdapter(adapter);
    }

    CommonSearchView.SearchViewListener searchViewListener = new CommonSearchView.SearchViewListener() {
        @Override
        public void onSearch(CommonSearchView view,String str) {
            getData();
            searchView.hideHistoryView();
        }

        @Override
        public void onCancel(CommonSearchView view) {
            searchView.hideHistoryView();
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
                v.setText(getText(R.string.team_group_select_member));
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
            ClientStateManager.setHistory(searchView.getListHistory(), ClientStateManager.HISTORY_SELECT_MEMBER);
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
                    setData(empListResult.getItemList());
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
            LibViewUtil.setViewVisibility(emptyView, View.VISIBLE);
            LibViewUtil.setViewVisibility(btnOk, View.GONE);
        }
    };

    @Override
    public void onSearch(CommonSearchView view, String str) {
        getData();
    }

    @Override
    public void onCancel(CommonSearchView view) {

    }

    class SelectMemberAdapter extends BaseAdapter {

        private LayoutInflater mInflater;
        private List<Emp> list;

        public SelectMemberAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        public void setList(List<Emp> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            if (list == null) {
                list = new ArrayList<>();
            }
            if (list.size() > 0) {
                LibViewUtil.setViewVisibility(btnOk, View.VISIBLE);
            } else {
                LibViewUtil.setViewVisibility(btnOk, View.GONE);
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
                convertView = mInflater.inflate(R.layout.item_select_member, null);
            }
            final CheckBox checkBox = ViewHolder.get(convertView, R.id.checkbox);
            final TextView txtName = ViewHolder.get(convertView, R.id.txt_name);
            final TextView txtContent = ViewHolder.get(convertView, R.id.txt_content);
            final Emp item = list.get(position);
            txtName.setText(StringUtil.getStringParams(item.getEmpCode(), item.getEmpName()));
            if (StringUtils.isEmpty(item.getBpCode())) {
                txtName.setTextColor(getResources().getColor(R.color.text_black));
                txtContent.setVisibility(View.GONE);
                checkBox.setButtonDrawable(R.drawable.checkbox1);
                convertView.setBackgroundResource(R.drawable.btn_white);
            } else {
                item.setIsCheck(false);
                txtName.setTextColor(getResources().getColor(R.color.text_grep));
                txtContent.setVisibility(View.VISIBLE);
                checkBox.setButtonDrawable(R.mipmap.checkbox_disable);
                convertView.setBackgroundColor(getResources().getColor(R.color.view_bg));
                txtContent.setText(StringUtil.getStringParams(item.getBpCode(), item.getBpName()));
            }
            if (item.isCheck()) {
                checkBox.setChecked(true);
            } else {
                checkBox.setChecked(false);
            }

            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!StringUtils.isEmpty(item.getBpCode())) {
                        return;
                    }
                    if (v == checkBox && !checkBox.isChecked()) {
                        checkBox.setChecked(true);
                    }
                    for (int i = 0; i < list.size(); i++) {
                        Emp emp = list.get(i);
                        emp.setIsCheck(i == position);
                        list.set(i, emp);
                    }
                    emp = item;
                    notifyDataSetChanged();

                }
            };
            checkBox.setOnClickListener(listener);
            convertView.setOnClickListener(listener);

            return convertView;
        }
    }
}
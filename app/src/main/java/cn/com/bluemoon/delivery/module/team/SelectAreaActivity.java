package cn.com.bluemoon.delivery.module.team;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import cz.msebera.android.httpclient.Header;
import org.apache.http.protocol.HTTP;
import org.kymjs.kjframe.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import cn.com.bluemoon.delivery.AppContext;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.team.ResultServiceAreaList;
import cn.com.bluemoon.delivery.app.api.model.team.ServiceArea;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.module.base.interf.IActionBarListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.delivery.utils.ViewHolder;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib.utils.LibViewUtil;
import cn.com.bluemoon.lib.view.CommonEmptyView;
import cn.com.bluemoon.lib.view.CommonProgressDialog;
import cn.com.bluemoon.lib.view.CommonSearchView;

public class SelectAreaActivity extends Activity implements CommonSearchView.SearchViewListener {

    private String TAG = "SelectAreaActivity";
    private SelectAreaActivity aty;
    private CommonSearchView searchView;
    private PullToRefreshListView listview;
    private Button btnOk;
    private TextView txtTotalNum;
    private LinearLayout layoutBottom;
    private TextView txtSelect;
    private CheckBox cb;
    private SelectAreaAdapter adapter;
    private CommonProgressDialog progressDialog;
    private List<String> listSelected;
    private List<ServiceArea> items;
    private int pageNext;
    private int pageMax = 1;
    private boolean pullUp;
    private boolean pullDown;
    private String bpCode;
    private String empCode;
    private boolean unRefresh;
    private CommonEmptyView emptyView;
    private String content = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initCustomActionBar();
        setContentView(R.layout.activity_select_area);
        aty = this;
        if (getIntent() == null) {
            return;
        }
        bpCode = getIntent().getStringExtra("bpCode");
        empCode = getIntent().getStringExtra("empCode");
        listSelected = new ArrayList<>();
        progressDialog = new CommonProgressDialog(aty);
        progressDialog.setCancelable(false);
        listview = (PullToRefreshListView) findViewById(R.id.listview_select_area);
        btnOk = (Button) findViewById(R.id.btn_ok);
        txtTotalNum = (TextView) findViewById(R.id.txt_total_num);
        layoutBottom = (LinearLayout) findViewById(R.id.layout_bottom);
        txtSelect = (TextView) findViewById(R.id.txt_select);
        cb = (CheckBox) findViewById(R.id.checkbox);
        btnOk.setOnClickListener(onClickListener);
        txtSelect.setOnClickListener(onClickListener);
        emptyView = PublicUtil.setEmptyView(listview, null, new CommonEmptyView.EmptyListener() {
            @Override
            public void onRefresh() {
                pullUp = false;
                pullDown = false;
                getData();
            }
        });
        searchView = (CommonSearchView) findViewById(R.id.searchview_select_area);
        searchView.setSearchViewListener(this);
        searchView.setListHistory(ClientStateManager.getHistory(ClientStateManager.HISTORY_SELECT_AREA));

        listview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                pullUp = false;
                pullDown = true;
                getData();

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                pullUp = true;
                pullDown = false;
                getData();
            }
        });
        listview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (view.getLastVisiblePosition() == view.getCount() - 1) {
                    if (layoutBottom.getVisibility() == View.VISIBLE) {
                        layoutBottom.setVisibility(View.GONE);
                    }
                } else {
                    if (layoutBottom.getVisibility() == View.GONE) {
                        layoutBottom.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (unRefresh) {
                    unRefresh = false;
                    return;
                }
                for (int i = 0; i < items.size(); i++) {
                    items.get(i).setIsCheck(isChecked);
                }
                if (adapter != null) {
                    adapter.setList(items);
                    adapter.notifyDataSetChanged();
                    setTotalNum(items);
                }
            }
        });

        pullDown = false;
        pullUp = false;
        getData();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (PublicUtil.isFastDoubleClick(1000)) {
                return;
            }
            if (v == txtSelect) {
                cb.setChecked(!cb.isChecked());
            } else if (v == btnOk) {
                if (listSelected.size() == 0) {
                    PublicUtil.showToast(getString(R.string.team_area_select_empty));
                    return;
                }
                if (progressDialog != null) {
                    progressDialog.show();
                }
                DeliveryApi.addServiceArea(bpCode, listSelected, empCode, ClientStateManager.getLoginToken(aty), addServiceAreaHandler);
            }
        }
    };

    private void getData() {

        if (!pullUp) {
            pageNext = 0;
            content = searchView.getText();
        }

        if (!pullUp && !pullDown && progressDialog != null) {
            progressDialog.show();
        }
        DeliveryApi.getServiceAreaList(ClientStateManager.getLoginToken(aty),
                bpCode, empCode, content, pageNext, AppContext.PAGE_SIZE, getServiceAreaListHandler);
    }


    private void setData(List<ServiceArea> list) {
        if (items == null) {
            items = new ArrayList<>();
        }
        if (pullUp && (pageNext >= pageMax || list == null || list.size() == 0)) {
            PublicUtil.showToast(R.string.card_no_list_data);
            return;
        }
        if (pullUp) {
            items.addAll(list);
        } else {
            items = list;
        }
        pageNext++;
        if (adapter == null) {
            adapter = new SelectAreaAdapter(aty);
        }
        adapter.setList(items);
        if (pullUp) {
            adapter.notifyDataSetChanged();
        } else {
            listview.setAdapter(adapter);
        }
        setTotalNum(items);
    }

    CommonSearchView.SearchViewListener searchViewListener = new CommonSearchView.SearchViewListener() {
        @Override
        public void onSearch(CommonSearchView view,String str) {
            content = str;
            pullDown = false;
            pullUp = false;
            getData();
            searchView.hideHistoryView();
        }

        @Override
        public void onCancel(CommonSearchView view) {
            searchView.hideHistoryView();
        }

    };

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
        new CommonActionBar(this.getActionBar(), new IActionBarListener() {

            @Override
            public void onBtnRight(View v) {

            }

            @Override
            public void onBtnLeft(View v) {
                finish();
            }

            @Override
            public void setTitle(TextView v) {
                v.setText(getText(R.string.team_area_select_title));
            }

        });

    }

    @Override
    public void onStop() {
        super.onStop();
        if (searchView != null)
            ClientStateManager.setHistory(searchView.getListHistory(), ClientStateManager.HISTORY_SELECT_AREA);
    }

    AsyncHttpResponseHandler getServiceAreaListHandler = new TextHttpResponseHandler(HTTP.UTF_8) {
        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            LogUtils.d(TAG, "getServiceAreaListHandler result = " + responseString);
            if (progressDialog != null)
                progressDialog.dismiss();
            listview.onRefreshComplete();
            try {
                ResultServiceAreaList result = JSON.parseObject(responseString, ResultServiceAreaList.class);
                if (result.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    setData(result.getItemList());
                    pageMax = result.getTotal() / AppContext.PAGE_SIZE;
                    if (result.getTotal() % AppContext.PAGE_SIZE > 0) {
                        pageMax++;
                    }
                } else {
                    PublicUtil.showErrorMsg(aty, result);
                    LibViewUtil.setViewVisibility(emptyView, View.VISIBLE);
                }
            } catch (Exception e) {
                LogUtils.e(TAG, e.getMessage());
                PublicUtil.showToastServerBusy();
                LibViewUtil.setViewVisibility(emptyView, View.VISIBLE);
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
            LibViewUtil.setViewVisibility(emptyView, View.VISIBLE);
        }
    };

    AsyncHttpResponseHandler addServiceAreaHandler = new TextHttpResponseHandler(HTTP.UTF_8) {
        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            LogUtils.d(TAG, "addServiceAreaHandler result = " + responseString);
            if (progressDialog != null)
                progressDialog.dismiss();
            try {
                ResultBase result = JSON.parseObject(responseString, ResultBase.class);
                if (result.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    PublicUtil.showToast(result.getResponseMsg());
                    setResult(RESULT_OK);
                    finish();
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
            PublicUtil.showToastServerOvertime();
        }
    };

    private void setTotalNum(List<ServiceArea> list) {
        int total = 0;
        boolean isAll = true;
        listSelected.clear();
        for (ServiceArea area : list) {
            if (area.isCheck()) {
                total += area.getTotalFamily();
                listSelected.add(area.getBpCode());
            } else {
                isAll = false;
            }
        }
        txtTotalNum.setText(String.format(getString(R.string.team_area_total_num), total));
        if (isAll && list.size() > 0) {
            if (!cb.isChecked()) {
                unRefresh = true;
                cb.setChecked(true);
            }
        } else {
            if (cb.isChecked()) {
                unRefresh = true;
                cb.setChecked(false);
            }
        }
    }

    @Override
    public void onSearch(CommonSearchView view, String str) {
        pullDown = false;
        pullUp = false;
        getData();
    }

    @Override
    public void onCancel(CommonSearchView view) {

    }

    class SelectAreaAdapter extends BaseAdapter {

        private LayoutInflater mInflater;
        private List<ServiceArea> list;

        public SelectAreaAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        public void setList(List<ServiceArea> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            if (list == null) {
                list = new ArrayList<>();
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
                convertView = mInflater.inflate(R.layout.item_select_area, null);
            }
            final CheckBox checkBox = ViewHolder.get(convertView, R.id.checkbox);
            LinearLayout layoutArea = ViewHolder.get(convertView, R.id.layout_area);
            TextView txtName = ViewHolder.get(convertView, R.id.txt_name);
            TextView txtNum = ViewHolder.get(convertView, R.id.txt_num);
            TextView txtAreaName = ViewHolder.get(convertView, R.id.txt_area_name);
            TextView txtAddress = ViewHolder.get(convertView, R.id.txt_address);
            final ServiceArea item = list.get(position);
            String name = StringUtil.getStringParams(item.getBpCode(), item.getBpName());
            if (!StringUtils.isEmpty(item.getBpCode1())) {
                layoutArea.setVisibility(View.VISIBLE);
                txtAreaName.setText(StringUtil.getStringParams(item.getBpCode1(), item.getBpName()));
                name = StringUtil.getStringParams(name, item.getYuanGarden(), item.getBalcony());
            } else {
                layoutArea.setVisibility(View.GONE);
            }
            txtNum.setText(String.format(getString(R.string.team_area_num), item.getTotalFamily()));
            txtName.setText(name);
            txtAddress.setText(String.format("%s%s%s%s%s", item.getProvinceName(), item
                    .getCityName(), item.getCountyName(), item.getVillageName(), item
                    .getStreetName()));
            if (item.isCheck() && !checkBox.isChecked()) {
                checkBox.setChecked(true);
            } else if (!item.isCheck() && checkBox.isChecked()) {
                checkBox.setChecked(false);
            }

            View.OnClickListener clickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkBox.setChecked(!list.get(position).isCheck());
                    if (layoutBottom.getVisibility() == View.GONE) {
                        layoutBottom.setVisibility(View.VISIBLE);
                    }
                    list.get(position).setIsCheck(!list.get(position).isCheck());
                    setTotalNum(list);
//                    notifyDataSetChanged();
                }
            };

            checkBox.setOnClickListener(clickListener);
            convertView.setOnClickListener(clickListener);

            return convertView;
        }
    }


}

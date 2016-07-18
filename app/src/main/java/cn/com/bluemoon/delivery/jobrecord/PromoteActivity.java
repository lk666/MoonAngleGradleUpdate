package cn.com.bluemoon.delivery.jobrecord;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

import cn.com.bluemoon.delivery.ClientStateManager;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.jobrecord.PromoteInfo;
import cn.com.bluemoon.delivery.app.api.model.jobrecord.ResultPromoteList;
import cn.com.bluemoon.delivery.async.listener.IActionBarListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.delivery.utils.ViewHolder;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib.view.CommonEmptyView;
import cn.com.bluemoon.lib.view.CommonProgressDialog;
import cn.com.bluemoon.lib.view.CommonSearchView;

/**
 * Created by LIANGJIANGLI on 2016/6/22.
 */
public class PromoteActivity extends Activity implements CommonSearchView.SearchViewListener{

    private String TAG = "PromoteActivity";
    private CommonProgressDialog progressDialog;
    private List<ResultPromoteList.Item> items = new ArrayList<>();
    private PullToRefreshListView listview;
    private PromoteAdapter adapter;
    private CommonSearchView searchView;
    private long timestamp;
    private boolean isPullUp;
    private boolean isPullDown;
    private String searchKey = "";
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promote);
        initCustomActionBar();
        listview = (PullToRefreshListView) findViewById(R.id.listview_promote);
        adapter = new PromoteAdapter(this);
        listview.setAdapter(adapter);
        PublicUtil.setEmptyView(listview, null, new CommonEmptyView.EmptyListener() {
            @Override
            public void onRefresh() {
                isPullDown = false;
                isPullUp = false;
                getList();
            }
        });
        searchView = (CommonSearchView) findViewById(R.id.search_view);
        progressDialog = new CommonProgressDialog(this);
        searchView.setSearchViewListener(this);
        searchView.setListHistory(ClientStateManager.getHistory(ClientStateManager.PROMOTE_KEY));

        listview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                isPullDown = true;
                isPullUp = false;
                getList();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                isPullDown = false;
                isPullUp = true;
                getList();
            }
        });
        getList();
    }

    private void getList(){
        if(!isPullUp){
            timestamp = 0;
        }
        if(!isPullUp&&!isPullDown&&progressDialog!=null){
            progressDialog.show();
        }
        DeliveryApi.getPromoteList(ClientStateManager.getLoginToken(this), searchKey, timestamp, getPromoteListHandler);
    }

    private void setData(List<ResultPromoteList.Item> itemList){

        if(itemList == null||itemList.size()==0){
            if(isPullUp){
                PublicUtil.showToast(R.string.card_no_list_data);
                return;
            } else {
                items.clear();
            }
        }else{
            if(isPullUp){
                items.addAll(itemList);
            }else{
                items.clear();
                items.addAll(itemList);
            }
        }
        isPullUp = false;
        isPullDown = false;
        adapter.notifyDataSetChanged();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ClientStateManager.setHistory(searchView.getListHistory(),ClientStateManager.PROMOTE_KEY);
    }

    AsyncHttpResponseHandler getPromoteListHandler = new TextHttpResponseHandler() {

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                              String responseString) {
            LogUtils.d("test", "getPromoteListHandler result = " + responseString);
            progressDialog.dismiss();
            try {
                ResultPromoteList result = JSON.parseObject(responseString,
                        ResultPromoteList.class);
                if (result.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    timestamp = result.getTimestamp();
                    setData(result.getItemList());
                } else {
                    PublicUtil.showErrorMsg(PromoteActivity.this, result);
                }
            } catch (Exception e) {
                PublicUtil.showToastServerBusy();
            } finally {
                listview.onRefreshComplete();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers,
                              String responseString, Throwable throwable) {
            LogUtils.d("test", "getPromoteListHandler result failed. statusCode="
                    + statusCode);
            progressDialog.dismiss();
            listview.onRefreshComplete();
            PublicUtil.showToastServerOvertime();
        }
    };

    @Override
    public void onSearch(CommonSearchView view, String str) {
		isPullDown = false;
        isPullUp = false;
        searchKey = str;
        DeliveryApi.getPromoteList(ClientStateManager.getLoginToken(PromoteActivity.this), searchKey, 0, getPromoteListHandler);
    }

    @Override
    public void onCancel(CommonSearchView view) {
        searchKey = "";
    }

    class PromoteAdapter extends BaseAdapter {

        private Context context;
        public PromoteAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_promote, null);
            final ResultPromoteList.Item item = items.get(position);
            TextView txtCommunity = ViewHolder.get(convertView, R.id.txt_community);
            TextView txtCommunity2 = ViewHolder.get(convertView, R.id.txt_community2);
            TextView txtWorkDate = ViewHolder.get(convertView, R.id.txt_work_date);
            final TextView txtHolidayDate = (TextView)convertView.findViewById(R.id.txt_holiday_date);
            final TextView txtHolidayDate2 = (TextView)convertView.findViewById(R.id.txt_holiday_date2);
            TextView txtOutdoorArea = ViewHolder.get(convertView, R.id.txt_outdoor_area);
            TextView txtEdit = ViewHolder.get(convertView, R.id.txt_edit);
            TextView txtPlaceType = ViewHolder.get(convertView, R.id.txt_place_type);
            LinearLayout layoutCommunity = ViewHolder.get(convertView, R.id.layout_community);
            txtCommunity.setText(String.format(getString(R.string.promote_append),item.getBpCode(), item.getBpName()));
            txtCommunity2.setText(String.format(getString(R.string.promote_append),item.getBpCode1(), item.getBpName1()));
            txtWorkDate.setText(String.format(getString(R.string.promote_work_date), StringUtil.formatPrice(item.getWorkPrice())));
            txtPlaceType.setText(item.getSiteTypeName());

            txtHolidayDate.setText(String.format(getString(R.string.promote_holiday_date),  StringUtil.formatPrice(item.getHolidayPrice())));
            txtHolidayDate2.setText(String.format(getString(R.string.promote_holiday_date),  StringUtil.formatPrice(item.getHolidayPrice())));

            ViewTreeObserver vto = txtHolidayDate.getViewTreeObserver();
            vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                private boolean isFirst;
                @Override
                public boolean onPreDraw() {
                    int lineCount = txtHolidayDate.getLineCount();
                    if (!isFirst) {
                        isFirst = true;
                        if (lineCount > 1) {
                            txtHolidayDate.setVisibility(View.GONE);
                            txtHolidayDate2.setVisibility(View.VISIBLE);
                        } else {
                            txtHolidayDate2.setVisibility(View.GONE);
                            txtHolidayDate.setVisibility(View.VISIBLE);
                        }
                    }
                    return true;
                }
            });

            txtOutdoorArea.setText(String.format(getString(R.string.promote_outdoor_area), StringUtil.formatArea(item.getUseArea())));
            txtEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    index = position;
                    Intent intent = new Intent(PromoteActivity.this, AddPromoteActivity.class);
                    intent.putExtra("bpCode", item.getBpCode());
                    intent.putExtra("isEdit", true);
                    startActivityForResult(intent, 2);
                }
            });
            layoutCommunity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PromoteActivity.this, PromoteDetailActivity.class);
                    intent.putExtra("bpCode", item.getBpCode());
                    startActivityForResult(intent, 1);
                }
            });
            return convertView;
        }
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
    }

    public void onPause() {
        super.onPause();

        MobclickAgent.onPageEnd(TAG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            if (requestCode == 1) {
                progressDialog.show();
                searchView.setText("");
                searchView.setFocus(false);
                searchView.hideHistoryView();
                searchKey = "";
                timestamp = 0;
                DeliveryApi.getPromoteList(ClientStateManager.getLoginToken(this), searchKey, timestamp, getPromoteListHandler);
            } else if (requestCode == 2) {
                if (data != null) {
                    PromoteInfo info = (PromoteInfo)data.getSerializableExtra("promote");
                    ResultPromoteList.Item item = items.get(index);
                    item.setWorkPrice(info.getWorkPrice());
                    item.setHolidayPrice(info.getHolidayPrice());
                    if ("inDoor".equals(info.getSiteType())) {
                        item.setSiteTypeName(getString(R.string.add_promote_indoor));
                    } else {
                        item.setSiteTypeName(getString(R.string.add_promote_outdoor));
                    }
                    item.setUseArea(info.getUseArea());
                    items.set(index, item);
                    adapter.notifyDataSetChanged();
                }
            }

        }
    }

    private void initCustomActionBar() {
        new CommonActionBar(getActionBar(), new IActionBarListener() {

            @Override
            public void setTitle(TextView v) {
                // TODO Auto-generated method stub
                v.setText(getResources().getString(R.string.promote_record_title));
            }

            @Override
            public void onBtnRight(View v) {
                Intent intent = new Intent(PromoteActivity.this, AddPromoteActivity.class);
                startActivityForResult(intent, 1);
            }

            @Override
            public void onBtnLeft(View v) {
                finish();
            }
        });
        ImageView right = (ImageView)this.findViewById(R.id.img_right);
        right.setImageResource(R.mipmap.ic_promote_add);
        right.setVisibility(View.VISIBLE);
    }
}

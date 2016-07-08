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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.protocol.HTTP;

import java.util.List;

import cn.com.bluemoon.delivery.ClientStateManager;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultOrderVo;
import cn.com.bluemoon.delivery.app.api.model.jobrecord.ResultPromoteList;
import cn.com.bluemoon.delivery.async.listener.IActionBarListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.delivery.utils.ViewHolder;
import cn.com.bluemoon.lib.view.CommonProgressDialog;
import cn.com.bluemoon.lib.view.CommonSearchView;

/**
 * Created by LIANGJIANGLI on 2016/6/22.
 */
public class PromoteActivity extends Activity{

    private String TAG = "PromoteActivity";
    private CommonProgressDialog progressDialog;
    private List<ResultPromoteList.Item> items;
    private ListView listview;
    private CommonSearchView searchView;
    private long timestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promote);
        initCustomActionBar();
        listview = (ListView) findViewById(R.id.listview_promote);
        searchView = (CommonSearchView) findViewById(R.id.search_view);
        progressDialog = new CommonProgressDialog(this);
        searchView.setListHistory(ClientStateManager.getHistory(ClientStateManager.PROMOTE_KEY));
        searchView.hideHistoryView();
        searchView.setSearchViewListener(new CommonSearchView.SearchViewListener() {
            @Override
            public void onSearch(String str) {
                PublicUtil.showToast("search key=" + str);
                searchView.hideHistoryView();
            }

            @Override
            public void onCancel() {
                searchView.hideHistoryView();
            }
        });
        progressDialog.show();
        DeliveryApi.getPromoteList(ClientStateManager.getLoginToken(this), "", 0, getPromoteListHandler);
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
                    items = result.getItemList();
                    if (items != null) {
                        PromoteAdapter adapter = new PromoteAdapter(PromoteActivity.this);
                        listview.setAdapter(adapter);
                    }
                } else {
                    PublicUtil.showErrorMsg(PromoteActivity.this, result);
                }
            } catch (Exception e) {
                PublicUtil.showToastServerBusy();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers,
                              String responseString, Throwable throwable) {
            LogUtils.d("test", "getPromoteListHandler result failed. statusCode="
                    + statusCode);
            progressDialog.dismiss();
            PublicUtil.showToastServerOvertime();
        }
    };

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
                    Intent intent = new Intent(PromoteActivity.this, AddPromoteActivity.class);
                    intent.putExtra("bpCode", item.getBpCode());
                    intent.putExtra("isEdit", true);
                    startActivityForResult(intent, 1);
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

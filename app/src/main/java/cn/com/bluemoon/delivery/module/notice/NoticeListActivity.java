package cn.com.bluemoon.delivery.module.notice;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.protocol.HTTP;

import java.util.List;

import cn.com.bluemoon.delivery.AppContext;
import cn.com.bluemoon.delivery.BuildConfig;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.message.Info;
import cn.com.bluemoon.delivery.app.api.model.message.ResultInfos;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.common.WebViewActivity;
import cn.com.bluemoon.delivery.module.base.interf.IActionBarListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.manager.ActivityManager;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib.view.CommonProgressDialog;

/**
 * 通知列表
 */
public class NoticeListActivity extends Activity {
    private String TAG = "NoticeListActivity";
    private NoticeListActivity main;
    private CommonProgressDialog progressDialog;
    private PullToRefreshListView listView;
    private NoiticeAdapter adapter;
    private ResultInfos notice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_list);
        main = this;
        ActivityManager.getInstance().pushOneActivity(this);
        initCustomActionBar();
        progressDialog = new CommonProgressDialog(main);


        listView = (PullToRefreshListView) findViewById(R.id.listview_main);
        listView.onRefreshComplete();
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                notice = null;
                getData();
            }

            @Override
            public void onPullUpToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                getData();
            }

        });

        getData();
    }


    private void getData() {
        String token = ClientStateManager.getLoginToken(main);
        if (progressDialog != null) {
            progressDialog.show();
        }
        DeliveryApi.getInformationList(token, AppContext.PAGE_SIZE, (null == notice ? 0 : notice
                .getTimestamp()), noticeHandler);
    }

    private void initCustomActionBar() {
        new CommonActionBar(getActionBar(), new IActionBarListener() {

            @Override
            public void onBtnRight(View v) {

            }

            @Override
            public void onBtnLeft(View v) {
                NoticeListActivity.this.finish();
            }

            @Override
            public void setTitle(TextView v) {
                v.setText(getString(R.string.title_notify));
            }

        });
    }


    AsyncHttpResponseHandler noticeHandler = new TextHttpResponseHandler(
            HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                              String responseString) {
            LogUtils.d(TAG, "messageHandler result = " + responseString);
            if (progressDialog != null)
                progressDialog.dismiss();
            listView.onRefreshComplete();
            try {
                ResultInfos resultInfos = JSON.parseObject(responseString,
                        ResultInfos.class);
                if (resultInfos.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    setData(resultInfos);
                } else {
                    PublicUtil.showErrorMsg(main, resultInfos);
                }
            } catch (Exception e) {
                LogUtils.e(TAG, e.getMessage());
                PublicUtil.showToastServerBusy();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers,
                              String responseString, Throwable throwable) {
            LogUtils.e(TAG, throwable.getMessage());
            if (progressDialog != null)
                progressDialog.dismiss();
            PublicUtil.showToastServerOvertime();
        }
    };


    private void setData(ResultInfos resultInfos) {
        if (notice == null) {
            notice = resultInfos;
            adapter = new NoiticeAdapter(main);
            adapter.setList(notice.getInfoList());
            listView.setAdapter(adapter);
        } else {
            notice.getInfoList().addAll(resultInfos.getInfoList());
            notice.setTimestamp(resultInfos.getTimestamp());
            adapter.notifyDataSetChanged();
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

    @SuppressLint("InflateParams")
    class NoiticeAdapter extends BaseAdapter {

        private Context context;
        private List<Info> lists;

        public NoiticeAdapter(Context context) {
            this.context = context;
        }

        public void setList(List<Info> list) {
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
            if (lists.size() == 0) {
                View viewEmpty = inflate.inflate(R.layout.layout_no_data, null);
                TextView txtContent = (TextView) viewEmpty.findViewById(R.id.txt_content);
                AbsListView.LayoutParams params = new AbsListView.LayoutParams(
                        AbsListView.LayoutParams.MATCH_PARENT, listView.getHeight());
                viewEmpty.setLayoutParams(params);
                txtContent.setText("当前无通知");
                return viewEmpty;
            }

            convertView = inflate.inflate(R.layout.activity_notice_item, null);

            ImageView txtReadSign = (ImageView) convertView.findViewById(R.id.iv_read_sign);
            TextView txtNoticeContent = (TextView) convertView.findViewById(R.id
                    .txt_notice_content);
            TextView txtNoticeDate = (TextView) convertView.findViewById(R.id.txt_notice_date);

            if (lists.get(position).isRead) {
                txtReadSign.setVisibility(View.GONE);
            } else {
                txtReadSign.setVisibility(View.VISIBLE);
            }
            txtNoticeContent.setText(lists.get(position).getInfoTitle());
            txtNoticeDate.setText(DateUtil.getTime(lists.get(position).getReleaseTime(),
                    "yyyy-MM-dd HH:mm"));

            int index = position % 2;
            if (index == 1) {
                convertView.setBackgroundResource(R.drawable.list_item_grep_bg);
            } else {
                convertView.setBackgroundResource(R.drawable.list_item_white_bg);
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = String.format(BuildConfig.H5_DOMAIN,"FE/angue/noticeDetail/"+lists.get(position)
                            .getInfoId());
                    WebViewActivity.startAction(context, url, "通知详情",false, null);
                    /*NoticeDetailActivity.startAction((Activity) context, lists.get(position)
                            .getInfoId(),Constants.TYPE_NOTICE, 0);*/
                    lists.get(position).isRead = true;
                    adapter.notifyDataSetChanged();
                }
            });
            return convertView;
        }

    }
}

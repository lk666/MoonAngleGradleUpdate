package cn.com.bluemoon.delivery.notice;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;

import java.util.List;

import cn.com.bluemoon.delivery.ClientStateManager;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.AppContext;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.message.Message;
import cn.com.bluemoon.delivery.app.api.model.message.ResultMessages;
import cn.com.bluemoon.delivery.async.listener.IActionBarListener;
import cn.com.bluemoon.delivery.manager.ActivityManager;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib.view.CommonProgressDialog;

/**
 * 消息列表
 */

public class MessageListActivity extends Activity {
    private String TAG = "MessageListActivity";
    private MessageListActivity main;
    private CommonProgressDialog progressDialog;
    private PullToRefreshListView listView;
    private MessageAdapter adapter;
    private ResultMessages messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        main = this;
        ActivityManager.getInstance().pushOneActivity(this);
        initCustomActionBar();
        progressDialog = new CommonProgressDialog(main);

        listView = (PullToRefreshListView) findViewById(R.id.listview_main);

        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                messages=null;
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

        DeliveryApi.getMessageList(token, AppContext.PAGE_SIZE,(null==messages?0:messages.getTimestamp()),messageHandler);
    }


    private void setData(ResultMessages resultMessages){
        if(messages==null){
            messages = resultMessages;
            adapter = new MessageAdapter(main);
            adapter.setList(messages.getMsgList());
            listView.setAdapter(adapter);
        }else{
            messages.getMsgList().addAll(resultMessages.getMsgList());
            messages.setTimestamp(resultMessages.getTimestamp());
            adapter.notifyDataSetChanged();
        }


    }

    AsyncHttpResponseHandler messageHandler = new TextHttpResponseHandler(
            HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                              String responseString) {
            LogUtils.d(TAG, "messageHandler result = " + responseString);
            if (progressDialog != null)
                progressDialog.dismiss();
            listView.onRefreshComplete();
            try {
                ResultMessages messageResult = JSON.parseObject(responseString,
                        ResultMessages.class);
                if (messageResult.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    setData(messageResult);
                } else {
                    PublicUtil.showErrorMsg(main, messageResult);
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


    private void initCustomActionBar() {
        new CommonActionBar(getActionBar(), new IActionBarListener() {

            @Override
            public void onBtnRight(View v) {

            }

            @Override
            public void onBtnLeft(View v){
                finish();
            }

            @Override
            public void setTitle(TextView v) {
                v.setText(getString(R.string.title_message));
            }

        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED)
            return;
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {

            }
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
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @SuppressLint("InflateParams")
    class MessageAdapter extends BaseAdapter {

        private Context context;
        private List<Message> lists;

        public MessageAdapter(Context context) {
            this.context = context;
        }

        public void setList(List<Message> list) {
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
                txtContent.setText("当前无消息");
                return viewEmpty;
            }

            convertView = inflate.inflate(R.layout.activity_message_item, null);

            int index = position % 2;
            if (index == 1) {
                convertView.setBackgroundResource(R.drawable.list_item_grep_bg);
            } else {
                convertView.setBackgroundResource(R.drawable.list_item_white_bg);
            }

            TextView txtMessageContent = (TextView) convertView
                    .findViewById(R.id.txt_message_content);
            TextView txtMessageDate = (TextView) convertView
                    .findViewById(R.id.txt_date);

            txtMessageContent.setText(lists.get(position).getMsgContent());
            txtMessageDate.setText(DateUtil.getTime(lists.get(position).getPushTime(),"yyyy-MM-dd  HH:mm"));

            return convertView;
        }

    }
}

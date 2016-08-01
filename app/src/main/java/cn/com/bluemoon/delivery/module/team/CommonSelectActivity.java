package cn.com.bluemoon.delivery.module.team;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.module.base.interf.IActionBarListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.PublicUtil;

public class CommonSelectActivity extends Activity {

    private String TAG = "CommonSelectActivity";
    private ListView listView;
    private SelectAdapter adapter;
    private String title;
    private List<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() != null) {
            title = getIntent().getStringExtra("title");
            list = getIntent().getStringArrayListExtra("list");
        }
        initCustomActionBar();
        setContentView(R.layout.activity_ticket_count);
        listView = (ListView) findViewById(R.id.listView_ticket);
        PublicUtil.setEmptyView(listView,String.format(getString(R.string.empty_hint),title),null);
        if (list == null) {
            return;
        }
        adapter = new SelectAdapter(this, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("index", position);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void initCustomActionBar() {
        new CommonActionBar(getActionBar(), new IActionBarListener() {

            @Override
            public void setTitle(TextView v) {
                v.setText(title);
            }

            @Override
            public void onBtnRight(View v) {

            }

            @Override
            public void onBtnLeft(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    class SelectAdapter extends BaseAdapter {

        private Context context;
        private List<String> list;

        public SelectAdapter(Context context, List<String> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return list.get(position);
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
            if (convertView == null) {
                TextView view = new TextView(context);
                view.setTextSize(15);
                view.setTextColor(getResources().getColor(R.color.text_black_light));
                view.setPadding(getResources().getDimensionPixelOffset(R.dimen.space_15),
                        getResources().getDimensionPixelOffset(R.dimen.space_12),
                        getResources().getDimensionPixelOffset(R.dimen.space_15),
                        getResources().getDimensionPixelOffset(R.dimen.space_12));
                view.setGravity(Gravity.CENTER);
                convertView = view;
            }

            ((TextView)convertView).setText(list.get(position));

            int index = position % 2;
            if (index == 1) {
                convertView.setBackgroundResource(R.drawable.list_item_grep_bg);
            } else {
                convertView.setBackgroundResource(R.drawable.list_item_white_bg);
            }

            return convertView;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            setResult(RESULT_CANCELED);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}

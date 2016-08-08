package cn.com.bluemoon.delivery.team;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import org.kymjs.kjframe.KJActivity;
import org.kymjs.kjframe.utils.StringUtils;

import cn.com.bluemoon.delivery.ClientStateManager;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.async.listener.IActionBarListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.lib.utils.LibViewUtil;
import cn.com.bluemoon.lib.view.CommonSearchView;

public class SearchActivity extends KJActivity {

    private String TAG = "SearchActivity";
    private CommonSearchView searchView;
    private String title;
    private Intent intent;
    private String key;
    public static final String KEY_RESULT = "result";

    @Override
    public void setRootView() {
        if (getIntent() != null) {
            title = getIntent().getStringExtra("title");
            key = getIntent().getStringExtra("key");
        }
        if(StringUtils.isEmpty(key)){
            return;
        }
        if(StringUtils.isEmpty(title)){
            title = getString(R.string.btn_search);
        }
        initCustomActionBar();
        searchView = new CommonSearchView(this);
        setContentView(searchView);
    }

    @Override
    public void initWidget() {
        super.initWidget();
        searchView.setSearchViewListener(searchViewListener);
        searchView.setHint(getString(R.string.team_group_search_hint));
        searchView.setListHistory(ClientStateManager.getHistory(key));
        searchView.showHistoryView();
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                searchView.setFocus(true);
                LibViewUtil.showKeyboard(searchView.getSearchEdittext());
            }
        }, 100);
    }


    CommonSearchView.SearchViewListener searchViewListener = new CommonSearchView.SearchViewListener() {
        @Override
        public void onSearch(CommonSearchView view,String str) {
            if (intent == null) {
                intent = new Intent();
            }
            intent.putExtra(KEY_RESULT, str);
            setResult(RESULT_OK,intent);
            finish();
        }

        @Override
        public void onCancel(CommonSearchView view) {
            setResult(RESULT_CANCELED);
            finish();
        }

    };


    private void initCustomActionBar() {
        new CommonActionBar(getActionBar(), new IActionBarListener() {

            @Override
            public void onBtnRight(View v) {

            }

            @Override
            public void onBtnLeft(View v) {
                LibViewUtil.hideIM(v);
                setResult(RESULT_CANCELED);
                finish();
            }

            @Override
            public void setTitle(TextView v) {
                v.setText(title);
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
            ClientStateManager.setHistory(searchView.getListHistory(), key);
    }
}

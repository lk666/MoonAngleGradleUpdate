package cn.com.bluemoon.delivery.module.card;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
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

import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.punchcard.ResultGetProduct;
import cn.com.bluemoon.delivery.app.api.model.punchcard.ResultGetProduct.Product;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.module.base.interf.IActionBarListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.ViewHolder;
import cn.com.bluemoon.delivery.utils.manager.ActivityManager;
import cn.com.bluemoon.lib.callback.CommonEditTextCallBack;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib.utils.LibViewUtil;
import cn.com.bluemoon.lib.view.CommonClearEditText;
import cn.com.bluemoon.lib.view.CommonProgressDialog;

/**
 * Created by liangjiangli on 2016/4/1.
 */
public class SearchProductActivity extends Activity {
    private String TAG = "SearchProductActivity";
    private CommonClearEditText etSearchProduct;
    private CommonProgressDialog progressDialog;
    private SearchProductActivity mContext;
    private GetProductAdapter getProductAdapter;
    private PullToRefreshListView listviewProduct;
    private boolean isPullUp;
    private List<Product> list;
    private long timestamp = 0;
    private TextView txtSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        ActivityManager manager = ActivityManager.getInstance();
        manager.pushOneActivity(this);
        mContext = this;
        progressDialog = new CommonProgressDialog(this);
        setContentView(R.layout.activity_search_product);
        listviewProduct = (PullToRefreshListView)findViewById(R.id.listview_product);
        listviewProduct.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                isPullUp = true;
                DeliveryApi.getProductList(ClientStateManager.getLoginToken(mContext), etSearchProduct.getText().toString(), timestamp, getProductHandler);
            }
        });
        etSearchProduct = (CommonClearEditText) findViewById(R.id.et_search_product);
        txtSearch = (TextView) findViewById(R.id.txt_search);
        etSearchProduct.setCallBack(editTextCallBack);
        etSearchProduct.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String condition = etSearchProduct.getText().toString();
                    isPullUp = false;
                    DeliveryApi.getProductList(ClientStateManager.getLoginToken(mContext), condition, 0, getProductHandler);
                }
                return false;
            }
        });

        txtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etSearchProduct.clearFocus();
                String condition = etSearchProduct.getText().toString();
                isPullUp = false;
                DeliveryApi.getProductList(ClientStateManager.getLoginToken(mContext), condition, 0, getProductHandler);
            }
        });

        initCustomActionBar();
        progressDialog.show();
        DeliveryApi.getProductList(ClientStateManager.getLoginToken(mContext), "", 0, getProductHandler);
    }

    CommonEditTextCallBack editTextCallBack = new CommonEditTextCallBack() {
        @Override
        public void afterTextChanged(Editable s) {
            super.afterTextChanged(s);
            if(etSearchProduct.isFocused()&&etSearchProduct.getText().toString().length()>0) {
                txtSearch.setText(getString(R.string.card_btn_search));
            }
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            super.onFocusChange(v, hasFocus);
            if(hasFocus){
                if(txtSearch.getVisibility()==View.GONE){
                    Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.activity_translate_right);
                    txtSearch.setAnimation(animation);
                    txtSearch.setVisibility(View.VISIBLE);
                }
            }else{
                LibViewUtil.hideIM(v);
                if(txtSearch.getVisibility()==View.VISIBLE){
                    txtSearch.setVisibility(View.GONE);
                }
            }
        }
    };

    AsyncHttpResponseHandler getProductHandler = new TextHttpResponseHandler(HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            LogUtils.d(TAG, "getProductHandler result = " + responseString);
            if(progressDialog != null)
                progressDialog.dismiss();
            listviewProduct.onRefreshComplete();
            try {
                ResultGetProduct result = JSON.parseObject(responseString, ResultGetProduct.class);
                if(result.getResponseCode()== Constants.RESPONSE_RESULT_SUCCESS){
                    timestamp = result.getTimestamp();
                    if (isPullUp && list != null) {
                        list.addAll(result.getProductList());
                        getProductAdapter.notifyDataSetChanged();
                    } else {
                        list = result.getProductList();
                        if (list != null) {
                            getProductAdapter = new GetProductAdapter(mContext, list);
                            listviewProduct.setAdapter(getProductAdapter);
                        }
                    }


                }else{
                    PublicUtil.showErrorMsg(mContext, result);
                }
            } catch (Exception e) {
                PublicUtil.showToastServerBusy();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString,
                              Throwable throwable) {
            LogUtils.e(TAG, throwable.getMessage());
            if(progressDialog != null)
                progressDialog.dismiss();
            listviewProduct.onRefreshComplete();
            PublicUtil.showToastServerOvertime();
        }
    };

    class GetProductAdapter extends BaseAdapter {

        private LayoutInflater mInflater;
        private List<Product> list;

        public GetProductAdapter(Context context, List<Product> list) {
            this.mInflater = LayoutInflater.from(context);
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_product_list, null);
            }
            LinearLayout layoutProduct = ViewHolder.get(convertView, R.id.layout_product);
            TextView txtProductName = ViewHolder.get(convertView, R.id.txt_product_name);
            View lineDotted = ViewHolder.get(convertView, R.id.line_dotted);
            View lineSilde = ViewHolder.get(convertView, R.id.line_silde);
            final Product product = list.get(position);
            if (position == list.size()-1) {
                lineDotted.setVisibility(View.GONE);
                lineSilde.setVisibility(View.VISIBLE);
            } else {
                lineDotted.setVisibility(View.VISIBLE);
                lineSilde.setVisibility(View.GONE);
            }

            txtProductName.setText(product.getProductCode() + "-" + product.getProductName());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("product", product);
                    intent.putExtras(bundle);
                    setResult(1, intent);
                    finish();

                }
            });
            return convertView;
        }

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
                v.setText(getText(R.string.search_product_title));
            }

        });
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
    }

}

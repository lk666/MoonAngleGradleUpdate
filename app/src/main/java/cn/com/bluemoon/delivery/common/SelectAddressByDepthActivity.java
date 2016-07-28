package cn.com.bluemoon.delivery.common;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.address.Area;
import cn.com.bluemoon.delivery.app.api.model.address.ResultArea;
import cn.com.bluemoon.delivery.manager.ActivityManager;
import cn.com.bluemoon.delivery.base.BaseListAdapter;
import cn.com.bluemoon.delivery.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.ViewHolder;
import cn.com.bluemoon.lib.view.CommonProgressDialog;


/**
 * 地址选择器
 * Created by luokai on 2016/6/29.
 */
public class SelectAddressByDepthActivity extends ListActivity implements OnListItemClickListener {
    public static final String EXTRA_AREA = "subRegionList";

    private CommonProgressDialog progressDialog;
    List<Area> listContent;
    /**
     * 当前深度
     */
    private int curDepth;
    /**
     * 最大深度深度
     */
    private int depth;
    /**
     * 返回的层级数据
     */
    private static List<Area> subRegionList;
    private boolean selectControl = true;
    private String dcode;
    private String type;
    private long startTime;
    private String TAG = "SelectAddressByDepthActivity";

    private TextAdapter adapter;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_select_address);
        ActivityManager.getInstance().pushOneActivity(this);
        dcode = getIntent().getStringExtra("dcode");
        type = getIntent().getStringExtra("type");
        depth = getIntent().getIntExtra("depth", 1);
        if (depth < 1) {
            depth = 1;
        }
        curDepth = 0;

        subRegionList = new ArrayList<>();
        listContent = new ArrayList<>();
        adapter = new TextAdapter(this, this);
        setListAdapter(adapter);

        if (progressDialog == null) {
            progressDialog = new CommonProgressDialog(this);
        }
        progressDialog.show();
        startTime = SystemClock.elapsedRealtime();

        DeliveryApi.getRegionSelect(dcode, type, getRegionHandler);
        setBackAction();
    }

    @Override
    protected void onDestroy() {
        ActivityManager.getInstance().popOneActivity(this);
        progressDialog = null;
        super.onDestroy();
    }

    private AsyncHttpResponseHandler getRegionHandler = new TextHttpResponseHandler(HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            LogUtils.d(TAG, "response result = " + responseString);
            try {

                ResultArea result = JSON.parseObject(responseString, ResultArea.class);
                if (result.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    if (result.getLists() != null && result.getLists().size() > 0) {
                        listContent.clear();
                        listContent.addAll(result.getLists());
                        adapter.setList(listContent);
                        adapter.notifyDataSetChanged();
                        curDepth++;
                    } else {
                        LogUtils.e(TAG, "call get region api failed.");
                    }

                } else {
                    PublicUtil.showErrorMsg(SelectAddressByDepthActivity.this, result);
                }

            } catch (Exception e) {
                PublicUtil.showToastServerBusy(SelectAddressByDepthActivity.this);
            } finally {
                dismissProgress();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString,
                              Throwable throwable) {
            LogUtils.e(TAG, "getRegionHandler  ------ onFailure : statusCode = " + statusCode);
            dismissProgress();
            PublicUtil.showToastServerOvertime(SelectAddressByDepthActivity.this);
        }
    };

    private void dismissProgress() {
        selectControl = true;
        if (progressDialog != null) {
            long waitTime = SystemClock.elapsedRealtime() - startTime;
            if (waitTime < 500) {
                try {
                    Thread.sleep(500 - waitTime);
                } catch (InterruptedException e) {
                }
            }
            progressDialog.dismiss();
        }
    }

    private void setBackAction() {
        ImageView backAction = (ImageView) findViewById(R.id.back_action);
        backAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED, null);
                finish();
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
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    @Override
    public void onItemClick(Object item, View view, int position) {
        if (selectControl) {
            selectControl = false;

            Area area = (Area) item;
            subRegionList.add(area);

            if (curDepth >= depth) {
                Intent mIntent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable(EXTRA_AREA, (Serializable) subRegionList);
                if (dcode != null && type != null) {
                    bundle.putString("type", type);
                }
                mIntent.putExtras(bundle);
                setResult(RESULT_OK, mIntent);
                this.finish();
            } else {
                if (progressDialog != null) {
                    progressDialog.show();
                }

                listContent = new ArrayList<>();
                DeliveryApi.getRegionSelect(area.getDcode(), area.getChildType(), getRegionHandler);
            }
        }
    }

    class TextAdapter extends BaseListAdapter<Area> {
        public TextAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.address_select_list_item;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            final Area item = (Area) getItem(position);
            if (item == null) {
                return;
            }

            TextView tv = ViewHolder.get(convertView, R.id.text1);
            tv.setText(item.getDname());

            setClickEvent(isNew, position, convertView);
        }
    }

    /**
     * @param context
     * @param dcode
     * @param type
     * @param depth       比如要选省市两级，传2，选市区镇，传3，>=1，超过实际深度以实际深度为准
     * @param requestCode
     */
    public static void actionStart(Activity context, String dcode, String type, int depth, int
            requestCode) {
        Intent intent = new Intent(context, SelectAddressByDepthActivity.class);
        intent.putExtra("dcode", dcode);
        intent.putExtra("type", type);
        intent.putExtra("depth", depth);
        context.startActivityForResult(intent, requestCode);
    }
}

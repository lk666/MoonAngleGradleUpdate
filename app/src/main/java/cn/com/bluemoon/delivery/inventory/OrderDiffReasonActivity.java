package cn.com.bluemoon.delivery.inventory;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;

import java.util.ArrayList;
import java.util.List;

import cn.com.bluemoon.delivery.ClientStateManager;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.Dict;
import cn.com.bluemoon.delivery.app.api.model.OrderVo;
import cn.com.bluemoon.delivery.app.api.model.ResultDict;
import cn.com.bluemoon.delivery.async.listener.IActionBarListener;
import cn.com.bluemoon.delivery.manager.ActivityManager;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.lib.tagview.Tag;
import cn.com.bluemoon.lib.tagview.TagListView;
import cn.com.bluemoon.lib.tagview.TagView;
import cn.com.bluemoon.lib.view.CommonProgressDialog;


public class OrderDiffReasonActivity extends Activity implements OnClickListener {
    private String TAG = "OrderDiffReasonActivity";
    private OrderDiffReasonActivity main;
    private Button btnSubmit;
    private EditText edContent;
    private TagListView tagListView;
    private TagView tagViewChecked;
    private Tag tagChecked;
    private Bitmap bm;
    private CommonProgressDialog progressDialog;
    private OrderVo order;

    private List<Dict> listDict;

    private String dictId;
    private String dictName;
    private String diffReasonDetail;
    private int pos = 0;
    private String isNot;
    private List<Tag> mTags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_diff_reason);
        main = this;
        initCustomActionBar();
        ActivityManager.getInstance().pushOneActivity(this);
        listDict = new ArrayList<Dict>();

        pos = getIntent().getIntExtra("pos", 0);
        if (getIntent().hasExtra("dictId")) {
            dictId = getIntent().getStringExtra("dictId");
        }
        if (getIntent().hasExtra("dictName")) {
            dictName = getIntent().getStringExtra("dictName");
        }
        if (getIntent().hasExtra("dictBackUp")) {
            diffReasonDetail = getIntent().getStringExtra("dictBackUp");
        }

        progressDialog = new CommonProgressDialog(this);
        btnSubmit = (Button) findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(this);
        edContent = (EditText) findViewById(R.id.ed_content);

        getData();
        //getDictInfo();
    }


    private void getData() {
        String token = ClientStateManager.getLoginToken(main);
        if (progressDialog != null) {
            progressDialog.show();
        }
        DeliveryApi.getDictInfo(token, dictInfoHandler);
    }


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

        switch (v.getId()) {
            case R.id.btn_submit:
                String dictId = "";
                if(tagListView.getTagsChecked().size() <= 0 ){
                    PublicUtil.showToast(main, getString(R.string.text_diff_reason));
                    return;
                }

                Tag tag =  tagListView.getTagsChecked().get(0);
                dictName = tag.getTitle();
                dictId = tag.getKey();

                diffReasonDetail = edContent.getText().toString();

                if ("".equals(dictName) || dictName == null) {
                    Toast.makeText(OrderDiffReasonActivity.this, getString(R.string.text_diff_reason), Toast.LENGTH_LONG).show();
                    return;
                }
              /*  for (int i = 0; i < listDict.size(); i++) {
                    if (dictName.equals(listDict.get(i).getDictName())) {
                        dictId = listDict.get(i).getDictId();
                    }
                }*/

                Intent it = new Intent();
                it.putExtra("dictId", dictId);
                it.putExtra("dictName", dictName);//差异原因
                it.putExtra("diffReasonDetail", diffReasonDetail);//差异原因说明
                it.putExtra("pos", pos);

                System.out.println(dictId+"-"+dictName+"-"+diffReasonDetail);
                setResult(RESULT_OK, it);
                main.finish();
                break;
        }
    }

    private void initTagListView() {

        tagListView = (TagListView) findViewById(R.id.tag_listview);
        tagListView.setMode(TagListView.Mode.single);
        mTags = new ArrayList<Tag>();
        for (int i = 0; i < listDict.size(); i++) {
            Tag tag = new Tag();
            tag.setId(i);
            if(!StringUtil.isEmpty(dictId) && dictId.equals(listDict.get(i).getDictId()) ){
                tag.setChecked(true);
            }
            tag.setKey(listDict.get(i).getDictId());
            tag.setTitle(listDict.get(i).getDictName());
            mTags.add(tag);

        }
        tagListView.setTags(mTags);
        initData();
    }


    private void initData() {

        if (mTags.size() <= 0) {
            return;
        }
        if(StringUtil.isEmpty(dictId)){
            return;
        }
        for (int i = 0; i < listDict.size(); i++) {
            if (dictId.equals(listDict.get(i).getDictId())) {
                tagListView.getViewByTag(mTags.get(i)).setBackgroundResource(R.drawable.btn_red_shape_large);
                ((TagView) tagListView.getViewByTag(mTags.get(i))).setTextColor(getResources().getColor(R.color.white));
            }
        }
        edContent.setText(diffReasonDetail);
    }


    private void initCustomActionBar() {
        new CommonActionBar(getActionBar(), new IActionBarListener() {

            @Override
            public void onBtnRight(View v) {

            }

            @Override
            public void onBtnLeft(View v) {
                OrderDiffReasonActivity.this.finish();
            }

            @Override
            public void setTitle(TextView v) {
                v.setText(getString(R.string.text_diff_reason_title));
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

    AsyncHttpResponseHandler dictInfoHandler = new TextHttpResponseHandler(HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                              String responseString) {
            LogUtils.d(TAG, "getDictInfo result = " + responseString);
            if (progressDialog != null) progressDialog.dismiss();
            try {
                ResultDict dictResult = JSON.parseObject(responseString,
                        ResultDict.class);
                if (dictResult.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    if (dictResult != null && dictResult.getDictList() != null) {
                        listDict.addAll(dictResult.getDictList());
                        initTagListView();
                    }
                } else {
                    PublicUtil.showErrorMsg(main, dictResult);
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
            if (progressDialog != null) progressDialog.dismiss();
            PublicUtil.showToastServerOvertime();
        }
    };

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

}

package cn.com.bluemoon.delivery.card;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.protocol.HTTP;

import cn.com.bluemoon.delivery.ClientStateManager;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.punchcard.ResultDiaryContent;
import cn.com.bluemoon.delivery.async.listener.IActionBarListener;
import cn.com.bluemoon.delivery.manager.ActivityManager;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.view.CommonAlertDialog;
import cn.com.bluemoon.lib.view.CommonProgressDialog;

/**
 * Created by liangjiangli on 2016/3/31.
 */
public class LogActivity extends Activity {
    private String TAG = "LogActivity";
    private EditText etLog;
    private Button btnSave;
    private CommonProgressDialog progressDialog;
    private LogActivity mContext;
    private boolean hasWorkDiary;
    private String logTxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        ActivityManager.getInstance().pushOneActivity(this);
        mContext = this;
        progressDialog = new CommonProgressDialog(this);
        progressDialog.setCancelable(false);
        setContentView(R.layout.activity_work_log);
        etLog = (EditText) findViewById(R.id.et_log);
        btnSave = (Button) findViewById(R.id.btn_save);
        hasWorkDiary = getIntent().getBooleanExtra("hasWorkDiary", true);
        if (hasWorkDiary) {
            if(progressDialog!=null) progressDialog.show();
            DeliveryApi.getWorkDiary(ClientStateManager.getLoginToken(mContext), getWorkDiaryHandler);
        }
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(PublicUtil.isFastDoubleClick(1000)) return;
                String text = etLog.getText().toString();
                if (StringUtils.isNotBlank(text)) {
                    if(progressDialog!=null)
                    progressDialog.show();
                    DeliveryApi.confirmWorkDiary(ClientStateManager.getLoginToken(mContext), text, new TextHttpResponseHandler(HTTP.UTF_8) {
                        @Override
                        public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                            if(progressDialog!=null)
                            progressDialog.dismiss();
                            PublicUtil.showToastServerOvertime(mContext);
                        }

                        @Override
                        public void onSuccess(int i, Header[] headers, String s) {
                            LogUtils.d("test", "confirmWorkDiary result = " + s);
                            if(progressDialog!=null)
                            progressDialog.dismiss();
                            try {
                                ResultBase result = JSON.parseObject(s, ResultBase.class);
                                if (result.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                                    PublicUtil.showToast(result.getResponseMsg());
                                    setResult(1);
                                    finish();
                                } else {
                                    PublicUtil.showErrorMsg(mContext, result);
                                }
                            } catch (Exception e) {
                                PublicUtil.showToastServerBusy(mContext);
                            }
                        }
                    });
                } else {
                    PublicUtil.showToast(getString(R.string.log_can_not_null));
                }
            }
        });
        initCustomActionBar();
    }
    private void initCustomActionBar() {
        new CommonActionBar(this.getActionBar(), new IActionBarListener() {

            @Override
            public void onBtnRight(View v) {

            }

            @Override
            public void onBtnLeft(View v) {
                showDialog();
            }

            @Override
            public void setTitle(TextView v) {
                v.setText(getText(R.string.work_log_title));
            }

        });
    }
    AsyncHttpResponseHandler getWorkDiaryHandler = new TextHttpResponseHandler(HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            LogUtils.d("test", "getWorkDiaryHandler result = " + responseString);
            if(progressDialog!=null)
            progressDialog.dismiss();
            try {
                ResultDiaryContent result = JSON.parseObject(responseString, ResultDiaryContent.class);
                if(null!=result && result.getResponseCode()==Constants.RESPONSE_RESULT_SUCCESS){
                    etLog.setText(result.getDiaryContent());
                    logTxt = result.getDiaryContent();
                } else {
                    PublicUtil.showToast(result.getResponseMsg());
                }
            } catch (Exception e) {
                PublicUtil.showToastServerBusy();
            }
        }
        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString,
                              Throwable throwable) {
            LogUtils.d("statusCode="+statusCode);
            if(progressDialog!=null)
                progressDialog.dismiss();
            PublicUtil.showToastServerOvertime();
        }
    };


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showDialog();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showDialog() {
        String content = etLog.getText().toString();
        if (StringUtils.isNotBlank(content) && !content.equals(logTxt)) {
            new CommonAlertDialog.Builder(mContext)
                    .setMessage(R.string.log_not_save_content)
                    .setNegativeButton(R.string.btn_ok,
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    finish();
                                }
                            }).setPositiveButton(R.string.btn_cancel, null)
                    .show();
        } else {
            finish();
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
}

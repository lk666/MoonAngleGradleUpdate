package cn.com.bluemoon.delivery.module.account;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;
import org.apache.http.protocol.HTTP;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.BuildConfig;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.ResultVersionInfo;
import cn.com.bluemoon.delivery.app.api.model.Version;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.base.interf.IActionBarListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;
import cn.com.bluemoon.delivery.utils.manager.CacheManager;
import cn.com.bluemoon.delivery.utils.manager.UpdateManager;
import cn.com.bluemoon.lib.utils.LibVersionUtils;
import cn.com.bluemoon.lib.view.CommonAlertDialog;

public class SettingInfoActivity extends BaseActivity {

    @Bind(R.id.txt_cache)
    TextView txtCache;
    @Bind(R.id.lin_general)
    LinearLayout linGeneral;
    @Bind(R.id.txt_check)
    TextView txtCheck;
    @Bind(R.id.lin_about)
    LinearLayout linAbout;
    private String size;
    private String curVersion;
    private int mode;
    private String title;

    public static void actStart(Context context, int mode) {
        Intent intent = new Intent(context, SettingInfoActivity.class);
        intent.putExtra("mode", mode);
        context.startActivity(intent);
    }

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
        mode = getIntent().getIntExtra("mode", Constants.MODE_GENERAL);
        if (mode == Constants.MODE_GENERAL) {
            title = getString(R.string.user_general);
        } else if (mode == Constants.MODE_CHECK) {
            title = getString(R.string.user_about);
        } else {
            title = getString(R.string.user_settings);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.account_setinfo;
    }

    @Override
    protected String getTitleString() {
        return title;
    }

    @Override
    public void initView() {
        setLayout();
    }

    @Override
    public void initData() {

    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        ResultVersionInfo resultVersionInfo = (ResultVersionInfo) result;
        String newVersion = "0.0.0";
        final Version resp = resultVersionInfo.getItemList();
        if (resp != null) {
            if (resp.getVersion() != null) {
                newVersion = resp.getVersion();
            }
            if (LibVersionUtils.isNewerVersion(curVersion, newVersion)) {
                showUpdateDialog(resp);
            } else {
                toast(R.string.new_version_alert_no_update);
            }
        } else {
            toast(R.string.new_version_alert_no_update);
        }
    }

    private void setLayout() {

        if (mode == Constants.MODE_GENERAL) {
            linGeneral.setVisibility(View.VISIBLE);
            setCacheSize();
        } else if (mode == Constants.MODE_CHECK) {
            linAbout.setVisibility(View.VISIBLE);
            curVersion = PublicUtil.getAppVersionNoSuffix(this);
            String versionString = curVersion;
            if (!BuildConfig.RELEASE) {
                versionString = curVersion
                        + String.format(getString(R.string.test_name),
                        BuildConfig.BUILD_CODE);
            }
            txtCheck.setText(versionString);
        }
    }

    private void setCacheSize() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    size = CacheManager.getBlueMoonCacheSize(SettingInfoActivity.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Handler handler = new Handler(getMainLooper());
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        txtCache.setText(size);
                    }
                });
            }
        }).start();
    }

    private void showUpdateDialog(final Version result) {
        CommonAlertDialog.Builder dialog = new CommonAlertDialog.Builder(this);
        dialog.setTitle(getString(R.string.new_version_alert_title));
        dialog.setMessage(StringUtil.getCheckVersionDescription(result.getDescription()));
        dialog.setCancelable(false);
        dialog.setNegativeButton(R.string.new_version_yes,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        new UpdateManager(SettingInfoActivity.this, result.getDownload(),
                                result.getVersion(),
                                new UpdateManager.UpdateCallback() {

                                    @Override
                                    public void onCancel() {
                                    }

                                    @Override
                                    public void onFailUpdate() {
                                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                                            @Override
                                            public void run() {
                                                toast(R.string.new_version_error);
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFinishUpdate() {

                                    }
                                }).showDownloadDialog();
                    }

                });
        dialog.setPositiveButton(R.string.new_version_no, null);
        dialog.show();

    }

    private void showClearDialog(){
        if ("0.0B".equalsIgnoreCase(txtCache.getText().toString())) {
            toast(getString(R.string.no_cache));
            return;
        }
        CommonAlertDialog.Builder dialog = new CommonAlertDialog.Builder(this);
        dialog.setMessage(R.string.clear_dialog_msg);
        dialog.setPositiveButton(R.string.btn_ok,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        CacheManager.clearBlueMoonCacheSize(SettingInfoActivity.this);
                        setCacheSize();
                        toast(R.string.clear_cache_success);
                    }
                });
        dialog.setNegativeButton(R.string.btn_cancel, null);
        dialog.show();
    }

    @OnClick({R.id.re_clearCache, R.id.re_check})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.re_clearCache:
                showClearDialog();
                break;
            case R.id.re_check:
                showWaitDialog();
                DeliveryApi.getLastVersion(getNewHandler(0, ResultVersionInfo.class));
                break;
        }
    }
}

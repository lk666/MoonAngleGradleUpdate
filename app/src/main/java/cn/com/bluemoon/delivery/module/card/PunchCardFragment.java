package cn.com.bluemoon.delivery.module.card;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.bluemoon.delivery.AppContext;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.card.PunchCard;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.module.base.interf.IActionBarListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.utils.LibConstants;
import cn.com.bluemoon.lib.view.CommonAlertDialog;
import cn.com.bluemoon.lib.view.CommonProgressDialog;

public class PunchCardFragment extends Fragment implements OnClickListener {
    private String TAG = "PunchCardFragment";
    private CardTabActivity mContext;
    @Bind(R.id.img_banner)
    ImageView imgBanner;
    @Bind(R.id.txt_date)
    TextView txtDate;
    @Bind(R.id.txt_week)
    TextView txtWeek;

    @Bind(R.id.layout_scan)
    LinearLayout layoutScan;
    @Bind(R.id.layout_input)
    LinearLayout layoutInput;
    @Bind(R.id.layout_reset)
    LinearLayout layoutReset;
    private CommonProgressDialog progressDialog;
    private FragmentActivity main;
    public LocationClient mLocationClient = null;
    PunchCard punchCard;
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mContext = (CardTabActivity) activity;
    }

    ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        main = getActivity();
        initCustomActionBar();
        View v = inflater.inflate(R.layout.fragment_tab_card_scan,
                container, false);
        ButterKnife.bind(this, v);
        progressDialog = new CommonProgressDialog(main);
        txtDate.setText(DateUtil.getTime(System.currentTimeMillis(), "MM.dd"));
        txtWeek.setText(DateUtil.getTime(System.currentTimeMillis(), "EEEE"));

        layoutScan.setOnClickListener(this);
        layoutReset.setOnClickListener(this);
        layoutInput.setOnClickListener(this);
        setImgBanner();

        mLocationClient = new LocationClient(getActivity());
        mLocationClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        mLocationClient.setLocOption(option);
        return v;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLocationClient != null) {
            mLocationClient.unRegisterLocationListener(myListener);
            mLocationClient = null;
        }
    }

    private void setImgBanner() {
        int width = AppContext.getInstance().getDisplayWidth();
        Drawable drawable = getResources().getDrawable(R.mipmap.bg_punch_title);
        ViewGroup.LayoutParams params = imgBanner.getLayoutParams();
        params.width = width;
        params.height = width * drawable.getIntrinsicHeight() / drawable.getIntrinsicWidth();
        imgBanner.setLayoutParams(params);
        imgBanner.setImageDrawable(drawable);
    }

    private void initCustomActionBar() {
        new CommonActionBar(mContext.getActionBar(), new IActionBarListener() {

            @Override
            public void onBtnRight(View v) {

            }

            @Override
            public void onBtnLeft(View v) {
                mContext.finish();
            }

            @Override
            public void setTitle(TextView v) {
                v.setText(getText(R.string.main_tab_card));
            }

        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED) {
            return;
        }
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Constants.REQUEST_SCAN:
                    String resultStr = data.getStringExtra(LibConstants.SCAN_RESULT);
                    Intent intent = new Intent(mContext, PunchCardOndutyActivity.class);
                    intent.putExtra("code", resultStr);
                    startActivityForResult(intent, 1);
                    break;
                case 1:

                    break;
            }
        } else if (resultCode == 2) {
            mContext.finish();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == layoutScan) {
            PublicUtil.openScanView(mContext, PunchCardFragment.this,
                    getString(R.string.btn_san_punch_card_text), Constants.REQUEST_SCAN);
        } else if (v == layoutInput) {
            Intent intent = new Intent(mContext, PunchCardOndutyActivity.class);
            startActivityForResult(intent, 1);
        }else  if(v == layoutReset){

            CommonAlertDialog.Builder dialog = new CommonAlertDialog.Builder(getActivity());
            dialog.setMessage(getString(R.string.btn_reset_confirm));
            dialog.setPositiveButton(R.string.btn_cancel,null);
            dialog.setNegativeButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    progressDialog.show();
                    mLocationClient.start();
                }
            });
            dialog.show();


        }
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



    public BDLocationListener myListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            LogUtils.d("location.getLatitude= " + location.getLatitude());
            LogUtils.d("location.getLongitude= " + location.getLongitude());
            LogUtils.d("location.getAltitude= " + location.getAltitude());
            mLocationClient.stop();
            if(null==punchCard){
                punchCard = new PunchCard();
            }
            if (location.getLocType() == BDLocation.TypeOffLineLocation) {
                punchCard.setLatitude(Constants.UNKNOW_LATITUDE);
                punchCard.setLongitude(Constants.UNKNOW_LONGITUDE);
            } else {
                punchCard.setLatitude(location.getLatitude());
                punchCard.setLongitude(location.getLongitude());
            }
            DeliveryApi.nowRest(ClientStateManager.getLoginToken(),punchCard,resetHandler);
        }
    };



    AsyncHttpResponseHandler resetHandler = new TextHttpResponseHandler(HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            LogUtils.d("test", "getOrdersHandler result = " + responseString);
            progressDialog.dismiss();
            try {
                ResultBase result = JSON.parseObject(responseString, ResultBase.class);
                PublicUtil.showToast(result.getResponseMsg());
            } catch (Exception e) {
                PublicUtil.showToastServerBusy(mContext);
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString,
                              Throwable throwable) {
            LogUtils.d("test", "getOrdersHandler result failed. statusCode=" + statusCode);
            progressDialog.dismiss();
            PublicUtil.showToastServerOvertime(mContext);
        }
    };
}

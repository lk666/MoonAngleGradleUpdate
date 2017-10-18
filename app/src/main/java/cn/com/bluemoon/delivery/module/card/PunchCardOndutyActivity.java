package cn.com.bluemoon.delivery.module.card;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
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

import java.util.ArrayList;
import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.address.Area;
import cn.com.bluemoon.delivery.app.api.model.card.PunchCard;
import cn.com.bluemoon.delivery.app.api.model.card.PunchCardType;
import cn.com.bluemoon.delivery.app.api.model.card.PunchParam;
import cn.com.bluemoon.delivery.app.api.model.card.RegionInfo;
import cn.com.bluemoon.delivery.app.api.model.card.ResultAddressInfo;
import cn.com.bluemoon.delivery.app.api.model.card.ResultCheckScanCode;
import cn.com.bluemoon.delivery.app.api.model.card.ResultGPSRegion;
import cn.com.bluemoon.delivery.app.api.model.card.ResultGetWorkTask;
import cn.com.bluemoon.delivery.app.api.model.card.WorkPlaceType;
import cn.com.bluemoon.delivery.app.api.model.card.WorkTask;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.module.base.interf.IActionBarListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.ui.TabSelector;
import cn.com.bluemoon.delivery.ui.dialog.AddressSelectPopWindow;
import cn.com.bluemoon.delivery.utils.AccelerateInterpolator;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.tagview.Tag;
import cn.com.bluemoon.lib.tagview.TagListView;
import cn.com.bluemoon.lib.utils.LibViewUtil;
import cn.com.bluemoon.lib.view.CommonAlertDialog;
import cn.com.bluemoon.lib.view.CommonProgressDialog;

public class PunchCardOndutyActivity extends Activity implements AddressSelectPopWindow
        .IAddressSelectDialog {

    private String TAG = "PunchCardOndutyActivity";
    private TabSelector layoutTab;
    private TagListView tagListViewCode;
    private TagListView tagListViewOther;
    private LinearLayout layoutChooseAddressCode;
    private LinearLayout layoutChooseAddressOther;
    private TextView txtAddressCode;
    private TextView txtRegionOther;
    private TextView etAddressOther;
    private TextView txtCharge;
    private TextView txtCardAddress;
    private Button btnPunchCard;
    private TextView txtCurrentAddress;
    private ImageView imgAddressRefresh;
    private CommonProgressDialog progressDialog;
    private ViewPager viewPager;
    private int currentItem = 0;
    private String workplaceCodeStr;
    private PunchCard punchCardCode;
    private PunchCard punchCardOther;
    private PunchCardType punchCardType;
    private PunchCardOndutyActivity main;
    private LinearLayout mainView;
    private LinearLayout layoutAddress;
    public LocationClient mLocationClient = null;
    private RegionInfo regionInfo;
    //是否正在定位
    private boolean isInit = true;
    //是否已经获取到定位的省市区,即是否可以选择地址（用于无编码打卡）
    private boolean isCanSelect;
    ObjectAnimator anim;
    public BDLocationListener myListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            LogUtils.d("TAG", "result =" + location.getLatitude());
            LogUtils.d("TAG", "result =" + location.getLongitude());
            LogUtils.d("TAG", "result =" + location.getAltitude());
//            LogUtils.d("TAG","result ="+location.getAddrStr());
            mLocationClient.stop();
            PunchCard punchCard = new PunchCard();
            if (isInit) {
                //获取首页地址信息
                if (location.getLocType() == BDLocation.TypeOffLineLocation) {
                    punchCard.setLatitude(Constants.UNKNOW_LATITUDE);
                    punchCard.setLongitude(Constants.UNKNOW_LONGITUDE);
                    txtCurrentAddress.setText(getString(R.string.work_address_fail_txt));
                    if (progressDialog != null)
                        progressDialog.dismiss();
                    isCanSelect = true;
                } else {
                    PunchParam param = new PunchParam();
                    param.setLatitude(location.getLatitude());
                    param.setLongitude(location.getLongitude());
                    param.setToken(ClientStateManager.getLoginToken());
                    // 获取定位地址
                    DeliveryApi.getGpsAddress(param, gpsHandler);
                    //  2017/10/17 获取无编码打卡的省市区
                    DeliveryApi.getGPSRegion(param, gpsRegionHandler);
                }

            } else {

                String workTask;
                if (currentItem == 0) {
                    if (punchCardCode != null) punchCard = punchCardCode;
                    workTask = CardUtils.getWorkTaskString(tagListViewCode.getTagsChecked());
                } else {
                    if (punchCardOther != null) punchCard = punchCardOther;
                    punchCard.setAddress(etAddressOther.getText().toString());
                    workTask = CardUtils.getWorkTaskString(tagListViewOther.getTagsChecked());
                }
                punchCard.setPunchCardType(punchCardType.toString());
                if (location.getLocType() == BDLocation.TypeOffLineLocation) {
                    punchCard.setLatitude(Constants.UNKNOW_LATITUDE);
                    punchCard.setLongitude(Constants.UNKNOW_LONGITUDE);
                } else {
                    punchCard.setLatitude(location.getLatitude());
                    punchCard.setLongitude(location.getLongitude());
                }
//            punchCard.setAltitude(location.getAltitude());

                if (TextUtils.isEmpty(ClientStateManager.getLoginToken(main))
                        || TextUtils.isEmpty(workTask)) {
                    btnPunchCard.setClickable(true);
                    return;
                }
                DeliveryApi.addPunchCardIn(ClientStateManager.getLoginToken(), punchCard,
                        workTask, confirmAttendanceHandler);
            }
            isInit = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_punch_card_input_onduty);
        main = this;
        mainView = (LinearLayout) findViewById(R.id.layout_main);
        initCustomActionBar();
        layoutTab = (TabSelector) findViewById(R.id.layout_tab);
        btnPunchCard = (Button) findViewById(R.id.btn_punch_card);
        btnPunchCard.setOnClickListener(onClickListener);
        progressDialog = new CommonProgressDialog(main);
        progressDialog.setCancelable(false);
        if (getIntent() != null) {
            workplaceCodeStr = getIntent().getStringExtra("code");
            if (!TextUtils.isEmpty(workplaceCodeStr)) {
                punchCardType = PunchCardType.scan;
            }
        }
        if (punchCardType == null) {
            punchCardType = PunchCardType.code;
        }
        initView();

        //定位设置
        mLocationClient = new LocationClient(this);
        mLocationClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setIsNeedAddress(false);

        mLocationClient.setLocOption(option);
        txtCurrentAddress.setText(getString(R.string.work_address_fail_txt));
        mLocationClient.start();
        layoutAddress.setClickable(false);
        startPropertyAnim(imgAddressRefresh);
        layoutTab.setOnClickListener(new TabSelector.CallBackListener() {

            @Override
            public void onClick(int currentTab) {
                LibViewUtil.hideIM(layoutTab);
                viewPager.setCurrentItem(currentTab);
            }
        });


        /*layoutTab.setOnClickListener(new LinearLayoutForTabSelector.OnTextClickListener() {
            @Override
            public void onLeftClick() {
                LibViewUtil.hideIM(layoutTab);
                viewPager.setCurrentItem(0);
            }

            @Override
            public void onRightClick() {
                LibViewUtil.hideIM(layoutTab);
                viewPager.setCurrentItem(1);
            }
        });*/
        etAddressOther.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(charSequence.toString())) {
                    etAddressOther.setHint(getString(R.string.card_input_address_hint));
                } else {
                    etAddressOther.setHint("");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

//                refreshBtn();
            }
        });
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isInit) {
                PublicUtil.showToast(getString(R.string.get_on_location));
                return;
            }
            LibViewUtil.hideIM(v);
            if (v == btnPunchCard) {
                if (!checkSumbit()) {
                    return;
                }
                btnPunchCard.setClickable(false);
                if (progressDialog != null) progressDialog.show();
                mLocationClient.start();
            } else if (v == layoutChooseAddressCode) {
                Intent intent = new Intent(main, GetWorkPlaceActivity.class);
                intent.putExtra("code", txtAddressCode.getText().toString());
                startActivityForResult(intent, 1);
            } else if (v == layoutChooseAddressOther) {
                if (!isCanSelect) {
                    PublicUtil.showToast(getString(R.string.get_on_location));
                    return;
                }
                String pId = "", cId = "", coId = "";
                if (regionInfo != null) {
                    pId = regionInfo.provinceCode;
                    cId = regionInfo.cityCode;
                    coId = regionInfo.countyCode;
                }
                AddressSelectPopWindow window = AddressSelectPopWindow.newInstance(main, pId
                        , cId, coId);
                window.setListener(main);
                window.show(mainView);

            } else if (v == layoutAddress) {
                isInit = true;
//                isCanSelect = false;
                txtCurrentAddress.setText(getString(R.string.work_address_fail_txt));
                layoutAddress.setClickable(false);
                startPropertyAnim(imgAddressRefresh);
                mLocationClient.start();
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLocationClient != null) {
            mLocationClient.unRegisterLocationListener(myListener);
            mLocationClient = null;
        }

    }

    private void initView() {

        txtCurrentAddress = (TextView) findViewById(R.id.text_address);
        imgAddressRefresh = (ImageView) findViewById(R.id.img_address_refresh);
        layoutAddress = (LinearLayout) findViewById(R.id.layout_address);
        layoutAddress.setOnClickListener(onClickListener);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        List<View> views = new ArrayList<>();
        LayoutInflater inflater = getLayoutInflater();
        View viewCode = inflater.inflate(R.layout.view_punch_card_input_code, null);
        View viewOther = inflater.inflate(R.layout.view_punch_card_input_other, null);
        views.add(viewCode);
        views.add(viewOther);
        viewPager.setAdapter(new CardViewPageAdapter(views));
        viewPager.setOnPageChangeListener(new CardOnPageChangeListener());
        //viewCode
        ImageView imgRight = (ImageView) viewCode.findViewById(R.id.img_right);
        txtAddressCode = (TextView) viewCode.findViewById(R.id.txt_address_code);
        txtCharge = (TextView) viewCode.findViewById(R.id.txt_charge);
        txtCardAddress = (TextView) viewCode.findViewById(R.id.txt_card_address);
        tagListViewCode = (TagListView) viewCode.findViewById(R.id.tag_listview_card_code);
        layoutChooseAddressCode = (LinearLayout) viewCode.findViewById(R.id
                .layout_choose_address_code);
        layoutChooseAddressCode.setOnClickListener(onClickListener);

        //viewOther
        txtRegionOther = (TextView) viewOther.findViewById(R.id.txt_region_other);
        etAddressOther = (TextView) viewOther.findViewById(R.id.et_address_other);
        tagListViewOther = (TagListView) viewOther.findViewById(R.id.tag_listview_card_other);
        layoutChooseAddressOther = (LinearLayout) viewOther.findViewById(R.id
                .layout_choose_address_other);
        layoutChooseAddressOther.setOnClickListener(onClickListener);
        if (PunchCardType.scan == punchCardType) {
            layoutTab.setVisibility(View.GONE);
            imgRight.setVisibility(View.GONE);
            txtAddressCode.setHint("");
            layoutChooseAddressCode.setClickable(false);
            setDataByCode(workplaceCodeStr);
        }
        viewPager.setCurrentItem(0);

    }

    private void setTags(TagListView tagListView, List<WorkTask> workTasks) {
        List<Tag> list = new ArrayList<>();
        if (workTasks != null) {
            if (workTasks.size() > 0) {
                tagListView.setVisibility(View.VISIBLE);
            } else {
                tagListView.setVisibility(View.GONE);
            }
            for (int i = 0; i < workTasks.size(); i++) {
                Tag tag = new Tag();
                tag.setId(i);
                tag.setKey(workTasks.get(i).getTaskCode());
                tag.setChecked(workTasks.get(i).isSelected);
                tag.setTitle(workTasks.get(i).getTaskName());
                list.add(tag);
            }
        }
        tagListView.setTags(list);
    }

    private void setDataByCode(String code) {
        if (!TextUtils.isEmpty(code)) {
            workplaceCodeStr = code;
            if (progressDialog != null) {
                progressDialog.show();
            }
            DeliveryApi.checkScanCodeCard(ClientStateManager.getLoginToken(main),
                    workplaceCodeStr, checkScanCodeHandler);
        }
    }

    private boolean checkSumbit() {
        if (currentItem == 0) {
            if (punchCardCode == null || TextUtils.isEmpty(punchCardCode.getAttendanceCode())) {
                PublicUtil.showToast(R.string.card_workplace_cannot_empty);
            } else if (tagListViewCode.getTagsChecked().size() <= 0) {
                PublicUtil.showToast(R.string.card_worktask_cannot_empty);
            } else {
                return true;
            }
        } else if (currentItem == 1) {
            if (punchCardOther == null || TextUtils.isEmpty(txtRegionOther.getText().toString()
                    .trim())) {
                PublicUtil.showToast(R.string.card_workplace_cannot_empty);
            } else if (TextUtils.isEmpty(punchCardOther.getProvinceName()) || TextUtils.isEmpty
                    (punchCardOther.getCityName()) || TextUtils.isEmpty(punchCardOther
                            .getCountyName())){
                PublicUtil.showToast(R.string.card_region_right);
            }else if (TextUtils.isEmpty(etAddressOther.getText().toString().trim())) {
                PublicUtil.showToast(R.string.card_address_cannot_empty);
            } else if (tagListViewOther.getTagsChecked().size() <= 0) {
                PublicUtil.showToast(R.string.card_worktask_cannot_empty);
            } else {
                return true;
            }
        }
        return false;
    }

    private void initCustomActionBar() {
        new CommonActionBar(getActionBar(), new IActionBarListener() {

            @Override
            public void setTitle(TextView v) {
                // TODO Auto-generated method stub
                v.setText(getResources().getString(R.string.main_tab_card));
            }

            @Override
            public void onBtnRight(View v) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onBtnLeft(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1:
                    if (data == null) return;
                    setDataByCode(data.getStringExtra("code"));
                    break;
            }
        }
    }

    private void showErrorCodeDialog(String msg, final int resultCode) {
        new CommonAlertDialog.Builder(main)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setResult(resultCode);
                        finish();
                    }
                })
                .show();

    }

    AsyncHttpResponseHandler checkScanCodeHandler = new TextHttpResponseHandler(HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            LogUtils.d(TAG, "checkScanCodeHandler result = " + responseString);
            if (progressDialog != null)
                progressDialog.dismiss();
            try {
                ResultCheckScanCode checkScanCodeResult = JSON.parseObject(responseString,
                        ResultCheckScanCode.class);
                if (checkScanCodeResult.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    if (punchCardCode == null) {
                        punchCardCode = new PunchCard();
                    }
                    punchCardCode.setAttendanceCode(checkScanCodeResult.getPunchCard()
                            .getAttendanceCode());
                    txtAddressCode.setText(checkScanCodeResult.getPunchCard().getAttendanceCode());
                    txtCharge.setText(CardUtils.getChargeNoCode(checkScanCodeResult.getPunchCard
                            ()));
                    txtCardAddress.setText(CardUtils.getAddress(checkScanCodeResult.getPunchCard
                            ()));
                    setTags(tagListViewCode, checkScanCodeResult.getWorkTaskList());
                } else {
                    if (PunchCardType.scan == punchCardType) {
                        if (Constants.RESPONSE_RESULT_TOKEN_EXPIRED == checkScanCodeResult
                                .getResponseCode()) {
                            PublicUtil.showMessageTokenExpire(main);
                        } else {
                            showErrorCodeDialog(checkScanCodeResult.getResponseMsg(), Activity
                                    .RESULT_CANCELED);
                        }
                    } else {
                        PublicUtil.showErrorMsg(main, checkScanCodeResult);
                    }
                }
            } catch (Exception e) {
                LogUtils.e(TAG, e.getMessage());
                PublicUtil.showToastServerBusy();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString,
                              Throwable throwable) {
            LogUtils.e(TAG, throwable.getMessage());
            if (progressDialog != null)
                progressDialog.dismiss();
            PublicUtil.showToastServerOvertime();
        }
    };

    AsyncHttpResponseHandler getWorkTaskHandler = new TextHttpResponseHandler(HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            LogUtils.d(TAG, "getWorkTaskHandler result = " + responseString);
//            if(progressDialog != null)
//                progressDialog.dismiss();
            try {
                ResultGetWorkTask getWorkTaskResult = JSON.parseObject(responseString,
                        ResultGetWorkTask.class);
                if (getWorkTaskResult.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    setTags(tagListViewOther, getWorkTaskResult.getWorkTaskList());
                } else {
                    PublicUtil.showErrorMsg(main, getWorkTaskResult);
                }
            } catch (Exception e) {
                LogUtils.e(TAG, e.getMessage());
                PublicUtil.showToastServerBusy();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString,
                              Throwable throwable) {
            LogUtils.e(TAG, throwable.getMessage());
//            if(progressDialog != null)
//                progressDialog.dismiss();
            PublicUtil.showToastServerOvertime();
        }
    };

    /**
     * 根据定位获取定位地址
     */
    AsyncHttpResponseHandler gpsHandler = new TextHttpResponseHandler(HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            LogUtils.d(TAG, "gpsHandler result = " + responseString);
            if (progressDialog != null)
                progressDialog.dismiss();
            try {
                ResultAddressInfo baseResult = JSON.parseObject(responseString, ResultAddressInfo
                        .class);
                if (baseResult.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    txtCurrentAddress.setText(baseResult.getAddressInfo().getFormattedAddress());
                } else {
                    PublicUtil.showErrorMsg(main, baseResult);
                }
            } catch (Exception e) {
                LogUtils.e(TAG, e.getMessage());
                PublicUtil.showToastServerBusy();
                txtCurrentAddress.setText(getString(R.string.work_address_fail_txt));
            } finally {
                stopPropertyAnim();
                layoutAddress.setClickable(true);
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString,
                              Throwable throwable) {
            LogUtils.e(TAG, throwable.getMessage());
            if (progressDialog != null)
                progressDialog.dismiss();
            PublicUtil.showToastServerOvertime();
            txtCurrentAddress.setText(getString(R.string.work_address_fail_txt));
            stopPropertyAnim();
            layoutAddress.setClickable(true);
        }
    };

    /**
     * 根据定位获取行政区域
     */
    AsyncHttpResponseHandler gpsRegionHandler = new TextHttpResponseHandler(HTTP.UTF_8) {

        @Override
        public void onFinish() {
            super.onFinish();
            isCanSelect = true;
            if (progressDialog != null)
                progressDialog.dismiss();
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            LogUtils.d(TAG, "gpsHandler result = " + responseString);

            try {
                ResultGPSRegion baseResult = JSON.parseObject(responseString, ResultGPSRegion
                        .class);
                if (baseResult.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    regionInfo = baseResult.regionInfo;
                    refreshRegion();
                } else {
                    PublicUtil.showErrorMsg(main, baseResult);
                }
            } catch (Exception e) {
                LogUtils.e(TAG, e.getMessage());
                PublicUtil.showToastServerBusy();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString,
                              Throwable throwable) {
            LogUtils.e(TAG, throwable.getMessage());
            PublicUtil.showToastServerOvertime();
        }
    };

    /**
     * 更新无编码打卡地址信息
     */
    private void refreshRegion() {
        if (regionInfo == null) {
            return;
        }
        if (punchCardOther == null) {
            punchCardOther = new PunchCard();
        }
        punchCardOther.setProvinceName(regionInfo.provinceName);
        punchCardOther.setCityName(regionInfo.cityName);
        punchCardOther.setCountyName(regionInfo.countyName);
        txtRegionOther.setText(CardUtils.getWorkPlaceAddress(punchCardOther));
    }


    // 动画实际执行
    private void startPropertyAnim(View view) {
        // 第二个参数"rotation"表明要执行旋转
        // 0f -> 360f，从旋转360度，也可以是负值，负值即为逆时针旋转，正值是顺时针旋转。
        anim = ObjectAnimator.ofFloat(view, "rotation", 0f, 359f);

        // 动画的持续时间，执行多久？
        anim.setDuration(1500);
        anim.setRepeatMode(Animation.RESTART);
        anim.setRepeatCount(Animation.INFINITE);
        anim.setInterpolator(new AccelerateInterpolator());

        // 正式开始启动执行动画
        anim.start();
    }

    // 动画实际执行
    private void stopPropertyAnim() {
        if (null != anim && anim.isStarted()) {
            // 正式开始启动执行动画
            anim.cancel();
        }
    }

    AsyncHttpResponseHandler confirmAttendanceHandler = new TextHttpResponseHandler(HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            LogUtils.d(TAG, "confirmAttendance result = " + responseString);
            if (progressDialog != null)
                progressDialog.dismiss();
            btnPunchCard.setClickable(true);
            try {
                ResultBase baseResult = JSON.parseObject(responseString, ResultBase.class);
                if (baseResult.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    PublicUtil.showToast(baseResult.getResponseMsg());
                    setResult(2);
                    finish();
                } else if (baseResult.getResponseCode() == Constants.RESPONSE_RESULT_CARD_FAIL) {
                    showErrorCodeDialog(baseResult.getResponseMsg(), 2);
                } else {
                    PublicUtil.showErrorMsg(main, baseResult);
                }
            } catch (Exception e) {
                LogUtils.e(TAG, e.getMessage());
                PublicUtil.showToastServerBusy();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString,
                              Throwable throwable) {
            LogUtils.e(TAG, throwable.getMessage());
            if (progressDialog != null)
                progressDialog.dismiss();
            btnPunchCard.setClickable(true);
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
    public void onSelect(Area provinceArea, Area cityArea, Area countryArea) {
        regionInfo = new RegionInfo();
        // 选择地区结束，返回的结果
        if (provinceArea != null) {
            regionInfo.provinceCode = provinceArea.getDcode();
            regionInfo.provinceName = provinceArea.getDname();
            if (cityArea != null) {
                regionInfo.cityCode = cityArea.getDcode();
                regionInfo.cityName = cityArea.getDname();
                if (countryArea != null) {
                    regionInfo.countyCode = countryArea.getDcode();
                    regionInfo.countyName = countryArea.getDname();
                }
            }
        }
        refreshRegion();
    }

    public class CardOnPageChangeListener implements ViewPager.OnPageChangeListener {

        public void onPageScrollStateChanged(int arg0) {


        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {


        }

        public void onPageSelected(final int arg0) {
            currentItem = arg0;
//            refreshBtn();
            if (arg0 == 0) {
                if (PunchCardType.scan != punchCardType) {
                    punchCardType = PunchCardType.code;
                }
                //layoutTab.barChange(0);

            } else {
                punchCardType = PunchCardType.other;
                //layoutTab.barChange(1);
                if (tagListViewOther.getTags() == null || tagListViewOther.getTags().size() <= 0) {
//                    if (progressDialog != null) progressDialog.show();
                    DeliveryApi.getWorkTask(ClientStateManager.getLoginToken(main), WorkPlaceType
                            .other.toString(), getWorkTaskHandler);
                }
            }

        }

    }

    public class CardViewPageAdapter extends PagerAdapter {
        private List<View> mListViews;

        public CardViewPageAdapter(List<View> mListViews) {
            this.mListViews = mListViews;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mListViews.get(position));
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mListViews.get(position), 0);
            return mListViews.get(position);
        }

        @Override
        public int getCount() {
            return mListViews.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

    }

}


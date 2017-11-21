package cn.com.bluemoon.delivery.module.card;

import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
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
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.ui.TabSelector;
import cn.com.bluemoon.delivery.ui.dialog.AddressSelectPopWindow;
import cn.com.bluemoon.delivery.ui.dialog.AddressSelectPopWindow.IAddressSelectDialog;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.tagview.TagListView;
import cn.com.bluemoon.lib.utils.LibViewUtil;
import cn.com.bluemoon.lib.view.CommonAlertDialog;

/**
 * 上班卡
 */
public class PunchCardOndutyActivity extends BaseActivity implements IAddressSelectDialog,
        OnPageChangeListener, View.OnClickListener {

    @Bind(R.id.layout_tab)
    TabSelector layoutTab;
    @Bind(R.id.btn_punch_card)
    Button btnPunchCard;
    @Bind(R.id.text_address)
    TextView txtCurrentAddress;
    @Bind(R.id.img_address_refresh)
    ImageView imgAddressRefresh;
    @Bind(R.id.viewpager)
    ViewPager viewPager;
    @Bind(R.id.layout_main)
    LinearLayout mainView;
    @Bind(R.id.layout_address)
    LinearLayout layoutAddress;
    private TagListView tagListViewCode;
    private TagListView tagListViewOther;
    private LinearLayout layoutChooseAddressCode;
    private LinearLayout layoutChooseAddressOther;
    private TextView txtAddressCode;
    private TextView txtRegionOther;
    private TextView etAddressOther;
    private TextView txtCharge;
    private TextView txtCardAddress;
    private int currentItem = 0;
    private String workplaceCodeStr;
    private PunchCard punchCardCode;
    private PunchCard punchCardOther;
    private PunchCardType punchCardType;
    public LocationClient mLocationClient = null;
    private RegionInfo regionInfo;
    //是否正在定位
    private boolean isInit = true;
    //是否已经获取到定位的省市区,即是否可以选择地址（用于无编码打卡）
    private boolean isCanSelect;
    ObjectAnimator anim;
    private final int REQUEST_ADD_PUNCH_CARD_IN = 1;
    private final int REQUEST_GET_GPS_REGION = 2;
    private final int REQUEST_GET_GPS_ADDRESS = 3;
    private final int REQUEST_GET_WORK_TASK = 4;
    private final int REQUEST_CHECK_SCAN_CODE_CARD = 5;
    public BDLocationListener myListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            LogUtils.d("TAG", "result =" + location.getLatitude());
            LogUtils.d("TAG", "result =" + location.getLongitude());
            LogUtils.d("TAG", "result =" + location.getAltitude());
            mLocationClient.stop();
            PunchCard punchCard = new PunchCard();
            if (isInit) {
                displayLocationInfo(location, punchCard);
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
                getLocationPunchCard(location, punchCard, workTask);
            }
            isInit = false;
        }
    };

    /**
     * 获取经纬度
     *
     * @param location
     */
    private void getLocationPunchCard(BDLocation location, final PunchCard punchCard, final String workTask) {
        punchCard.setPunchCardType(punchCardType.toString());
        if (location.getLocType() == BDLocation.TypeGpsLocation
                || location.getLocType() == BDLocation.TypeNetWorkLocation) {
            //获取经纬度成功了！！
            punchCard.setLatitude(location.getLatitude());
            punchCard.setLongitude(location.getLongitude());
            punchCard(punchCard, workTask);
        } else {
            punchCard.setLatitude(Constants.UNKNOW_LATITUDE);
            punchCard.setLongitude(Constants.UNKNOW_LONGITUDE);
            new CommonAlertDialog.Builder(this)
                    .setMessage(R.string.sure_punch_card)
                    .setTxtGravity(Gravity.CENTER)
                    .setNegativeButton(R.string.dialog_continue, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            punchCard(punchCard, workTask);
                        }
                    })
                    .setPositiveButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            btnPunchCard.setClickable(true);
                        }
                    })
                    .setCancelable(false).show();
        }
    }

    /**
     * 调用打卡接口
     * @param punchCard
     * @param workTask
     */
    private void punchCard(PunchCard punchCard, String workTask) {
        if (TextUtils.isEmpty(getToken())
                || TextUtils.isEmpty(workTask)) {
            btnPunchCard.setClickable(true);
            return;
        }
        showWaitDialog(false);
        DeliveryApi.addPunchCardIn(getToken(), punchCard,
                workTask, getNewHandler(REQUEST_ADD_PUNCH_CARD_IN, ResultBase.class));
    }

    /**
     * 显示地理信息
     *
     * @param location
     */
    private void displayLocationInfo(BDLocation location, PunchCard punchCard) {
        //获取首页地址信息
        if (location.getLocType() == BDLocation.TypeOffLineLocation) {
            punchCard.setLatitude(Constants.UNKNOW_LATITUDE);
            punchCard.setLongitude(Constants.UNKNOW_LONGITUDE);
            txtCurrentAddress.setText(getString(R.string.work_address_fail_txt));
            hideWaitDialog();
            isCanSelect = true;
        } else {
            PunchParam param = new PunchParam();
            param.setLatitude(location.getLatitude());
            param.setLongitude(location.getLongitude());
            param.setToken(getToken());
            // 获取定位地址
            DeliveryApi.getGpsAddress(param, getNewHandler(REQUEST_GET_GPS_ADDRESS, ResultAddressInfo.class));
            //  2017/10/17 获取无编码打卡的省市区
            DeliveryApi.getGPSRegion(param, getNewHandler(REQUEST_GET_GPS_REGION, ResultGPSRegion.class));
        }
    }

    @Override
    public void initView() {
        btnPunchCard.setOnClickListener(this);
        if (getIntent() != null) {
            workplaceCodeStr = getIntent().getStringExtra("code");
            if (!TextUtils.isEmpty(workplaceCodeStr)) {
                punchCardType = PunchCardType.scan;
            }
        }
        if (punchCardType == null) {
            punchCardType = PunchCardType.code;
        }

        layoutAddress.setOnClickListener(this);
        List<View> views = new ArrayList<>();
        LayoutInflater inflater = getLayoutInflater();
        View viewCode = inflater.inflate(R.layout.view_punch_card_input_code, null);
        View viewOther = inflater.inflate(R.layout.view_punch_card_input_other, null);
        views.add(viewCode);
        views.add(viewOther);
        viewPager.setAdapter(new CardViewPageAdapter(views));
        viewPager.setOnPageChangeListener(this);
        //viewCode
        ImageView imgRight = (ImageView) viewCode.findViewById(R.id.img_right);
        txtAddressCode = (TextView) viewCode.findViewById(R.id.txt_address_code);
        txtCharge = (TextView) viewCode.findViewById(R.id.txt_charge);
        txtCardAddress = (TextView) viewCode.findViewById(R.id.txt_card_address);
        tagListViewCode = (TagListView) viewCode.findViewById(R.id.tag_listview_card_code);
        layoutChooseAddressCode = (LinearLayout) viewCode.findViewById(R.id
                .layout_choose_address_code);
        layoutChooseAddressCode.setOnClickListener(this);

        //viewOther
        txtRegionOther = (TextView) viewOther.findViewById(R.id.txt_region_other);
        etAddressOther = (TextView) viewOther.findViewById(R.id.et_address_other);
        tagListViewOther = (TagListView) viewOther.findViewById(R.id.tag_listview_card_other);
        layoutChooseAddressOther = (LinearLayout) viewOther.findViewById(R.id
                .layout_choose_address_other);
        layoutChooseAddressOther.setOnClickListener(this);
        if (PunchCardType.scan == punchCardType) {
            layoutTab.setVisibility(View.GONE);
            imgRight.setVisibility(View.GONE);
            txtAddressCode.setHint("");
            layoutChooseAddressCode.setClickable(false);
            setDataByCode(workplaceCodeStr);
        }
        viewPager.setCurrentItem(0);
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
            }
        });

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
        anim = CardUtils.startPropertyAnim(imgAddressRefresh);
        layoutTab.setOnClickListener(new TabSelector.CallBackListener() {

            @Override
            public void onClick(int currentTab) {
                LibViewUtil.hideIM(layoutTab);
                viewPager.setCurrentItem(currentTab);
            }
        });

    }

    @Override
    public void initData() {

    }

    @Override
    protected String getTitleString() {
        return getString(R.string.main_tab_card);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_punch_card_input_onduty;
    }

    @Override
    public void onErrorResponse(int requestCode, ResultBase result) {
        if (requestCode == REQUEST_ADD_PUNCH_CARD_IN
                && result.getResponseCode() == Constants.RESPONSE_RESULT_CARD_FAIL) {
            showErrorCodeDialog(result.getResponseMsg(), 2);
        } else if (requestCode == REQUEST_CHECK_SCAN_CODE_CARD
                && result.getResponseCode() != Constants.RESPONSE_RESULT_TOKEN_EXPIRED) {
            showErrorCodeDialog(result.getResponseMsg(), RESULT_CANCELED);
        } else {
            super.onErrorResponse(requestCode, result);
        }

    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        if (requestCode == REQUEST_ADD_PUNCH_CARD_IN) {
            toast(result.getResponseMsg());
            setResult(2);
            finish();
        } else if (requestCode == REQUEST_GET_GPS_REGION) {
            ResultGPSRegion baseResult = (ResultGPSRegion) result;
            regionInfo = baseResult.regionInfo;
            refreshRegion();
        } else if (requestCode == REQUEST_GET_GPS_ADDRESS) {
            ResultAddressInfo ResultAddressInfo = (ResultAddressInfo) result;
            txtCurrentAddress.setText(ResultAddressInfo.getAddressInfo().getFormattedAddress());
            CardUtils.stopPropertyAnim(anim);
            layoutAddress.setClickable(true);
        } else if (requestCode == REQUEST_GET_WORK_TASK) {
            ResultGetWorkTask getWorkTaskResult = (ResultGetWorkTask) result;
            CardUtils.setTags(getWorkTaskResult.getWorkTaskList(), tagListViewOther, false);
        } else if (requestCode == REQUEST_CHECK_SCAN_CODE_CARD) {
            if (punchCardCode == null) {
                punchCardCode = new PunchCard();
            }
            ResultCheckScanCode checkScanCodeResult = (ResultCheckScanCode) result;
            PunchCard punchCard = checkScanCodeResult.getPunchCard();
            punchCardCode.setAttendanceCode(punchCard.getAttendanceCode());
            txtAddressCode.setText(punchCard.getAttendanceCode());
            txtCharge.setText(CardUtils.getChargeNoCode(punchCard));
            txtCardAddress.setText(CardUtils.getAddress(punchCard));
            CardUtils.setTags(checkScanCodeResult.getWorkTaskList(), tagListViewCode, false);
        }
    }

    @Override
    public void onSuccessException(int requestCode, Throwable t) {
        super.onSuccessException(requestCode, t);
        if (requestCode == REQUEST_GET_GPS_ADDRESS) {
            txtCurrentAddress.setText(getString(R.string.work_address_fail_txt));
            CardUtils.stopPropertyAnim(anim);
            layoutAddress.setClickable(true);
        }
    }

    @Override
    public void onFailureResponse(int requestCode, Throwable t) {
        super.onFailureResponse(requestCode, t);
        if (requestCode == REQUEST_GET_GPS_ADDRESS) {
            txtCurrentAddress.setText(getString(R.string.work_address_fail_txt));
            CardUtils.stopPropertyAnim(anim);
            layoutAddress.setClickable(true);
        }
    }

    @Override
    public void onFinishResponse(int requestCode) {
        super.onFinishResponse(requestCode);
        if (requestCode == REQUEST_ADD_PUNCH_CARD_IN) {
            btnPunchCard.setClickable(true);
        } else if (requestCode == REQUEST_GET_GPS_REGION) {
            isCanSelect = true;
        }
    }

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
        new CommonAlertDialog.Builder(this)
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

    private void setDataByCode(String code) {
        if (!TextUtils.isEmpty(code)) {
            workplaceCodeStr = code;
            showWaitDialog(false);
            DeliveryApi.checkScanCodeCard(getToken(), workplaceCodeStr,
                    getNewHandler(REQUEST_CHECK_SCAN_CODE_CARD, ResultCheckScanCode.class));
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
                    .getCountyName())) {
                PublicUtil.showToast(R.string.card_region_right);
            } else if (TextUtils.isEmpty(etAddressOther.getText().toString().trim())) {
                PublicUtil.showToast(R.string.card_address_cannot_empty);
            } else if (tagListViewOther.getTagsChecked().size() <= 0) {
                PublicUtil.showToast(R.string.card_worktask_cannot_empty);
            } else {
                return true;
            }
        }
        return false;
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

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        currentItem = position;
        if (position == 0) {
            if (PunchCardType.scan != punchCardType) {
                punchCardType = PunchCardType.code;
            }

        } else {
            punchCardType = PunchCardType.other;
            if (tagListViewOther.getTags() == null || tagListViewOther.getTags().size() <= 0) {
                DeliveryApi.getWorkTask(getToken(), WorkPlaceType.other.toString(),
                        getNewHandler(REQUEST_GET_WORK_TASK, ResultGetWorkTask.class));
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onClick(View v) {
        LibViewUtil.hideIM(v);
        if (isInit) {
            toast(R.string.get_on_location);
            return;
        }
        switch (v.getId()) {
            case R.id.btn_punch_card:
                if (!checkSumbit()) {
                    return;
                }
                btnPunchCard.setClickable(false);
                mLocationClient.start();
                break;
            case R.id.layout_choose_address_code:
                GetWorkPlaceActivity.startAct(this, txtAddressCode.getText().toString(), 1);
                break;
            case R.id.layout_choose_address_other:
                if (!isCanSelect) {
                    toast(R.string.get_on_location);
                    return;
                }
                String pId = "", cId = "", coId = "";
                if (regionInfo != null) {
                    pId = regionInfo.provinceCode;
                    cId = regionInfo.cityCode;
                    coId = regionInfo.countyCode;
                }
                AddressSelectPopWindow window = AddressSelectPopWindow.newInstance(this, pId
                        , cId, coId);
                window.setListener(this);
                window.show(mainView);
                break;
            case R.id.layout_address:
                isInit = true;
                txtCurrentAddress.setText(getString(R.string.work_address_fail_txt));
                layoutAddress.setClickable(false);
                anim = CardUtils.startPropertyAnim(imgAddressRefresh);
                mLocationClient.start();
                break;
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
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLocationClient != null) {
            mLocationClient.unRegisterLocationListener(myListener);
            mLocationClient = null;
        }

    }
}

package cn.com.bluemoon.delivery.module.card;


import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import bluemoon.com.lib_x5.utils.X5PermissionsUtil;
import butterknife.Bind;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.card.PunchCard;
import cn.com.bluemoon.delivery.app.api.model.card.PunchParam;
import cn.com.bluemoon.delivery.app.api.model.card.ResultAddressInfo;
import cn.com.bluemoon.delivery.app.api.model.punchcard.ResultShowPunchCardDetail;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.module.base.BaseFragment;
import cn.com.bluemoon.delivery.module.event.PunchCardEvent;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.ImageRotateUtil;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.tagview.TagListView;
import cn.com.bluemoon.lib.view.CommonAlertDialog;

/**
 * 下班卡
 */
public class PunchCardGetOffWordFragment extends BaseFragment {
    @Bind(R.id.text_address)
    TextView txtCurrentAddress;
    @Bind(R.id.img_address_refresh)
    ImageView imgAddressRefresh;
    @Bind(R.id.layout_address)
    LinearLayout layoutAddress;
    @Bind(R.id.tv_start_time)
    TextView txtStartTime;
    @Bind(R.id.txt_name_and_mobile)
    TextView txtNameAndMobile;
    @Bind(R.id.txt_address)
    TextView txtAddress;
    @Bind(R.id.img_address_down)
    ImageView imgAddressDown;
    @Bind(R.id.line_view_address)
    View lineAddressView;
    @Bind(R.id.txt_start_time)
    TextView txtAddressStartTime;
    @Bind(R.id.txt_start_address)
    TextView txtAddressStart;
    @Bind(R.id.layout_start_address)
    LinearLayout layoutStartAddress;
    @Bind(R.id.img_down)
    ImageView imgDown;
    @Bind(R.id.line_view)
    View lineView;
    @Bind(R.id.tag_listview)
    TagListView tagListView;
    @Bind(R.id.txt_log_content)
    TextView txtLogContent;
    @Bind(R.id.txt_diary_content)
    TextView txtDiaryContent;
    @Bind(R.id.txt_img_content)
    TextView txtImgContent;

    private final int REQUEST_ADD_PUNCH_CARD_OUT = 1;
    private final int REQUEST_GET_GPS_ADDRESS = 2;
    private final int REQUEST_GET_PUNCH_CARD = 3;

    public LocationClient mLocationClient = null;
    PunchCard punchCard;
    boolean hasWorkDiary = true;

    private boolean control; //控制多次点击
    private boolean isInit;
    private boolean isPunchCard;
    ObjectAnimator anim;

    public BDLocationListener myListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            LogUtils.d("location.getLatitude= " + location.getLatitude());
            LogUtils.d("location.getLongitude= " + location.getLongitude());
            LogUtils.d("location.getAltitude= " + location.getAltitude());
            mLocationClient.stop();
            if (isInit) {
                displayLocationInfo(location);
            } else if (isPunchCard) {
                getLocationPunchCard(location);
            }
        }
    };

    /**
     * 显示地理信息
     *
     * @param location
     */
    private void displayLocationInfo(BDLocation location) {
        //获取首页地址信息
        if (location.getLocType() == BDLocation.TypeOffLineLocation) {
            txtCurrentAddress.setText(getString(R.string.work_address_fail_txt));
            punchCard.setLatitude(Constants.UNKNOW_LATITUDE);
            punchCard.setLongitude(Constants.UNKNOW_LONGITUDE);
            hideWaitDialog();
        } else {
            PunchParam param = new PunchParam();
            param.setLatitude(location.getLatitude());
            param.setLongitude(location.getLongitude());
            param.setToken(ClientStateManager.getLoginToken());
            DeliveryApi.getGpsAddress(param, getNewHandler(REQUEST_GET_GPS_ADDRESS, ResultAddressInfo.class));
        }
        isInit = false;
    }

    /**
     * 获取经纬度并且调用打卡接口
     *
     * @param location
     */
    private void getLocationPunchCard(BDLocation location) {
        String message;
        String negativeText;
        String positiveText;
        if (location.getLocType() == BDLocation.TypeGpsLocation
                || location.getLocType() == BDLocation.TypeNetWorkLocation) {
            //获取经纬度成功了！！
            punchCard.setLatitude(location.getLatitude());
            punchCard.setLongitude(location.getLongitude());
            message = getString(R.string.sure_get_off_work);
            negativeText = getString(R.string.yes);
            positiveText = getString(R.string.no);
        } else {
            punchCard.setLatitude(Constants.UNKNOW_LATITUDE);
            punchCard.setLongitude(Constants.UNKNOW_LONGITUDE);
            message = getString(R.string.sure_get_off_work2);
            negativeText = getString(R.string.dialog_continue);
            positiveText = getString(R.string.dialog_cancel);
        }
        new CommonAlertDialog.Builder(getActivity())
                .setMessage(message)
                .setTxtGravity(Gravity.CENTER)
                .setNegativeButton(negativeText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showWaitDialog(false);
                        DeliveryApi.addPunchCardOut(getToken(), punchCard,
                                CardUtils.getWorkTaskString(tagListView.getTagsChecked()),
                                getNewHandler(REQUEST_ADD_PUNCH_CARD_OUT, ResultBase.class));
                    }
                })
                .setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        control = false;
                    }
                })
                .setCancelable(false).show();
        isPunchCard = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLocationClient != null) {
            mLocationClient.unRegisterLocationListener(myListener);
            mLocationClient = null;
        }
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.puncard_info);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.get_off_work;
    }


    @Override
    public void initView() {
        mLocationClient = new LocationClient(getActivity());
        mLocationClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        mLocationClient.setLocOption(option);
        isInit = true;
        isPunchCard = false;
        txtCurrentAddress.setText(getString(R.string.work_address_fail_txt));
        mLocationClient.start();
        anim = CardUtils.startPropertyAnim(imgAddressRefresh);
        layoutAddress.setClickable(false);
        showWaitDialog(false);
    }


    @Override
    public void initData() {
        DeliveryApi.getPunchCard(getToken(), getNewHandler(REQUEST_GET_PUNCH_CARD, ResultShowPunchCardDetail.class));
    }

    @OnClick({R.id.layout_address, R.id.btn_punch_card, R.id.layout_work_diary,
            R.id.layout_upload_image, R.id.layout_work_log, R.id.layout_work_task, R.id.layout_start_card})
    public void onClick(View v) {
        if (!control) {
            control = true;
            if (isInit) {
                control = false;
                PublicUtil.showToast(getString(R.string.get_on_location));
                return;
            }
            switch (v.getId()) {
                case R.id.layout_start_card: //开始打卡下拉信息
                    if (layoutStartAddress.getVisibility() == View.VISIBLE) {
                        ImageRotateUtil.rotateArrow(true, imgAddressDown);
                        layoutStartAddress.setVisibility(View.GONE);
                        lineAddressView.setVisibility(View.GONE);
                    } else {
                        ImageRotateUtil.rotateArrow(false, imgAddressDown);
                        layoutStartAddress.setVisibility(View.VISIBLE);
                        lineAddressView.setVisibility(View.VISIBLE);
                    }
                    control = false;
                    break;
                case R.id.layout_work_task: //工作任务下拉信息
                    if (tagListView.getVisibility() == View.VISIBLE) {
                        ImageRotateUtil.rotateArrow(true, imgDown);
                        tagListView.setVisibility(View.GONE);
                        lineView.setVisibility(View.GONE);
                    } else {
                        ImageRotateUtil.rotateArrow(false, imgDown);
                        tagListView.setVisibility(View.VISIBLE);
                        lineView.setVisibility(View.VISIBLE);
                    }
                    control = false;
                    break;
                case R.id.layout_address:
                    //顶部定位（定位中...)
                    isInit = true;
                    anim = CardUtils.startPropertyAnim(imgAddressRefresh);
                    txtCurrentAddress.setText(getString(R.string.work_address_fail_txt));
                    layoutAddress.setClickable(false);
                    mLocationClient.start();
                    control = false;
                    break;
                case R.id.btn_punch_card:
                    validInfo();
                    break;
                case R.id.layout_work_diary:
                    //日报
                    WorkDiaryActivity.startAct(getActivity());
                    break;
                case R.id.layout_work_log:
                    //日志
                    LogActivity.startAct(getActivity(), hasWorkDiary);
                    break;
                case R.id.layout_upload_image:
                    //上传照片
                    UploadImageActivity.startAct(getActivity());
                    break;
            }
        }

    }

    /**
     * 验证打卡信息
     */
    private void validInfo() {
        if (punchCard != null) {
            if (tagListView.getTagsChecked().size() == 0) {
                control = false;
                toast(R.string.card_worktask_cannot_empty);
            } else if (!hasWorkDiary) {
                control = false;
                toast(R.string.log_not_input);
            } else {
                //先判断是否开了gps
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    if (!PublicUtil.isOPenLocation(getActivity())) {
                        control = false;
                        PublicUtil.showLocationSettingDialog(getActivity());
                    }
                } else {
                    String[] permissions = X5PermissionsUtil.PERMISSION_LOCATION;
                    if (X5PermissionsUtil.lacksPermissions(getActivity(), permissions)) {
                        control = false;
                        this.requestPermissions(permissions,1);
                    }
                }
                if (control) {
                    isPunchCard = true;
                    if (mLocationClient.isStarted()) {
                        mLocationClient.stop();
                    }
                    mLocationClient.start();
                }

            }
        } else {
            control = false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0; i < grantResults.length; ++i) {
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                //在用户已经拒绝授权的情况下，如果shouldShowRequestPermissionRationale返回false则
                // 可以推断出用户选择了“不在提示”选项，在这种情况下需要引导用户至设置页手动授权
                if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permissions[i])) {
                    PublicUtil.showLocationSettingDialog(getActivity());
                    break;
                }
            }
        }
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase resultBase) {
        switch (requestCode) {
            case REQUEST_ADD_PUNCH_CARD_OUT:
                toast(resultBase.getResponseMsg());
                getActivity().finish();
                break;
            case REQUEST_GET_GPS_ADDRESS:
                ResultAddressInfo resultAddressInfo = (ResultAddressInfo) resultBase;
                String address = resultAddressInfo.getAddressInfo().getFormattedAddress();
                if (TextUtils.isEmpty(address)) {
                    address = getString(R.string.address_fail_txt);
                }
                txtCurrentAddress.setText(address);
                CardUtils.stopPropertyAnim(anim);
                layoutAddress.setClickable(true);
                break;
            case REQUEST_GET_PUNCH_CARD:
                ResultShowPunchCardDetail result = (ResultShowPunchCardDetail) resultBase;
                punchCard = result.getPunchCard();
                if (!(tagListView.getTags() != null && tagListView.getTags().size() > 0)) {
                    CardUtils.setTags(result.getWorkTaskList(), tagListView, true);
                }
                txtStartTime.setText(DateUtil.getTime(punchCard.getPunchInTime(), "yyyy-MM-dd" +
                        " HH:mm"));
                txtAddressStartTime.setText(DateUtil.getTime(punchCard.getPunchInTime(),
                        "yyyy-MM-dd HH:mm"));
                address = punchCard.getPunchInGpsAddress();
                if (TextUtils.isEmpty(address)) {
                    address = getString(R.string.address_no_gps_txt);
                }
                txtAddressStart.setText(address);
                txtNameAndMobile.setText(CardUtils.getCharge(punchCard));
                txtAddress.setText(CardUtils.getAddress(punchCard));
                if (punchCard.getHasWorkDiary()) {
                    txtLogContent.setText(getString(R.string.work_log_show));
                    hasWorkDiary = true;
                } else {
                    hasWorkDiary = false;
                    txtLogContent.setText(getString(R.string.input_today_log));
                }
                if (punchCard.getTotalBreedSalesNum() > 0) {
                    txtDiaryContent.setText(String.valueOf(punchCard.getTotalSalesNum()));
                } else {
                    txtDiaryContent.setText(getString(R.string.input_today_sales));
                }
                if (punchCard.getUploadImgNum() > 0) {
                    txtImgContent.setText(String.valueOf(punchCard.getUploadImgNum()));
                } else {
                    txtImgContent.setText(getString(R.string.upload_image));
                }
                break;
        }
    }

    @Override
    public void onErrorResponse(int requestCode, ResultBase result) {
        super.onErrorResponse(requestCode, result);
        if (requestCode == REQUEST_ADD_PUNCH_CARD_OUT) {
            control = false; //失败释放控制
        }
    }

    @Override
    public void onSuccessException(int requestCode, Throwable t) {
        super.onSuccessException(requestCode, t);
        if (requestCode == REQUEST_GET_GPS_ADDRESS) {
            txtCurrentAddress.setText(getString(R.string.work_address_fail_txt));
            CardUtils.stopPropertyAnim(anim);
            layoutAddress.setClickable(true);
        } else if (requestCode == REQUEST_ADD_PUNCH_CARD_OUT) {
            control = false; //失败释放控制
        }
    }


    @Override
    public void onFailureResponse(int requestCode, Throwable t) {
        super.onFailureResponse(requestCode, t);
        if (requestCode == REQUEST_GET_GPS_ADDRESS) {
            txtCurrentAddress.setText(getString(R.string.work_address_fail_txt));
            CardUtils.stopPropertyAnim(anim);
            layoutAddress.setClickable(true);
        } else if (requestCode == REQUEST_ADD_PUNCH_CARD_OUT) {
            control = false; //失败释放控制
        }
    }

    @Override
    protected boolean isUseEventBus() {
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        control = false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(PunchCardEvent event) {
        initData(); //日志、日报、照片改变了刷新
    }
}

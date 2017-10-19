package cn.com.bluemoon.delivery.module.card;


import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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
import cn.com.bluemoon.delivery.app.api.model.card.PunchCard;
import cn.com.bluemoon.delivery.app.api.model.card.PunchParam;
import cn.com.bluemoon.delivery.app.api.model.card.ResultAddressInfo;
import cn.com.bluemoon.delivery.app.api.model.card.WorkTask;
import cn.com.bluemoon.delivery.app.api.model.punchcard.ResultShowPunchCardDetail;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.module.base.interf.IActionBarListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.AccelerateInterpolator;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.ImageRotateUtil;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.tagview.Tag;
import cn.com.bluemoon.lib.tagview.TagListView;
import cn.com.bluemoon.lib.view.CommonAlertDialog;
import cn.com.bluemoon.lib.view.CommonProgressDialog;

public class PunchCardGetOffWordFragment extends Fragment implements OnClickListener {
    private String TAG = "PunchCardGetOffWordFragment";
    private CardTabActivity mContext;
    private ImageView imgBanner;
    private CommonProgressDialog progressDialog;
    private TagListView tagListView;
    private TextView txtStartTime;
    private TextView txtNameAndMobile;
    private TextView txtAddress;
    private TextView txtLogContent;
    private TextView txtDiaryContent;
    private TextView txtImgContent;
    private LinearLayout layoutWorkTask;
    private LinearLayout layoutWorkLog;
    private LinearLayout layoutWorkDiary;
    private LinearLayout layoutUploadImage;
    private TextView txtCurrentAddress;
    private ImageView imgAddressRefresh;
    private View lineView;
    private View lineAdressView;
    private ImageView imgDown;
    private Button btnPunchCard;
    private LinearLayout layoutStartCard;
    private LinearLayout layoutStartAddress;
    private ImageView imgAddressDown;
    private TextView txtAddressStartTime;
    private TextView txtAddressStart;
    private LinearLayout layoutAddress;
    public LocationClient mLocationClient = null;
    PunchCard punchCard;
    boolean hasWorkDiary = true;
    boolean hasWrokDaily = true;
    boolean hasImage = true;
    private List<Tag> mTags;
    private boolean control;
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

            LogUtils.d("aaaa isInit===============>" + isInit);
            LogUtils.d("aaaa isPunchCard===============>" + isPunchCard);

            if (isInit) {
                //获取首页地址信息
                if (location.getLocType() == BDLocation.TypeOffLineLocation) {
                    txtCurrentAddress.setText(getString(R.string.work_address_fail_txt));

                    punchCard.setLatitude(Constants.UNKNOW_LATITUDE);
                    punchCard.setLongitude(Constants.UNKNOW_LONGITUDE);
                    if (progressDialog != null)
                        progressDialog.dismiss();
                } else {
                    PunchParam param = new PunchParam();
                    param.setLatitude(location.getLatitude());
                    param.setLongitude(location.getLongitude());
                    param.setToken(ClientStateManager.getLoginToken());
                    DeliveryApi.getGpsAddress(param, gpsHandler);
                }
                isInit = false;
            } else if(isPunchCard) {
                if (location.getLocType() == BDLocation.TypeOffLineLocation) {
                    punchCard.setLatitude(Constants.UNKNOW_LATITUDE);
                    punchCard.setLongitude(Constants.UNKNOW_LONGITUDE);
                } else {
                    punchCard.setLatitude(location.getLatitude());
                    punchCard.setLongitude(location.getLongitude());
                }

                //punchCard.setAltitude(location.getAltitude());
                progressDialog.show();
                DeliveryApi.addPunchCardOut(ClientStateManager.getLoginToken(),
                        punchCard, CardUtils.getWorkTaskString(tagListView.getTagsChecked()),
                        confirmAttendanceHandler);
                isPunchCard = false;
            }
        }
    };

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mContext = (CardTabActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initCustomActionBar();

        View v = inflater.inflate(R.layout.get_off_work, container, false);

        layoutStartAddress = (LinearLayout) v.findViewById(R.id.layout_start_address);
        layoutStartCard = (LinearLayout) v.findViewById(R.id.layout_start_card);
        imgAddressDown = (ImageView) v.findViewById(R.id.img_address_down);
        txtAddressStartTime = (TextView) v.findViewById(R.id.txt_start_time);
        txtAddressStart = (TextView) v.findViewById(R.id.txt_start_address);
        layoutAddress = (LinearLayout) v.findViewById(R.id.layout_address);
        tagListView = (TagListView) v.findViewById(R.id.tag_listview);
        txtStartTime = (TextView) v.findViewById(R.id.tv_start_time);
        txtNameAndMobile = (TextView) v.findViewById(R.id.txt_name_and_mobile);
        txtAddress = (TextView) v.findViewById(R.id.txt_address);
        txtLogContent = (TextView) v.findViewById(R.id.txt_log_content);
        txtDiaryContent = (TextView) v.findViewById(R.id.txt_diary_content);
        txtImgContent = (TextView) v.findViewById(R.id.txt_img_content);
        layoutWorkTask = (LinearLayout) v.findViewById(R.id.layout_work_task);
        layoutWorkLog = (LinearLayout) v.findViewById(R.id.layout_work_log);
        layoutWorkDiary = (LinearLayout) v.findViewById(R.id.layout_work_diary);
        layoutUploadImage = (LinearLayout) v.findViewById(R.id.layout_upload_image);
        btnPunchCard = (Button) v.findViewById(R.id.btn_punch_card);
        imgDown = (ImageView) v.findViewById(R.id.img_down);
        lineView = v.findViewById(R.id.line_view);
        lineAdressView = v.findViewById(R.id.line_view_address);
        txtCurrentAddress = (TextView) v.findViewById(R.id.text_address);
        imgAddressRefresh = (ImageView) v.findViewById(R.id.img_address_refresh);

        layoutAddress.setOnClickListener(this);
        layoutWorkLog.setOnClickListener(this);
        layoutWorkDiary.setOnClickListener(this);
        layoutUploadImage.setOnClickListener(this);
        layoutStartCard.setOnClickListener(this);
        layoutWorkTask.setOnClickListener(this);
        btnPunchCard.setOnClickListener(this);
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
        startPropertyAnim(imgAddressRefresh);
        layoutAddress.setClickable(false);
        progressDialog = new CommonProgressDialog(mContext);
        progressDialog.setCancelable(false);
        progressDialog.show();
        DeliveryApi.getPunchCard(ClientStateManager.getLoginToken(getActivity()),
                getPunchCardHandler);
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
                    PublicUtil.showErrorMsg(mContext, baseResult);
                }
            } catch (Exception e) {
                LogUtils.e(TAG, e.getMessage());
                txtCurrentAddress.setText(getString(R.string.work_address_fail_txt));
                PublicUtil.showToastServerBusy();
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
            txtCurrentAddress.setText(getString(R.string.work_address_fail_txt));
            PublicUtil.showToastServerOvertime();
            stopPropertyAnim();
            layoutAddress.setClickable(true);
        }
    };


    // 动画实际执行
    private void startPropertyAnim(View view) {
        // 第二个参数"rotation"表明要执行旋转
        // 0f -> 360f，从旋转360度，也可以是负值，负值即为逆时针旋转，正值是顺时针旋转。
        anim = ObjectAnimator.ofFloat(view, "rotation", 0f, 359f);

        // 动画的持续时间，执行多久？
        anim.setDuration(1500);
        anim.setRepeatMode(ObjectAnimator.RESTART);
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
            control = false;
            if (progressDialog != null)
                progressDialog.dismiss();
            try {
                ResultBase baseResult = JSON.parseObject(responseString, ResultBase.class);
                if (baseResult.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    PublicUtil.showToast(baseResult.getResponseMsg());
                    getActivity().finish();
                } else {
                    PublicUtil.showErrorMsg(mContext, baseResult);
                }
            } catch (Exception e) {
                PublicUtil.showToastServerBusy();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString,
                              Throwable throwable) {
            control = false;
            if (progressDialog != null)
                progressDialog.dismiss();
            PublicUtil.showToastServerOvertime();
        }
    };

    AsyncHttpResponseHandler getPunchCardHandler = new TextHttpResponseHandler(HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            LogUtils.d("test", "getPunchCardHandler result = " + responseString);
            progressDialog.dismiss();
            try {
                ResultShowPunchCardDetail result = JSON.parseObject(responseString,
                        ResultShowPunchCardDetail.class);
                if (null != result && result.getResponseCode() == Constants
                        .RESPONSE_RESULT_SUCCESS) {
                    //PublicUtil.showToast(result.getResponseMsg());
                    punchCard = result.getPunchCard();
                    if (!(tagListView.getTags() != null && tagListView.getTags().size() > 0)) {
                        setTags(result.getWorkTaskList());
                    }
                    txtStartTime.setText(DateUtil.getTime(punchCard.getPunchInTime(), "yyyy-MM-dd" +
                            " HH:mm"));
                    txtAddressStartTime.setText(DateUtil.getTime(punchCard.getPunchInTime(),
                            "yyyy-MM-dd HH:mm"));
                    txtAddressStart.setText(punchCard.getPunchInGpsAddress());
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
                        hasWrokDaily = true;
                        txtDiaryContent.setText(String.valueOf(punchCard.getTotalSalesNum()));
                    } else {
                        hasWrokDaily = false;
                        txtDiaryContent.setText(getString(R.string.input_today_sales));
                    }
                    if (punchCard.getUploadImgNum() > 0) {
                        hasImage = true;
                        txtImgContent.setText(String.valueOf(punchCard.getUploadImgNum()));
                    } else {
                        hasImage = false;
                        txtImgContent.setText(getString(R.string.upload_image));
                    }

                    //DeliveryApi.getPunchCardById(ClientStateManager.getLoginToken(getActivity()
                    // ), Long.valueOf(punchCard.getPunchCardId()), getPunchCardByIdHandler);
                } else {
                    progressDialog.dismiss();
                    PublicUtil.showToast(result.getResponseMsg());
                }
            } catch (Exception e) {
                progressDialog.dismiss();
                PublicUtil.showToastServerBusy();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString,
                              Throwable throwable) {
            LogUtils.d("statusCode=" + statusCode);
            progressDialog.dismiss();
            PublicUtil.showToastServerOvertime();
        }
    };

    private void setTags(List<WorkTask> workTasks) {
        List<Tag> list = new ArrayList<>();
        if (workTasks != null) {
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
                v.setText(getText(R.string.puncard_info));
            }

        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        control = false;
        if (resultCode == 1) {
            DeliveryApi.getPunchCard(ClientStateManager.getLoginToken(getActivity()),
                    getPunchCardHandler);
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

    @Override
    public void onClick(View v) {

        if (!control) {
            control = true;
            if (isInit) {
                control = false;
                PublicUtil.showToast(getString(R.string.get_on_location));
                return;
            }
            if (v == layoutWorkDiary) {
                Intent intent = new Intent(mContext, WorkDiaryActivity.class);
                intent.putExtra("hasWrokDaily", hasWrokDaily);
                PunchCardGetOffWordFragment.this.startActivityForResult(intent, 1);
            } else if (v == layoutUploadImage) {
                Intent intent = new Intent(mContext, UploadImageActivity.class);
                intent.putExtra("hasImage", hasImage);
                PunchCardGetOffWordFragment.this.startActivityForResult(intent, 1);
            } else if (v == layoutWorkLog) {
                Intent intent = new Intent(mContext, LogActivity.class);
                intent.putExtra("hasWorkDiary", hasWorkDiary);
                PunchCardGetOffWordFragment.this.startActivityForResult(intent, 1);
            } else if (v == layoutWorkTask) {
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
            } else if (v == layoutStartCard) {
                if (layoutStartAddress.getVisibility() == View.VISIBLE) {
                    ImageRotateUtil.rotateArrow(true, imgAddressDown);
                    layoutStartAddress.setVisibility(View.GONE);
                    lineAdressView.setVisibility(View.GONE);
                } else {
                    ImageRotateUtil.rotateArrow(false, imgAddressDown);
                    layoutStartAddress.setVisibility(View.VISIBLE);
                    lineAdressView.setVisibility(View.VISIBLE);
                }
                control = false;
            } else if (v == btnPunchCard) {
                if (punchCard != null) {
                    if (tagListView.getTagsChecked().size() == 0) {
                        control = false;
                        PublicUtil.showToast(getString(R.string.card_worktask_cannot_empty));
                    } else if (!hasWorkDiary) {
                        control = false;
                        PublicUtil.showToast(getString(R.string.log_not_input));
                    } else {
                        new CommonAlertDialog.Builder(mContext).setMessage(getString(R.string
                                .sure_get_off_work)).setNegativeButton(R.string.yes, new
                                DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                isPunchCard = true;
                                if(mLocationClient.isStarted()){
                                    mLocationClient.stop();
                                }
                                progressDialog.show();
                                mLocationClient.start();
                            }
                        }).setPositiveButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                control = false;
                            }
                        }).setCancelable(false).show();
                    }
                } else {
                    control = false;
                }

            } else if (v == layoutAddress) {
                isInit = true;
                startPropertyAnim(imgAddressRefresh);
                txtCurrentAddress.setText(getString(R.string.work_address_fail_txt));
                layoutAddress.setClickable(false);
                mLocationClient.start();
                control = false;
            }
        }

    }
}

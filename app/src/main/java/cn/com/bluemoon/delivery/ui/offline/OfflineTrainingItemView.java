package cn.com.bluemoon.delivery.ui.offline;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.lib_widget.module.form.BMAngleBtn2View;

/**
 * Created by tangqiwei on 2017/6/1.
 */

public class OfflineTrainingItemView extends FrameLayout {

    @BindView(R.id.txt_course_title)
    TextView txtCourseTitle;
    @BindView(R.id.txt_state)
    TextView txtState;
    @BindView(R.id.txt_ytd)
    TextView txtYtd;
    @BindView(R.id.txt_time_quantum)
    TextView txtTimeQuantum;
    @BindView(R.id.txt_sign_in_time)
    TextView txtSignInTime;
    @BindView(R.id.llayout_sign_in_time)
    LinearLayout llayoutSignInTime;
    @BindView(R.id.txt_lecturer_name)
    TextView txtLecturerName;
    @BindView(R.id.llayout_lecturer_name)
    LinearLayout llayoutLecturerName;
    @BindView(R.id.txt_classroom)
    TextView txtClassroom;
    @BindView(R.id.layout_classroom)
    LinearLayout layoutClassroom;
    @BindView(R.id.txt_address)
    TextView txtAddress;
    @BindView(R.id.llayout_address)
    LinearLayout llayoutAddress;
    @BindView(R.id.txt_willnum)
    TextView txtWillnum;
    @BindView(R.id.llayout_willnum)
    LinearLayout llayoutWillnum;
    @BindView(R.id.txt_signed_in_the_number_of)
    TextView txtSignedInTheNumberOf;
    @BindView(R.id.llayout_signed_in_the_number_of)
    LinearLayout llayoutSignedInTheNumberOf;
    @BindView(R.id.txt_evaluate_the_number_of)
    TextView txtEvaluateTheNumberOf;
    @BindView(R.id.llayout_evaluate_the_number_of)
    LinearLayout llayoutEvaluateTheNumberOf;
    @BindView(R.id.btn_btn)
    BMAngleBtn2View btnBtn;
    @BindView(R.id.txt_btn)
    TextView txtBtn;
    @BindView(R.id.llayout_btn)
    LinearLayout llayoutBtn;
    @BindView(R.id.llayout_parent)
    LinearLayout llayoutParent;
    @BindView(R.id.img_qcode)
    ImageView imgQcode;
    @BindView(R.id.offline_qcode_click)
    View offlineQcodeClick;

    public OfflineTrainingItemView(Context context) {
        super(context);
        init();
    }

    public OfflineTrainingItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OfflineTrainingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public OfflineTrainingItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_offline_training_item, this, true);
        ButterKnife.bind(this);
        setBackgroundColor(Color.parseColor("#F2F2F2"));
    }

    public void setQcodeGone(){
        setQcode(null,false,null,null);
    }
    public void setQcodeVisible(OnClickListener listener,Integer typeTag,Integer positionTag){
        setQcode(listener,true,typeTag,positionTag);
    }
    public void setQcode(OnClickListener listener,boolean isShowQcode,Integer typeTag,Integer positionTag){
        if(listener==null||!isShowQcode){
            imgQcode.setVisibility(View.GONE);
            offlineQcodeClick.setVisibility(View.GONE);
        }else{
            imgQcode.setVisibility(View.VISIBLE);
            offlineQcodeClick.setVisibility(View.VISIBLE);
            offlineQcodeClick.setOnClickListener(listener);
            offlineQcodeClick.setTag(R.id.tag_position,positionTag);
            offlineQcodeClick.setTag(R.id.tag_type,typeTag);
        }
    }

    /**
     * 设置课程名称
     * @param courseTitle
     */
    public OfflineTrainingItemView setTxtCourseTitle(String courseTitle) {
        txtCourseTitle.setText(courseTitle);
        return this;
    }

    /**
     * 设置状态
     * @param state
     * @param isRed 是否为橙色
     * @return
     */
    public OfflineTrainingItemView setTxtState(String state,boolean isRed) {
        txtState.setVisibility(TextUtils.isEmpty(state)?View.GONE:View.VISIBLE);
        txtState.setText(state);
        txtState.setEnabled(isRed);
        return this;
    }
    public void setTxtStateIsShow(boolean isShow){
        txtState.setVisibility(isShow?View.VISIBLE:View.GONE);
    }
    /**
     * 设置年月日
     * @param ytd
     */
    public OfflineTrainingItemView setTxtYtd(String ytd) {
        txtYtd.setText(ytd);
        return this;
    }

    /**
     * 上课时间段
     * @param timeQuantum
     */
    public OfflineTrainingItemView setTxtTimeQuantum(String timeQuantum) {
        txtTimeQuantum.setText(timeQuantum);
        return this;
    }

    /**
     * 签到时间
     * @param signInTime
     */
    public OfflineTrainingItemView setTxtSignInTime(long signInTime,boolean isRed) {
        llayoutSignInTime.setVisibility(signInTime<=0?View.GONE:View.VISIBLE);
        if(signInTime>0){
            txtSignInTime.setText(DateUtil.getTimeToYMDHM(signInTime));
        }
        return setTxtState(signInTime<=0?"未签到":"已签到",isRed);
    }

    /**
     * 讲师名称
     * @param lecturerName
     * @return
     */
    public OfflineTrainingItemView setTxtLecturerName(String lecturerName) {
        llayoutLecturerName.setVisibility(TextUtils.isEmpty(lecturerName)?View.GONE:View.VISIBLE);
        txtLecturerName.setText(lecturerName);
        return this;
    }

    /**
     * 教室
     * @param classroom
     * @return
     */
    public OfflineTrainingItemView setTxtClassroom(String classroom) {
        layoutClassroom.setVisibility(TextUtils.isEmpty(classroom)?View.GONE:View.VISIBLE);
        txtClassroom.setText(classroom);
        return this;
    }

    /**
     * 地址
     * @param address
     * @return
     */
    public OfflineTrainingItemView setTxtAddress(String address) {
        llayoutAddress.setVisibility(TextUtils.isEmpty(address)?View.GONE:View.VISIBLE);
        txtAddress.setText(address);
        return this;
    }

    /**
     * 报名人数
     * @param willnum
     * @return
     */
    public OfflineTrainingItemView setTxtWillnum(String willnum) {
        llayoutWillnum.setVisibility(TextUtils.isEmpty(willnum)?View.GONE:View.VISIBLE);
        txtWillnum.setText(willnum);
        return this;
    }
    /**
     * 报名人数
     * @param isShow
     * @return
     */
    public void setTxtWillnumIsShow(boolean isShow) {
        llayoutWillnum.setVisibility(isShow?View.VISIBLE:View.GONE);
    }

    /**
     * 签到人数
     * @param signedInTheNumberOf
     * @return
     */
    public OfflineTrainingItemView setTxtSignedInTheNumberOf(String signedInTheNumberOf) {
        llayoutSignedInTheNumberOf.setVisibility(TextUtils.isEmpty(signedInTheNumberOf)?View.GONE:View.VISIBLE);
        txtSignedInTheNumberOf.setText(signedInTheNumberOf);
        return this;
    }
    /**
     * 签到人数
     * @param isShow
     * @return
     */
    public void setTxtSignedInTheNumberOfIsShow(boolean isShow) {
        llayoutSignedInTheNumberOf.setVisibility(isShow?View.VISIBLE:View.GONE);
    }
    /**
     * 评价人数
     * @param evaluateTheNumberOf
     * @return
     */
    public OfflineTrainingItemView setTxtEvaluateTheNumberOf(String evaluateTheNumberOf) {
        llayoutEvaluateTheNumberOf.setVisibility(TextUtils.isEmpty(evaluateTheNumberOf)?View.GONE:View.VISIBLE);
        txtEvaluateTheNumberOf.setText(evaluateTheNumberOf);
        return this;
    }
    /**
     * 评价人数
     * @param isShow
     * @return
     */
    public void setTxtEvaluateTheNumberOfIsShow(boolean isShow) {
        llayoutEvaluateTheNumberOf.setVisibility(isShow?View.VISIBLE:View.GONE);
    }

    public void setBtnBtnIsShow(boolean isShow){
        llayoutBtn.setVisibility(isShow?View.VISIBLE:View.GONE);
    }
    public void setBtnBtn(OnClickListener clickListener,String textBtn,Integer typeTag,Integer positionTag) {
        if(clickListener!=null){
            llayoutBtn.setVisibility(View.VISIBLE);
            btnBtn.setOnClickListener(clickListener);
            btnBtn.setText(textBtn);
            btnBtn.setTag(R.id.tag_position,positionTag);
            btnBtn.setTag(R.id.tag_type,typeTag);
        }else{
            llayoutBtn.setVisibility(View.GONE);
        }
    }
    public void setTxtBtnIsShow(boolean isShow){
        txtBtn.setVisibility(isShow?View.VISIBLE:View.GONE);
    }
    public void setTxtBtn(OnClickListener clickListener,String textBtn,Integer typeTag,Integer positionTag) {
        if(clickListener!=null){
            txtBtn.setVisibility(View.VISIBLE);
            txtBtn.setOnClickListener(clickListener);
            txtBtn.setText(textBtn);
            txtBtn.setTag(R.id.tag_position,positionTag);
            txtBtn.setTag(R.id.tag_type,typeTag);
        }else{
            txtBtn.setVisibility(View.GONE);
        }
    }


    public void setLlayoutParent(boolean isNew,OnClickListener clickListener,Integer typeTag,Integer positionTag){
        if(isNew){
            llayoutParent.setOnClickListener(clickListener);
        }
        llayoutParent.setTag(R.id.tag_position,positionTag);
        llayoutParent.setTag(R.id.tag_type,typeTag);
    }

}

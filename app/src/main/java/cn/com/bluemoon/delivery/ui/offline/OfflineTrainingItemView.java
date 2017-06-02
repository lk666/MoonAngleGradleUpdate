package cn.com.bluemoon.delivery.ui.offline;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.bluemoon.delivery.R;

/**
 * Created by tangqiwei on 2017/6/1.
 */

public class OfflineTrainingItemView extends FrameLayout {

    @Bind(R.id.txt_course_title)
    TextView txtCourseTitle;
    @Bind(R.id.txt_state)
    TextView txtState;
    @Bind(R.id.txt_ytd)
    TextView txtYtd;
    @Bind(R.id.txt_time_quantum)
    TextView txtTimeQuantum;
    @Bind(R.id.txt_sign_in_time)
    TextView txtSignInTime;
    @Bind(R.id.llayout_sign_in_time)
    LinearLayout llayoutSignInTime;
    @Bind(R.id.txt_lecturer_name)
    TextView txtLecturerName;
    @Bind(R.id.llayout_lecturer_name)
    LinearLayout llayoutLecturerName;
    @Bind(R.id.txt_classroom)
    TextView txtClassroom;
    @Bind(R.id.layout_classroom)
    LinearLayout layoutClassroom;
    @Bind(R.id.txt_address)
    TextView txtAddress;
    @Bind(R.id.llayout_address)
    LinearLayout llayoutAddress;
    @Bind(R.id.txt_willnum)
    TextView txtWillnum;
    @Bind(R.id.llayout_willnum)
    LinearLayout llayoutWillnum;
    @Bind(R.id.txt_signed_in_the_number_of)
    TextView txtSignedInTheNumberOf;
    @Bind(R.id.llayout_signed_in_the_number_of)
    LinearLayout llayoutSignedInTheNumberOf;
    @Bind(R.id.txt_train_classroom)
    TextView txtTrainClassroom;
    @Bind(R.id.llayout_train_classroom)
    LinearLayout llayoutTrainClassroom;
    @Bind(R.id.txt_train_address)
    TextView txtTrainAddress;
    @Bind(R.id.llayout_train_address)
    LinearLayout llayoutTrainAddress;
    @Bind(R.id.btn_btn)
    Button btnBtn;
    @Bind(R.id.txt_btn)
    TextView txtBtn;
    @Bind(R.id.llayout_btn)
    LinearLayout llayoutBtn;
    @Bind(R.id.llayout_parent)
    LinearLayout llayoutParent;

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
    public OfflineTrainingItemView setTxtSignInTime(String signInTime) {
        llayoutSignInTime.setVisibility(TextUtils.isEmpty(signInTime)?View.GONE:View.VISIBLE);
        txtSignInTime.setText(signInTime);
        return this;
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
     * 培训教室
     * @param trainClassroom
     * @return
     */
    public OfflineTrainingItemView setTxtTrainClassroom(String trainClassroom) {
        llayoutTrainClassroom.setVisibility(TextUtils.isEmpty(trainClassroom)?View.GONE:View.VISIBLE);
        txtTrainClassroom.setText(trainClassroom);
        return this;
    }

    /**
     * 培训地址
     * @param trainAddress
     * @return
     */
    public OfflineTrainingItemView setTxtTrainAddress(String trainAddress) {
        llayoutTrainAddress.setVisibility(TextUtils.isEmpty(trainAddress)?View.GONE:View.VISIBLE);
        txtTrainAddress.setText(trainAddress);
        return this;
    }


    private void setBtnBtn(OnClickListener clickListener,String textBtn,Object tag) {
        if(clickListener!=null){
            llayoutBtn.setVisibility(View.VISIBLE);
            llayoutBtn.setOnClickListener(clickListener);
            btnBtn.setText(textBtn);
            btnBtn.setTag(tag);
        }else{
            llayoutBtn.setVisibility(View.GONE);
        }
    }

    private void setTxtBtn(OnClickListener clickListener,String textBtn,Object tag) {
        if(clickListener!=null){
            llayoutBtn.setVisibility(View.VISIBLE);
            txtBtn.setVisibility(View.VISIBLE);
            llayoutBtn.setOnClickListener(clickListener);
            txtBtn.setText(textBtn);
            txtBtn.setTag(tag);
        }else{
            llayoutBtn.setVisibility(View.GONE);
            txtBtn.setVisibility(View.GONE);
        }
    }

    public OfflineTrainingItemView setLlayoutBtn(OnClickListener[] clickListeners,String[] textBtns,Object[] tags) {
        if(clickListeners!=null&clickListeners.length>0){
            if(clickListeners.length==1){
                setBtnBtn(clickListeners[0],textBtns[0],tags[0]);
            }else if(clickListeners.length==2){
                setBtnBtn(clickListeners[0],textBtns[0],tags[0]);
                setTxtBtn(clickListeners[1],textBtns[1],tags[1]);
            }else{

            }
        }
        return this;
    }

    public OfflineTrainingItemView setLlayoutParent(OnClickListener clickListener){
        llayoutParent.setOnClickListener(clickListener);
        return this;
    }

}

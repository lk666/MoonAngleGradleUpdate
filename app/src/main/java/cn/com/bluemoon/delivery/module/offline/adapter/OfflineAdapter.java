package cn.com.bluemoon.delivery.module.offline.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.offline.CurriculumsTable;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.ui.offline.OfflineTrainingItemView;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DateUtil;

/**
 * Created by tangqiwei on 2017/6/1.
 */

public class OfflineAdapter extends BaseListAdapter<CurriculumsTable> {
    /**
     * 点击事件的类型
     */
    public final static int TO_NEXT_DETAILS = 0, TO_NEXT_EVALUATE = 1, TO_REALITY = 2,TO_QCODE = 3;
    /**
     * 学生的列表还是教师的列表
     */
    public final static String LIST_STUDENTS = "student", LIST_TEACHER = "teacher";
    /**
     * 三个列表 未开始 进行中 已结束
     */
    public final static int LIST_NOSTART = 0, LIST_ING = 1, LIST_END = 2;

    private String roleMode;
    private int positionMode;

    public OfflineAdapter(Context context, OnListItemClickListener listener, String roleMode) {
        this(context, listener, roleMode, LIST_NOSTART);
    }

    public OfflineAdapter(Context context, OnListItemClickListener listener, String roleMode, int
            positionMode) {
        super(context, listener);
        this.roleMode = roleMode;
        this.positionMode = positionMode;
    }

    public void setList(List<CurriculumsTable> list, int positionMode) {
        setList(list);
        this.positionMode = positionMode;
    }

    @Override
    protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
        OfflineTrainingItemView itemView = (OfflineTrainingItemView) convertView;

        CurriculumsTable curriculumsTable = (CurriculumsTable) getItem(position);

        itemView.setTxtCourseTitle(curriculumsTable.courseName).setTxtYtd(DateUtil.getTimes
                (curriculumsTable.startTime, curriculumsTable.endTime));

        itemView.setLlayoutParent(isNew, this, TO_NEXT_DETAILS, position);

        switch (roleMode) {
            case LIST_STUDENTS:
                if (positionMode == LIST_ING) {
                    itemView.setBtnBtn(this, "我要评价", TO_NEXT_EVALUATE, position);
                }
                if (positionMode == LIST_NOSTART) {
                    itemView.setBtnBtnIsShow(false);
                    itemView.setQcodeVisible(this,TO_QCODE,position);
                }else{
                    itemView.setQcodeGone();
                }
                if (positionMode == LIST_END) {
                    itemView.setTxtSignInTime(curriculumsTable.signTime, false);
                    if (curriculumsTable.signTime > 0 && !curriculumsTable.status.equals
                            (Constants.OFFLINE_STATUS_CLOSE_CLASS)) {
                        itemView.setBtnBtn(this, "我要评价", TO_NEXT_EVALUATE, position);
                    } else {
                        itemView.setBtnBtnIsShow(false);
                    }
                } else {
                    itemView.setTxtSignInTime(curriculumsTable.signTime, true);
                }
                itemView.setTxtLecturerName(curriculumsTable.teacherName)
                        .setTxtClassroom(curriculumsTable.room)
                        .setTxtAddress(curriculumsTable.address);
                break;
            case LIST_TEACHER:
                itemView.setTxtWillnum(String.valueOf(curriculumsTable.enrollNum) + "人")
                        .setTxtSignedInTheNumberOf(String.valueOf(curriculumsTable.signNum) + "人")
                        .setTxtClassroom(curriculumsTable.room)
                        .setTxtAddress(curriculumsTable.address);
                if (curriculumsTable.status.equals(Constants.OFFLINE_STATUS_IN_CLASS) || curriculumsTable.status.equals(Constants.OFFLINE_STATUS_END_CLASS)) {
                    itemView.setBtnBtn(this, "录入培训实际", TO_REALITY, position);
                    itemView.setTxtBtn(this, "评价学员", TO_NEXT_EVALUATE, position);
                }else{
                    itemView.setBtnBtnIsShow(false);
                    itemView.setTxtBtnIsShow(false);
                }
                switch (positionMode) {
                    case LIST_NOSTART:

                        itemView.setTxtState(getTeacherStateText(curriculumsTable.status), true);
                        itemView.setTxtLecturerName(curriculumsTable.teacherName);
                        itemView.setTxtEvaluateTheNumberOfIsShow(false);
                        break;
                    case LIST_ING:

                        itemView.setTxtState(getTeacherStateText(curriculumsTable.status), false);
                        itemView.setTxtEvaluateTheNumberOf(String.valueOf(curriculumsTable.evaluatedNum) + "人");
                        itemView.setTxtLecturerName("");

                        break;
                }
                break;
            default:
                break;
        }
    }


    @Override
    protected View getConvertView() {
        OfflineTrainingItemView itemView = new OfflineTrainingItemView(context);

        itemView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams
                .MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT));
        return itemView;
    }

    protected String getTeacherStateText(String state) {
        if (roleMode == LIST_TEACHER) {
            switch (state) {
                case Constants.OFFLINE_STATUS_WAITING_CLASS:
                    return "未开始";
                case Constants.OFFLINE_STATUS_IN_CLASS:
                    return "进行中";
                case Constants.OFFLINE_STATUS_END_CLASS:
                    return "已结束";
                case Constants.OFFLINE_STATUS_CLOSE_CLASS:
                    return "已关闭";

            }
        }
        return "";
    }


    @Override
    protected int getLayoutId() {
        return 0;
    }
}

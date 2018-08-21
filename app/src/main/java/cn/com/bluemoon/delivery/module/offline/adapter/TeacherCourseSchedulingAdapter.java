package cn.com.bluemoon.delivery.module.offline.adapter;

import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.offline.ResultPlanscan;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.ViewUtil;
import cn.com.bluemoon.lib_widget.module.form.BMPrefixTextView;

/**
 * 教师排课详情
 * Created by tangqiwei on 2018/8/16.
 */

public class TeacherCourseSchedulingAdapter extends BaseQuickAdapter<ResultPlanscan.Course,
        BaseViewHolder> {

    private boolean isOpenYear = false;

    public TeacherCourseSchedulingAdapter() {
        super(R.layout.item_teacher_course_scheduling);
    }

    @Override
    protected void convert(BaseViewHolder helper, ResultPlanscan.Course item) {
//        @Bind(R.id.top_line) View topLine;
        int position = helper.getPosition();
        helper.setVisible(R.id.top_line, position != 1);
        if (position == 1 || !DateUtil.getTime(getData().get(position - 1).startTime).equals
                (DateUtil.getTime(item.startTime))) {
            helper.setVisible(R.id.txt_date, true);
            helper.setVisible(R.id.txt_month, true);
            helper.setText(R.id.txt_date, DateUtil.getTimeMill(item.startTime, "dd"));
            helper.setText(R.id.txt_month, DateUtil.getTimeMill(item.startTime, "MM月"));
            if (isOpenYear) {
                helper.setVisible(R.id.txt_year, true);
                helper.setText(R.id.txt_year, DateUtil.getTime(item.startTime, "yyyy"));
            } else {
                helper.setVisible(R.id.txt_year, false);
            }
        } else {
            helper.setVisible(R.id.txt_date, false);
            helper.setVisible(R.id.txt_month, false);
            helper.setVisible(R.id.txt_year, false);
        }
        //在没有打开显示年份时发现存在年份不同的，打开并且重新刷新
        if (!isOpenYear && !DateUtil.getTime(getData().get(position - 1).startTime, "yyyy").equals
                (DateUtil.getTime(item.startTime, "yyyy"))) {
            isOpenYear = true;
            notifyDataSetChanged();
        }

        helper.setText(R.id.txt_course_time, new StringBuffer().append(DateUtil.getTimeToHours
                (item.startTime)).append("-").append(DateUtil.getTimeToHours(item.endTime))
                .toString());
        helper.setText(R.id.txt_course_name, item.courseName);
        BMPrefixTextView txtTeacherName = helper.getView(R.id.txt_teacher_name);
        txtTeacherName.setContentText(item.teacherName);
        BMPrefixTextView txtClassroom = helper.getView(R.id.txt_classroom);
        txtClassroom.setContentText(item.room);

        final RelativeLayout layout = helper.getView(R.id.root);
        final View bLine = helper.getView(R.id.bottom_line);
        if(layout.getTag()==null||!(Boolean) layout.getTag()){
            layout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver
                    .OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    reSetLinHeight(layout, bLine);
                }
            });
            layout.setTag(true);
        }

    }

    public void reSetLinHeight(RelativeLayout layout, View bLine) {
        RelativeLayout.LayoutParams l = (RelativeLayout.LayoutParams) bLine
                .getLayoutParams();
        l.height = layout.getHeight() - ViewUtil.dp2px(34);
        bLine.setLayoutParams(l);
    }

}

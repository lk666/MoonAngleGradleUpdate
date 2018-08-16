package cn.com.bluemoon.delivery.module.offline.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.offline.ResultPlanscan;

/**
 * 教师排课详情
 * Created by tangqiwei on 2018/8/16.
 */

public class TeacherCourseSchedulingAdapter extends BaseQuickAdapter<ResultPlanscan.Course,BaseViewHolder> {


    public TeacherCourseSchedulingAdapter() {
        super(R.layout.layout_teacher_course_scheduling);
    }

    @Override
    protected void convert(BaseViewHolder helper, ResultPlanscan.Course item) {

    }
}

package cn.com.bluemoon.delivery.sz.taskManager;

import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.sz.adapter.TaskDetailAdapter;

/**
 * Created by Wan.N
 * Date       2016/9/9
 * Desc      通知详情页面
 */
public class NotificationDetailActivity extends BaseActivity {
    @Bind(R.id.advice_content_tv)
    TextView tvAdviceContent;

    @Bind(R.id.user_task_lv)
    ListView taskListLv;

    @Override
    protected int getLayoutId() {
        return R.layout.sz_activity_task_notification_detail;
    }

    @Override
    public void initView() {
        //TODO 模拟数据
        tvAdviceContent.setText("这几天天气真不错这几天天气真不错这几天天气真不错这几天天气真不错" +
                "这几天天气真不错这几天天气真不错这几天天气真不错这几天天气真不错这几天天气真不错" +
                "这几天天气真不错这几天天气真不错这几天天气真不错这几天天气真不错");
        //TODO 模拟数据
        List<Object> list = new ArrayList<>();
        list.add(new Object());
        list.add(new Object());
        list.add(new Object());
        list.add(new Object());
        TaskDetailAdapter adapter = new TaskDetailAdapter(this, list);
        taskListLv.setAdapter(adapter);
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.sz_task_notification_detail);
    }

    @Override
    public void initData() {

    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {

    }
}

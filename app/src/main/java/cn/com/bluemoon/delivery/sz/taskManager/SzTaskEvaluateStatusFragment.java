package cn.com.bluemoon.delivery.sz.taskManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.base.BaseFragment;
import cn.com.bluemoon.delivery.sz.adapter.TaskEvaluateStatusAdapter;
import cn.com.bluemoon.delivery.sz.util.DisplayUtil;
import cn.com.bluemoon.delivery.sz.util.LogUtil;
import cn.com.bluemoon.delivery.sz.view.ChildListView;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;

/**
 * Created by Wan.N
 * Date       2016/9/7
 * Desc      待评价/已评价界面（共用）
 */
public class SzTaskEvaluateStatusFragment extends BaseFragment {
    @Bind(R.id.evaluate_data_lv)
    PullToRefreshListView evaluate_data_lv;

    public static final String ACTIVITY_TYPE = "ACTIVITY_TYPE";
    public static final int ACTIVITY_TYPE_TO_EVALUATE = 0;//待评价
    public static final int ACTIVITY_TYPE_HAVE_EVALUATED = 1;//已评价
    private int activityType = -1;//记录需要展示的类型（0;待评价  1;已评价）

    @Override
    protected void onBeforeCreateView() {
        Bundle bundle = getArguments();
        activityType = bundle.getInt(ACTIVITY_TYPE, -1);
        LogUtil.i("evaluateType:" + activityType);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.sz_fragment_task_evaluate_status;
    }

    @Override
    public void initView() {
        //TODO 模拟数据
        List<Object> list = new ArrayList<>();
        list.add(new Object());
        list.add(new Object());
        list.add(new Object());
        list.add(new Object());
        list.add(new Object());
        list.add(new Object());
        list.add(new Object());
        TaskEvaluateStatusAdapter adapter = new TaskEvaluateStatusAdapter(getActivity(), list);
        evaluate_data_lv.setAdapter(adapter);
        initListener();
    }

    /**
     * 设置Listview的高度
     */
    public void setListViewHeight(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    @Override
    public void initData() {

    }

    private void initListener() {
        evaluate_data_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = null;
                if (activityType == ACTIVITY_TYPE_TO_EVALUATE) {
                    intent = new Intent(getActivity(), SzWriteEvaluateActivity.class);
                } else if (activityType == ACTIVITY_TYPE_HAVE_EVALUATED) {
                    intent = new Intent(getActivity(), SzTaskOrEvaluateDetailActivity.class);
                    intent.putExtra(SzTaskOrEvaluateDetailActivity.ACTIVITY_TYPE, SzTaskOrEvaluateDetailActivity.ACTIVITY_TYPE_EVALUATE_DETAIL);
                } else {
                }
                if (intent != null) {
                    getActivity().startActivity(intent);
                }
            }
        });
    }


    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {

    }
}

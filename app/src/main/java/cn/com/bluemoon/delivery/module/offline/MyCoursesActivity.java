package cn.com.bluemoon.delivery.module.offline;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.OffLineApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.offline.CurriculumsTable;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.module.offline.adapter.OfflineAdapter;
import cn.com.bluemoon.delivery.ui.common.BmSegmentView;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;

/**
 * Created by tangqiwei on 2017/6/2.
 */

public class MyCoursesActivity extends BaseActivity implements OnListItemClickListener{

    @Bind(R.id.segment_tab)
    BmSegmentView segmentTab;
    @Bind(R.id.listview_offline)
    PullToRefreshListView listviewOffline;

    private OfflineAdapter adapter;

    private String status;//默认到哪个页签

    public static void actionStart(Context context){
        actionStart(context, "waitingClass");
    }

    public static void actionStart(Context context,String status){
        Intent intent=new Intent(context,MyCoursesActivity.class);
        intent.putExtra("status",status);
        context.startActivity(intent);
    }

    private List<String> getTextList(){
        List<String> titleList=new ArrayList<>();
        titleList.add(getString(R.string.offline_waiting_courses));
        titleList.add(getString(R.string.offline_courses_ing));
        titleList.add(getString(R.string.offline_courses_record));
        return titleList;
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.offline_my_courses);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_offline_list;
    }

    @Override
    public void initView() {
        adapter=new OfflineAdapter(this,this,OfflineAdapter.LIST_TEACHER);
    }

    @Override
    public void initData() {
        segmentTab.setTextList(getTextList());
        segmentTab.checkUIChange(0);
        listviewOffline.setAdapter(adapter);
        OffLineApi.teacherCourseList(getToken(),System.currentTimeMillis(),status,getNewHandler(0, CurriculumsTable.class));
    }


    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @Override
    public void onItemClick(Object item, View view, int position) {

    }
}

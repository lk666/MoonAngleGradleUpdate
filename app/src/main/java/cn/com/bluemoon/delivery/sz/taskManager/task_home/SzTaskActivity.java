package cn.com.bluemoon.delivery.sz.taskManager.task_home;

import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.module.base.BaseTabActivity;
import cn.com.bluemoon.delivery.sz.meeting.SchedualAddMeetingActivity;
import cn.com.bluemoon.delivery.sz.meeting.SzMsgCountController;
import cn.com.bluemoon.delivery.sz.util.PageJumps;
import cn.com.bluemoon.delivery.sz.util.ViewUtil;
import cn.com.bluemoon.lib.view.ImageViewForClick;

public class SzTaskActivity extends BaseTabActivity {

    private Context context = null;
    private ViewPager vpager_taskItem;
    private List<Fragment> taskFragmentList = new ArrayList<Fragment>();
    private FragmentPagerAdapter fPagerAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initCustomActionBar();
        setContentView(R.layout.activity_sz_task);
        context = SzTaskActivity.this;

        initWidget();
    }


    public void initWidget() {
        vpager_taskItem = (ViewPager) this.findViewById(R.id.vpager_taskItem);

        taskFragmentList.add(new TaskRecordFragment());
        taskFragmentList.add(new TaskAppraiseFragment());

        FragmentManager fm = getSupportFragmentManager();
        fPagerAdapter = new FragmentPagerAdapter(fm) {
            @Override
            public int getCount() {
                // TODO Auto-generated method stub
                return taskFragmentList.size();
            }

            @Override
            public Fragment getItem(int position) {
                // TODO Auto-generated method stub
                return taskFragmentList.get(position);
            }

            @Override
            public int getItemPosition(Object object) {
                return super.getItemPosition(object);
            }

        };
        vpager_taskItem.setAdapter(fPagerAdapter);
        vpager_taskItem.setOffscreenPageLimit(2);
        vpager_taskItem.setCurrentItem(0, false);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.finish();
        }
        return false;
    }

    private void initCustomActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.sz_top_task_bar);


        RadioGroup radioGroup = (RadioGroup) actionBar.getCustomView().findViewById(R.id.rgroup);
        ImageViewForClick backBtn = (ImageViewForClick) actionBar.getCustomView().findViewById(R.id.img_back);
        ImageViewForClick msgBtn = (ImageViewForClick) actionBar.getCustomView().findViewById(R.id.img_right2);
        ImageViewForClick setBtn = (ImageViewForClick) actionBar.getCustomView().findViewById(R.id.img_right);
        final RadioButton rb_left = (RadioButton) actionBar.getCustomView().findViewById(R.id.rb_left);
        final RadioButton rb_right = (RadioButton) actionBar.getCustomView().findViewById(R.id.rb_right);
        final TextView numTv = (TextView) actionBar.getCustomView().findViewById(R.id.num_tv);

        ViewUtil.setTipsNum(numTv, 0);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        setBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PageJumps.PageJumps(SzTaskActivity.this, SchedualAddMeetingActivity.class, null);
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_left) {
                    rb_left.setTextColor(getResources().getColor(R.color.title_background));
                    rb_right.setTextColor(getResources().getColor(R.color.white));
                    vpager_taskItem.setCurrentItem(0);
                } else if (checkedId == R.id.rb_right) {
                    rb_left.setTextColor(getResources().getColor(R.color.white));
                    rb_right.setTextColor(getResources().getColor(R.color.title_background));
                    vpager_taskItem.setCurrentItem(1);
                }
            }
        });
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(SzTaskActivity.class.getSimpleName());
        SzMsgCountController.getInstance().initMsgCount();
//		requestMsgNum();
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(SzTaskActivity.class.getSimpleName());
    }

}

package cn.com.bluemoon.delivery.sz.taskManager.task_home;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.base.BaseFragment;
import cn.com.bluemoon.delivery.sz.taskManager.SzTaskEvaluateStatusFragment;
import cn.com.bluemoon.delivery.sz.view.NoScrollViewPager;

/**
 * Created by Wan.N
 * Date       2016/9/7 10:04
 * Desc     任务评价fragment
 */
public class TaskAppraiseFragment extends BaseFragment {

    @Bind(R.id.rg_evaluate)
    RadioGroup rg_evaluate;

    @Bind(R.id.rb_left)
    RadioButton rb_left;

    @Bind(R.id.rb_right)
    RadioButton rb_right;

    @Bind(R.id.evalueate_status_vp)
    NoScrollViewPager evalueate_status_vp;

    private List<Fragment> evalueteStatusList = new ArrayList<>();
    private SzTaskEvaluateStatusFragment toEvaluateFragment;
    private SzTaskEvaluateStatusFragment haveEvaluatedFragment;

    @Override
    protected int getLayoutId() {
        return R.layout.sz_fragment_task_evaluate;
    }

    @Override
    public void initView() {
        initViewPage();
        initListener();
        initData();
    }

    @Override
    public void initData() {

    }


    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {

    }

    private void initViewPage() {
        evalueate_status_vp.setCanScroll(false);
        evalueteStatusList.clear();

        toEvaluateFragment = new SzTaskEvaluateStatusFragment();
        Bundle bundle1 = new Bundle();
        bundle1.putInt(SzTaskEvaluateStatusFragment.ACTIVITY_TYPE, SzTaskEvaluateStatusFragment.ACTIVITY_TYPE_TO_EVALUATE);
        toEvaluateFragment.setArguments(bundle1);

        Bundle bundle2 = new Bundle();
        bundle2.putInt(SzTaskEvaluateStatusFragment.ACTIVITY_TYPE, SzTaskEvaluateStatusFragment.ACTIVITY_TYPE_HAVE_EVALUATED);
        haveEvaluatedFragment = new SzTaskEvaluateStatusFragment();
        haveEvaluatedFragment.setArguments(bundle2);

        evalueteStatusList.add(toEvaluateFragment);
        evalueteStatusList.add(haveEvaluatedFragment);

        FragmentManager childFragmentManager = getChildFragmentManager();
        evalueate_status_vp.setAdapter(new FragmentPagerAdapter(childFragmentManager) {
            @Override
            public Fragment getItem(int i) {
                return evalueteStatusList.get(i);
            }

            @Override
            public int getCount() {
                return evalueteStatusList.size();
            }
        });
        evalueate_status_vp.setOffscreenPageLimit(2);
        evalueate_status_vp.setCurrentItem(0, false);
    }

    private void initListener() {
        rg_evaluate.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_left) {
                    evalueate_status_vp.setCurrentItem(0);
                } else if (checkedId == R.id.rb_right) {
                    evalueate_status_vp.setCurrentItem(1);
                }
            }
        });
    }


}

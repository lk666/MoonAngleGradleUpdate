package cn.com.bluemoon.delivery.module.base;

import android.support.v4.app.Fragment;

import com.umeng.analytics.MobclickAgent;

/**
 * 基础fragment
 * Created by lk on 2016/6/13.
 */
public class BaseFragment extends Fragment implements IShowDialog{

    /**
     * 默认TAG
     * @return getClass().getSimpleName()
     */
    protected String getDefaultTag() {
        return this.getClass().getSimpleName();
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getDefaultTag());
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(getDefaultTag());
    }

    @Override
    public void showProgressDialog() {
        if (getActivity() instanceof  IShowDialog) {
            ((IShowDialog)getActivity()).showProgressDialog();
        }
    }

    @Override
    public void dismissProgressDialog() {
        if (getActivity() instanceof  IShowDialog) {
            ((IShowDialog)getActivity()).dismissProgressDialog();
        }
    }
}

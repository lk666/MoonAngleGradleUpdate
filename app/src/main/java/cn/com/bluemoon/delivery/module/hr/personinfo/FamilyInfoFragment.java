package cn.com.bluemoon.delivery.module.hr.personinfo;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.HRApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.personalinfo.ResultGetFamilyInfo;
import cn.com.bluemoon.delivery.app.api.model.personalinfo.ResultGetFamilyInfo.FamilyListBean;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.module.newbase.BaseFragment;
import cn.com.bluemoon.delivery.module.newbase.view.CommonActionBar;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.view.CommonEmptyView;

/**
 * 家庭情况
 * Created by ljl on 2017/6/14.
 */

public class FamilyInfoFragment extends BaseFragment<CommonActionBar> implements OnListItemClickListener {


    @Bind(R.id.lv_family)
    ListView lvFamily;

    public static Fragment newInstance() {
        FamilyInfoFragment fragment = new FamilyInfoFragment();
        return fragment;
    }


    @Override
    protected String getTitleString() {
        return getString(R.string.family_info_title);
    }

    @Override
    protected void initTitleBarView(View title) {
        super.initTitleBarView(title);
        CommonActionBar actionBar = (CommonActionBar) title;
        actionBar.getTvRightView().setText(R.string.add_collect_wash);
        actionBar.getTvRightView().setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActionBarBtnRightClick() {
        super.onActionBarBtnRightClick();
        pushFragment(AddFamilyInfoFragment.newInstance(AddFamilyInfoFragment.ADD_TYPE));
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_family_info;
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        ResultGetFamilyInfo r = (ResultGetFamilyInfo) result;
        List<FamilyListBean> list = r.familyList;
        if (list != null) {
            FamilyAdapter adapter = new FamilyAdapter(getActivity(), this);
            adapter.setList(list);
            lvFamily.setAdapter(adapter);
        }
    }

    @Override
    protected void initData() {
        showWaitDialog();
        HRApi.getFamilyInfo(getToken(), getNewHandler(1, ResultGetFamilyInfo.class));
    }

    @Override
    protected void initContentView(View mainView) {
        PublicUtil.setEmptyView(lvFamily, null, new CommonEmptyView.EmptyListener() {
            @Override
            public void onRefresh() {
                initData();
            }
        });
    }

    @Override
    public void onItemClick(Object item, View view, int position) {

    }

    class FamilyAdapter extends BaseListAdapter<FamilyListBean> {

        public FamilyAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.item_family_info;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            TextView txtName  = getViewById(R.id.txt_name);
            TextView txtEdit  = getViewById(R.id.txt_edit);
            TextView txtGender  = getViewById(R.id.txt_gender);
            TextView txtBirthday  = getViewById(R.id.txt_birthday);
            TextView txtCompany  = getViewById(R.id.txt_company);
            TextView txtWorkPlace  = getViewById(R.id.txt_work_place);
            setClickEvent(isNew, position, txtEdit);
            FamilyListBean bean = list.get(position);
            txtName.setText(bean.relationship+"-"+bean.fullName);
            txtGender.setText("性别："+bean.gender);
            txtBirthday.setText("出生日期："+ DateUtil.getTime(bean.birthday, "yyyy-MM-dd"));
            if (TextUtils.isEmpty(bean.workPlace)) {
                txtWorkPlace.setVisibility(View.GONE);
            } else {
                txtWorkPlace.setText("工作单位："+bean.workPlace);
            }
            if (TextUtils.isEmpty(bean.menberPosition)) {
                txtCompany.setVisibility(View.GONE);
            } else {
                txtCompany.setText("职位："+bean.menberPosition);
            }
        }
    }


}

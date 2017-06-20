package cn.com.bluemoon.delivery.module.hr.personinfo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alipay.tscenter.biz.rpc.vkeydfp.result.BaseResult;

import butterknife.Bind;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.HRApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.personalinfo.ResultGetAddressInfo;
import cn.com.bluemoon.delivery.module.newbase.BaseFragment;
import cn.com.bluemoon.delivery.module.newbase.view.CommonActionBar;
import cn.com.bluemoon.lib_widget.module.form.BMFieldArrow1View;
import cn.com.bluemoon.lib_widget.module.form.BMFieldText1View;
import cn.com.bluemoon.lib_widget.module.form.BmCellTextView;

/**
 * 爱好特长
 * Created by lk on 2017/6/19.
 */

public class InterestFragment extends BaseFragment<CommonActionBar> {

    private boolean isEdit;
    public static Fragment newInstance() {
        InterestFragment fragment = new InterestFragment();
        return fragment;
    }

    @Override
    protected void initTitleBarView(View title) {
        super.initTitleBarView(title);
        CommonActionBar actionBar = (CommonActionBar) title;
        actionBar.getTvRightView().setText(R.string.team_group_detail_edit);
        actionBar.getTvRightView().setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActionBarBtnRightClick() {
        TextView txtRight = getTitleBar().getTvRightView();
        if (isEdit) {
            //showWaitDialog();
            //HRApi.saveAddress(null, null, fieldCartAddress.getContent(), type, getToken(), getNewHandler(2, BaseResult.class));
        } else {
            /*layoutInfo.setVisibility(View.GONE);
            layoutEdit.setVisibility(View.VISIBLE);
            txtRight.setText(R.string.btn_save);
            if (!TextUtils.isEmpty(r.addressInfo.address)) {
                fieldAddress.setContent(r.addressInfo.toString());
            } else {
                fieldAddress.setContent("");
            }
            if (!TextUtils.isEmpty(r.addressInfo.address)) {
                fieldDetailAddress.setContent(r.addressInfo.address);
            } else {
                fieldDetailAddress.setContent("");
            }
            if (LIVE_TYPE.equals(type)) {
                if (!TextUtils.isEmpty(r.addressInfo.carWait)) {
                    fieldCartAddress.setContent(r.addressInfo.carWait);
                } else {
                    fieldCartAddress.setContent("");
                }
            } else {
                txtCartAddress.setVisibility(View.GONE);
            }*/

        }
        isEdit = !isEdit;
    }


    @Override
    protected String getTitleString() {
        return "爱好特长";
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_interest;
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {


    }

    @Override
    protected void initData() {
        showWaitDialog();
        HRApi.getInterest(getToken(), getNewHandler(1, ResultGetAddressInfo.class));
    }

    @Override
    protected void initContentView(View mainView) {

    }

}

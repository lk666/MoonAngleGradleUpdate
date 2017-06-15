package cn.com.bluemoon.delivery.module.hr.personinfo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.HRApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.personalinfo.ResultGetAddressInfo;
import cn.com.bluemoon.delivery.module.newbase.BaseFragment;
import cn.com.bluemoon.delivery.module.newbase.view.CommonActionBar;
import cn.com.bluemoon.lib_widget.module.form.BmCellTextView;

/**
 * 现地址/家庭地址
 * Created by lk on 2017/6/14.
 */

public class AddressInfoFragment extends BaseFragment<CommonActionBar> {
    @Bind(R.id.txt_address)
    BmCellTextView txtAddress;
    @Bind(R.id.txt_detail_address)
    BmCellTextView txtDetailAddress;
    @Bind(R.id.txt_cart_address)
    BmCellTextView txtCartAddress;
    private String type;

    public static Fragment newInstance(String type) {
        AddressInfoFragment fragment = new AddressInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    protected String getTitleString() {
        type = getArguments().getString("type");
        if ("live".equals(type)) {
            return getString(R.string.current_address_title);
        }
        return getString(R.string.family_address_title);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_family_address;
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        ResultGetAddressInfo r = (ResultGetAddressInfo)result;
        txtDetailAddress.setContentText(r.addressInfo.address);
        txtAddress.setContentText(r.addressInfo.toString());
        if ("live".equals(type)) {
            if (TextUtils.isEmpty(r.addressInfo.carWait)) {
                txtCartAddress.setContentText(getString(R.string.not_input));
            } else {
                txtCartAddress.setContentText(r.addressInfo.carWait);
            }
        } else {
            txtCartAddress.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initData() {
        HRApi.getAddressInfo(type, getToken(), getNewHandler(1, ResultGetAddressInfo.class));
    }

    @Override
    protected void initContentView(View mainView) {

    }



}

package cn.com.bluemoon.delivery.module.hr.personinfo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alipay.tscenter.biz.rpc.vkeydfp.result.BaseResult;

import butterknife.Bind;
import butterknife.ButterKnife;
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
    @Bind(R.id.layout_info)
    LinearLayout layoutInfo;
    @Bind(R.id.field_address)
    BMFieldArrow1View fieldAddress;
    @Bind(R.id.field_detail_address)
    BMFieldText1View fieldDetailAddress;
    @Bind(R.id.field_cart_address)
    BMFieldText1View fieldCartAddress;
    @Bind(R.id.layout_edit)
    LinearLayout layoutEdit;
    private String type;
    public final static String LIVE_TYPE = "live";
    public final static String HOME_TYPE = "home";
    private boolean isEdit;
    private ResultGetAddressInfo r;

    public static Fragment newInstance(String type) {
        AddressInfoFragment fragment = new AddressInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        fragment.setArguments(bundle);
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
            showWaitDialog();
            HRApi.saveAddress(null, null, fieldCartAddress.getContent(), type, getToken(), getNewHandler(2, BaseResult.class));
        } else {
            layoutInfo.setVisibility(View.GONE);
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
            }

        }
        isEdit = !isEdit;
    }


    @Override
    protected String getTitleString() {
        type = getArguments().getString("type");
        if (LIVE_TYPE.equals(type)) {
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
        if (requestCode == 1) {
            r = (ResultGetAddressInfo) result;
            String nullStr = getString(R.string.not_input);
            String address = r.addressInfo.address;
            txtDetailAddress.setContentText(TextUtils.isEmpty(address) ? nullStr : address);
            String detailAddress = r.addressInfo.toString();
            txtAddress.setContentText(TextUtils.isEmpty(detailAddress) ? nullStr : detailAddress);
            if (LIVE_TYPE.equals(type)) {
                if (TextUtils.isEmpty(r.addressInfo.carWait)) {
                    txtCartAddress.setContentText(nullStr);
                } else {
                    txtCartAddress.setContentText(r.addressInfo.carWait);
                }
            } else {
                txtCartAddress.setVisibility(View.GONE);
            }
        } else if (requestCode == 2) {
            TextView txtRight = getTitleBar().getTvRightView();
            txtRight.setText(R.string.team_group_detail_edit);
            layoutEdit.setVisibility(View.GONE);
            layoutInfo.setVisibility(View.VISIBLE);
            initData();
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

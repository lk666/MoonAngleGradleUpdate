package cn.com.bluemoon.delivery.module.hr.personinfo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.Bind;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.HRApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.address.Area;
import cn.com.bluemoon.delivery.app.api.model.personalinfo.ResultGetAddressInfo;
import cn.com.bluemoon.delivery.app.api.model.personalinfo.ResultGetAddressInfo.AddressInfoBean;
import cn.com.bluemoon.delivery.module.newbase.BaseFragment;
import cn.com.bluemoon.delivery.module.newbase.view.CommonActionBar;
import cn.com.bluemoon.delivery.ui.dialog.AddressSelectDialog;
import cn.com.bluemoon.delivery.ui.dialog.AddressSelectDialog.IAddressSelectDialog;
import cn.com.bluemoon.delivery.utils.ViewUtil;
import cn.com.bluemoon.lib_widget.module.form.BMFieldArrow1View;
import cn.com.bluemoon.lib_widget.module.form.BMFieldArrow1View.FieldArrowListener;
import cn.com.bluemoon.lib_widget.module.form.BMFieldText1View;
import cn.com.bluemoon.lib_widget.module.form.BmCellTextView;

/**
 * 现地址/家庭地址
 * Created by lk on 2017/6/14.
 */

public class AddressInfoFragment extends BaseFragment<CommonActionBar> implements FieldArrowListener, IAddressSelectDialog {
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
    private AddressInfoBean addressInfo;
    private final int REQUEST_GET_INFO = 1;
    private final int REQUEST_SAVE_INFO = 2;


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
            changeInfoView(txtRight);
        } else if (addressInfo != null){
            changeSaveView(txtRight);
        }

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

    /**
     * 调用保存成功后切换到查看界面
     **/
    private void changeInfoView(TextView txtRight) {
        ViewUtil.hideKeyboard(txtRight);
        if (TextUtils.isEmpty(provinceName) || TextUtils.isEmpty(fieldDetailAddress.getContent())) {
            toast(R.string.please_input_all_info);
            return;
        }
        if (LIVE_TYPE.equals(type) && TextUtils.isEmpty(fieldCartAddress.getContent())) {
            toast(R.string.please_input_all_info);
            return;
        }
        showWaitDialog();
        AddressInfoBean bean = new AddressInfoBean();
        bean.provinceCode = provinceId;
        bean.provinceName = provinceName;
        bean.cityCode = cityId;
        bean.cityName = cityName;
        bean.countyCode = countyId;
        bean.countyName = countyName;
        HRApi.saveAddress(bean, fieldDetailAddress.getContent(), fieldCartAddress.getContent(),
                type, getToken(), getNewHandler(REQUEST_SAVE_INFO, ResultBase.class));
    }

    /**
     * 切换保存的界面
     **/
    private void changeSaveView(TextView txtRight) {
        layoutInfo.setVisibility(View.GONE);
        layoutEdit.setVisibility(View.VISIBLE);
        txtRight.setText(R.string.btn_save);
        if (!TextUtils.isEmpty(addressInfo.address)) {
            fieldAddress.setContent(addressInfo.toString());
        } else {
            fieldAddress.setContent("");
        }
        if (!TextUtils.isEmpty(addressInfo.address)) {
            fieldDetailAddress.setContent(addressInfo.address);
        } else {
            fieldDetailAddress.setContent("");
        }
        if (LIVE_TYPE.equals(type)) {
            if (!TextUtils.isEmpty(addressInfo.carWait)) {
                fieldCartAddress.setContent(addressInfo.carWait);
            } else {
                fieldCartAddress.setContent("");
            }
        } else {
            fieldCartAddress.setVisibility(View.GONE);
        }
        isEdit = true;
    }


    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        if (requestCode == REQUEST_GET_INFO) {
            addressInfo = ((ResultGetAddressInfo) result).addressInfo;
            provinceId = addressInfo.provinceCode;
            cityId = addressInfo.cityCode;
            countyId = addressInfo.countyCode;
            provinceName = addressInfo.provinceName;
            cityName = addressInfo.cityName;
            countyName = addressInfo.countyName;
            String nullStr = getString(R.string.not_input);
            String address = addressInfo.address;
            txtDetailAddress.setContentText(TextUtils.isEmpty(address) ? nullStr : address);
            String detailAddress = addressInfo.toString();
            txtAddress.setContentText(TextUtils.isEmpty(detailAddress) ? nullStr : detailAddress);
            if (LIVE_TYPE.equals(type)) {
                if (TextUtils.isEmpty(addressInfo.carWait)) {
                    txtCartAddress.setContentText(nullStr);
                } else {
                    txtCartAddress.setContentText(addressInfo.carWait);
                }
            } else {
                txtCartAddress.setVisibility(View.GONE);
            }
        } else if (requestCode == REQUEST_SAVE_INFO) {
            TextView txtRight = getTitleBar().getTvRightView();
            txtRight.setText(R.string.team_group_detail_edit);
            layoutEdit.setVisibility(View.GONE);
            layoutInfo.setVisibility(View.VISIBLE);
            isEdit = false;
            initData();
        }

    }

    @Override
    protected void initData() {
        HRApi.getAddressInfo(type, getToken(), getNewHandler(REQUEST_GET_INFO, ResultGetAddressInfo.class));
    }

    @Override
    protected void initContentView(View mainView) {
        fieldAddress.setListener(this);
    }

    private String provinceId, cityId, countyId;
    private String provinceName, cityName, countyName;

    @Override
    public void onClickLayout(View view) {
        AddressSelectDialog dialog = AddressSelectDialog.newInstance(provinceId, cityId, countyId);
        dialog.setListener(this);
        dialog.show(getFragmentManager(), "");
    }

    @Override
    public void onClickRight(View view) {

    }

    @Override
    public void onSelect(Area province, Area city, Area country) {
        StringBuffer strBuf = new StringBuffer();
        if (province != null) {
            provinceId = province.getDcode();
            provinceName = province.getDname();
            strBuf.append(provinceName + " ");
        }
        if (city != null) {
            cityId = city.getDcode();
            cityName = city.getDname();
            strBuf.append(cityName + " ");
        }
        if (country != null) {
            countyId = country.getDcode();
            countyName = country.getDname();
            strBuf.append(countyName);
        } else {
            countyId = null;
            countyName = null;
        }
        fieldAddress.setContent(strBuf.toString());
    }
}

package cn.com.bluemoon.delivery.module.hr.personinfo;

import android.support.v4.app.Fragment;
import android.view.View;

import butterknife.Bind;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.ResultUser;
import cn.com.bluemoon.delivery.module.newbase.BaseFragment;
import cn.com.bluemoon.delivery.module.newbase.view.CommonActionBar;
import cn.com.bluemoon.lib_widget.module.form.BMFieldArrow1View;
import cn.com.bluemoon.lib_widget.module.form.BmCellTextView;

/**
 * 个人信息
 * Created by lk on 2017/6/14.
 */

public class PersonInfoFragment extends BaseFragment<CommonActionBar> implements
        BMFieldArrow1View.FieldArrowListener {
    private static final int REQUEST_CODE_GET_INFO = 0x777;
    @Bind(R.id.bctv_info)
    BmCellTextView bctvInfo;
    @Bind(R.id.bfav_phone)
    BMFieldArrow1View bfavPhone;
    @Bind(R.id.bfav_base_info)
    BMFieldArrow1View bfavBaseInfo;
    @Bind(R.id.bfav_contact)
    BMFieldArrow1View bfavContact;
    @Bind(R.id.bfav_tx_address)
    BMFieldArrow1View bfavTxAddress;
    @Bind(R.id.bfav_hk_address)
    BMFieldArrow1View bfavHkAddress;
    @Bind(R.id.bfav_family)
    BMFieldArrow1View bfavFamily;
    @Bind(R.id.bfav_favorite)
    BMFieldArrow1View bfavFavorite;

    public static Fragment newInstance() {
        PersonInfoFragment fragment = new PersonInfoFragment();
        return fragment;
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.user_info);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_person_info;
    }

    @Override
    protected void initContentView(View mainView) {
        bfavContact.setListener(this);
    }

    @Override
    protected void initData() {
        showWaitDialog();
        DeliveryApi.getUserInfo(getToken(), getNewHandler(REQUEST_CODE_GET_INFO, ResultUser.class));
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        switch (requestCode) {
            // 获取个人信息
            case REQUEST_CODE_GET_INFO:
                setData((ResultUser) result);
                break;
        }
    }

    /**
     * 设置个人信息
     */
    private void setData(ResultUser result) {
        if (result.getUser() == null) {
            return;
        }
        bctvInfo.setContentText(result.getUser().getRealName() + " "
                + result.getUser().getAccount());
        bfavPhone.setContent(result.getUser().getMobileNo());
    }

    @Override
    public void onClickLayout(View view) {
        switch (view.getId()) {
            // 联系人
            case R.id.bfav_contact:
                pushFragment(ContactFragment.newInstance());
                break;
        }
    }

    @Override
    public void onClickRight(View view) {

    }
}

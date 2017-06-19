package cn.com.bluemoon.delivery.module.hr.personinfo;

import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;

import butterknife.Bind;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.HRApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.hr.personinfo.ResultGetContactInfo;
import cn.com.bluemoon.delivery.module.newbase.BaseFragment;
import cn.com.bluemoon.delivery.module.newbase.view.CommonActionBar;
import cn.com.bluemoon.lib_widget.module.form.BmCellTextView;

/**
 * 联系方式
 */

public class ContactFragment extends BaseFragment<CommonActionBar> {
    private static final int REQUEST_CODE_GET_CONTACT_INFO = 0x777;
    @Bind(R.id.bctv_weichat)
    BmCellTextView bctvWeichat;
    @Bind(R.id.bctv_emailPers)
    BmCellTextView bctvEmailPers;
    @Bind(R.id.bctv_officePlace)
    BmCellTextView bctvOfficePlace;
    @Bind(R.id.bctv_officePhone)
    BmCellTextView bctvOfficePhone;
    @Bind(R.id.bctv_officeSeat)
    BmCellTextView bctvOfficeSeat;
    @Bind(R.id.bctv_contactName)
    BmCellTextView bctvContactName;
    @Bind(R.id.bctv_contactRelation)
    BmCellTextView bctvContactRelation;
    @Bind(R.id.bctv_contactMobile)
    BmCellTextView bctvContactMobile;
    @Bind(R.id.bctv_contactName2)
    BmCellTextView bctvContactName2;
    @Bind(R.id.bctv_contactRelation2)
    BmCellTextView bctvContactRelation2;
    @Bind(R.id.bctv_contactMobile2)
    BmCellTextView bctvContactMobile2;

    public static Fragment newInstance() {
        ContactFragment fragment = new ContactFragment();
        return fragment;
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.create_collect_order_customer_phone);
    }

    @Override
    protected void initTitleBarView(View title) {
        super.initTitleBarView(title);

        CommonActionBar titleBar = (CommonActionBar) title;
        titleBar.getTvRightView().setVisibility(View.VISIBLE);
        titleBar.getTvRightView().setText(R.string.promote_edit_txt);
    }

    @Override
    protected void onActionBarBtnRightClick() {
        super.onActionBarBtnRightClick();
        pushFragment(EditContactFragment.newInstance(contactInfo));
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_contact;
    }

    @Override
    protected void initContentView(View mainView) {

    }

    @Override
    protected void initData() {
        showWaitDialog();
        HRApi.getContactInfo(getToken(),
                getNewHandler(REQUEST_CODE_GET_CONTACT_INFO, ResultGetContactInfo.class));
    }


    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        switch (requestCode) {
            // 获取联系方式
            case REQUEST_CODE_GET_CONTACT_INFO:
                setData(((ResultGetContactInfo) result).contactInfo);
                break;
        }
    }

    private ResultGetContactInfo.ContactInfoBean contactInfo;

    /**
     * 设置个人信息
     */
    private void setData(ResultGetContactInfo.ContactInfoBean contactInfo) {
        if (contactInfo == null) {
            return;
        }
        this.contactInfo = contactInfo;
        bctvWeichat.setContentText(getText(contactInfo.weichat));
        bctvEmailPers.setContentText(getText(contactInfo.emailPers));
        bctvOfficePlace.setContentText(getText(contactInfo.officePlace));
        bctvOfficePhone.setContentText(getText(contactInfo.officePhone));
        bctvOfficeSeat.setContentText(getText(contactInfo.officeSeat));
        bctvContactName.setContentText(getText(contactInfo.contactName));
        bctvContactRelation.setContentText(getText(contactInfo.contactRelation));
        bctvContactMobile.setContentText(getText(contactInfo.contactMobile));
        bctvContactName2.setContentText(getText(contactInfo.contactName2));
        bctvContactRelation2.setContentText(getText(contactInfo.contactRelation2));
        bctvContactMobile2.setContentText(getText(contactInfo.contactMobile2));
    }

    private String getText(String str) {
        return TextUtils.isEmpty(str) ? getString(R.string.txt_empty_string) : str;
    }
}

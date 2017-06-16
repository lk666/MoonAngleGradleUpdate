package cn.com.bluemoon.delivery.module.hr.personinfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;

import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.HRApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.card.Workplace;
import cn.com.bluemoon.delivery.app.api.model.hr.personinfo.ResultGetContactInfo;
import cn.com.bluemoon.delivery.module.card.GetWorkPlaceActivity;
import cn.com.bluemoon.delivery.module.newbase.BaseFragment;
import cn.com.bluemoon.delivery.module.newbase.view.CommonActionBar;
import cn.com.bluemoon.lib_widget.module.form.BMFieldArrow1View;
import cn.com.bluemoon.lib_widget.module.form.BMFieldText1View;

/**
 * 编辑联系方式
 */

public class EditContactFragment extends BaseFragment<CommonActionBar> implements
        BMFieldArrow1View.FieldArrowListener {
    private static final int REQUEST_CODE_SAVE_CONTACT = 0x777;
    private static final String EXTRA_CONTACT_INFO = "EXTRA_CONTACT_INFO";
    private static final int REQUEST_CODE_GET_WORK_PLACE = 0x77;
    @Bind(R.id.bctv_weichat)
    BMFieldText1View bctvWeichat;
    @Bind(R.id.bctv_emailPers)
    BMFieldText1View bctvEmailPers;
    @Bind(R.id.bctv_officePlace)
    BMFieldArrow1View bctvOfficePlace;
    @Bind(R.id.bctv_officePhone)
    BMFieldText1View bctvOfficePhone;
    @Bind(R.id.bctv_officeSeat)
    BMFieldText1View bctvOfficeSeat;
    @Bind(R.id.bctv_contactName)
    BMFieldText1View bctvContactName;
    @Bind(R.id.bctv_contactRelation)
    BMFieldText1View bctvContactRelation;
    @Bind(R.id.bctv_contactMobile)
    BMFieldText1View bctvContactMobile;
    @Bind(R.id.bctv_contactName2)
    BMFieldText1View bctvContactName2;
    @Bind(R.id.bctv_contactRelation2)
    BMFieldText1View bctvContactRelation2;
    @Bind(R.id.bctv_contactMobile2)
    BMFieldText1View bctvContactMobile2;
    private ResultGetContactInfo.ContactInfoBean initContactInfo;

    public static Fragment newInstance(ResultGetContactInfo.ContactInfoBean contactInfo) {
        EditContactFragment fragment = new EditContactFragment();
        Bundle data = new Bundle();
        data.putSerializable(EXTRA_CONTACT_INFO, contactInfo);
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    protected void onGetArguments() {
        super.onGetArguments();
        initContactInfo = (ResultGetContactInfo.ContactInfoBean)
                getArguments().getSerializable(EXTRA_CONTACT_INFO);
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
        titleBar.getTvRightView().setText(R.string.btn_save);
    }

    @Override
    protected void onActionBarBtnRightClick() {
        super.onActionBarBtnRightClick();

        if (TextUtils.isEmpty(bctvContactName.getContent())) {
            toast(getString(R.string.err_empty_contact_name));
        } else if (TextUtils.isEmpty(bctvContactRelation.getContent())) {
            toast(getString(R.string.err_empty_contact_relation));
        } else if (TextUtils.isEmpty(bctvContactMobile.getContent())) {
            toast(getString(R.string.err_empty_contact_phone));
        } else {
            showWaitDialog();
            HRApi.saveContact(bctvContactMobile.getContent(),
                    bctvContactMobile2.getContent(),
                    bctvContactName.getContent(),
                    bctvContactName2.getContent(),
                    bctvContactRelation.getContent(),
                    bctvContactRelation2.getContent(),
                    bctvEmailPers.getContent(),
                    bctvOfficePhone.getContent(),
                    bctvOfficePlace.getContent(),
                    bctvOfficeSeat.getContent(),
                    getToken(),
                    bctvWeichat.getContent(),
                    getNewHandler(REQUEST_CODE_SAVE_CONTACT, ResultBase.class));
        }
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_edit_contact;
    }

    @Override
    protected void initContentView(View mainView) {
        bctvOfficePlace.setListener(this);
    }

    @Override
    protected void initData() {
        if (initContactInfo == null) {
            return;
        }

        bctvWeichat.setContent(initContactInfo.weichat);
        bctvEmailPers.setContent(initContactInfo.emailPers);
        bctvOfficePlace.setContent(initContactInfo.officePlace);
        bctvOfficePhone.setContent(initContactInfo.officePhone);
        bctvOfficeSeat.setContent(initContactInfo.officeSeat);
        bctvContactName.setContent(initContactInfo.contactName);
        bctvContactRelation.setContent(initContactInfo.contactRelation);
        bctvContactMobile.setContent(initContactInfo.contactMobile);
        bctvContactName2.setContent(initContactInfo.contactName2);
        bctvContactRelation2.setContent(initContactInfo.contactRelation2);
        bctvContactMobile2.setContent(initContactInfo.contactMobile2);
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        switch (requestCode) {
            // 编辑联系方式
            case REQUEST_CODE_SAVE_CONTACT:

                initContactInfo.contactMobile = bctvContactMobile.getContent();
                initContactInfo.contactMobile2 = bctvContactMobile2.getContent();
                initContactInfo.contactName = bctvContactName.getContent();
                initContactInfo.contactName2 = bctvContactName2.getContent();
                initContactInfo.contactRelation = bctvContactRelation.getContent();
                initContactInfo.contactRelation2 = bctvContactRelation2.getContent();
                initContactInfo.emailPers = bctvEmailPers.getContent();
                initContactInfo.officePhone = bctvOfficePhone.getContent();
                initContactInfo.officePlace = bctvOfficePlace.getContent();
                initContactInfo.officeSeat = bctvOfficeSeat.getContent();
                initContactInfo.weichat = bctvWeichat.getContent();

                toast(getString(R.string.txt_save_success));
                back();
                break;
        }
    }

    @Override
    public void onClickLayout(View view) {
        switch (view.getId()) {
            // 办公地点
            case R.id.bctv_officePlace:
                Intent intent = new Intent(getContext(), GetWorkPlaceActivity.class);
                startActivityForResult(intent, REQUEST_CODE_GET_WORK_PLACE);
                break;
        }
    }

    @Override
    public void onClickRight(View view) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_GET_WORK_PLACE:
                    if (data == null) return;
                    Workplace workplace = (Workplace) data.getSerializableExtra(GetWorkPlaceActivity
                            .EXTRA_WORK_PLACE);
                    if (workplace != null) {
                        bctvOfficePlace.setContent(workplace.getWorkplaceCode() + "-" + workplace
                                .getWorkplaceName());
                    }
                    break;
            }
        }
    }
}

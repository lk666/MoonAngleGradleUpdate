package cn.com.bluemoon.delivery.module.hr.personinfo;

import android.support.v4.app.Fragment;
import android.view.View;

import butterknife.Bind;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.HRApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.hr.personinfo.ResultGetBaseInfo;
import cn.com.bluemoon.delivery.module.newbase.BaseFragment;
import cn.com.bluemoon.delivery.module.newbase.view.CommonActionBar;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.lib_widget.module.form.BmCellTextView;

/**
 * 基本信息
 */

public class BasicInfoFragment extends BaseFragment<CommonActionBar> {
    private static final int REQUEST_CODE_GET_INFO = 0x777;
    @Bind(R.id.bctv_inDate)
    BmCellTextView bctvInDate;
    @Bind(R.id.bctv_educationHighest)
    BmCellTextView bctvEducationHighest;
    @Bind(R.id.bctv_gradSchool)
    BmCellTextView bctvGradSchool;
    @Bind(R.id.bctv_major)
    BmCellTextView bctvMajor;
    @Bind(R.id.bctv_blood)
    BmCellTextView bctvBlood;
    @Bind(R.id.bctv_marriage)
    BmCellTextView bctvMarriage;
    @Bind(R.id.bctv_idcard)
    BmCellTextView bctvIdcard;
    @Bind(R.id.bctv_bankNo)
    BmCellTextView bctvBankNo;
    @Bind(R.id.bctv_emailComp)
    BmCellTextView bctvEmailComp;
    @Bind(R.id.bctv_hkAddress)
    BmCellTextView bctvHkAddress;
    @Bind(R.id.bctv_txAddress)
    BmCellTextView bctvTxAddress;

    public static Fragment newInstance() {
        BasicInfoFragment fragment = new BasicInfoFragment();
        return fragment;
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.txt_base_info);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_base_info;
    }

    @Override
    protected void initContentView(View mainView) {
    }

    @Override
    protected void initData() {
        showWaitDialog();
        HRApi.getBaseInfo(getToken(), getNewHandler(REQUEST_CODE_GET_INFO,
                ResultGetBaseInfo.class));
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        switch (requestCode) {
            // 获取基本信息
            case REQUEST_CODE_GET_INFO:
                setData(((ResultGetBaseInfo) result).baseInfo);
                break;
        }
    }

    private void setData(ResultGetBaseInfo.BaseInfoBean baseInfo) {
        if (baseInfo == null) {
            return;
        }
        if (baseInfo.inDate > 0) {
            bctvInDate.setContentText(DateUtil.getTime(baseInfo.inDate));
        }
        bctvEducationHighest.setContentText(baseInfo.educationHighest);
        bctvGradSchool.setContentText(baseInfo.educationHighest);
        bctvMajor.setContentText(baseInfo.major);
        bctvBlood.setContentText(baseInfo.blood);
        bctvMarriage.setContentText(baseInfo.marriage);
        bctvIdcard.setContentText(baseInfo.idcard);
        bctvBankNo.setContentText(baseInfo.bankNo);
        bctvEmailComp.setContentText(baseInfo.emailComp);
        bctvHkAddress.setContentText(baseInfo.hkAddress);
        bctvTxAddress.setContentText(baseInfo.txAddress);
    }
}

package cn.com.bluemoon.delivery.module.hr.personinfo;

import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.HRApi;
import cn.com.bluemoon.delivery.app.api.model.Dict;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.ResultDict;
import cn.com.bluemoon.delivery.app.api.model.personalinfo.ResultGetInterstInfo;
import cn.com.bluemoon.delivery.module.newbase.BaseFragment;
import cn.com.bluemoon.delivery.module.newbase.view.CommonActionBar;
import cn.com.bluemoon.delivery.utils.ViewUtil;
import cn.com.bluemoon.lib.view.CommonAlertDialog;
import cn.com.bluemoon.lib_widget.module.choice.BMRadioItemView;
import cn.com.bluemoon.lib_widget.module.choice.entity.RadioItem;
import cn.com.bluemoon.lib_widget.module.form.BMFieldParagraphView;
import cn.com.bluemoon.lib_widget.module.form.BMFieldText1View;
import cn.com.bluemoon.lib_widget.module.form.BMSelectorView;
import cn.com.bluemoon.lib_widget.module.form.BMSelectorView.ClickListener;
import cn.com.bluemoon.lib_widget.module.form.BmCellParagraphView;

/**
 * 爱好特长
 * Created by lk on 2017/6/19.
 */

public class InterestFragment extends BaseFragment<CommonActionBar> implements ClickListener {

    @Bind(R.id.txt_interest)
    BmCellParagraphView txtInterest;
    @Bind(R.id.layout_info)
    LinearLayout layoutInfo;
    @Bind(R.id.field_interest)
    BMFieldParagraphView fieldInterest;
    @Bind(R.id.field_interest2)
    BMSelectorView fieldInterest2;
    @Bind(R.id.field_interest_detail)
    BMFieldParagraphView fieldInterestDetail;
    @Bind(R.id.layout_edit)
    LinearLayout layoutEdit;
    @Bind(R.id.field_other)
    BMFieldText1View fieldOther;
    @Bind(R.id.txt_specialty)
    TextView txtSpecialty;
    @Bind(R.id.txt_perform_experience)
    TextView txtPerformExperience;
    private boolean isEdit;
    private String specialty;
    private ResultGetInterstInfo interstInfo;
    private List<RadioItem> list;
    private final int REQUEST_GET_INTEREST = 1;
    private final int REQUEST_GET_DICT_INFO = 2;
    private final int REQUEST_SAVE_INTEREST = 3;

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
            ViewUtil.hideKeyboard(txtRight);
            new CommonAlertDialog.Builder(getActivity()).setMessage(R.string.save_info_tips)
                    .setPositiveButton(R.string.btn_cancel_space, null)
                    .setNegativeButton(R.string.btn_ok_space, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            showWaitDialog();
                            ResultGetInterstInfo bean = new ResultGetInterstInfo();
                            bean.interest = fieldInterest.getContent();
                            bean.otherHobby = fieldOther.getContent();
                            bean.performExperience = fieldInterestDetail.getContent();
                            bean.specialty = specialty;
                            HRApi.saveInterest(bean, getToken(), getNewHandler(REQUEST_SAVE_INTEREST, ResultBase.class));
                        }
                    }).show();
        } else if (interstInfo != null) {
            specialty = interstInfo.specialty;
            layoutInfo.setVisibility(View.GONE);
            layoutEdit.setVisibility(View.VISIBLE);
            txtRight.setText(R.string.btn_save);
            initEditView();
            isEdit = true;
        }
    }

    /**
     * 初始化编辑界面
     **/
    private void initEditView() {
        fieldInterest.setContent(interstInfo.interest);
        fieldInterestDetail.setContent(interstInfo.performExperience);
        if (list.size() > 0) {
            //舞蹈、唱歌等是否选择了
            for (RadioItem item : list) {
                if (!TextUtils.isEmpty(interstInfo.specialty)) {
                    String[] specialtys = interstInfo.specialty.split(getString(R.string.punctuation_1));
                    for (String s : specialtys) {
                        if (s.equals(item.text)) {
                            item.type = BMRadioItemView.TYPE_SELECT;
                            break;
                        } else {
                            item.type = BMRadioItemView.TYPE_NORMAL;
                        }
                    }
                } else {
                    item.type = BMRadioItemView.TYPE_NORMAL;
                }
            }
            //其他是否选择了
            if (!TextUtils.isEmpty(interstInfo.otherHobby)) {
                list.get(list.size() - 1).type = BMRadioItemView.TYPE_SELECT;
                fieldOther.setVisibility(View.VISIBLE);
                fieldOther.setContent(interstInfo.otherHobby);
            } else {
                list.get(list.size() - 1).type = BMRadioItemView.TYPE_NORMAL;
                fieldOther.setVisibility(View.GONE);
            }
        }
        fieldInterest2.setData(list);
    }


    @Override
    protected String getTitleString() {
        return getString(R.string.txt_interest);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_interest;
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        if (requestCode == REQUEST_GET_INTEREST) {
            setInitDataView((ResultGetInterstInfo) result);
        } else if (requestCode == REQUEST_GET_DICT_INFO) {
            setSeletorData((ResultDict) result);
        } else if (requestCode == REQUEST_SAVE_INTEREST) {
            isEdit = false;
            layoutEdit.setVisibility(View.GONE);
            layoutInfo.setVisibility(View.VISIBLE);
            getTitleBar().getTvRightView().setText(R.string.team_group_detail_edit);
            initData();
        }


    }

    /**
     * 初始化爱好的业务字典
     **/
    private void setSeletorData(ResultDict result) {
        ResultDict resultDict = result;
        List<Dict> dicts = resultDict.getItemList();
        if (dicts != null) {
            int size = dicts.size();
            if (size > 0) {
                list = new ArrayList<>();
                for (int i = 0; i < size; i++) {
                    Dict dict = dicts.get(i);
                    list.add(new RadioItem(dict, dict.getDictName(), BMRadioItemView.TYPE_NORMAL));
                }
                list.add(new RadioItem("0", getString(R.string.other_insterest), BMRadioItemView.TYPE_NORMAL));
                fieldInterest2.setData(list);
            }
        }
    }

    /**
     * 初始化数据跟界面
     **/
    private void setInitDataView(ResultGetInterstInfo result) {
        interstInfo = result;
        String interest = interstInfo.interest;
        if (!TextUtils.isEmpty(interest)) {
            txtInterest.setContentText(interest);
        } else {
            txtInterest.setContentText(getString(R.string.not_input));
        }
        String otherHobby = interstInfo.otherHobby;
        String specialty = interstInfo.specialty;
        if (TextUtils.isEmpty(otherHobby) && TextUtils.isEmpty(specialty)) {
            txtSpecialty.setText(getString(R.string.not_input));
        } else {
            StringBuffer strBuff = new StringBuffer();
            if (!TextUtils.isEmpty(specialty)) {
                strBuff.append(specialty);
                if (!TextUtils.isEmpty(otherHobby))
                    strBuff.append(getString(R.string.punctuation_1));
            }
            if (!TextUtils.isEmpty(otherHobby)) {
                strBuff.append(otherHobby);
            }
            txtSpecialty.setText(strBuff.toString());
        }
        String performExperience = interstInfo.performExperience;
        if (TextUtils.isEmpty(performExperience)) {
            txtPerformExperience.setVisibility(View.GONE);
        } else {
            txtPerformExperience.setVisibility(View.VISIBLE);
            txtPerformExperience.setText(performExperience);
        }
    }

    @Override
    protected void initData() {
        showWaitDialog();
        HRApi.getInterest(getToken(), getNewHandler(REQUEST_GET_INTEREST, ResultGetInterstInfo.class));
        if (list == null) {
            DeliveryApi.getDictInfo("CRM_PERSON_INFO_SPECIALTY", getNewHandler(REQUEST_GET_DICT_INFO, ResultDict.class));
        }
    }

    @Override
    protected void initContentView(View mainView) {
        fieldInterest2.setListener(this);
    }

    @Override
    public void onSelected(List<RadioItem> list) {
        int size = list.size();
        specialty = "";
        if (size > 0) {
            RadioItem item = list.get(list.size() - 1);
            if (!(item.value instanceof Dict)) {
                fieldOther.setVisibility(View.VISIBLE);
                fieldInterest2.setLineVisible(View.GONE);
                size--;
            } else {
                fieldOther.setVisibility(View.GONE);
                fieldInterest2.setLineVisible(View.VISIBLE);
                fieldOther.setContent("");
            }
            StringBuffer strBuff = new StringBuffer();
            for (int i = 0; i < size; i++) {
                strBuff.append(list.get(i).text);
                if (i != size - 1) {
                    strBuff.append(getString(R.string.punctuation_1));
                }
            }
            specialty = strBuff.toString();
        }
    }
}

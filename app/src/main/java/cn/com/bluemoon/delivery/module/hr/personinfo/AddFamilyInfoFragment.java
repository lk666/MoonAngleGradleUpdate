package cn.com.bluemoon.delivery.module.hr.personinfo;

import android.app.AlertDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.HRApi;
import cn.com.bluemoon.delivery.app.api.model.Dict;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.ResultDict;
import cn.com.bluemoon.delivery.app.api.model.personalinfo.ResultGetFamilyInfo.FamilyListBean;
import cn.com.bluemoon.delivery.module.newbase.BaseFragment;
import cn.com.bluemoon.delivery.module.newbase.view.CommonActionBar;
import cn.com.bluemoon.delivery.ui.selectordialog.ShadowSingleOptionSelectDialog;
import cn.com.bluemoon.delivery.ui.selectordialog.SingleOptionSelectDialog;
import cn.com.bluemoon.delivery.utils.DateUtil;
import cn.com.bluemoon.delivery.utils.ViewUtil;
import cn.com.bluemoon.lib.view.CommonAlertDialog;
import cn.com.bluemoon.lib.view.CommonDatePickerDialog;
import cn.com.bluemoon.lib_widget.module.choice.BMRadioItemView;
import cn.com.bluemoon.lib_widget.module.choice.BMRadioView;
import cn.com.bluemoon.lib_widget.module.choice.entity.RadioItem;
import cn.com.bluemoon.lib_widget.module.form.BMFieldArrow1View;
import cn.com.bluemoon.lib_widget.module.form.BMFieldArrow1View.FieldArrowListener;
import cn.com.bluemoon.lib_widget.module.form.BMFieldText1View;

/**
 * 新增编辑家庭情况
 * Created by ljl on 2017/6/14.
 */

public class AddFamilyInfoFragment extends BaseFragment<CommonActionBar> {


    @Bind(R.id.item_radio)
    BMRadioView itemRadio;
    @Bind(R.id.layout_relationship)
    BMFieldArrow1View layoutRelationship;
    @Bind(R.id.layout_birthday)
    BMFieldArrow1View layoutBirthday;
    @Bind(R.id.layout_name)
    BMFieldText1View layoutName;
    @Bind(R.id.layout_sur_name)
    BMFieldText1View layoutSurName;
    @Bind(R.id.layout_workplace)
    BMFieldText1View layoutWorkplace;
    @Bind(R.id.layout_position)
    BMFieldText1View layoutPosition;
    private String type;
    public final static String MODIFY_TYPE = "modify";
    public final static String ADD_TYPE = "add";
    List<String> relationshipList;
    private String gender;
    private FamilyListBean bean;

    public static Fragment newInstance(String type) {
        AddFamilyInfoFragment fragment = new AddFamilyInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static Fragment newInstance(String type, FamilyListBean bean) {
        AddFamilyInfoFragment fragment = new AddFamilyInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        bundle.putSerializable("bean", bean);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    protected String getTitleString() {
        type = getArguments().getString("type");
        if (MODIFY_TYPE.equals(type)) {
            bean = (FamilyListBean) getArguments().getSerializable("bean");
            return getString(R.string.modify_family_info_title);
        }
        return getString(R.string.add_family_info_title);
    }

    @Override
    protected void initTitleBarView(View title) {
        super.initTitleBarView(title);
        CommonActionBar actionBar = (CommonActionBar) title;
        actionBar.getTvRightView().setText(R.string.btn_save);
        actionBar.getTvRightView().setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActionBarBtnRightClick() {
        ViewUtil.hideKeyboard(getTitleBar());
        new CommonAlertDialog.Builder(getActivity()).setMessage(R.string.save_info_tips)
                .setPositiveButton(R.string.btn_cancel_space, null)
                .setNegativeButton(R.string.btn_ok_space, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        saveFamliyInfo();
                    }
                }).show();
    }

    private void saveFamliyInfo() {
        if (TextUtils.isEmpty(layoutRelationship.getContent())
                || TextUtils.isEmpty(layoutName.getContent())
                || TextUtils.isEmpty(layoutSurName.getContent())
                || TextUtils.isEmpty(gender)) {
            toast(R.string.please_input_all_info);
            return;
        }
        showWaitDialog();
        FamilyListBean bean = new FamilyListBean();
        bean.name = layoutName.getContent();
        bean.surname = layoutSurName.getContent();
        bean.relationship = layoutRelationship.getContent();
        bean.gender = gender;
        boolean hasInputBirthday = false;
        if (!TextUtils.isEmpty(layoutBirthday.getContent())) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {
                hasInputBirthday = true;
                Date date = sdf.parse(layoutBirthday.getContent());
                bean.birthday = date.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        bean.workPlace = layoutWorkplace.getContent();
        bean.menberPosition = layoutPosition.getContent();
        if (this.bean != null) {
            bean.familyId = this.bean.familyId;
        }
        HRApi.saveOrUpdateFamily(hasInputBirthday, bean, getToken(), getNewHandler(2, ResultBase.class));
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_add_family_info;
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        if (requestCode == 1) {
            ResultDict r = (ResultDict) result;
            if (r.getItemList() != null && r.getItemList().size() > 0) {
                relationshipList = new ArrayList<>();
                for (Dict dict : r.getItemList()) {
                    relationshipList.add(dict.getDictId());
                }
            }
        } else if (requestCode == 2) {
            toast(result.getResponseMsg());
            back();
        }
    }

    @Override
    protected void initData() {
        DeliveryApi.getDictInfo("CRM_PERSON_INFO_RELATIONSHIP", getNewHandler(1, ResultDict.class));
    }

    @Override
    protected void initContentView(View mainView) {
        FieldArrowListener listener = new FieldArrowListener() {
            @Override
            public void onClickLayout(View view) {
                if (view == layoutRelationship) {
                    showSelectView();
                } else {
                    showDatePickerDialog();
                }
            }

            @Override
            public void onClickRight(View view) {

            }
        };
        layoutRelationship.setListener(listener);
        layoutBirthday.setListener(listener);
        //如果是编辑界面，就显示数据
        if (bean != null) {
            gender = bean.gender;
            layoutRelationship.setContent(bean.relationship);
            layoutName.setContent(bean.name);
            layoutSurName.setContent(bean.surname);
            //9999年份代表字段为空
            if (!"9999".equals(DateUtil.getTime(bean.birthday, "yyyy"))) {
                layoutBirthday.setContent(DateUtil.getTime(bean.birthday, "yyyy-MM-dd"));
            }
            if (!TextUtils.isEmpty(bean.workPlace)) {
                layoutWorkplace.setContent(bean.workPlace);
            }
            if (!TextUtils.isEmpty(bean.menberPosition)) {
                layoutPosition.setContent(bean.menberPosition);
            }
        }

        List<RadioItem> list = new ArrayList<>();
        String maleStr = getString(R.string.gender_male);
        list.add(new RadioItem(null, maleStr, maleStr.equals(gender) ? BMRadioItemView.TYPE_SELECT : BMRadioItemView.TYPE_NORMAL));
        String femaleStr = getString(R.string.gender_female);
        list.add(new RadioItem(null, femaleStr, femaleStr.equals(gender) ? BMRadioItemView.TYPE_SELECT : BMRadioItemView.TYPE_NORMAL));
        itemRadio.setListener(new BMRadioView.ClickListener() {
            @Override
            public void onSelected(int position, Object value) {
                gender = ((RadioItem) value).text.toString();
            }
        });
        itemRadio.setData(list);
    }


    private ShadowSingleOptionSelectDialog dialog;

    private void showSelectView() {
        if (relationshipList.isEmpty()) {
            return;
        }
        int index = relationshipList.size() > 2 ? 1 : 0;
        if (dialog == null) {
            dialog = new ShadowSingleOptionSelectDialog(getActivity(), "",
                    relationshipList, index, new SingleOptionSelectDialog.OnButtonClickListener() {
                @Override
                public void onOKButtonClick(int index, String text) {
                    layoutRelationship.setContent(text);
                }

                @Override
                public void onCancelButtonClick() {

                }
            });
        }

        dialog.show();
    }

    private CommonDatePickerDialog datePicker;
    private OnDateSetListener mDateSetListener = new OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            StringBuffer strBuf = new StringBuffer(year + "-");
            if (monthOfYear < 9) {
                strBuf.append(0);
            }
            strBuf.append(monthOfYear + 1).append("-");
            if (dayOfMonth < 10) {
                strBuf.append(0);
            }
            strBuf.append(dayOfMonth);
            layoutBirthday.setContent(strBuf.toString());
        }
    };

    public void showDatePickerDialog() {
        int mIYear;
        int mIMon;
        int mIDay;
        String birthday = layoutBirthday.getContent();
        if (!TextUtils.isEmpty(birthday)) {
            String[] value = birthday.split("-");
            mIYear = Integer.valueOf(value[0]);
            mIMon = Integer.valueOf(value[1]) - 1;
            mIDay = Integer.valueOf(value[2]);
        } else {
            Calendar dateAndTime = Calendar.getInstance(Locale.CHINA);
            mIYear = dateAndTime.get(Calendar.YEAR);
            mIMon = dateAndTime.get(Calendar.MONTH);
            mIDay = dateAndTime.get(Calendar.DAY_OF_MONTH);
        }

        if (datePicker == null) {
            datePicker = new CommonDatePickerDialog(getActivity(),
                    AlertDialog.THEME_HOLO_LIGHT, mDateSetListener, mIYear, mIMon, mIDay);
            datePicker.getDatePicker().setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);
            //时间范围控制，当前时间之前
            datePicker.getDatePicker().init(mIYear, mIMon, mIDay, new DatePicker.OnDateChangedListener() {
                @Override
                public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {
                    StringBuffer sb = new StringBuffer();
                    sb.append(i);
                    int mon = i1 + 1;
                    if (mon < 10) {
                        sb.append(0);
                    }
                    sb.append(mon);
                    if (i2 < 10) {
                        sb.append(0);
                    }
                    sb.append(i2);
                    Calendar calendar = Calendar.getInstance(Locale.CHINA);
                    long currentTime = calendar.getTime().getTime();
                    if (Integer.valueOf(sb.toString()) > Integer.valueOf(DateUtil.getTime(currentTime, "yyyyMMdd"))) {
                        int year = Integer.valueOf(DateUtil.getTime(currentTime, "yyyy"));
                        int month = Integer.valueOf(DateUtil.getTime(currentTime, "MM")) - 1;
                        int day = Integer.valueOf(DateUtil.getTime(currentTime, "dd"));
                        datePicker.init(year, month, day, this);
                    }
                }
            });
            datePicker.show();
        } else if (!datePicker.isShowing()) {
            datePicker.updateDate(mIYear, mIMon, mIDay);
            datePicker.getDatePicker().setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);
            datePicker.show();
        }
    }
}

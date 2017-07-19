package cn.com.bluemoon.delivery.module.offline;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.lib_widget.module.choice.BMCheckList2View;
import cn.com.bluemoon.lib_widget.module.choice.BMRadioItemView;
import cn.com.bluemoon.lib_widget.module.choice.entity.RadioItem;
import cn.com.bluemoon.lib_widget.module.choice.interf.CheckListener;
import cn.com.bluemoon.lib_widget.module.form.BMAngleBtn3View;
import cn.com.bluemoon.lib_widget.module.form.BmCellTextView;

public class TeacherScanPlanActivity extends BaseActivity implements CheckListener{

    @Bind(R.id.view_all)
    BMRadioItemView viewAll;
    @Bind(R.id.btn_sign)
    BMAngleBtn3View btnSign;
    @Bind(R.id.layout_bottom)
    LinearLayout layoutBottom;
    @Bind(R.id.view_info)
    BmCellTextView viewInfo;
    @Bind(R.id.view_code)
    BmCellTextView viewCode;
    @Bind(R.id.view_theme)
    BmCellTextView viewTheme;
    @Bind(R.id.list_check)
    BMCheckList2View listCheck;
    @Bind(R.id.activity_teacher_scan_plan)
    RelativeLayout activityTeacherScanPlan;

    @Override
    protected String getTitleString() {
        return getString(R.string.offline_sign);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_teacher_scan_plan;
    }

    @Override
    public void initView() {
        listCheck.setListener(this);
        List<RadioItem> list2 = new ArrayList<>();
        list2.add(new RadioItem("0", "111", -1));
        list2.add(new RadioItem("1", "222", 0));
        list2.add(new RadioItem("2", "3333", 1));
        list2.add(new RadioItem("3", "44444", 1));
        listCheck.setData(list2);

    }

    @Override
    public void initData() {

    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {

    }

    @OnClick({R.id.view_all, R.id.btn_sign})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.view_all:
                if(viewAll.getType()==BMRadioItemView.TYPE_NORMAL){
                    viewAll.setType(BMRadioItemView.TYPE_SELECT);
                    listCheck.selectAll();
                }else if(viewAll.getType()==BMRadioItemView.TYPE_SELECT){
                    viewAll.setType(BMRadioItemView.TYPE_NORMAL);
                    listCheck.clearAll();
                }

                break;
            case R.id.btn_sign:
                break;
        }
    }

    public void checkAll(){
        if(listCheck.isSelectAll()){
            viewAll.seleced();
        }else{
            viewAll.cancel();
        }
    }

    @Override
    public void onSelected(View view, int position) {
        checkAll();
    }

    @Override
    public void onCancel(View view, int position) {
        checkAll();
    }

    @Override
    public void onClickDisable(View view, int position) {

    }
}

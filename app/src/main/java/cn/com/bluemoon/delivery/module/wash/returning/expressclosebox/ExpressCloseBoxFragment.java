package cn.com.bluemoon.delivery.module.wash.returning.expressclosebox;

import android.content.Intent;
import android.graphics.Color;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.ReturningApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.clothing.ResultUserInfo;
import cn.com.bluemoon.delivery.module.base.BaseFragment;
import cn.com.bluemoon.delivery.module.order.OrdersUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.callback.CommonEditTextCallBack;
import cn.com.bluemoon.lib.swipe.menu.SwipeMenu;
import cn.com.bluemoon.lib.swipe.menu.SwipeMenuCreator;
import cn.com.bluemoon.lib.swipe.menu.SwipeMenuItem;
import cn.com.bluemoon.lib.swipe.menu.SwipeMenuListView;
import cn.com.bluemoon.lib.utils.LibConstants;
import cn.com.bluemoon.lib.utils.LibViewUtil;
import cn.com.bluemoon.lib.view.ClearEditText;

/**
 * Created by ljl on 2016/9/28.
 */
public class ExpressCloseBoxFragment extends BaseFragment {

    @BindView(R.id.et_delivery_num)
    ClearEditText etExpressCode;
    @BindView(R.id.et_emy_num)
    ClearEditText etEmpNum;
    @BindView(R.id.txt_delivery_name)
    TextView txtDeliveryName;
    @BindView(R.id.txt_company)
    TextView txtCompany;
    @BindView(R.id.list_return_number)
    SwipeMenuListView listReturnNumber;
    @BindView(R.id.btn_query)
    Button btnQuery;
    @BindView(R.id.line_dotted)
    View lineDotted;
    @BindView(R.id.txt_amount)
    TextView txtAmount;

    private String companyCode;
    private String companyName;
    private ClothesNoAdapter adapter;
    private List<String> codes = new ArrayList<>();
    private String boxCode;

    @Override
    protected String getTitleString() {
        return getString(R.string.express_close_box_tab1);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_delivery_close_box;
    }

    @OnClick({R.id.layout_company, R.id.img_scan, R.id.btn_query, R.id.img_add, R.id.txt_add, R.id.btn_ok})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_company:
                Intent intent = new Intent(getActivity(), ExpressCompanyActivity.class);
                intent.putExtra("companyCode", companyCode);
                ExpressCloseBoxFragment.this.startActivityForResult(intent, 1);
                break;
            case R.id.img_add:
            case R.id.txt_add:
                BackClothesScanActivity.actStart(this, getString(R.string.scan_clothes_num_title),getString(R.string.input_by_hand), null, boxCode, codes,2);
                break;
            case R.id.img_scan:
                PublicUtil.openNewScanView(this, getString(R.string.scan_delivery_title2), getString(R.string.input_by_hand), null, 3);
                break;
            case R.id.btn_query:
                String empCode = etEmpNum.getText().toString();
                if (StringUtils.isNotBlank(empCode)) {
                    showWaitDialog();
                    DeliveryApi.getEmp(getToken(), empCode, getNewHandler(1, ResultUserInfo.class));
                } else {
                    toast(getString(R.string.please_input_emp_code));
                }
                break;
            case R.id.btn_ok:
                empCode = etEmpNum.getText().toString();
                String empName = txtDeliveryName.getText().toString();
                String companyName = txtCompany.getText().toString();
                String expressCode = etExpressCode.getText().toString();
                if (!StringUtils.isNotBlank(companyName)) {
                    toast(getString(R.string.please_select_company));
                    return;
                }
                if (!StringUtils.isNotBlank(expressCode)) {
                    toast(getString(R.string.please_scan_delivery_num));
                    return;
                }
                if (!StringUtils.isNotBlank(empCode)) {
                    toast(getString(R.string.emp_code_is_empty));
                    return;
                }
                if (!StringUtils.isNotBlank(empName)) {
                    toast(getString(R.string.emp_name_is_empty));
                    return;
                }
                if (codes.size() == 0) {
                    toast(getString(R.string.close_box_clothes_code_is_empty));
                    return;
                }
                showWaitDialog();
                ReturningApi.closeBox(codes, boxCode, companyCode, companyName,empCode,
                        empName,expressCode,getToken(), getNewHandler(2, ResultBase.class));
                break;
        }
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        hideWaitDialog();
        if (requestCode == 1) {
            ResultUserInfo info = (ResultUserInfo) result;
            txtDeliveryName.setText(info.getEmpName());
        } else if (requestCode == 2) {
            toast(result.getResponseMsg());
            codes.clear();
            adapter.notifyDataSetChanged();
            txtAmount.setText(getString(R.string.total_amount2, codes.size()));
            txtCompany.setText("");
            etExpressCode.setText("");
            etEmpNum.setText("");
            txtDeliveryName.setText("");
            boxCode = null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 1) {
            companyCode = data.getStringExtra("companyCode");
            companyName = data.getStringExtra("companyName");
            txtCompany.setText(companyName);
        } else if (data != null && requestCode == 3) {
            String number = data.getStringExtra(LibConstants.SCAN_RESULT);
            etExpressCode.setText(number);
        } else if (data != null && requestCode == 2) {
            List<String> codeList = data.getStringArrayListExtra("codes");
            if (codeList != null && !codeList.isEmpty()) {
                boxCode = data.getStringExtra("boxCode");
                codes.clear();
                codes.addAll(codeList);
                txtAmount.setText(getString(R.string.total_amount2, codes.size()));
                SwipeMenuCreator creator = new SwipeMenuCreator() {
                    @Override
                    public void create(SwipeMenu menu) {
                        SwipeMenuItem returnGoodsItem = new SwipeMenuItem(getActivity());
                        returnGoodsItem.setBackground(R.color.text_red);
                        returnGoodsItem.setWidth(OrdersUtils.dp2px(68, getActivity()));
                        returnGoodsItem.setTitle(R.string.delete_return_clothes);
                        returnGoodsItem.setTitleSize(15);
                        returnGoodsItem.setTitleColor(Color.WHITE);
                        menu.addMenuItem(returnGoodsItem);
                    }
                };
                listReturnNumber.setMenuCreator(creator);
                listReturnNumber.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(int position, SwipeMenu menu,
                                                   int index) {
                        if (index == 0) {
                            codes.remove(position);
                            txtAmount.setText(getString(R.string.total_amount2, codes.size()));
                            if (codes.isEmpty()) {
                                boxCode = null;
                            }
                            adapter.notifyDataSetChanged();
                            LibViewUtil.setListViewHeight2(listReturnNumber);
                        }
                        return false;
                    }
                });
                listReturnNumber.setPullRefreshEnable(false);
                adapter = new ClothesNoAdapter(getActivity());
                adapter.setList(codes);
                listReturnNumber.setAdapter(adapter);
                LibViewUtil.setListViewHeight2(listReturnNumber);
            }
        }
    }

    @Override
    public void initView() {
        txtAmount.setText(getString(R.string.total_amount2, 0));
        etEmpNum.setCallBack(new CommonEditTextCallBack() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                if (StringUtils.isEmpty(s.toString())) {
                    txtDeliveryName.setText("");
                }
            }
        });
    }

    @Override
    public void initData() {

    }


}

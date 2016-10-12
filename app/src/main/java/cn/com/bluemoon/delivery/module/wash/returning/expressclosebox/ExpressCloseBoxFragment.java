package cn.com.bluemoon.delivery.module.wash.returning.expressclosebox;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.base.BaseFragment;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.order.OrdersUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
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

    @Bind(R.id.et_delivery_num)
    ClearEditText etDeliveryNum;
    @Bind(R.id.et_emy_num)
    ClearEditText etEmyNum;
    @Bind(R.id.et_delivery_name)
    ClearEditText etDeliveryName;
    @Bind(R.id.txt_company)
    TextView txtCompany;
    @Bind(R.id.list_return_number)
    SwipeMenuListView listReturnNumber;
    @Bind(R.id.btn_query)
    Button btnQuery;
    @Bind(R.id.line_dotted)
    View lineDotted;
    @Bind(R.id.layout_clothes_list)
    LinearLayout layoutClothesList;

    private String companyCode;
    private String companyName;
    ClothesNoAdapter adapter;

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
                PublicUtil.openNewScanView(this, getString(R.string.scan_delivery_title), getString(R.string.input_by_hand), null, 2);
                break;
            case R.id.img_scan:
                PublicUtil.openNewScanView(this, getString(R.string.scan_clothes_num_title), getString(R.string.input_by_hand), null, 3);
                break;
        }
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {

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
            etDeliveryNum.setText(number);
        } else if (data != null && requestCode == 2) {
            //List<String> numbers = data.getStringArrayListExtra("numbers");
            final List<String> numbers = new ArrayList<>();
            numbers.add("569865895489069506509");
            numbers.add("569865895489069506509");
            numbers.add("569865895489069506509");
            if (numbers != null && !numbers.isEmpty()) {
                layoutClothesList.setVisibility(View.VISIBLE);
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
                            numbers.remove(position);
                            if (numbers.isEmpty()) {
                                layoutClothesList.setVisibility(View.GONE);
                            } else {
                                adapter.notifyDataSetChanged();
                                LibViewUtil.setListViewHeight2(listReturnNumber);
                            }
                        }
                        return false;
                    }
                });
                listReturnNumber.setPullRefreshEnable(false);
                adapter = new ClothesNoAdapter(getActivity());
                adapter.setList(numbers);
                listReturnNumber.setAdapter(adapter);
                LibViewUtil.setListViewHeight2(listReturnNumber);
            }
        }
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {

    }


}

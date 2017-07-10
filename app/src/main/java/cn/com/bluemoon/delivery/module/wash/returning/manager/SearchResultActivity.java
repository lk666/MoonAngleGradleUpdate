package cn.com.bluemoon.delivery.module.wash.returning.manager;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.util.ArrayList;

import cn.com.bluemoon.delivery.app.api.model.wash.manager.ResultBackOrder;
import cn.com.bluemoon.delivery.module.newbase.BaseFragmentActivity;

/**
 * 查询结果
 * Created by tangqiwei on 2017/7/10.
 */

public class SearchResultActivity extends BaseFragmentActivity {


    public static final String LIST_DATA = "list";
    private ArrayList<ResultBackOrder.BackOrderListBean> backOrderList;

    @Override
    protected void onBeforeSetContentLayout(Bundle savedInstanceState) {
        backOrderList= (ArrayList<ResultBackOrder.BackOrderListBean>) getIntent().getSerializableExtra(LIST_DATA);
    }

    @Override
    protected Fragment getMainFragment() {
        Fragment fragment;
        if(backOrderList==null||backOrderList.size()==0){
            fragment=new SearchResultNullDataFragment();
        }else{
            fragment=new SignFragment();
            Bundle bundle=new Bundle();
            bundle.putSerializable(LIST_DATA,backOrderList);
            fragment.setArguments(bundle);
        }
        return fragment;
    }
}

package cn.com.bluemoon.delivery.module.hr.personinfo;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;

import cn.com.bluemoon.delivery.app.api.model.card.AddressInfo;
import cn.com.bluemoon.delivery.module.newbase.BaseFragmentActivity;

/**
 * 专享洗衣卡详情
 */
public class PersonInfoActivity extends BaseFragmentActivity {



    @Override
    protected Fragment getMainFragment() {
        return AddFamilyInfoFragment.newInstance(AddFamilyInfoFragment.ADD_TYPE);
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, PersonInfoActivity.class);
        context.startActivity(intent);
    }
}
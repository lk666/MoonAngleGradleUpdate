package cn.com.bluemoon.delivery.module.hr.personinfo;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import cn.com.bluemoon.delivery.module.newbase.BaseFragmentActivity;

/**
 * 个人信息，fragment层次：<br/>
 */
public class PersonInfoActivity extends BaseFragmentActivity {

    @Override
    protected Fragment getMainFragment() {
        return PersonInfoFragment.newInstance();
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, PersonInfoActivity.class);
        context.startActivity(intent);
    }
}
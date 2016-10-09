package cn.com.bluemoon.delivery.module.wash.returning.clothescheck;

import android.view.View;
import android.widget.Button;

import butterknife.Bind;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.base.BaseFragment;

/**
 * 衣物清点
 * Created by lk on 2016/9/14.
 */
public class ClothesCheckFragment extends BaseFragment {
    @Bind(R.id.btn_back_order_check)
    Button btnBackOrderCheck;
    @Bind(R.id.btn_clothes_check)
    Button btnClothesCheck;

    @Override
    protected String getTitleString() {
        return getString(R.string.clothes_check_title);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tab_clothes_check;
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {

    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {

    }

    @OnClick({R.id.btn_back_order_check, R.id.btn_clothes_check})
    public void onClick(View view) {
        switch (view.getId()) {
            // todo 清点还衣单
            case R.id.btn_back_order_check:
                break;
            // 清点衣物
            case R.id.btn_clothes_check:
                ScanBackOrderCodeActivity.actionStart(getActivity(), this);
                break;
        }
    }
}
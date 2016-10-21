package cn.com.bluemoon.delivery.module.wash.returning.expressclosebox;

import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.ReturningApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.app.api.model.wash.expressclosebox.ResultExpressDetail;
import cn.com.bluemoon.delivery.module.wash.returning.manager.LogisticsActivity;
import cn.com.bluemoon.lib.utils.LibViewUtil;

public class ExpressDetailActivity extends BaseActivity {

    @Bind(R.id.txt_company)
    TextView txtCompany;
    @Bind(R.id.txt_delivery_num)
    TextView txtDeliveryNum;
    @Bind(R.id.txt_emy_num)
    TextView txtEmyNum;
    @Bind(R.id.txt_delivery_name)
    TextView txtDeliveryName;
    @Bind(R.id.txt_amount)
    TextView txtAmount;
    @Bind(R.id.list_return_number)
    ListView listReturnNumber;
    private String companyName;
    private String expressCode;
    private String receiverCode;
    private String receiver;
    private String companyCode;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_express_detail;
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.express_number_detail);
    }

    @Override
    public void initView() {
        showWaitDialog();
        companyName = getIntent().getStringExtra("companyName");
        expressCode = getIntent().getStringExtra("expressCode");
        receiverCode = getIntent().getStringExtra("receiverCode");
        receiver = getIntent().getStringExtra("receiver");
        companyCode = getIntent().getStringExtra("companyCode");
        ReturningApi.queryExpressDetail(expressCode, getToken(), getNewHandler(1, ResultExpressDetail.class));
    }

    @Override
    public void initData() {

    }

    @OnClick(R.id.txt_logistics)
    public void onClick(View v) {
        LogisticsActivity.actStart(this, companyCode,expressCode);
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        hideWaitDialog();
        txtCompany.setText(companyName);
        txtDeliveryNum.setText(expressCode);
        txtEmyNum.setText(receiverCode);
        txtDeliveryName.setText(receiver);

        ResultExpressDetail r = (ResultExpressDetail)result;
        List<String> list = r.getBackOrderCodeList();
        if (list != null && !list.isEmpty()) {
            txtAmount.setText(getString(R.string.total_amount2, list.size()));
            ClothesNoAdapter adapter = new ClothesNoAdapter(this);
            adapter.setList(list);
            listReturnNumber.setAdapter(adapter);
            LibViewUtil.setListViewHeight2(listReturnNumber);
        }

    }
}

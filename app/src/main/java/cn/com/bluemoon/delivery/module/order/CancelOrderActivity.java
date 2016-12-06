package cn.com.bluemoon.delivery.module.order;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.other.ReasonBean;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;

/**
 * Created by ljl on 2016/12/5.
 * 取消接单原因
 */
public class CancelOrderActivity extends BaseActivity{
    @Bind(R.id.list_reason)
    ListView listReason;
    @Bind(R.id.btn_ok)
    Button btnOk;
    private String orderId;
    private List<ReasonBean> reasonList;

    @Override
    protected String getTitleString() {
        return getString(R.string.order_cancle_reason_title);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_cancel_order_reason;
    }

    @Override
    public void initView() {
        orderId = getIntent().getStringExtra("orderId");
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isSelect = false;
                StringBuffer sbBuff = new StringBuffer();
                for (ReasonBean bean : reasonList) {
                    if (bean.isSelect()) {
                        isSelect = true;
                        sbBuff.append(bean.getReason());
                        sbBuff.append(",");
                    }
                }
                if (isSelect) {
                    showWaitDialog();
                    String buffString = sbBuff.toString();
                    String reasonKey = buffString.substring(0, buffString.length() - 1);
                    DeliveryApi.cancelAppointmentOrder(getToken(), orderId, reasonKey, getNewHandler(1, ResultBase.class));
                } else {
                    toast(getString(R.string.order_cancle_reason_select_tips));
                }

            }
        });
    }

    @Override
    public void initData() {
        String reasons = getString(R.string.order_cancle_reason_list);
        String[] reasonAttr = reasons.split(",");
        ReasonListAdapter adapter = new ReasonListAdapter(this);
        reasonList = new ArrayList<>();
        for (String s : reasonAttr) {
            ReasonBean bean = new ReasonBean();
            bean.setReason(s);
            reasonList.add(bean);
        }
        adapter.setList(reasonList);
        listReason.setAdapter(adapter);
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        hideWaitDialog();
        toast(result.getResponseMsg());
        setResult(RESULT_OK);
        finish();
    }

    class ReasonListAdapter extends BaseListAdapter<ReasonBean> {

        public ReasonListAdapter(Context context) {
            super(context, null);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.list_item_order_cancle_reason;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            TextView txtReason = getViewById(R.id.txt_reason);
            final CheckBox cbSelect = getViewById(R.id.cb_select);
            final ReasonBean bean = list.get(position);
            txtReason.setText(bean.getReason());
            cbSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bean.setSelect(cbSelect.isChecked());
                }
            });
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cbSelect.setChecked(!cbSelect.isChecked());
                    bean.setSelect(cbSelect.isChecked());
                }
            });
        }
    }
}

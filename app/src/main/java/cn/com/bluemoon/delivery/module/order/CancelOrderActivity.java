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

import butterknife.BindView;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.Dict;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.ResultDict;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;

/**
 * Created by ljl on 2016/12/5.
 * 取消接单原因
 */
public class CancelOrderActivity extends BaseActivity{
    @BindView(R.id.list_reason)
    ListView listReason;
    @BindView(R.id.btn_ok)
    Button btnOk;
    private String orderId;
    private List<String> reasonList;

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
        reasonList = new ArrayList<>();
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!reasonList.isEmpty()) {
                    showWaitDialog();
                    StringBuffer sBuff = new StringBuffer();
                    for (String s : reasonList) {
                        sBuff.append(s);
                        sBuff.append(",");
                    }
                    String reasonKey = sBuff.substring(0, sBuff.length() - 1);
                    DeliveryApi.cancelAppointmentOrder(getToken(), orderId, reasonKey, getNewHandler(2, ResultBase.class));
                } else {
                    toast(getString(R.string.order_cancle_reason_select_tips));
                }

            }
        });
    }

    @Override
    public void initData() {
        showWaitDialog();
        DeliveryApi.getDictInfo(Constants.CRM_DISPATCH_CANCEL_REASON, getNewHandler(1, ResultDict.class));
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        hideWaitDialog();
        if (requestCode == 2) {
            toast(result.getResponseMsg());
            setResult(RESULT_OK);
            finish();
        } else {
            ResultDict resultDict = (ResultDict)result;
            if (resultDict.getItemList() != null && resultDict.getItemList().size() > 0) {
                ReasonListAdapter adapter = new ReasonListAdapter(this);
                adapter.setList(resultDict.getItemList());
                listReason.setAdapter(adapter);
            }

        }

    }

    class ReasonListAdapter extends BaseListAdapter<Dict> {

        public ReasonListAdapter(Context context) {
            super(context, null);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.list_item_order_cancle_reason;
        }

        @Override
        protected void setView(final int position, View convertView, ViewGroup parent, boolean isNew) {
            TextView txtReason = getViewById(R.id.txt_reason);
            final CheckBox cbSelect = getViewById(R.id.cb_select);
            final Dict dict = list.get(position);
            txtReason.setText(dict.getDictName());
            cbSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    modifyReason(cbSelect.isChecked(), dict.getDictId());
                }
            });
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cbSelect.setChecked(!cbSelect.isChecked());
                    modifyReason(cbSelect.isChecked(),  dict.getDictId());
                }
            });
        }

        private void modifyReason(boolean isCheck, String dictId) {
            if (isCheck) {
                reasonList.add(dictId);
            } else {
                reasonList.remove(dictId);
            }
        }
    }
}

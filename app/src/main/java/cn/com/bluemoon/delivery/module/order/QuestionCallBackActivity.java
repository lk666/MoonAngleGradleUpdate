package cn.com.bluemoon.delivery.module.order;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.Dict;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.ResultDict;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.lib.qrcode.decoding.Intents;

/**
 * 配送问题反馈
 */
public class QuestionCallBackActivity extends BaseActivity{
    @Bind(R.id.list_reason)
    ListView listReason;
    @Bind(R.id.btn_ok)
    Button btnOk;
    private String orderId;
    private String dispatchId;
    private String dispatchStatus;
    private List<String> reasonList;
    private String otherReason;

    public static void  actionStart(Context context, String orderId, String dispatchId, String dispatchStatus) {
        Intent intent = new Intent(context, QuestionCallBackActivity.class);
        intent.putExtra("orderId", orderId);
        intent.putExtra("dispatchId", dispatchId);
        intent.putExtra("dispatchStatus", dispatchStatus);
        context.startActivity(intent);
    }

    @Override
    protected String getTitleString() {
        return getString(R.string.question_feedback_title);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_question_callback;
    }

    @Override
    public void initView() {
        orderId = getIntent().getStringExtra("orderId");
        dispatchId = getIntent().getStringExtra("dispatchId");
        dispatchStatus = getIntent().getStringExtra("dispatchStatus");
        reasonList = new ArrayList<>();
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!reasonList.isEmpty()) {
                    for (String s : reasonList) {
                        if ("otherReason".equals(s) && StringUtils.isEmpty(otherReason)) {
                            toast(getString(R.string.input_other_reason));
                            return;
                        }
                    }
                    showWaitDialog();
                    DeliveryApi.saveFeedBackQuestionInfo(getToken(), dispatchId, dispatchStatus,orderId, reasonList, otherReason, getNewHandler(2, ResultBase.class));
                } else {
                    toast(getString(R.string.question_less_one));
                }

            }
        });
    }

    @Override
    public void initData() {
        showWaitDialog();
        DeliveryApi.getDictInfo(Constants.CRM_DISPATCH_FEEDBACK_INFO, getNewHandler(1, ResultDict.class));
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
            final EditText etReason = getViewById(R.id.et_reason);
            final Dict dict = list.get(position);
            txtReason.setText(dict.getDictName());
            cbSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    modifyReason(cbSelect.isChecked(), dict.getDictId(), etReason);
                }
            });
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cbSelect.setChecked(!cbSelect.isChecked());
                    modifyReason(cbSelect.isChecked(),  dict.getDictId(), etReason);
                }
            });
            etReason.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    otherReason = editable.toString();
                }
            });
        }

        private void modifyReason(boolean isCheck, String dictId, EditText etReason) {
            if (isCheck) {
                reasonList.add(dictId);
            } else {
                reasonList.remove(dictId);
            }
            if("otherReason".equals(dictId)){
                if (isCheck) {
                    etReason.setVisibility(View.VISIBLE);
                } else {
                    etReason.setVisibility(View.GONE);
                    etReason.setText("");
                }
            }
        }
    }
}

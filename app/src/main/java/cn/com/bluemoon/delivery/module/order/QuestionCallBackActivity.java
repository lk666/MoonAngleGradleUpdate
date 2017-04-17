package cn.com.bluemoon.delivery.module.order;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import butterknife.Bind;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.Dict;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.ResultDict;
import cn.com.bluemoon.delivery.app.api.model.other.ResultFeedBackExpandInfo;
import cn.com.bluemoon.delivery.app.api.model.other.ResultFeedBackExpandInfo.ItemListBean;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.event.OrderChangeEvent;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.lib.qrcode.decoding.Intents;
import cn.com.bluemoon.lib.utils.LibStringUtil;

/**
 * 配送问题反馈
 */
public class QuestionCallBackActivity extends BaseActivity{
    @Bind(R.id.list_reason)
    ListView listReason;
    @Bind(R.id.btn_ok)
    Button btnOk;
    private String orderId;
    private String orderSource;
    private List<ItemListBean> reasonList;
    private String otherReason;

    public static void  actionStart(Context context, String orderId, String orderSource) {
        Intent intent = new Intent(context, QuestionCallBackActivity.class);
        intent.putExtra("orderId", orderId);
        intent.putExtra("orderSource", orderSource);
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
        orderSource = getIntent().getStringExtra("orderSource");
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (reasonList !=null && !reasonList.isEmpty()) {
                    List<String> paramDictId = new ArrayList<String>();
                    for (ItemListBean bean : reasonList) {
                        if (bean.isSelected && "otherReason".equals(bean.getDictId()) && !StringUtils.isNoneBlank(otherReason)) {
                            toast(getString(R.string.input_other_reason));
                            return;
                        }
                        if (bean.isSelected) {
                            paramDictId.add(bean.getDictId());
                        }
                    }

                    if (paramDictId.isEmpty()) {
                        toast(getString(R.string.question_less_one));
                        return;
                    }

                    showWaitDialog();
                    DeliveryApi.saveFeedBackQuestionInfo(getToken(), orderId, orderSource, paramDictId, otherReason, getNewHandler(2, ResultBase.class));
                }

            }
        });
    }

    @Override
    public void initData() {
        showWaitDialog();
        DeliveryApi.getFeedBackExpandInfo(getToken(), orderId, orderSource,
                Constants.CRM_DISPATCH_FEEDBACK_INFO, getNewHandler(1, ResultFeedBackExpandInfo.class));
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        hideWaitDialog();
        if (requestCode == 2) {
            toast(result.getResponseMsg());
            EventBus.getDefault().post(new OrderChangeEvent());
            setResult(RESULT_OK);
            finish();
        } else {
            ResultFeedBackExpandInfo r = (ResultFeedBackExpandInfo)result;
            if (r.getItemList() != null && r.getItemList().size() > 0) {
                otherReason = r.getQuestionValue();
                ReasonListAdapter adapter = new ReasonListAdapter(this);
                reasonList = r.getItemList();
                adapter.setList(reasonList);
                listReason.setAdapter(adapter);
            }

        }

    }

    class ReasonListAdapter extends BaseListAdapter<ItemListBean> {
        private Context mContext;
        public ReasonListAdapter(Context context) {
            super(context, null);
            this.mContext = context;
        }

        @Override
        protected int getLayoutId() {
            return R.layout.list_item_order_cancle_reason;
        }

        @Override
        protected void setView(final int position, View convertView, ViewGroup parent, boolean isNew) {
            final TextView txtReason = getViewById(R.id.txt_reason);
            final CheckBox cbSelect = getViewById(R.id.cb_select);
            final EditText etReason = getViewById(R.id.et_reason);
            final ItemListBean bean = list.get(position);
            txtReason.setText(bean.getDictName());
            cbSelect.setChecked(bean.isSelected);
            if (bean.isSelected) {
                txtReason.setTextColor(mContext.getResources().getColor(R.color.text_black));
            } else {
                txtReason.setTextColor(mContext.getResources().getColor(R.color.text_black_light));
            }
            etReason.setFilters(new InputFilter[]{new InputFilter() {
                public CharSequence filter(CharSequence source, int start, int end,
                                           Spanned dest, int dstart, int dend) {
                    String text = source.toString();
                    for (int i = 0; i < text.length(); i++){
                        char c = text.charAt(i);
                        String s = String.valueOf(c);
                        if (!LibStringUtil.isChinese(s) && !Character.isDigit(c)
                                && !Pattern.compile("[a-zA-Z]*|\\p{P}|\\s*|\\n").matcher(s).matches()) {
                            toast(getString(R.string.should_not_input_emoji));
                            return "";
                        }
                    }
                    return source;
                }
            }, new InputFilter.LengthFilter(50)});
            final boolean isOther = "otherReason".equals(bean.getDictId());
            if(isOther && bean.isSelected && StringUtils.isNoneBlank(otherReason)) {
                etReason.setText(otherReason);
                etReason.setVisibility(View.VISIBLE);
            }
            cbSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isCheck) {
                    if (isCheck) {
                        if (isOther) {
                            //其他选择，与上面的列表互斥
                            etReason.setVisibility(View.VISIBLE);
                            for (int i = 0; i < list.size(); i++) {
                                list.get(i).isSelected = (i == position);
                            }
                        } else {
                            for (ItemListBean otherBean : list) {
                                if (otherBean.isSelected && "otherReason".equals(otherBean.getDictId())) {
                                    otherBean.isSelected = false;
                                    etReason.setVisibility(View.GONE);
                                    etReason.setText("");
                                    hideSoftInput(etReason);
                                }
                            }
                            bean.isSelected = true;
                        }
                    } else {
                        if (isOther) {
                            etReason.setVisibility(View.GONE);
                            etReason.setText("");
                            hideSoftInput(etReason);
                        }
                        bean.isSelected = isCheck;
                    }
                    notifyDataSetChanged();
                }
            });
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cbSelect.setChecked(!cbSelect.isChecked());
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
    }

    private void hideSoftInput(View v) {
        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
}

package cn.com.bluemoon.delivery.sz.taskManager;

import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.base.interf.IActionBarListener;
import cn.com.bluemoon.delivery.sz.bean.EventMessageBean;
import cn.com.bluemoon.delivery.sz.util.LogUtil;
import cn.com.bluemoon.delivery.ui.CommonActionBar;

public class InputToolsActivity extends BaseActivity{

    public static final String MAXTEXTLENGHT="MAXTEXTLENGHT";//修改时为有值
    public int maxTextLenght=0;//最大字数上限
    public static final String INTENTITEMTAG="INTENTITEMTAG";//用于标识那一列Item 项中的输入跳转过来
    public int intentItemTag=0;
    public static final String INPUTTITELCONTENT="INPUTTITELCONTENT";//修改时为有值
    public String inputContent="";
    public static final String VIEWNAME="VIEWNAME";//指定控件接收内容
    public String viewName="";
    public static final String INPUTTITEL="INPUTTITEL";//输入内容的标题
    public  String inputTitel="";

    @Bind(R.id.ed_input)
    EditText ed_input;
    @Bind(R.id.tv_maxLenght)
    TextView tv_maxLenght;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_sz_task_input;
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {

    }

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
        intentItemTag=getIntent().getIntExtra(INTENTITEMTAG,0);
        maxTextLenght=getIntent().getIntExtra(MAXTEXTLENGHT,0);
        inputContent=getIntent().getStringExtra(INPUTTITELCONTENT);
        viewName=getIntent().getStringExtra(VIEWNAME);

        inputTitel=getIntent().getStringExtra(INPUTTITEL);

        LogUtil.i("intentNum:"+intentItemTag+"/ maxTextLenght"+maxTextLenght+" /inputContent:"+inputContent);
    }

    @Override
    protected String getTitleString() {
        return inputTitel;
    }

    @Override
    public void initView() {
        initCustomActionBar();

        if (!TextUtils.isEmpty(inputContent)){
            ed_input.setText(inputContent);
            Editable editable = ed_input.getText();
            int len = editable.length();
            tv_maxLenght.setText(len+"/"+maxTextLenght);
            if (len <= maxTextLenght) {
                Selection.setSelection(editable, editable.length());
            }
        }
        TextViewWhater(ed_input,maxTextLenght);//上限限制
    }

    @Override
    public void initData() {

    }


    public void TextViewWhater(final EditText editText, final int maxLen){
        TextWatcher tw=new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                Editable editable = editText.getText();
                LogUtil.d("onTextChanged----->"+editable.toString());
                int len = editable.length();
                tv_maxLenght.setText(len+"/"+maxLen);
                if (len > maxLen) {
//                    if (StringUtils.isNotBlank(msg)) {
//                        PublicUtil.showToast(msg);
//                    }
                    int selEndIndex = Selection.getSelectionEnd(editable);
                    String str = editable.toString();
                    String newStr = str.substring(0, maxLen);
                    editText.setText(newStr);
                    editable = editText.getText();

                    int newLen = editable.length();
                    if (selEndIndex > newLen) {
                        selEndIndex = editable.length();
                    }
                    Selection.setSelection(editable, selEndIndex);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        editText.addTextChangedListener(tw);

    }


    private void initCustomActionBar() {
            CommonActionBar titleBar = new CommonActionBar(
                    getActionBar(), new IActionBarListener() {
                @Override
                public void onBtnRight(View v) {
                    EventMessageBean messageBean=new EventMessageBean();
                    messageBean.setEventMsgAction(intentItemTag+"");
                    messageBean.setEventMsgContent(ed_input.getText().toString());
                    messageBean.setReMark(viewName);
                    EventBus.getDefault().post(messageBean);
//
                    finish();
                }
                @Override
                public void onBtnLeft(View v) {
                    finish();
                }
                @Override
                public void setTitle(TextView v) {
                    v.setText(getTitleString());

                }

            });
        TextView tv_right=titleBar.getTvRightView();
        tv_right.setVisibility(View.VISIBLE);
        tv_right.setText("提交");


    }

}

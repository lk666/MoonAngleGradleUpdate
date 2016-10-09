package cn.com.bluemoon.delivery.sz.meeting;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import org.greenrobot.eventbus.EventBus;
import org.kymjs.kjframe.KJActivity;
import org.kymjs.kjframe.ui.BindView;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.sz.bean.EventMessageBean;

/**
 * 添加会议安排
 * @author jiangyuehua
 * */
public class InputUtilActivity extends KJActivity {

	@BindView(id=R.id.et_popContent)
	private EditText et_popContent;

	@BindView(id=R.id.btn_inputCancel,click = true)
	private Button btn_inputCancel;
	@BindView(id=R.id.btn_inputCommit,click = true)
	private Button btn_inputCommit;

	private Context context;

	private String inputContent="";
	private String input_Key="";

	private InputMethodManager imm =null;

	@Override
	public void setRootView() {
		setFinishOnTouchOutside(false);//外围点击不消失
		setContentView(R.layout.pop_meeting_input);
	}

	@Override
	public void initWidget() {
		super.initWidget();

		context=InputUtilActivity.this;
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		inputContent= getIntent().getExtras().getString("inputContent");
		input_Key= getIntent().getExtras().getString("input_Key");
		if (!TextUtils.isEmpty(inputContent)){
			et_popContent.setText(inputContent);
		}

	}

	@Override
	public void widgetClick(View v) {
		EventMessageBean messageBean=new EventMessageBean();
		super.widgetClick(v);
		switch (v.getId()){
			case R.id.btn_inputCommit:
				if (input_Key.equals("input_MeetingContent")){
					messageBean.setEventMsgAction("input_MeetingContent");
					messageBean.setEventMsgContent(et_popContent.getText().toString());
				}else if(input_Key.equals("input_MeetingTheme")){
					messageBean.setEventMsgAction(input_Key);
					messageBean.setEventMsgContent(et_popContent.getText().toString());
				}
				finish();
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0); //强制隐藏键盘
				EventBus.getDefault().post(messageBean);
				break;
			case R.id.btn_inputCancel:
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0); //强制隐藏键盘
				finish();
				break;

			default:

				break;
		}

	}

}









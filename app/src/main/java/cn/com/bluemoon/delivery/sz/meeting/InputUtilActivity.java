package cn.com.bluemoon.delivery.sz.meeting;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
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

	private Context contxt;

	private String inputContent="";


	@Override
	public void setRootView() {

		setContentView(R.layout.pop_meeting_input);
		contxt=InputUtilActivity.this;
		inputContent= getIntent().getExtras().getString("inputContent");
		if (!TextUtils.isEmpty(inputContent)){
			et_popContent.setText(inputContent);
		}


	}

	@Override
	public void initWidget() {
		super.initWidget();

	}

	@Override
	public void widgetClick(View v) {
		super.widgetClick(v);
		switch (v.getId()){
			case R.id.btn_inputCommit:
				EventMessageBean messageBean=new EventMessageBean();
				messageBean.setEventMsgAction("MeetingContent");
				messageBean.setEventMsgContent(et_popContent.getText().toString());
				EventBus.getDefault().post(messageBean);
				finish();

				break;
			case R.id.btn_inputCancel:
				finish();

				break;

			default:

				break;
		}

	}

}









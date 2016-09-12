package cn.com.bluemoon.delivery.sz.taskManager;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.kymjs.kjframe.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.bluemoon.delivery.AppContext;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.DeliveryApi;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.ResultToken;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.base.interf.IActionBarListener;
import cn.com.bluemoon.delivery.sz.adapter.TaskAppraiseChooseAdapter;
import cn.com.bluemoon.delivery.sz.bean.EventMessageBean;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.lib.utils.LibViewUtil;

/**人员选择（单选）*/
public class AppraiseChooserActivity extends BaseActivity {

	Context context;
	@Bind(R.id.lv_appraise_chooser)
	ListView lv_appraise_chooser;
	@Bind(R.id.et_search_appraiser)
	EditText et_search_appraiser;

	/**名称加编号的形式*/
	public static String APPRAISE_NAME="APPRAISE_NAME";//有初始值的名称
	public String APPRAISE_NAME_CONTENT="";
	public static String APPRAISE_NAME_ACTION="APPRAISE_NAME_ACTION";
	public static int APPRAISE_NAME_ACTION_CONTENT=11;

//	public static String APPRAISE_VIEW_NAME="APPRAISER";
//	public static String APPRAISE_VIEW_NAME_CONTENT="";

	private InputMethodManager imm = null;


	List<Object> appreaiseList=new ArrayList<>();
	TaskAppraiseChooseAdapter appraiseChooseAdapter=null;

	@Override
	protected int getLayoutId() {
		return R.layout.activity_sz_task_appraise_chooser;
	}


	@Override
	public void initView() {
		ButterKnife.bind(this);
		context=AppraiseChooserActivity.this;
		initCustomActionBar();

//		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//		et_search_appraiser.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
//		et_search_appraiser.requestFocus();
//		imm.showSoftInput(et_search_appraiser, 0);

		et_search_appraiser.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH ||
						(event != null && event.getKeyCode() == KeyEvent.KEYCODE_SEARCH)) {
					String keyword = et_search_appraiser.getText().toString();
					return true;
				}
				return false;
			}
		});


	}

	@Override
	public void initData() {
		APPRAISE_NAME_CONTENT=getIntent().getStringExtra(APPRAISE_NAME);
		initAdapter();
	}

	/**查询人员接口*/
	private void login(String name,String psw){
		if (StringUtils.isEmpty(name) || StringUtils.isEmpty(psw)) {
			LibViewUtil.toast(AppContext.getInstance(),
					AppContext.getInstance().getString(R.string.register_not_empty));
			return;
		}
		showWaitDialog();
		DeliveryApi.ssoLogin(name, psw, getNewHandler(0, ResultToken.class));
	}

	/**接口返回的数据*/
	@Override
	public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {

	}

	private void initAdapter() {
		appreaiseList.add( new Object());
		appreaiseList.add( new Object());
		appreaiseList.add( new Object());

		//TODO
//		对比角标 选中上一次选 中的人员


		appraiseChooseAdapter=new TaskAppraiseChooseAdapter(context,appreaiseList);
		lv_appraise_chooser.setAdapter(appraiseChooseAdapter);

		lv_appraise_chooser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				PublicUtil.showToast(lv_appraise_chooser.getCheckedItemPosition()+"");
				appraiseChooseAdapter.setSelectedIndex(position);
				appraiseChooseAdapter.notifyDataSetChanged();



				EventMessageBean messageBean=new EventMessageBean();
				messageBean.setEventMsgAction(APPRAISE_NAME_ACTION_CONTENT+"");
				messageBean.setEventMsgContent(APPRAISE_NAME_CONTENT+"1");

				EventBus.getDefault().post(messageBean);

				finish();

			}
		});
	}

	private void initCustomActionBar() {
		CommonActionBar titleBar = new CommonActionBar(
				getActionBar(), new IActionBarListener() {
			@Override
			public void onBtnRight(View v) {

			}
			@Override
			public void onBtnLeft(View v) {
				finish();
			}
			@Override
			public void setTitle(TextView v) {
				v.setText(R.string.sz_task_add_task_appraise_choose);
			}
		});
//		TextView tv_right=titleBar.getTvRightView();
//		tv_right.setVisibility(View.VISIBLE);
//		tv_right.setText("确定");


	}


}

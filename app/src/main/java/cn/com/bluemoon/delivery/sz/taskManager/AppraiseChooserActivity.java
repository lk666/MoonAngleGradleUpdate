package cn.com.bluemoon.delivery.sz.taskManager;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.EventBus;
import org.kymjs.kjframe.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.ResultToken;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.module.base.BaseActivity;
import cn.com.bluemoon.delivery.module.base.interf.IActionBarListener;
import cn.com.bluemoon.delivery.sz.adapter.TaskAppraiseChooseAdapter;
import cn.com.bluemoon.delivery.sz.api.SzApi;
import cn.com.bluemoon.delivery.sz.bean.EventMessageBean;
import cn.com.bluemoon.delivery.sz.bean.taskManager.UserInfoBean;
import cn.com.bluemoon.delivery.sz.bean.taskManager.UserInfoListBean;
import cn.com.bluemoon.delivery.sz.util.CacheServerResponse;
import cn.com.bluemoon.delivery.sz.util.LogUtil;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.PublicUtil;

/**人员选择（单选）*/
public class AppraiseChooserActivity extends BaseActivity implements View.OnClickListener{

	Context context;
	@Bind(R.id.lv_appraise_chooser)
	ListView lv_appraise_chooser;
	@Bind(R.id.et_search_appraiser)
	EditText et_search_appraiser;
	@Bind(R.id.iv_searchDelete)
	ImageView iv_searchDelete;
	@Bind(R.id.ll_localUserHint)
	LinearLayout ll_localUserHint;

	/**名称加编号的形式*/
	public static String APPRAISE_NAME="APPRAISE_NAME";//有初始值的名称
	public String APPRAISE_NAME_CONTENT="";
	public static String APPRAISE_NAME_ACTION="APPRAISE_NAME_ACTION";
	public static int APPRAISE_NAME_ACTION_CONTENT=1001;//不可小于20 任务项为20
	public static String USERBEAN="USERBEAN";
	/**用于存储在本地的实例文件*/
	public String USERINFOLISTBEAN="UserInfoListBean";
	public UserInfoBean user=null;

//	public static String APPRAISE_VIEW_NAME="APPRAISER";
//	public static String APPRAISE_VIEW_NAME_CONTENT="";

	private InputMethodManager imm = null;

	List<UserInfoBean> appreaiseList=new ArrayList<>();
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

		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		et_search_appraiser.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH ||
						(event != null && event.getKeyCode() == KeyEvent.KEYCODE_SEARCH)) {
					String keyword = et_search_appraiser.getText().toString();
					searchByKeyword(keyword);
					return true;
				}
				return false;
			}
		});
		iv_searchDelete.setOnClickListener(this);
		showDeleteInputIcon(et_search_appraiser, iv_searchDelete);

		initAdapter();

	}

	@Override
	protected void onBeforeSetContentLayout() {
		super.onBeforeSetContentLayout();
		APPRAISE_NAME_CONTENT=getIntent().getStringExtra(APPRAISE_NAME);
		user= (UserInfoBean) getIntent().getSerializableExtra(USERBEAN);
		USERINFOLISTBEAN=USERINFOLISTBEAN+ ClientStateManager.getUserName();
	}

	@Override
	public void initData() {
		showLocalUserConent();

	}
	/**先读取本地，显示常用联系人*/
	public void showLocalUserConent(){
		UserInfoListBean userInfoListBean=
				(UserInfoListBean) CacheServerResponse.readObject(context,USERINFOLISTBEAN);
		//搜索时显示常用联系人的提示
		ll_localUserHint.setVisibility(View.VISIBLE);
		if (userInfoListBean!=null){
			List<UserInfoBean> userInfoBeanList=userInfoListBean.getData();
				for (UserInfoBean bean:userInfoBeanList) {
					LogUtil.i("常用人员："+bean.toString());
				}
			if (!appreaiseList.isEmpty()){
				appreaiseList.clear();
				appraiseChooseAdapter.notifyDataSetChanged();
			}
			appreaiseList.addAll(userInfoBeanList);
			appraiseChooseAdapter.notifyDataSetChanged();
		}
	}



	/**查询人员接口*/
	private void searchByKeyword(String queryStr){
		if (!StringUtils.isEmpty(queryStr)) {
			showWaitDialog();
			SzApi.searchByKeyword(queryStr, getNewHandler(0, ResultToken.class));
		}
	}


	public void showDeleteInputIcon(EditText editText, final View view) {
		TextWatcher tw = new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				//
				if (!TextUtils.isEmpty(s)) {
					view.setVisibility(View.VISIBLE);
				} else {
					view.setVisibility(View.INVISIBLE);
//					等于空时显示常用
					showLocalUserConent();
				}
			}
			@Override
			public void beforeTextChanged(
					CharSequence s, int start, int count,int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
			}
		};
		editText.addTextChangedListener(tw);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.iv_searchDelete:
				et_search_appraiser.setText("");
				iv_searchDelete.setVisibility(View.INVISIBLE);
				showLocalUserConent();

				break;
			default:
				break;
		}
	}


	/**接口返回的数据*/
	@Override
	public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
		LogUtil.i("人员查询："+jsonString);

		UserInfoListBean userInfoListBean= JSON.parseObject(jsonString,UserInfoListBean.class);
		if (userInfoListBean!=null){
			//搜索时不显示常用联系人的提示
			ll_localUserHint.setVisibility(View.GONE);
			List<UserInfoBean> userInfoBeen=userInfoListBean.getData();
			if (!appreaiseList.isEmpty()){
				appreaiseList.clear();
			}
			appreaiseList.addAll(userInfoBeen);
			appraiseChooseAdapter.notifyDataSetChanged();
			imm.hideSoftInputFromWindow(et_search_appraiser.getWindowToken(), 0);
		}
	}

	private void initAdapter() {
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

				UserInfoBean userInfoBean= (UserInfoBean) parent.getAdapter().getItem(position);
				if (user!=null){
					if (userInfoBean.getUID().equals(user.getUID())){
						PublicUtil.showToast("评价人不可以选择自己！");
						return;
					}
				}
				EventMessageBean messageBean=new EventMessageBean();
				messageBean.setEventMsgAction(APPRAISE_NAME_ACTION_CONTENT+"");
//				反回实体
				messageBean.setEventMsgContent(JSON.toJSONString(userInfoBean));
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

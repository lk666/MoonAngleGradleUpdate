package cn.com.bluemoon.delivery.sz.meeting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.kymjs.kjframe.KJActivity;
import org.kymjs.kjframe.ui.BindView;

import java.util.ArrayList;
import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.module.base.interf.IActionBarListener;
import cn.com.bluemoon.delivery.sz.adapter.MeetingerChooseAdapter;
import cn.com.bluemoon.delivery.sz.bean.MeetingerChooseBean.UserInfoDetailsBean;
import cn.com.bluemoon.delivery.sz.util.LogUtil;
import cn.com.bluemoon.delivery.ui.CommonActionBar;

public class MeetingerChooseActivity extends KJActivity {

	@BindView(id= R.id.hs_chooseMeetinger)
	private HorizontalScrollView hs_chooseMeetinger;
	@BindView(id= R.id.ll_chooseUser)
	private LinearLayout ll_chooseUser;

	@BindView(id= R.id.lv_meetinger)
	private ListView lv_meetinger;

	private List<UserInfoDetailsBean> userInfoDetailsBeanList=new ArrayList<UserInfoDetailsBean>();
/**	装载选取后的参会人员集合*/
	private List<UserInfoDetailsBean> userCheckedBeanList=new ArrayList<UserInfoDetailsBean>();



	private MeetingerChooseAdapter meetingerChooseAdapter=null;

	private Context context;

	private int tag=0;


	@Override
	public void setRootView() {
		setContentView(R.layout.activity_meetinger_choose);

	}

	@Override
	public void initWidget() {
		super.initWidget();

		initCustomActionBar();

		for (int i=0;i<20;i++){
			UserInfoDetailsBean userInfoDetailsBean=new UserInfoDetailsBean();
				userInfoDetailsBean.setDepartment("研发"+i);
				userInfoDetailsBean.setDepartmentID("12"+i);
				userInfoDetailsBean.setEmail("ws4511@163.com"+i);
				userInfoDetailsBean.setTel("075590909090"+i);
				userInfoDetailsBean.setEmergencyTel("13688886666"+i);
				userInfoDetailsBean.setOriginPlace("深圳"+i);
				userInfoDetailsBean.setPostState(i);
				userInfoDetailsBean.setSex("男"+i);
				userInfoDetailsBean.setStaffID("8009000"+i);
				userInfoDetailsBean.setStaffName("张"+i);
				userInfoDetailsBean.setStaffID(i+"");

			userInfoDetailsBeanList.add(userInfoDetailsBean);
		}


		EventBus.getDefault().register(this);

		context=MeetingerChooseActivity.this;
		meetingerChooseAdapter=new MeetingerChooseAdapter(context,userInfoDetailsBeanList,userCheckedBeanList);
		lv_meetinger.setAdapter(meetingerChooseAdapter);

	}

	@Override
	public void widgetClick(View v) {
		super.widgetClick(v);
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void getUserInfoMeetinger(UserInfoDetailsBean userInfoDetailsBean){
		if (userInfoDetailsBean!=null){
			if (userInfoDetailsBean.isChecked()==true){//添加
				addMeetingUserView(userInfoDetailsBean);
			}else{//列表中去掉勾选 选中也要去除
				LogUtil.i("列表中去掉勾选 Tag"+tag+"/");
//				根据名称来判断 遍历所有
				for (int i=0;i<tag;i++){
					View convertView=ll_chooseUser.getChildAt(i);
					TextView tv_convertName= (TextView) convertView.findViewById(R.id.tv_userName);
					if (tv_convertName!=null){
						String convertName=tv_convertName.getText().toString();
	//				LogUtil.i("列表中去掉勾选 Tag"+tag+"/"+convertName);
						if (convertName.equals(userInfoDetailsBean.getStaffName())){
	//						ll_chooseUser.removeViewAt(i);
							checkNameRemove(convertView,convertName);

						}
					}
				}
			}
		}

	}


	/**添加参会人员视图*/
	private void addMeetingUserView(UserInfoDetailsBean userInfoDetailsBean){

		final View view = LayoutInflater.from(context).inflate(R.layout.activity_meetinger_choose_item_user,null);
		TextView tv_userName= (TextView) view.findViewById(R.id.tv_userName);

		userCheckedBeanList.add(userInfoDetailsBean);
		LogUtil.i("选中的人："+userCheckedBeanList.toString());

		tv_userName.setText(userInfoDetailsBean.getStaffName());
		ll_chooseUser.addView(view,tag);
		tag++;

		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				TextView tv_userName= (TextView)v.findViewById(R.id.tv_userName);
				String userName=tv_userName.getText().toString();

				checkNameRemove(view,userName);

//				LogUtil.d("userCheckedBeanList"+userCheckedBeanList.toString());
//				LogUtil.d("userInfoDetailsBeanList"+userInfoDetailsBeanList.toString());
			}
		});

	}

	/**根据名称遍历集合删除数据及视图*/
	private void checkNameRemove(View view,String userName){
		for (UserInfoDetailsBean userInfoDetailsBean:userInfoDetailsBeanList) {
//					根据名字编号遍历查找相应的实体
			if (userInfoDetailsBean.getStaffName().equals(userName)){
				if (userCheckedBeanList.contains(userInfoDetailsBean)){
					LogUtil.v("是否去除："+userCheckedBeanList.contains(userInfoDetailsBean));
					userCheckedBeanList.remove(userInfoDetailsBean);
				}
				userInfoDetailsBean.setChecked(false);
				meetingerChooseAdapter.notifyDataSetChanged();
				ll_chooseUser.removeView(view);
			}
		}
		tag--;
		LogUtil.v("checkNameRemove==="+userCheckedBeanList.toString());
	}


	private void initCustomActionBar() {

		CommonActionBar commonActionBar=new CommonActionBar(getActionBar(),new IActionBarListener() {

			@Override
			public void onBtnRight(View v) {
				// TODO Auto-generated method stu
			}

			@Override
			public void onBtnLeft(View v) {
				// TODO Auto-generated method stub
				finish();
			}

			@Override
			public void setTitle(TextView v) {
				// TODO Auto-generated method stub
				v.setText(R.string.sz_meeting_user_choose);
			}
		});

		TextView tv_right=commonActionBar.getTvRightView();
		tv_right.setVisibility(View.VISIBLE);
		tv_right.setText("确定");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}
}

package cn.com.bluemoon.delivery.sz.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.sz.bean.MeetingerChooseBean.UserInfoDetailsBean;
import cn.com.bluemoon.delivery.sz.util.LogUtil;

/**
 * 参会人员选择列表
 * @author jiangyuehua
 */
public class MeetingerChooseAdapter extends BaseAdapter {

	private LayoutInflater inflater;

	private List<UserInfoDetailsBean> userInfoDetailsBeanList;
	private List<UserInfoDetailsBean> userCheckedBeanList;

	private Map<Integer,Boolean > selected=null;

	public MeetingerChooseAdapter(Context context,
								  List<UserInfoDetailsBean> userInfoDetailsBeanList,
								  List<UserInfoDetailsBean> userCheckedBeanList) {
		inflater = LayoutInflater.from(context);
		selected=new HashMap<Integer,Boolean>();
		this.userInfoDetailsBeanList = userInfoDetailsBeanList;
		this.userCheckedBeanList = userCheckedBeanList;
	}

	@Override
	public int getCount() {
		return userInfoDetailsBeanList == null ? 0 : userInfoDetailsBeanList.size();
	}

	@Override
	public Object getItem(int position) {
		return userInfoDetailsBeanList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View view, ViewGroup arg2) {

		UserInfoViewHolder userInfoViewHolder=null;

		if (view == null) {
			view = inflater.inflate(R.layout.activity_sz_choose_item,null);
			userInfoViewHolder = new UserInfoViewHolder(view);
			view.setTag(userInfoViewHolder);
		} else {
			userInfoViewHolder = (UserInfoViewHolder) view.getTag();
		}

		final UserInfoDetailsBean userBean=userInfoDetailsBeanList.get(position);

		userInfoViewHolder.getCbMeetinger().setOnCheckedChangeListener(
				new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						if (isChecked){
							selected.put(position,isChecked);
							userBean.setChecked(true);
							LogUtil.v("userBean"+userBean.toString());
							LogUtil.v("getView==="+userCheckedBeanList.toString());
							if (!userCheckedBeanList.contains(userBean)){//不包含时
									EventBus.getDefault().post(userBean);
								}
						}else{
								selected.remove(position);
								userBean.setChecked(false);
	//							包含时删除 及更新界面--界面去处理
								EventBus.getDefault().post(userBean);
						}

					}
				});

//		同步数据标识是否选中（数据、本地双匹配）
		if (selected.get(position)!=null && userBean.isChecked()==true){
			userInfoViewHolder.getCbMeetinger().setChecked(true);
		}else{
			userInfoViewHolder.getCbMeetinger().setChecked(false);

		}
		LogUtil.d("selected.get("+position+")===="+selected.get(position));

		userInfoViewHolder.getTv_userName().setText(userBean.getStaffName()+userBean.getStaffID());

		return view;
	}


	class UserInfoViewHolder {
		@Bind(R.id.cb_meetinger)
		CheckBox cb_meetinger;
		@Bind( R.id.iv_userImg)
		ImageView iv_userImg;
		@Bind(R.id.tv_userName)
		TextView tv_userName;

		public UserInfoViewHolder(View view) {
			ButterKnife.bind(this, view);
		}

		public CheckBox getCbMeetinger() {
			return cb_meetinger;
		}
		public ImageView getIvUserImg() {
			return iv_userImg;
		}
		public TextView getTv_userName() {
			return tv_userName;
		}
	}

}

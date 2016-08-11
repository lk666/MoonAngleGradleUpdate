package cn.com.bluemoon.delivery.sz.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;



import java.util.List;

import cn.com.bluemoon.delivery.R;

import cn.com.bluemoon.delivery.sz.bean.SchedualBean;
import cn.com.bluemoon.delivery.sz.util.DateUtil;


public class ScheduleAdapter extends BaseAdapter {

	private List<SchedualBean> list;
	private Context context;


	public ScheduleAdapter(Context context , List<SchedualBean> list) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.list = list;
	}

	public void refresh( List<SchedualBean> list){
		this.list = list;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewSchedualBeanHolder holder;
		if(convertView==null) {
			holder = new ViewSchedualBeanHolder();
			LayoutInflater inflater = LayoutInflater.from(context);
			convertView = inflater.inflate(R.layout.item_task, null);
			
			holder.imgStatus = (ImageView) convertView.findViewById(R.id.status_iv);
			holder.txtContent = (TextView) convertView.findViewById(R.id.content_tv);
			holder.txtDate = (TextView) convertView.findViewById(R.id.datetime_tv);
			holder.txtStatus = (TextView) convertView.findViewById(R.id.status_tv);
			convertView.setTag(holder);
			
		}
		else {
			holder = (ViewSchedualBeanHolder) convertView.getTag();
		}
		SchedualBean schedualBean = list.get(position);
		if(schedualBean.getScheduleType() == 0){
			holder.imgStatus.setVisibility(View.VISIBLE);
			holder.imgStatus.setBackgroundResource(R.drawable.bg_circle_blue_shape);

			holder.txtStatus.setText("可调整");
			holder.txtDate.setText(DateUtil.showPeriodTime(schedualBean.getbTime(),schedualBean.geteTime()));
			holder.txtContent.setText(schedualBean.getScheduleTitle());
		}else if(schedualBean.getScheduleType() == 1){
			holder.imgStatus.setVisibility(View.VISIBLE);
			holder.imgStatus.setBackgroundResource(R.drawable.bg_circle_red_shape);

			holder.txtStatus.setText("不可调整");
			holder.txtDate.setText(DateUtil.showPeriodTime(schedualBean.getbTime(),schedualBean.geteTime()));
			holder.txtContent.setText(schedualBean.getScheduleTitle());
		}else{
			holder.imgStatus.setVisibility(View.INVISIBLE);
			holder.txtStatus.setText("无日程安排");
			holder.txtDate.setText("");
			holder.txtContent.setText("");
		}


		return convertView;
	}
	
	class ViewSchedualBeanHolder {
		View imgStatus;
		TextView txtStatus;
		TextView txtDate;
		TextView txtContent;
	}

}

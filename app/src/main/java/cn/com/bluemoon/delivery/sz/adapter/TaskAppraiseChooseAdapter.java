package cn.com.bluemoon.delivery.sz.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.sz.view.RoundImageView;

/**
 * Created by jiangyuehua on 16/9/10.
 */
public class TaskAppraiseChooseAdapter extends BaseAdapter{
	Context context=null;

	List<Object> list=null;

	public TaskAppraiseChooseAdapter(Context context,List<Object> list ){
		this.context=context;
		this.list=list;

	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	int selectedIndex = -1;

	public void setSelectedIndex(int index){
		selectedIndex = index;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder=null;
		if (convertView==null){
			convertView= LayoutInflater.from(context).inflate(R.layout.activity_sz_choose_item,null);
			viewHolder=new ViewHolder(convertView);
			convertView.setTag(viewHolder);
		}else{
			viewHolder= (ViewHolder) convertView.getTag();
		}

		if (selectedIndex==position){
			viewHolder.cb_meetinger.setChecked(true);
			viewHolder.tv_userName.setTextColor(
					context.getResources().getColor(R.color.title_background));

		}else{
			viewHolder.cb_meetinger.setChecked(false);
			viewHolder.tv_userName.setTextColor(
					context.getResources().getColor(R.color.task_item_left_text_color));

		}


		return convertView;
	}


	static class ViewHolder {
		@Bind(R.id.cb_meetinger)
		CheckBox cb_meetinger;
		@Bind(R.id.iv_userImg)
		RoundImageView iv_userImg;
		@Bind(R.id.tv_userName)
		TextView tv_userName;

		ViewHolder(View view) {
			ButterKnife.bind(this, view);
		}
	}
}

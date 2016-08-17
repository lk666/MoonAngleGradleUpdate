package cn.com.bluemoon.delivery.sz.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.sz.bean.MsgListItemBean;
import cn.com.bluemoon.delivery.sz.util.Constants;
import cn.com.bluemoon.delivery.sz.util.TimeUtil;
import cn.com.bluemoon.delivery.sz.util.ViewUtil;
import cn.com.bluemoon.delivery.sz.view.MySettingItemView;
import cn.com.bluemoon.delivery.utils.StringUtil;


public class MessageListAdapter extends BaseAdapter {

	private List<MsgListItemBean> list;
	private Context context;



	public MessageListAdapter(Context context , List<MsgListItemBean> list) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.list = list;
	}

	public void refresh( List<MsgListItemBean> list){
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
		if(list!= null && !list.isEmpty()){
			return list.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewMsgListItemBeanHolder holder;
		if(convertView==null) {
			holder = new ViewMsgListItemBeanHolder();
			LayoutInflater inflater = LayoutInflater.from(context);
			convertView = inflater.inflate(R.layout.item_meeting_msg_list, null);

			holder.datetimeTv = (TextView) convertView.findViewById(R.id.time_tv);
			holder.titleTv = (TextView) convertView.findViewById(R.id.content_tv);
			holder.line1Miv = (MySettingItemView) convertView.findViewById(R.id.line1_miv);
			holder.line2Miv = (MySettingItemView) convertView.findViewById(R.id.line1_miv);
			holder.line3Miv = (MySettingItemView) convertView.findViewById(R.id.line1_miv);

			convertView.setTag(holder);
			
		}
		else {
			holder = (ViewMsgListItemBeanHolder) convertView.getTag();
		}
		MsgListItemBean msgListItemBean = list.get(position);

		long timestamp = TimeUtil.timeStrToTimeStamp(msgListItemBean.getSendTime());
		String timeStr = TimeUtil.getSendMsgTime(timestamp);
		holder.datetimeTv.setText(timeStr);
		if(StringUtil.isEmptyString(msgListItemBean.getMsgTitle())){
			holder.titleTv.setText("");
		}else{
			holder.titleTv.setText(msgListItemBean.getMsgTitle());
		}

		return convertView;
	}
	
	class ViewMsgListItemBeanHolder {
		TextView datetimeTv;
		TextView titleTv;
		MySettingItemView line1Miv;
		MySettingItemView line2Miv;
		MySettingItemView line3Miv;
	}

}

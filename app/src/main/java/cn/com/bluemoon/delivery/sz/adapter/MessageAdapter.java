package cn.com.bluemoon.delivery.sz.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.sz.bean.MainMsgCountBean;
import cn.com.bluemoon.delivery.sz.util.Constants;
import cn.com.bluemoon.delivery.sz.util.TimeUtil;
import cn.com.bluemoon.delivery.sz.util.ViewUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;


public class MessageAdapter extends BaseAdapter {

	private List<MainMsgCountBean> list;
	private Context context;
	private HashMap<String,String> typeHashmap;
	private HashMap<String,Integer> imgHashmap;


	public MessageAdapter(Context context , List<MainMsgCountBean> list) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.list = list;
		typeHashmap = new HashMap<String,String>();
		typeHashmap.put(Constants.MAIN_MSG_WAIT_REMIND,context.getString(R.string.sz_main_wait_msg_remind));
		typeHashmap.put(Constants.MAIN_MSG_MEETING_REMIND,context.getString(R.string.sz_main_meeting_msg_remind));
		typeHashmap.put(Constants.MAIN_MSG_ADVICE_REMIND,context.getString(R.string.sz_main_advice_msg_remind));
		typeHashmap.put(Constants.MAIN_MSG_CONFLICT_REMIND,context.getString(R.string.sz_main_conflict_msg_remind));
		typeHashmap.put(Constants.MAIN_MSG_DELEGATION_REMIND,context.getString(R.string.sz_main_delegation_msg_remind));

		imgHashmap = new HashMap<String,Integer>();
		imgHashmap.put(Constants.MAIN_MSG_WAIT_REMIND,R.mipmap.msg_wait);
		imgHashmap.put(Constants.MAIN_MSG_MEETING_REMIND,R.mipmap.msg_meeting);
		imgHashmap.put(Constants.MAIN_MSG_ADVICE_REMIND,R.mipmap.msg_advice);
		imgHashmap.put(Constants.MAIN_MSG_CONFLICT_REMIND,R.mipmap.msg_conflict);
		imgHashmap.put(Constants.MAIN_MSG_DELEGATION_REMIND,R.mipmap.msg_delegation);
	}

	public void refresh( List<MainMsgCountBean> list){
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
		ViewMainMsgCountBeanHolder holder;
		if(convertView==null) {
			holder = new ViewMainMsgCountBeanHolder();
			LayoutInflater inflater = LayoutInflater.from(context);
			convertView = inflater.inflate(R.layout.item_message, null);
			
			holder.typeIv = (ImageView) convertView.findViewById(R.id.type_iv);
			holder.typeTv = (TextView) convertView.findViewById(R.id.type_tv);
			holder.numTv = (TextView) convertView.findViewById(R.id.num_tv);
			holder.contentTv = (TextView) convertView.findViewById(R.id.content_tv);
			holder.timeTv = (TextView)convertView.findViewById(R.id.datetime_tv);

			convertView.setTag(holder);
			
		}
		else {
			holder = (ViewMainMsgCountBeanHolder) convertView.getTag();
		}
		MainMsgCountBean mainMsgCountBean = list.get(position);

		ViewUtil.setTipsNum(holder.numTv,mainMsgCountBean.getMsgCounts());

		holder.contentTv.setText(mainMsgCountBean.getMsgInfo());
		String msgTime = mainMsgCountBean.getMsgTime();
		if(StringUtil.isEmptyString(msgTime)){
			holder.timeTv.setText("");
		}else{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				Date time = sdf.parse(msgTime);
				String timeStr = TimeUtil.getChatTime(time.getTime());
				holder.timeTv.setText(timeStr);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		String typeStr = typeHashmap.get(mainMsgCountBean.getMsgType());
		Integer resid = imgHashmap.get(mainMsgCountBean.getMsgType());
		if(!StringUtil.isEmptyString(typeStr)){
			holder.typeTv.setText(typeStr);
		}else{
			holder.typeTv.setText("未读信息");
		}

		if(resid != null){
			holder.typeIv.setImageResource(resid);
		}else{
			holder.typeIv.setImageResource(R.mipmap.msg_advice);
		}
		return convertView;
	}
	
	class ViewMainMsgCountBeanHolder {
		ImageView typeIv;
		TextView typeTv;
		TextView numTv;
		TextView contentTv;
		TextView timeTv;
	}

}

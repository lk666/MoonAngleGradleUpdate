package cn.com.bluemoon.delivery.sz.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.sz.bean.MsgListItemBean;
import cn.com.bluemoon.delivery.sz.meeting.SzMsgAdviceReplyActivity;
import cn.com.bluemoon.delivery.sz.meeting.SzMsgConflictActivity;
import cn.com.bluemoon.delivery.sz.meeting.SzMsgWaitActivity;
import cn.com.bluemoon.delivery.sz.util.Constants;
import cn.com.bluemoon.delivery.sz.util.TimeUtil;
import cn.com.bluemoon.delivery.sz.util.ViewUtil;
import cn.com.bluemoon.delivery.sz.view.LongClickDialog;
import cn.com.bluemoon.delivery.sz.view.MySettingItemView;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;


public class MessageListAdapter extends BaseAdapter {

	private List<MsgListItemBean> list;
	private Context context;
	private int msgType;



	public MessageListAdapter(Context context , List<MsgListItemBean> list,int msgType) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.list = list;
		this.msgType = msgType;
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

			holder.contentLlt = (LinearLayout) convertView.findViewById(R.id.content_llt);
			holder.datetimeTv = (TextView) convertView.findViewById(R.id.time_tv);
			holder.titleTv = (TextView) convertView.findViewById(R.id.content_tv);
			holder.line1Miv = (TextView) convertView.findViewById(R.id.line1_miv);
			holder.line2Miv = (TextView) convertView.findViewById(R.id.line1_miv);
			holder.line3Miv = (TextView) convertView.findViewById(R.id.line1_miv);

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

		holder.contentLlt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				clickDeal(position);
			}
		});

		holder.contentLlt.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View view) {
				longClickDeal(position);
				return true;
			}
		});

		return convertView;
	}

	private void longClickDeal(int position) {
		//PublicUtil.showToast("llongClickDealo"+position);
		MsgListItemBean msgListItemBean = list.get(position);
		LongClickDialog dialog = new LongClickDialog(context);
		dialog.show();
	}

	private void clickDeal(int position) {
		PublicUtil.showToast("clickDeal"+position);
		MsgListItemBean msgListItemBean = list.get(position);
		Intent intent;
		switch (msgType){
			case Constants.MAIN_MSG_WAIT_REMIND:
				intent = new Intent(context,SzMsgWaitActivity.class);
				context.startActivity(intent);
				break;
			case Constants.MAIN_MSG_MEETING_REMIND:
				break;
			case Constants.MAIN_MSG_ADVICE_REMIND:
				intent = new Intent(context,SzMsgAdviceReplyActivity.class);
				context.startActivity(intent);
				break;
			case Constants.MAIN_MSG_CONFLICT_REMIND:
				intent = new Intent(context,SzMsgConflictActivity.class);
				context.startActivity(intent);
				break;
			case Constants.MAIN_MSG_DELEGATION_REMIND:
				break;
			default:
				PublicUtil.showToast("无该信息类型");
				break;
		}
	}

	class ViewMsgListItemBeanHolder {
		LinearLayout contentLlt;
		TextView datetimeTv;
		TextView titleTv;
		TextView line1Miv;
		TextView line2Miv;
		TextView line3Miv;
	}

}

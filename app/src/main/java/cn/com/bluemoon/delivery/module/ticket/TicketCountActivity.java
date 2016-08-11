package cn.com.bluemoon.delivery.module.ticket;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import org.kymjs.kjframe.KJActivity;
import org.kymjs.kjframe.ui.BindView;
import org.kymjs.kjframe.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.ResultVenueInfo;
import cn.com.bluemoon.delivery.app.api.model.VenueInfo;
import cn.com.bluemoon.delivery.module.base.interf.IActionBarListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.ViewUtil;
import cn.com.bluemoon.delivery.utils.manager.ActivityManager;
import cn.com.bluemoon.lib.view.CommonProgressDialog;

public class TicketCountActivity extends KJActivity {

	private String TAG = "TicketCodeActivity";
	private CommonProgressDialog progressDialog;
	@BindView(id = R.id.listView_ticket)
	private ListView listView;
	private TicketCountAdapter adapter;
	private String type;
	private ResultVenueInfo items;
	private String title;

	@Override
	public void setRootView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_ticket_count);
	}

	@Override
	public void initWidget() {
		// TODO Auto-generated method stub
		super.initWidget();
		if(getIntent()!=null){
			type = getIntent().getStringExtra("type");
			title = getIntent().getStringExtra("title");
			items = (ResultVenueInfo) getIntent().getSerializableExtra("items");
		}
		initCustomActionBar();
		ActivityManager.getInstance().pushOneActivity(aty);
		progressDialog = new CommonProgressDialog(this);
		setData();
	}
	
	@Override
	public void widgetClick(View v) {
		// TODO Auto-generated method stub
		super.widgetClick(v);
	}

	private void setData() {
		if(type==null||items==null){
			return;
		}
		List<VenueInfo> list = items.getItemList();
		if(list==null){
			list = new ArrayList<>();
			ViewUtil.toastErrorData();
		}
		adapter = new TicketCountAdapter(aty,items.getItemList());
		listView.setAdapter(adapter);
	}
	
	private void initCustomActionBar() {
		new CommonActionBar(getActionBar(), new IActionBarListener() {
			
			@Override
			public void setTitle(TextView v) {
				// TODO Auto-generated method stub
				if(Constants.TYPE_VENUE.equals(type)){
					v.setText(R.string.ticket_venue_title);
				}else if(Constants.TYPE_TIMES.equals(type)){
					if(StringUtils.isEmpty(title)){
						v.setText(R.string.ticket_times_title);
					}else{
						v.setText(title);
					}
				}else{
					v.setText(R.string.ticket_check_title);
				}
				
			}
			
			@Override
			public void onBtnRight(View v) {
				// TODO Auto-generated method stub

			}
			
			@Override
			public void onBtnLeft(View v) {
				// TODO Auto-generated method stub
				setResult(RESULT_CANCELED);
				finish();
			}
		});
	}

	class TicketCountAdapter extends BaseAdapter {

		private Context context;
		private List<VenueInfo> list;

		public TicketCountAdapter(Context context,List<VenueInfo> list) {
			this.context = context;
			this.list = list;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				LayoutInflater inflate = LayoutInflater.from(context);
				convertView = inflate.inflate(R.layout.ticket_count_item, null);
			}

			TextView txtTime = (TextView) convertView
					.findViewById(R.id.txt_time);
			if(Constants.TYPE_VENUE.equals(type)){
				txtTime.setText(list.get(position).getVenueSname());
			}else if(Constants.TYPE_TIMES.equals(type)){
				txtTime.setText(list.get(position).getTimesName());
			}

			int index = position % 2;
			if (index == 1) {
				convertView.setBackgroundResource(R.drawable.list_item_grep_bg);
			} else {
				convertView.setBackgroundResource(R.drawable.list_item_white_bg);
			}

			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					bundle.putSerializable("item",list.get(position));
					intent.putExtras(bundle);
					setResult(RESULT_OK, intent);
					finish();
				}
			});

			return convertView;
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);

	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
		if (progressDialog != null)
			progressDialog.dismiss();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			setResult(RESULT_CANCELED);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}

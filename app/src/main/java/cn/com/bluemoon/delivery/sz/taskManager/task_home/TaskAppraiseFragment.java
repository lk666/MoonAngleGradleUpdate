package cn.com.bluemoon.delivery.sz.taskManager.task_home;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import cn.com.bluemoon.delivery.R;

/**
 * Created by jiangyuehua on 16/9/8.
 */
public class TaskAppraiseFragment extends Fragment implements View.OnClickListener {

	private Context context=null;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View convertView=inflater.inflate(R.layout.activity_sz_task_record_fragment,null);
		return convertView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		ButterKnife.bind(this,view);
		context=getActivity();
		initView();
	}

	private void initView() {
		// TODO Auto-generated method stub
//		ActivityManager.getInstance().pushOneActivity(this);//通知
	}


	@Override
	public void onClick(View v) {

	}
}

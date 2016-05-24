package cn.com.bluemoon.delivery.async.listener;

import android.view.View;
import android.widget.TextView;

public interface IActionBarListener {

	public void setTitle(TextView v);
	public void onBtnLeft(View v);
	public void onBtnRight(View v);
}

package cn.com.bluemoon.delivery.module.base.interf;

import android.view.View;
import android.widget.TextView;

public interface IActionBarListener {

	 void setTitle(TextView v);
	 void onBtnLeft(View v);
	 void onBtnRight(View v);
}
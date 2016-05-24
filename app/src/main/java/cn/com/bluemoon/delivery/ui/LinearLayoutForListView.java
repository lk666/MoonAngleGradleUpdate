package cn.com.bluemoon.delivery.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

public class LinearLayoutForListView extends LinearLayout {
	private BaseAdapter mAdapter;

	public void setAdapter(BaseAdapter adapter) {
		this.mAdapter = adapter;
		if(mAdapter == null)
			return;
		fillLinearLayout();
		
	}

	public void fillLinearLayout() {
		removeAllViews();
		int count = mAdapter.getCount();
		for (int i = 0; i < count; i++) {
			View v = mAdapter.getView(i, null, null);
		    
			
			addView(v, i);
		}
	}

	public LinearLayoutForListView(Context context) {
		super(context);
	}
	
	public LinearLayoutForListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public LinearLayoutForListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
}

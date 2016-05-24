package cn.com.bluemoon.delivery.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

/**
 * 解决GridView在SrollView里面显示不全: </br>
 * 解决嵌套在listview里，gridview还有滑动的效果，去掉滑动效果
 */
public class CustomGridView extends GridView {
	private boolean mIsScrollable = false;

	public CustomGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public CustomGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public CustomGridView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public void setScrollaable(boolean isScrollable) {
		this.mIsScrollable = isScrollable;
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		if(mIsScrollable) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		} else {
			int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
			super.onMeasure(widthMeasureSpec, expandSpec); 
		}

	}

/*	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		if (ev.getAction() == MotionEvent.ACTION_MOVE) {
			return true;
		}
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_MOVE) {
			return false;
		}
		return super.onTouchEvent(ev);
	}*/
}

package cn.com.bluemoon.delivery.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

public class DrawableRightListenerEditText extends EditText{
	private DrawableRightListener mRightListener ;
	
	final int DRAWABLE_LEFT = 0;
    final int DRAWABLE_TOP = 1;
    final int DRAWABLE_RIGHT = 2;
    final int DRAWABLE_BOTTOM = 3;
 
    
 
	public DrawableRightListenerEditText(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}
 
	public DrawableRightListenerEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
 
	public DrawableRightListenerEditText(Context context) {
		super(context);
	}
 
	public void setDrawableRightListener(DrawableRightListener listener) {
		this.mRightListener = listener;
	}
 
	
	public interface DrawableRightListener {
		public void onDrawableRightClick(View view) ;
	}
	
 
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
			
			if (mRightListener != null) {
				Drawable drawableRight = getCompoundDrawables()[DRAWABLE_RIGHT] ;
				if (drawableRight != null && event.getRawX() >= (getRight() - drawableRight.getBounds().width())) {
					mRightListener.onDrawableRightClick(this) ;
	        		return true ;
				}
			}
			break;
		}
		
		return super.onTouchEvent(event);
	}


}

package cn.com.bluemoon.delivery.sz.view.MeetingTimeView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by jiangyuehua on 16/7/26.
 */
public class AsyncMeetingTextView extends TextView {

	Paint linePaint=null;
	int width,height;
	int scale=12;//10分钟为一个单位  5分钟为一单位
	int lineWidth=1;
	public AsyncMeetingTextView(Context context) {
		this(context,null);
	}

	public AsyncMeetingTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		linePaint=new Paint();
		linePaint.setColor(Color.GRAY);
		linePaint.setStrokeWidth(lineWidth);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		float scaleWidthUnit=getWidth()/scale;
		int scaleHeight=getHeight();

//		画刻度
		for (int i=0;i<=scale;i++){
			if (i!=0){
				canvas.drawLine(scaleWidthUnit*i,scaleHeight-scaleWidthUnit/2,
						scaleWidthUnit*i,scaleHeight,linePaint);
			}else if(i==scale){
				canvas.drawLine(scaleWidthUnit*i,scaleHeight-scaleWidthUnit/2,
						scaleWidthUnit*i,scaleHeight,linePaint);
			}
		}
		/**四周描边*/
//		canvas.drawLine(0,0,getWidth(),0,linePaint);
//		canvas.drawLine(0,0,0,getHeight(),linePaint);
//		canvas.drawLine(getWidth(),0,getWidth(),getHeight(),linePaint);
//		canvas.drawLine(0,getHeight(),getWidth(),getHeight(),linePaint);
	}
}

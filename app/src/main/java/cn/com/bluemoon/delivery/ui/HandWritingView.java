package cn.com.bluemoon.delivery.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import cn.com.bluemoon.delivery.R;

public class HandWritingView extends View {
	private Path path;
	private static Bitmap tempBitmap;
	private static Bitmap originalBitmap;
	public static Bitmap new1Bitmap = null; 
	private static Bitmap new2Bitmap = null;
	private boolean isClear = false;
	private Canvas canvas = null; 
	private Paint paint = null;
	private float clickX = 0, clickY = 0; 
    private float startX = 0, startY = 0; 
    private int height;
    private int width;
    
    public boolean isSign = false;
    
    public Bitmap getBitmap() {
    	if (!isClear) {
    		return Bitmap.createBitmap(new1Bitmap, 0, 0, this.getWidth(), this.getHeight());
    	}
    	
    	return Bitmap.createBitmap(new2Bitmap, 0, 0, this.getWidth(), this.getHeight());
    }
    
    public void setWAndH(int width, int height) {
    	this.height = height;
    	this.width = width;
    }

	public HandWritingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		path = new Path();
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 1;
		//options.outWidth = width;
		//options.outHeight = height;
		tempBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.sign, options);
		
		//tempBitmap = Bitmap.createBitmap(tempBitmap, 0, 0, 320-25, 480-20);
		originalBitmap = tempBitmap.copy(Bitmap.Config.ARGB_8888, true);
		new1Bitmap = Bitmap.createBitmap(originalBitmap);
	}
	public void clear() {
		isClear = true;
		new2Bitmap = Bitmap.createBitmap(originalBitmap);
		invalidate();
	}
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		
		height = this.getHeight();
		width = this.getWidth();
		canvas.clipRect(2,2,width-2,height-2);
		canvas.drawBitmap(HandWriting(new1Bitmap), 0, 0, null);
		//canvas.drawColor(Color.argb(150, 120, 120, 120));
		canvas.drawPath(path, paint);
	}
	@SuppressLint("HandlerLeak")
	public Bitmap HandWriting(Bitmap originalBitmap) {
		if (isClear) {
			canvas = new Canvas(new2Bitmap);
		} else {
			canvas = new Canvas(originalBitmap);
		}
		paint = new Paint();
		paint.setColor(Color.argb(255, 0, 0, 0));
		paint.setStrokeWidth(5);
		paint.setMaskFilter(new EmbossMaskFilter(new float[] { 1, 1, 1 }, 0.4f, 6, 3.5f));
		paint.setStyle(Style.STROKE);
		paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setFilterBitmap(true);
        paint.setSubpixelText(true);  
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        if (isClear) { 
            return new2Bitmap; 
        } 
        return originalBitmap; 
	}
	@SuppressLint("ClickableViewAccessibility")
	@Override 
    public boolean onTouchEvent(MotionEvent event) { 
        startX = event.getX(); 
        startY = event.getY();
        switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			touchDown(event);
			return true;
		case MotionEvent.ACTION_MOVE:
			touchMove(event);
			return true;
		case MotionEvent.ACTION_UP:
			touchUp(event);
			return true;
		default:
			break;
		}
       
        return super.onTouchEvent(event); 
    } 
	private void touchDown(MotionEvent event) {
		clickX = startX;
		clickY = startY;
		path.moveTo(startX, startY);
		invalidate();
	}
	private void touchMove(MotionEvent event) {
		path.quadTo(clickX, clickY, (clickX + startX)/2, (clickY+startY)/2);
		clickX = startX;
    	clickY = startY;
        invalidate();
        isSign = true;
    }
	private void touchUp(MotionEvent event){
    	canvas.drawPath(path, paint);
		path.reset();
    }
	@Override
    protected void onDetachedFromWindow() {
    	super.onDetachedFromWindow();
    }

}

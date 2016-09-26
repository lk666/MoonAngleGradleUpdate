package cn.com.bluemoon.delivery.sz.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.com.bluemoon.delivery.R;


@SuppressLint({ "InflateParams", "NewApi" })
public class MySettingItemView extends LinearLayout {
	
	private ImageView imgLeft;
	private TextView txtHint;
	private TextView txtContent;
	private ImageView imgRight;
	private String hint;
	private String content;
	private int hintColor = 0;
	private int contentColor = 0;
	private int iconLeftRseId = 0;
	private boolean rightVisible = true;
	
	public MySettingItemView(Context context) {
		super(context);
		init(context, null);
	}
	
	public MySettingItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	public MySettingItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}
	
	private void init(Context context,AttributeSet attributeSet){
		readStyleParameters(context,attributeSet);
		setDefaulStyle();
		removeAllViews();
		View view = LayoutInflater.from(context).inflate(R.layout.item_my_setting, null);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		params.gravity = Gravity.CENTER_VERTICAL;
		imgLeft = (ImageView) view.findViewById(R.id.img_left);
		txtHint = (TextView) view.findViewById(R.id.txt_hint);
		txtContent = (TextView) view.findViewById(R.id.txt_content);
		imgRight = (ImageView) view.findViewById(R.id.img_right);
		if(iconLeftRseId!=0){
			imgLeft.setImageResource(iconLeftRseId);
			imgLeft.setVisibility(View.VISIBLE);
		}
		if(hintColor!=0)
			txtHint.setTextColor(hintColor);
		if(contentColor!=0)
			txtContent.setTextColor(contentColor);
		if(hint!=null)
			txtHint.setText(hint);
		if(content!=null)
			txtContent.setText(content);
		if(!rightVisible)
			imgRight.setVisibility(View.GONE);
		addView(view,params);
	}
	
	private void readStyleParameters(Context context,AttributeSet attributeSet) {
		TypedArray a = context.obtainStyledAttributes(attributeSet,
				R.styleable.MySetItemView);
		try {
			iconLeftRseId = a.getResourceId(R.styleable.MySetItemView_setMyLeftDrawable, 0);
			hintColor = a.getColor(R.styleable.MySetItemView_setMyHintColor, 0);
			contentColor = a.getColor(R.styleable.MySetItemView_setMyContentColor, 0);
			hint = a.getString(R.styleable.MySetItemView_setMyHintTxt);
			content = a.getString(R.styleable.MySetItemView_setMyContentTxt);
			rightVisible = a.getBoolean(R.styleable.MySetItemView_setMyRightVisible, true);
		} finally {
			a.recycle();
		}
	}
	
	private void setDefaulStyle(){
		try {
			if(getBackground()==null){
				setBackgroundResource(R.drawable.btn_white);
			}
			int paddingLeft = getPaddingLeft();
			int paddingRight = getPaddingRight();
			if(getPaddingLeft()==0){
				paddingLeft = getResources().getDimensionPixelOffset(R.dimen.space_14);
			}
			if(getPaddingRight()==0){
				paddingRight = getResources().getDimensionPixelOffset(R.dimen.space_17);
			}
			setPadding(paddingLeft, getPaddingTop(), paddingRight, getPaddingBottom());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public TextView getHintView(){
		return txtHint;
	}
	
	public TextView getContentView(){
		return txtContent;
	}
	
	public ImageView getIconView(){
		return imgRight;
	}

}

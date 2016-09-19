package cn.com.bluemoon.delivery.sz.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.sz.util.LogUtil;
import cn.com.bluemoon.delivery.sz.util.UIUtil;

/**
 * Created by jiangyuehua on 16/9/7.
 */
public class TaskTextView extends LinearLayout {

	private Context context;
	private RelativeLayout rl_taskItem;
	private TextView tv_leftName,tv_rightContent;
	private ImageView iv_arrow;

	private int rt_line_lenght=0;
	private int rt_max_lenght=0;
	private int text_size_left=0;
	private int text_size_right=0;
	private int tv_height=0;
	private int rt_gravityStyle=0;
	private int hideArrow=1;//1:为显示 0为隐藏


	private String text_right="";
	private String text_left="";
	private String text_right_hint="";
	private String text_left_hint="";


	private OnClickListener onClickListener=null;

	private int parentWidthMeasureSpec;
	private int parentHeightMeasureSpec;

	public TaskTextView(Context context) {
		this(context,null);
	}

	public TaskTextView(Context context, AttributeSet attrs) {
		this(context, attrs,0);
	}

	public TaskTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.context=context;
		LayoutInflater.from(context).inflate(R.layout.activity_sz_task_textview,this);
		TypedArray typedArray =
				context.getTheme().obtainStyledAttributes(attrs,
						R.styleable.TaskTextView, defStyleAttr, 0);

		rt_line_lenght=typedArray.getInt(R.styleable.TaskTextView_rt_line_lenght,1);
		rt_max_lenght=typedArray.getInt(R.styleable.TaskTextView_rt_max_lenght,0);
		rt_gravityStyle=typedArray.getInt(R.styleable.TaskTextView_rt_gravityStyle,0);
		hideArrow=typedArray.getInt(R.styleable.TaskTextView_hideArrow,1);

		text_right=typedArray.getString(R.styleable.TaskTextView_text_right);
		text_left=typedArray.getString(R.styleable.TaskTextView_text_left);

		text_right_hint=typedArray.getString(R.styleable.TaskTextView_text_right_hint);
		text_left_hint=typedArray.getString(R.styleable.TaskTextView_text_left_hint);

		text_size_left=typedArray.getDimensionPixelSize(R.styleable.TaskTextView_text_size_left,
				(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, getResources().getDisplayMetrics()));
		text_size_right=typedArray.getDimensionPixelSize(R.styleable.TaskTextView_text_size_right,
				(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, getResources().getDisplayMetrics()));
		tv_height=typedArray.getDimensionPixelSize(R.styleable.TaskTextView_tv_height,
				(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, getResources().getDisplayMetrics()));

		typedArray.recycle();

		initView();
	}

	private void initView() {
		rl_taskItem= (RelativeLayout) findViewById(R.id.rl_taskItem);
		if (tv_height!=0)
			setTastItem(rl_taskItem);


		tv_leftName= (TextView) findViewById(R.id.tv_leftName);
		tv_rightContent= (TextView) findViewById(R.id.tv_rightContent);
		iv_arrow= (ImageView) findViewById(R.id.iv_arrow);
		tv_leftName.setTextSize(UIUtil.px2dip(context,text_size_left));
		tv_rightContent.setTextSize(UIUtil.px2dip(context,text_size_right));
		tv_leftName.setHint(text_left_hint);
		tv_rightContent.setHint(text_right_hint);

		tv_leftName.setText(text_left);
		tv_rightContent.setText(text_right);
		tv_rightContent.setFilters(new InputFilter[] { new InputFilter.LengthFilter(rt_max_lenght)});
		tv_rightContent.setMaxLines(rt_line_lenght);
		LogUtil.v("rt_max_lenght:"+rt_max_lenght+"/ "+tv_height+"/"+UIUtil.px2dip(context,tv_height));
		if (rt_gravityStyle==0)
			tv_rightContent.setGravity(Gravity.LEFT);
		else
			tv_rightContent.setGravity(Gravity.RIGHT);

		if (hideArrow==0)
			iv_arrow.setVisibility(GONE);
		else
			iv_arrow.setVisibility(VISIBLE);


	}


	private void setTastItem(View view){
		LinearLayout.LayoutParams rl=new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,tv_height);
		view.setLayoutParams(rl);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		parentWidthMeasureSpec=widthMeasureSpec;
		parentHeightMeasureSpec=heightMeasureSpec;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
	}

	public void setTv_height(int tv_height) {
		this.tv_height = tv_height;
		setTastItem(rl_taskItem);
	}

	public void setText_size_left(int text_size_left) {
		this.text_size_left = text_size_left;
		tv_leftName.setTextSize(text_size_left);
	}

	public void setText_size_right(int text_size_right) {
		this.text_size_right = text_size_right;
		tv_rightContent.setTextSize(text_size_right);
	}

	public void setText_right(String text_right) {
		this.text_right = text_right;
		tv_rightContent.setText(text_right);
	}

	public void setText_left(String text_left) {
		this.text_left = text_left;
		tv_leftName.setText(text_left);
	}

	public TextView getTv_leftName() {
		return tv_leftName;
	}
	public TextView getTv_rightContent() {
		return tv_rightContent;
	}

	public interface OnRightTextOnClickListener{
		void OnClickListener();
	}

	public void setOnRightTextOnClickListener(OnClickListener onClickListener){
		this.onClickListener=onClickListener;
		tv_rightContent.setOnClickListener(onClickListener);
	}

	public void setText_right_hint(String text_right_hint) {
		this.text_right_hint = text_right_hint;
		tv_rightContent.setHint(text_right_hint);

	}
}

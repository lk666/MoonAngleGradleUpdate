package cn.com.bluemoon.delivery.ui.selectordialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.com.bluemoon.delivery.R;

/**
 * 数字WheelView滚轮
 * todo 简化计算
 * todo 抽象、封装
 * todo 异常处理，如空数据时
 *
 * @author luokai
 */
public class NumberWheelView extends View {
    /**
     * 滑动放开后刷新间隔,ms
     */
    private static final long FRESH_INTRV = 8;

    /**
     * 默认加速度,像素/秒
     */
    private static float DEF_A = 3000;

    /**
     * 非循环时，阻尼（最大拉动的）高度比item高度的倍数
     */
    private final static float MAX_DRAG_LENGTH_RATE = 0.25f;

    private static final int MOVE = 0x004;
    private static final int ACTION_UP = 0x005;
    /**
     * 滑动后自动滚动到选中位置
     */
    private static final int BALANCE = 0x006;
    /**
     * 控件宽度
     */
    private float controlWidth;
    /**
     * 是否滑动中
     */
    private volatile boolean isScrolling = false;
    /**
     * 默认选中的项，可以只使用默认选择项的位置绘制所有
     */
    private ItemObject defSelectedItem;
    /**
     * 用于记录滑动距离的临时坐标变量
     */
    private int countY;
    /**
     * 用于记录actionDown时间戳的临时坐标变量，用于计算速度
     */
    private long downTime;
    /**
     * 默认字体
     */
    private float normalFont = 14.0f;
    /**
     * 选中的时候字体
     */
    private float selectedFont = 22.0f;
    /**
     * 单元格高度
     */
    private int unitHeight = 50;
    /**
     * 显示多少个内容
     */
    private int itemNumber = 7;
    /**
     * 默认字体颜色
     */
    private int normalColor = 0xff999999;
    /**
     * 选中时候的字体颜色
     */
    private int selectedColor = 0xff333333;
    /**
     * 选择监听
     */
    private OnSelectChangedListener onSelectListener;
    /**
     * 是否可用
     */
    private boolean isEnable = true;

    /**
     * 非循环时，阻尼（最大拉动的）高度
     */
    private int maxDragLength;

    /**
     * 选中的index
     */
    private int selectedIndex = -1;

    /**
     * 是否循环数据
     */
    private boolean isLoop = false;

    /**
     * 正在修改数据，避免ConcurrentModificationException异常
     */
    private boolean isClearing = false;
    private int controlHeight;
    private TextPaint textPaint;
    private Rect textRect;
    /**
     * 记录上次手指滑动位置
     */
    private int scrollY;
    private int lastScrollY;

    /**
     * 开始数字
     */
    private int startNum;

    /**
     * 结束数字
     */
    private int endNum;

    private ExecutorService exe = Executors.newSingleThreadExecutor();
    private volatile boolean isRunning = false;

    public NumberWheelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttr(context, attrs);
        init();
    }

    public NumberWheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttr(context, attrs);
        init();
    }

    public NumberWheelView(Context context) {
        super(context);
        init();
    }

    /**
     * 初始化，获取设置的属性
     */
    protected void initAttr(Context context, AttributeSet attrs) {
        TypedArray attribute = context.obtainStyledAttributes(attrs, R.styleable.SimpleWheelView);
        unitHeight = (int) attribute.getDimension(R.styleable.SimpleWheelView_unitHeight, unitHeight);
        itemNumber = attribute.getInt(R.styleable.SimpleWheelView_itemNumber, itemNumber);

        normalFont = attribute.getDimension(R.styleable.SimpleWheelView_normalTextSize, normalFont);
        selectedFont = attribute.getDimension(R.styleable.SimpleWheelView_selectedTextSize, selectedFont);
        normalColor = attribute.getColor(R.styleable.SimpleWheelView_normalTextColor, normalColor);
        selectedColor = attribute.getColor(R.styleable.SimpleWheelView_selectedTextColor, selectedColor);

        isLoop = attribute.getBoolean(R.styleable.SimpleWheelView_isLoop, false);
        isEnable = attribute.getBoolean(R.styleable.SimpleWheelView_isEnable, true);

        attribute.recycle();
        if (itemNumber < 1) {
            itemNumber = 1;
        }
        if (itemNumber % 2 == 0) {
            itemNumber++;
        }

        controlHeight = itemNumber * unitHeight;
        maxDragLength = (int) (controlHeight / 2 + MAX_DRAG_LENGTH_RATE * unitHeight);
        defSelectedItem = null;

        textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textRect = new Rect();
    }

    /**
     * 初始化数据
     */
    private void init() {
        isClearing = true;
        int y = (controlHeight - unitHeight) / 2;
        defSelectedItem = new ItemObject(selectedIndex, y);
        isClearing = false;
        DisplayMetrics dm = getResources().getDisplayMetrics();
        DEF_A = dm.heightPixels;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        controlWidth = getWidth();
        if (controlWidth != 0) {
            setMeasuredDimension(getWidth(), itemNumber * unitHeight);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (defSelectedItem == null || isClearing) {
            return;
        }
        // y = 0 位置处的index
        int topIndex = (int) (Math.floor(defSelectedItem.index - (double) defSelectedItem.cury /
                unitHeight));
        // y = controlHeight位置处的index
        int bottomIndex = (int) (Math.ceil((double) (controlHeight - defSelectedItem.cury) /
                unitHeight + defSelectedItem.index)) - 1;

        // 循环时
        if (isLoop) {
            for (int index = topIndex; index <= bottomIndex; index++) {
                drawText(canvas, getMeasuredWidth(), index);
            }
        } else {
            topIndex = topIndex < 0 ? 0 : topIndex;
            int size = getSize();
            bottomIndex = bottomIndex < size ? bottomIndex : size - 1;

            for (int index = topIndex; index <= bottomIndex; index++) {
                drawText(canvas, getMeasuredWidth(), index);
            }
        }
    }

    /**
     * 绘制单个item
     *
     * @param canvas         画板
     * @param containerWidth 容器宽度
     * @param index          位置
     */
    private void drawText(Canvas canvas, int containerWidth, int index) {

        int y = getYPosition(index);

        boolean isSelected = false;

        // 判断是否在选择区域
        if (isInSelectedZoom(index)) {
            // 获取距离标准位置的距离
            float moveToSelect = Math.abs(getMoveToSelectedDistance(index));
            // 完成度
            float rate = 1.0f - moveToSelect / (float) unitHeight;

            //  颜色渐变
            int R = (int) (Color.red(normalColor) + (Color.red(selectedColor) - Color.red
                    (normalColor)) * rate);
            int G = (int) (Color.green(normalColor) + (Color.green(selectedColor) - Color.green
                    (normalColor)) * rate);
            int B = (int) (Color.blue(normalColor) + (Color.blue(selectedColor) - Color.blue
                    (normalColor)) * rate);

            int color = Color.rgb(R, G, B);
            textPaint.setColor(color);

            // 计算当前字体大小
            float textSize = normalFont + ((selectedFont - normalFont) * rate);
            textPaint.setTextSize(textSize);

            // 是否已处于“已选中”区，滑动过程中的监听
            if (moveToSelect < unitHeight / 2.0) {
                onSelectListener(index);
                isSelected = true;
            }

        } else {
            textPaint.setColor(normalColor);
            textPaint.setTextSize(normalFont);
        }

        // 返回包围整个字符串的最小的一个Rect区域
        String text = getItemText(index, isSelected);
        String itemText = (String) TextUtils.ellipsize(text, textPaint,
                containerWidth, TextUtils.TruncateAt.END);
        textPaint.getTextBounds(itemText, 0, itemText.length(), textRect);

        // 绘制内容
        canvas.drawText(itemText, controlWidth / 2 - textRect.width() / 2,
                y + (unitHeight + textRect.height()) / 2, textPaint);
    }

    /**
     * 滑动结束后，选项选中判断，若选中项变更，发送回调
     */
    private void onEndSelectListener(final int index) {
        if (onSelectListener != null) {
            post(new Runnable() {
                @Override
                public void run() {
                    if (!isLoop) {
                        onSelectListener.onEndSelected(index, getSelectedNumber());
                    } else {
                        int oriIndex = getOriIndex(index);
                        onSelectListener.onEndSelected(oriIndex, getSelectedNumber());
                    }
                }
            });
        }
    }

    /**
     * 滑动中，选项选中判断
     */
    private void onSelectListener(int index) {
        if (selectedIndex != index) {
            selectedIndex = index;
        }
    }

    /**
     * 获取移动到选中位置需要的距离
     */
    private float getMoveToSelectedDistance(int index) {
        return (float) (controlHeight - unitHeight) / 2 - getYPosition(index);
    }

    /**
     * 获取上y坐标
     */
    private int getYPosition(int index) {
        return defSelectedItem.cury + (index - defSelectedItem.index) * unitHeight;
    }

    /**
     * 判断是否在选择区域内（有一条边在选中区域），以开始改变颜色与字体大小
     */
    private boolean isInSelectedZoom(int index) {
        float txtRectTop = getYPosition(index);
        float txtRectBottom = txtRectTop + unitHeight;
        float topDiv = controlHeight / 2 - unitHeight / 2;
        float bottomDiv = controlHeight / 2 + unitHeight / 2;

        if (txtRectTop >= topDiv && txtRectTop <= bottomDiv) {
            return true;
        }
        if (txtRectBottom >= topDiv && txtRectBottom <= bottomDiv) {
            return true;
        }
        if (txtRectTop <= topDiv && txtRectBottom >= bottomDiv) {
            return true;
        }
        return false;
    }

    /**
     * 默认选项数据结构，通过改变其moveLength属性，可以在ondraw中实时计算显示的文本
     */
    private class ItemObject {
        /**
         * 在列表中的index，循环滚动时可以<0
         */
        private int index = 0;

        /**
         * 当前的y坐标
         */
        private int cury = 0;

        /**
         * 循环滚动时使用，总高度
         */
        private int totalHeight;


        /**
         * 设置当前位置
         *
         * @param moveLength 移动的距离
         */
        public void move(int moveLength) {
            this.cury += moveLength;
            if (isLoop) {
                if (this.cury > 0) {
                    this.cury = this.cury % totalHeight;
                } else {
                    this.cury = totalHeight + this.cury % totalHeight;
                }
            }
        }

        public ItemObject(int index, int y) {
            this.index = index;
            this.cury = y;
            totalHeight = unitHeight * getSize();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnable) {
            return true;
        }

        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isRunning = false;
                handler.removeMessages(ACTION_UP);
                handler.removeMessages(BALANCE);
                handler.removeMessages(MOVE);


                isScrolling = true;
                downTime = System.currentTimeMillis();
                countY = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                int move = y - scrollY;
                countY += Math.abs(move);
                move(move);
                break;
            case MotionEvent.ACTION_UP:
                // 继续滚动，再移动到选项
                int dir = y - lastScrollY > 0 ? 1 : -1;
                long detalTime = System.currentTimeMillis() - downTime;
                if (detalTime < 1) {
                    detalTime = 1;
                }
                int speed = (int) (countY * 500.0f / (float) detalTime) * dir;
                handler.sendMessage(handler.obtainMessage(ACTION_UP, speed, 0));
                break;
            default:
                break;
        }
        lastScrollY = scrollY;
        scrollY = y;
        return true;
    }

    /**
     * 移动
     *
     * @param move 移动的位移
     */
    private void actionMove(int move) {
        if (move == 0) {
            return;
        }

        if (!isLoop) {
            // 判断是否会到达滚动的“极限值”
            // 向下滚动
            if (move > 0) {
                int y0 = getYPosition(0);
                if (y0 + move > maxDragLength) {
                    move = maxDragLength - y0;
                }
            } else {
                int yend = getYPosition(getSize() - 1);
                int top = controlHeight - maxDragLength - unitHeight;
                if (yend + move < top) {
                    move = top - yend;
                }
            }
        }

        defSelectedItem.move(move);
        invalidate();
    }

    /**
     * 手指放开后的动作
     *
     * @param speed 像素/秒
     */
    private synchronized void actionUpMove(final float speed) {
        exe.execute((new Runnable() {

            @Override
            public void run() {
                if (isRunning) {
                    return;
                }
                isRunning = true;

                // 是否向下运动
                final boolean isDown = speed > 0;

                // 默认加速度
                float a = DEF_A;

                float vo2 = speed * speed;

                // 预计会滚动的距离
                int length = (int) (vo2 * 0.5 / a);

                if (!isLoop) {
                    // 惯性下滑时，不能超过阻尼高度
                    if (isDown) {
                        int y0 = getYPosition(0);
                        int max = maxDragLength - y0;
                        if (length > max) {
                            a = vo2 * 0.5f / max;
                        }
                    }

                    // 惯性上滑时，不能超过阻尼高度
                    else {
                        int yend = getYPosition(getSize() - 1);
                        int max = yend - controlHeight + maxDragLength + unitHeight;
                        if (length > max) {
                            a = vo2 * 0.5f / max;
                        }
                    }
                }

                final float acceleration = a;

                int move;
                float curSpeed = Math.abs(speed);
                do {
                    try {
                        Thread.sleep(FRESH_INTRV);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    float speed = curSpeed - acceleration * FRESH_INTRV * 0.001f;

                    if (speed <= 0) {
                        speed = 0;
                    }

                    move = (int) (0.5 * (curSpeed + speed) * (curSpeed - speed) /
                            acceleration);

                    curSpeed = speed;
                    if (!isDown) {
                        move = -move;
                    }

                    if (!isRunning) {
                        return;
                    }
                    move(move);
                } while (curSpeed > 0);
                isScrolling = false;
                toSelectedZoom();
            }
        }));
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MOVE:
                    actionMove(msg.arg1);
                    break;
                case ACTION_UP:
                    actionUpMove(msg.arg1);
                    break;
                case BALANCE:
                    balance();
                    break;
                default:
                    break;
            }
        }

    };

    /**
     * 滑动结束后，自动滚动到最近的选项
     */
    private void toSelectedZoom() {
//        Log.e("toSelectedZoom", "toSelectedZoom" + Thread.currentThread().getId());
        handler.sendEmptyMessage(BALANCE);
    }

    /**
     * 平衡到选中位置
     */
    private void balance() {
        exe.execute(new Runnable() {
            @Override
            public void run() {
                if (!isRunning) {
                    return;
                }
                scrollToIndex(selectedIndex);
            }
        });
    }

    /**
     * 从零加速到指定位置，异步线程中调用
     */
    private void scrollToIndex(int index) {
        isScrolling = true;
        int d = (int) getMoveToSelectedDistance(index);
        final boolean isDown = d > 0;
        final int detal = isDown ? d : -d;
        if (detal <= 0) {
            onEndSelectListener(selectedIndex);
            return;
        }

        int move;
        int total = 0;
        float curSpeed = 0;
        float detalSpeed = DEF_A * FRESH_INTRV * 0.001f;
        do {
            try {
                Thread.sleep(FRESH_INTRV);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            float speed = curSpeed + detalSpeed;
            move = (int) (0.5 * (curSpeed + speed) * (speed - curSpeed) / DEF_A);
            total += move;
            if (total >= detal) {
                move -= total - detal;
            }
            curSpeed = speed;
            if (!isDown) {
                move = -move;
            }
            if (!isRunning) {
                return;
            }
            move(move);
        } while (total < detal);
        onEndSelectListener(selectedIndex);
        isScrolling = false;
        isRunning = false;
    }

    /**
     * 获取总数据个数（不循环的）
     */
    private int getSize() {
        return endNum - startNum + 1;
    }

    /**
     * 获取指定项的在列表中的index
     *
     * @param index 滚轮中的index（可超出原列表index范围）
     */
    private int getOriIndex(int index) {
        int size = getSize();
        if (index >= 0) {
            return index % size;
        } else {
            int mod = index % size;
            if (mod == 0) {
                return mod;
            } else {
                return size + mod;
            }
        }
    }

    /**
     * 获取指定项的text
     *
     * @param index      滚轮中的index（可超出原列表index范围）
     * @param isSelected 是否被选中
     */
    protected String getItemText(int index, boolean isSelected) {
        if (isLoop) {
            index = getOriIndex(index);
        }
        return getListItemText(index);
    }

    /**
     * 获取列表中指定项的text
     *
     * @param index 滚轮中的index（不可超出原列表index范围）
     */
    private String getListItemText(int index) {
        return (startNum + index) + "";
    }

    private void move(int move) {
        if (move != 0) {
            handler.sendMessage(handler.obtainMessage(MOVE, move, 0));
        }
    }

    /**
     * 设置数据 （第一次）
     *
     * @param startNum      开始的数字
     * @param endNum        结束的数字
     * @param selectedIndex 选中项
     */
    public void initData(int startNum, int endNum, int selectedIndex) {
        if (selectedIndex < 0 || startNum > endNum || selectedIndex > endNum - startNum) {
            throw new IllegalArgumentException();
        }

        this.isRunning = false;
        this.startNum = startNum;
        this.endNum = endNum;
        this.selectedIndex = selectedIndex;
        init();
    }

    /**
     * 重置数据
     *
     * @param startNum      开始的数字
     * @param endNum        结束的数字
     * @param selectedIndex 选中项
     */
    public void resetData(int startNum, int endNum, int selectedIndex) {
        initData(startNum, endNum, selectedIndex);
        invalidate();
    }

    /**
     * 获取选中项index
     */
    public int getSelectedIndex() {
        return getOriIndex(selectedIndex);
    }

    /**
     * 获取选中的数字
     */
    public int getSelectedNumber() {
        return startNum + getOriIndex(selectedIndex);
    }

    /**
     * 是否正在滑动
     */
    public boolean isScrolling() {
        return isScrolling;
    }

    /**
     * 是否可用
     */
    public boolean isEnable() {
        return isEnable;
    }

    /**
     * 重置滚动到默认选项
     */
    public void reSetToDefault() {
        if (defSelectedItem == null) {
            return;
        }

        isRunning = false;
        exe.execute(new Runnable() {
            @Override
            public void run() {
                isRunning = true;
                scrollToIndex(defSelectedItem.index);
            }
        });
    }

    /**
     * 设置是否可用
     */
    public void setEnable(boolean isEnable) {
        this.isEnable = isEnable;
    }

    /**
     * 设置监听
     */
    public void setOnSelectListener(OnSelectChangedListener onSelectListener) {
        this.onSelectListener = onSelectListener;
    }

    /**
     * 将index设为默认选项并滚动到它
     */
    public void setDefaultTo(final int index) {
        if (defSelectedItem == null) {
            return;
        }

        isRunning = false;
        exe.execute(new Runnable() {
            @Override
            public void run() {
                defSelectedItem.cury = getYPosition(index);
                defSelectedItem.index = index;
                isRunning = true;
                scrollToIndex(defSelectedItem.index);
            }
        });
    }

    /**
     * 不再使用控件时，停止异步线程
     */
    public void release() {
        if (exe != null) {
            isRunning = false;
            exe.shutdown();
        }
    }

    /**
     * 动态设置是否滚动，必须在{@link #init()}后调用
     */
    public void setLoop(boolean isLoop) {
        this.isLoop = isLoop;
        if (defSelectedItem == null) {
            return;
        }
        isRunning = false;
        selectedIndex = defSelectedItem.index;
        init();
        invalidate();
    }
}
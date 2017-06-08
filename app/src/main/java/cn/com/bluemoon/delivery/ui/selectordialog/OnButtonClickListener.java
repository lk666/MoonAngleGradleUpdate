package cn.com.bluemoon.delivery.ui.selectordialog;

/**
 * 点击按钮时的回调接口
 */
public interface OnButtonClickListener {
    /**
     * 点击确定时的回调，此方法能在初始化对话框定义事件时直接使用选择的日期字符串
     */
     void onOKButtonClick(long timeStamp);

    /**
     * 点击取消按钮的回调
     */
     void onCancleButtonClick();

    /**
     * 选中时间大于最大时间时提示，null为不做判断
     */
    String getCompareTips();
}
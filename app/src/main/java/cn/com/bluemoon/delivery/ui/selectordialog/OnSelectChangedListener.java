package cn.com.bluemoon.delivery.ui.selectordialog;

/**
 * 选择监听
 *
 * @author luokai
 */
public interface OnSelectChangedListener {
    /**
     * 滚动结束后选中的内容
     *
     * @param index 选中项在原列表中的index
     */
    void onEndSelected(int index, Object text);

    /**
     * 滚动过程选中的内容
     *
     * @param index 选中项在原列表中的index
     */
//    void onSelectedChanged(int index, Object text);
}


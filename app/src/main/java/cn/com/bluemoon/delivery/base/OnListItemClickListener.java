package cn.com.bluemoon.delivery.base;

import android.view.View;

import cn.com.bluemoon.delivery.base.BaseListAdapter;

/**
 * {@link BaseListAdapter}项点击事件回调
 * Created by luokai on 2016/6/14.
 */
public interface OnListItemClickListener {
    void onItemClick(Object item, View view, int position);
}

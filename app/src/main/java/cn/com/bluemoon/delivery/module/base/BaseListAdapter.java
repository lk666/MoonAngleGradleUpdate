package cn.com.bluemoon.delivery.module.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * 基础列表adapter
 * Created by lk on 2016/6/14.
 */
public abstract class BaseListAdapter<T> extends BaseAdapter implements View.OnClickListener {

    protected static final int KEY_POSITON = 0xFFF11222;

    protected List<T> list;
    protected Context context;
    protected OnListItemClickListener  listener;

    public BaseListAdapter(Context context, OnListItemClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setList(List<T> list) {
        if (list != null) {
            this.list = list;
        }
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return list == null ? null : list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        boolean isNew = false;
        if (convertView == null) {
            isNew = true;
            convertView = LayoutInflater.from(context).inflate(getLayoutId(), null);
        }

        setView(position, convertView, parent, isNew);

        return convertView;
    }

    /**
     * 设置点击事件
     */
    protected void setClickEvent(boolean isNew, int position, View... vs) {
        for (View v :vs) {
            v.setTag(KEY_POSITON, position);
            if (isNew) {
                v.setOnClickListener(this);
            }
        }
    }

    @Override
    public void onClick(View view) {
        final Object object = view.getTag(KEY_POSITON);
        if (object != null && object instanceof Integer) {
            final int position = (Integer) object;
            if (position < getCount()) {
                if (listener != null) {
                    listener.onItemClick(getItem(position), view, position);
                }
            }
        }
    }

    /**
     * 获取布局文件
     *
     * @return R.layout.*
     */
    protected abstract int getLayoutId();

    /**
     * getView的剩余操作
     *
     * @param position
     * @param convertView
     * @param parent
     * @param isNew       是否新建的convertView，即非复用控件
     */
    protected abstract void setView(int position, View convertView, ViewGroup parent, boolean isNew);
}


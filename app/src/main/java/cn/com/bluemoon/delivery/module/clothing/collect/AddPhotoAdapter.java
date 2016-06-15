package cn.com.bluemoon.delivery.module.clothing.collect;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;

/**
 * 最后一项为添加照片的adapter，当达到最大数目时，隐藏最后一项
 * Created by lk on 2016/6/15.
 */
public class AddPhotoAdapter extends BaseListAdapter<String>{
    public AddPhotoAdapter(Context context, OnListItemClickListener listener) {
        super(context, listener);
    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {

    }
}

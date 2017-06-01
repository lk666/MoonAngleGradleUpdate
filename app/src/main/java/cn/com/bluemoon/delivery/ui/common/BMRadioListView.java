package cn.com.bluemoon.delivery.ui.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.ui.common.entity.RadioItem;

/**
 * Created by bm on 2017/5/26.
 */

public class BMRadioListView extends FrameLayout implements OnListItemClickListener {

    private ListView listView;
    private RadioAdapter radioAdapter;
    private List<RadioItem> itemList;
    private int layoutId = -1;
    private String curKey;
    private ClickListener listener;

    public BMRadioListView(Context context) {
        super(context);
        init(null);
    }

    public BMRadioListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_bm_radio_list, this, true);
        listView = (ListView) findViewById(R.id.list_view);
        initAttrs(attrs);
    }

    //初始化属性值
    private void initAttrs(AttributeSet attrs) {
        if (attrs == null) return;
        TypedArray attribute = getContext().obtainStyledAttributes(attrs, R.styleable
                .BMRadioListView);
        int n = attribute.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = attribute.getIndex(i);
            switch (attr) {
                case R.styleable.BMRadioListView_radio_list_item_layout:
                    setLayoutId(attribute.getResourceId(attr, -1));
                    break;
            }
        }
        attribute.recycle();
    }

    //设置选择监听
    public void setListener(ClickListener listener){
        this.listener = listener;
    }

    //设置数据
    public void setData(List<RadioItem> list) {
        if (list == null) {
            list = new ArrayList<>();
        }
        itemList = list;
        updateAdapter();
    }

    //设置item布局样式（必须为BMRadioItemView）
    private void setLayoutId(int layoutId) {
        this.layoutId = layoutId;
    }

    //更新列表数据
    private void updateAdapter() {
        if(itemList==null){
            itemList = new ArrayList<>();
        }
        if (radioAdapter == null) {
            radioAdapter = new RadioAdapter(getContext(), this);
            radioAdapter.setLayoutId(layoutId);
            radioAdapter.setList(itemList);
            listView.setAdapter(radioAdapter);
        } else {
            radioAdapter.setList(itemList);
            radioAdapter.notifyDataSetChanged();
        }
        for (int i=0;i<itemList.size();i++){
            if(BMRadioItemView.TYPE_SELECT==itemList.get(i).type){
                curKey = itemList.get(i).key;
                //监听触发
                if(listener!=null){
                    listener.onSelected(i,curKey);
                }
                //单选
                break;
            }
        }
    }

    public String getCurKey() {
        return curKey;
    }

    @Override
    public void onItemClick(Object item, View view, int position) {
        RadioItem radioItem = (RadioItem) item;
        if (radioItem.type != BMRadioItemView.TYPE_NORMAL) return;
        for (int i = 0; i < itemList.size(); i++) {
            if (itemList.get(i).type != -1) {
                if (i == position) {
                    itemList.get(i).type = BMRadioItemView.TYPE_SELECT;
                } else {
                    itemList.get(i).type = BMRadioItemView.TYPE_NORMAL;
                }
            }
        }
        //更新界面
        updateAdapter();
    }

    class RadioAdapter extends BaseListAdapter<RadioItem> {

        private int layoutId = -1;

        public RadioAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        public void setLayoutId(int layoutId) {
            this.layoutId = layoutId;
        }

        @Override
        protected int getLayoutId() {
            return layoutId == -1 ? R.layout.item_bm_radio_item_view : layoutId;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            if (convertView instanceof BMRadioItemView) {
                RadioItem item = list.get(position);
                ((BMRadioItemView) convertView).setType(item.type);
                ((BMRadioItemView) convertView).setContent(item.text);
                setClickEvent(isNew, position, ((BMRadioItemView) convertView).getLayoutMain());
            }
        }
    }

    public interface ClickListener {
        void onSelected(int position, String key);
    }
}

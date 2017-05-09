package cn.com.bluemoon.delivery.module.wash.enterprise.createorder;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.wash.enterprise.ResultGetCooperationList;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.lib.view.ScrollGridView;

/**
 * 选择衣物类型固定高度的grid
 */

public class ClothesSelectTypeGrid extends ScrollGridView implements OnListItemClickListener {

    public ClothesSelectTypeGrid(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ClothesSelectTypeGrid(Context context) {
        super(context);
        init();
    }


    private void init() {
        setHorizontalSpacing(0);
        setVelocityScale(0);
        setNumColumns(3);
        setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
    }

    public interface IClothesSelectTypeGrid {
        void onSelectedChanged(int onLevelPosition, int twoLevelPosition);
    }

    private IClothesSelectTypeGrid listener;

    public void setListener(IClothesSelectTypeGrid listener) {
        this.listener = listener;
    }

    class ItemAdapter extends BaseListAdapter<ResultGetCooperationList.GoodsInfoListBean> {
        public ItemAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.item_select_clothes_type_child_item;
        }

        @Override
        protected void setView(int position, View convertView, ViewGroup parent, boolean isNew) {
            ResultGetCooperationList.GoodsInfoListBean item = list.get(position);
            if (item == null) {
                return;
            }

            ClothesSelectTypeView v = getViewById(R.id.v);

            v.setData(item);

            setClickEvent(isNew, position, convertView);
        }
    }

    private List<ResultGetCooperationList.GoodsInfoListBean> list;
    private int onLevelPosition;
    private ItemAdapter adapter;

    public void setData(int onLevelPosition, List<ResultGetCooperationList.GoodsInfoListBean>
            list) {
        this.list = list;
        this.onLevelPosition = onLevelPosition;
        adapter = new ItemAdapter(getContext(), this);
        adapter.setList(this.list);
        setAdapter(adapter);
    }

    @Override
    public void onItemClick(Object item, View view, int position) {
        ResultGetCooperationList.GoodsInfoListBean bean = (ResultGetCooperationList
                .GoodsInfoListBean) item;
        if (bean != null && listener != null) {
            listener.onSelectedChanged(onLevelPosition, position);
        }
    }
}

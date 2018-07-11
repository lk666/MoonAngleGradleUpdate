package cn.com.bluemoon.delivery.module.wash.enterprise.createorder;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.wash.enterprise.ResultGetCooperationList;

/**
 * 选择衣物类型头部view
 */

public class ClothesSelectTypeHeader extends LinearLayout {

    @BindView(R.id.gv_wash)
    ClothesSelectTypeGrid gvWash;

    public ClothesSelectTypeHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ClothesSelectTypeHeader(Context context) {
        super(context);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.item_select_clothes_type_header, this,
                true);
        ButterKnife.bind(this);
    }

    public void setListener(ClothesSelectTypeGrid.IClothesSelectTypeGrid listener) {
        if (gvWash != null) {
            gvWash.setListener(listener);
        }
    }

    public static final int DEF_ONE_LEVEL = -777;

    public void setData(List<ResultGetCooperationList.GoodsInfoListBean> goodsInfoList) {
        if (gvWash != null) {
            gvWash.setData(DEF_ONE_LEVEL, goodsInfoList);
        }
    }

    public void refreshData() {
        gvWash.refreshData();
    }
}

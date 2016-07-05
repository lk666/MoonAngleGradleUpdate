package cn.com.bluemoon.delivery.module.clothing.collect;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.clothing.ClothesType;
import cn.com.bluemoon.delivery.utils.ImageLoaderUtil;

/**
 * 衣物名称类型View
 * Created by lk on 2016/6/30.
 */
public class ClothesNameView extends FrameLayout {
    ClothesType type;
    int colorSelected;
    int colorNormal;

    ImageView ivType;
    ImageView ivChecked;
    TextView tvType;

    public int getIndex() {
        return index;
    }

    public ClothesType getType() {
        return type;
    }

    int index;

    public ClothesNameView(Context context, ClothesType type, int index) {
        super(context);
        this.type = type;
        this.index = index;
        init();
    }

    private void init() {
        colorSelected = getContext().getResources().getColor(R.color.btn_blue);
        colorNormal = getContext().getResources().getColor(R.color.text_black_light);

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View itemView = inflater.inflate(R.layout.item_clothes_type, null);

        ivType = (ImageView) itemView.findViewById(R.id.iv_type);
        ivChecked = (ImageView) itemView.findViewById(R.id.iv_checked);
        tvType = (TextView) itemView.findViewById(R.id.tv_type);

        ImageLoaderUtil.displayImage(getContext(), type.getImgPath(), ivType);
        tvType.setText(type.getClothesName());
        ivChecked.setVisibility(View.GONE);

        addView(itemView);
    }

    public void setChecked(boolean isChecked) {
        if (isChecked) {
            ivChecked.setVisibility(View.VISIBLE);
            tvType.setTextColor(colorSelected);
        } else {
            ivChecked.setVisibility(View.GONE);
            tvType.setTextColor(colorNormal);
        }
    }
}
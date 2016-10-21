package cn.com.bluemoon.delivery.module.clothing.collect;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.clothing.ClothesTypeInfo;

/**
 * 服务类型View
 * Created by lk on 2016/6/30.
 */
public class ClothesTypeInfoView extends FrameLayout {
    ClothesTypeInfo typeInfo;

    TextView tvTypeName;
    View vUnderline;
    int colorSelected;
    int colorNormal;

    int bgColorSelected;
    int bgColorNormal;

    public int getIndex() {
        return index;
    }

    public ClothesTypeInfo getTypeInfo() {
        return typeInfo;
    }

    int index;

    public ClothesTypeInfoView(Context context, ClothesTypeInfo typeInfo, int index) {
        super(context);
        this.typeInfo = typeInfo;
        this.index = index;
        init();
    }

    private void init() {

        colorSelected = getContext().getResources().getColor(R.color.text_red);
        colorNormal = getContext().getResources().getColor(R.color.text_black_light);

        bgColorSelected = getContext().getResources().getColor(R.color.white);
        bgColorNormal = getContext().getResources().getColor(R.color.transparent);

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View itemView = inflater.inflate(R.layout.item_clothes_type_info, null);

        tvTypeName = (TextView) itemView.findViewById(R.id.tv_type_name);
        vUnderline = itemView.findViewById(R.id.v_underline);

        tvTypeName.setText(typeInfo.getTypeName());
        vUnderline.setVisibility(GONE);

        addView(itemView);
    }

    public void setChecked(boolean isChecked) {
        if (isChecked) {
            tvTypeName.setTextColor(colorSelected);
            vUnderline.setVisibility(View.VISIBLE);
            setBackgroundColor(bgColorSelected);
        } else {
            tvTypeName.setTextColor(colorNormal);
            vUnderline.setVisibility(View.GONE);
            setBackgroundColor(bgColorNormal);
        }
    }
}
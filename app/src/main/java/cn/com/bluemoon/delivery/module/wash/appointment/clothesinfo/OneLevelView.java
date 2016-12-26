package cn.com.bluemoon.delivery.module.wash.appointment.clothesinfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.clothing.ClothesTypeInfo;
import cn.com.bluemoon.delivery.app.api.model.wash.appointment.OneLevel;

/**
 * 一级分类View
 * Created by lk on 2016/6/30.
 */
public class OneLevelView extends FrameLayout {
    OneLevel oneLevel;

    TextView tvTypeName;
    View vUnderline;
    int colorSelected;
    int colorNormal;

    int bgColorSelected;
    int bgColorNormal;

    public int getIndex() {
        return index;
    }

    public OneLevel getOneLevel() {
        return oneLevel;
    }

    int index;

    public OneLevelView(Context context, OneLevel typeInfo, int index) {
        super(context);
        this.oneLevel = typeInfo;
        this.index = index;
        init();
    }

    private void init() {

        colorSelected = getContext().getResources().getColor(R.color.text_red);
        colorNormal = getContext().getResources().getColor(R.color.text_black_light);

        bgColorSelected = getContext().getResources().getColor(R.color.white);
        bgColorNormal = getContext().getResources().getColor(R.color.transparent);

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View itemView = inflater.inflate(R.layout.item_one_level, null);

        tvTypeName = (TextView) itemView.findViewById(R.id.tv_type_name);
        vUnderline = itemView.findViewById(R.id.v_underline);

        tvTypeName.setText(oneLevel.getOneLevelName());
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
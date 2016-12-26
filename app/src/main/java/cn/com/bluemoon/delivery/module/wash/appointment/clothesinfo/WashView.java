package cn.com.bluemoon.delivery.module.wash.appointment.clothesinfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.wash.appointment.TwoLevel;
import cn.com.bluemoon.lib.utils.ImageLoaderUtil;
import cn.com.bluemoon.lib.utils.LibViewUtil;

/**
 * 商品View
 * Created by lk on 2016/6/30.
 */
public class WashView extends FrameLayout {
    TwoLevel twoLevel;
    int colorSelected;
    int colorNormal;

    ImageView ivType;
    ImageView ivChecked;
    TextView tvType;

    public int getIndex() {
        return index;
    }

    public TwoLevel getTwoLevel() {
        return twoLevel;
    }

    int index;

    public WashView(Context context, TwoLevel type, int index) {
        super(context);
        this.twoLevel = type;
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

        ImageLoaderUtil.displayImage(twoLevel.getClothImgUrl(), ivType);
        String name = twoLevel.getWashName() == null ? "" : twoLevel.getWashName();
        tvType.setText(name);
        LibViewUtil.setMaxEcplise(tvType, 2, name);
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
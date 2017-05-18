package cn.com.bluemoon.delivery.module.wash.enterprise.createorder;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.wash.enterprise.ResultGetCooperationList;
import cn.com.bluemoon.delivery.utils.ViewUtil;
import cn.com.bluemoon.lib.view.SquareLayout;

/**
 * 选择衣物类型固定高度的grid
 */

public class ClothesSelectTypeView extends SquareLayout {

    @Bind(R.id.iv_selected)
    ImageView ivSelected;
    @Bind(R.id.iv)
    ImageView iv;
    @Bind(R.id.tv)
    TextView tv;
    @Bind(R.id.ll)
    LinearLayout ll;

    public ClothesSelectTypeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ClothesSelectTypeView(Context context) {
        super(context);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.item_select_clothes_type, this, true);
        int width = (int) (ViewUtil.getScreenWidth(getContext()) * 0.15);
        ButterKnife.bind(this);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, width);
        iv.setLayoutParams(lp);
        isGetImage = false;
    }

    private ResultGetCooperationList.GoodsInfoListBean data;

    private boolean isGetImage = false;

    class MySimpleTarget extends SimpleTarget<Bitmap> {

        public MySimpleTarget() {
        }

        @Override
        public void onResourceReady(Bitmap bitmap, GlideAnimation
                glideAnimation) {
            iv.setImageBitmap(bitmap);
            isGetImage = true;
        }
    }

    public void setData(ResultGetCooperationList.GoodsInfoListBean data) {
        this.data = data;
        tv.setText(data.washName);

        if (!isGetImage) {
            Glide.with(getContext())
                    .load(data.imgPath)
                    .asBitmap()
                    .placeholder(R.mipmap.loading_img_logo)
                    .error(R.mipmap.place_holder)
                    .into(new MySimpleTarget());
        } else {
            Glide.with(getContext())
                    .load(data.imgPath)
                    .into(iv);
        }

        if (data.isSelected) {
            ivSelected.setVisibility(VISIBLE);
            tv.setTextColor(getContext().getResources().getColor(R.color.text_blue_1eb8ff));
            ll.setBackgroundResource(R.drawable.border_blue);
        } else {
            ivSelected.setVisibility(GONE);
            tv.setTextColor(getContext().getResources().getColor(R.color.text_black_light));
            ll.setBackgroundColor(getResources().getColor(R.color.transparent));
        }
    }
}

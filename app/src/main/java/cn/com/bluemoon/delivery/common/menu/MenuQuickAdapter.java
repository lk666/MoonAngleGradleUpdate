package cn.com.bluemoon.delivery.common.menu;

import android.graphics.Color;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.UserRight;
import cn.com.bluemoon.delivery.utils.ImageLoaderUtil;
import cn.com.bluemoon.lib_widget.module.form.BmMarkView;

/**
 * 缩下来的快捷图标
 * Created by bm on 2017/11/17.
 */

public class MenuQuickAdapter extends BaseQuickAdapter<UserRight, BaseViewHolder> {

    public MenuQuickAdapter() {
        super(R.layout.item_menu_quick_view);
    }

    @Override
    protected void convert(BaseViewHolder helper, UserRight item) {
        //根据返回的颜色设置图片
        ImageView imgView = helper.getView(R.id.img_menu);
        ImageLoaderUtil.displayImageWithColor(item.getIconImg(), imgView, Color.parseColor("#" +
                item.getColor()));
    }
}

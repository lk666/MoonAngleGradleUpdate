package cn.com.bluemoon.delivery.common.menu;

import android.graphics.Color;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.UserRight;
import cn.com.bluemoon.delivery.utils.ImageLoaderUtil;
import cn.com.bluemoon.lib_widget.module.form.BmMarkView;

/**
 * 弹起快捷方式
 * Created by bm on 2017/11/17.
 */

public class MenuEditAdapter extends BaseItemDraggableAdapter<UserRight, BaseViewHolder> {

    private boolean isEdit;

    public MenuEditAdapter() {
        super(R.layout.item_menu_edit_view, null);
    }

    public void setEdit(boolean isEdit) {
        this.isEdit = isEdit;
        notifyDataSetChanged();
    }

    @Override
    protected void convert(BaseViewHolder helper, UserRight item) {
        helper.setText(R.id.txt_menu, item.getMenuName())
                .setVisible(R.id.img_edit, isEdit)
                .addOnClickListener(R.id.img_edit);

        BmMarkView markView = helper.getView(R.id.num_menu);
        markView.setMarkViewWidthAndText(isEdit ? 0 : item.getAmount());

        //根据返回的颜色设置图片
        ImageView imgView = helper.getView(R.id.img_menu);
        ImageLoaderUtil.displayImageWithColor(item.getIconImg(), imgView, Color.parseColor("#" +
                item.getColor()));
    }
}

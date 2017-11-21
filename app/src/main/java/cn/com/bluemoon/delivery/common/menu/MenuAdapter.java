package cn.com.bluemoon.delivery.common.menu;

import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.utils.ImageLoaderUtil;
import cn.com.bluemoon.delivery.utils.ViewUtil;
import cn.com.bluemoon.lib_widget.module.form.BmMarkView;

/**
 * 菜单adapter
 * Created by bm on 2017/11/16.
 */

public class MenuAdapter extends BaseSectionQuickAdapter<MenuSection, BaseViewHolder> {

    private boolean isEdit;

    public MenuAdapter() {
        super(R.layout.item_menu_view, R.layout.item_menu_head, null);
    }

    public void setEdit(boolean isEdit) {
        this.isEdit = isEdit;
        notifyDataSetChanged();
    }

    @Override
    protected void convertHead(BaseViewHolder helper, MenuSection item) {
        helper.setText(R.id.txt_head, item.header);
    }

    @Override
    protected void convert(BaseViewHolder helper, MenuSection item) {

        BmMarkView markView = helper.getView(R.id.num_menu);
        markView.setMarkViewWidthAndText(isEdit ? 0 : item.t.getAmount());

        helper.setText(R.id.txt_menu, item.t.getMenuName());

        ImageView imgEdit = helper.getView(R.id.img_edit);
        ViewUtil.setViewVisibility(imgEdit, isEdit ? View.VISIBLE : View.GONE);
        imgEdit.setEnabled(!item.t.isQuick);
        helper.addOnClickListener(R.id.img_edit);

        //根据返回的颜色设置图片
        ImageView imgView = helper.getView(R.id.img_menu);
        ImageLoaderUtil.displayImageWithColor(item.t.getIconImg(), imgView, Color.parseColor("#" + item.t
                .getColor()));

    }
}

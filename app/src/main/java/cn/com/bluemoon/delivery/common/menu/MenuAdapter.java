package cn.com.bluemoon.delivery.common.menu;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.utils.ImageLoaderUtil;
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

        helper.setText(R.id.txt_menu, item.t.getMenuName())
                .addOnClickListener(R.id.img_edit);

        ImageView imgView = helper.getView(R.id.img_menu);
        ImageLoaderUtil.displayImage(mContext, item.t.getIconImg(), imgView);

        helper.setVisible(R.id.img_edit, isEdit);
        ImageView imgEdit = helper.getView(R.id.img_edit);
        imgEdit.setEnabled(item.t.getAmount() <= 0);
        helper.addOnClickListener(R.id.img_edit);

    }
}

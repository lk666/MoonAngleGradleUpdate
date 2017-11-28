/*
package cn.com.bluemoon.delivery.common.menu;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import cn.com.bluemoon.delivery.AppContext;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.utils.ImageLoaderUtil;
import cn.com.bluemoon.delivery.utils.ViewUtil;
import cn.com.bluemoon.lib_widget.module.form.BmMarkView;
import cn.com.bluemoon.lib_widget.utils.WidgeUtil;

*/
/**
 * 菜单adapter
 * Created by bm on 2017/11/16.
 *//*


public class MenuCopyAdapter extends BaseSectionQuickAdapter<MenuSection, BaseViewHolder> {

    private boolean isEdit;
    private int editWidth;
    private int noNumWidth;
    private int rightWidth;

    public MenuCopyAdapter() {
        super(R.layout.item_menu_view, R.layout.item_menu_head, null);
        int width = AppContext.getInstance().getDisplayWidth()/2;
        editWidth = width - WidgeUtil.dip2px(AppContext.getInstance(), 76);
        noNumWidth = width - WidgeUtil.dip2px(AppContext.getInstance(), 52);
        rightWidth = WidgeUtil.dip2px(AppContext.getInstance(), 4);
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

        //根据返回的颜色设置图片
        ImageView imgView = helper.getView(R.id.img_menu);
        ImageLoaderUtil.displayImageWithColor(item.t.getIconImg(), imgView, Color.parseColor("#"
                + item.t.getColor()));

        //设置编辑图标
        ImageView imgEdit = helper.getView(R.id.img_edit);
        ViewUtil.setViewVisibility(imgEdit, isEdit ? View.VISIBLE : View.GONE);
        imgEdit.setEnabled(!item.t.isQuick);
        helper.addOnClickListener(R.id.img_edit);

        //设置角标
        BmMarkView markView = helper.getView(R.id.num_menu);
        //设置角标并获取角标宽度
        int numWidth = markView.setMarkViewWidthAndText(isEdit ? 0 : item.t.getAmount());
        if (item.t.getAmount() <= 0) {
            //如果角标没有时，宽度为0
            numWidth = noNumWidth;
        } else {
            //有角标时，减去角标宽度和右边4dp
            numWidth = noNumWidth - numWidth - rightWidth;
        }
        //设置名字
        TextView txtMenu = helper.getView(R.id.txt_menu);
        //实时设置最大宽度
        txtMenu.setMaxWidth(isEdit ? editWidth : numWidth);
        txtMenu.setText(item.t.getMenuName());
    }
}
*/

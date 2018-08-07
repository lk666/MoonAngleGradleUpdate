package cn.com.bluemoon.delivery.common.menu;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.ui.ReplaceImageView;
import cn.com.bluemoon.delivery.utils.ImageLoaderUtil;
import cn.com.bluemoon.delivery.utils.ViewUtil;

/**
 * 菜单adapter
 * Created by bm on 2017/11/16.
 */

public class MenuAdapter extends BaseSectionQuickAdapter<MenuSection, BaseViewHolder> {

    public MenuAdapter() {
        super(R.layout.main_gridview_item, R.layout.item_menu_old_head, null);
    }

    @Override
    protected void convertHead(BaseViewHolder helper, MenuSection item) {

    }

    @Override
    protected void convert(BaseViewHolder helper, MenuSection item) {

        if (MenuCode.empty.toString().equals(item.t.getMenuCode())) {
            helper.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.white));
            helper.setVisible(R.id.txt_menu_item, false);
            helper.setVisible(R.id.img_menu_item, false);
            helper.setVisible(R.id.txt_dispatch_count, false);
        } else {
            //设置名字
            helper.setText(R.id.txt_menu_item, item.t.getMenuName());

            //根据返回的颜色设置图片
            ReplaceImageView imgView = helper.getView(R.id.img_menu_item);
            imgView.setImageUrl(item.t.getIconImg());

            //设置角标
            int num = item.t.getAmount();
            TextView txtNum = helper.getView(R.id.txt_dispatch_count);
            ViewUtil.setViewVisibility(txtNum, num > 0 ? View.VISIBLE : View.GONE);
            helper.setVisible(R.id.txt_dispatch_count, num > 0);
            helper.setText(R.id.txt_dispatch_count, num > 99 ? mContext.getString(R.string
                    .more_amount) : String.valueOf(num));

            helper.itemView.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable
                    .btn_white));
            helper.setVisible(R.id.txt_menu_item, true);
            helper.setVisible(R.id.img_menu_item, true);
        }


    }
}

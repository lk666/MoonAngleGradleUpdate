package cn.com.bluemoon.delivery.common.menu;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import cn.com.bluemoon.delivery.R;

/**
 * 编辑菜单的背景图
 * Created by bm on 2017/11/17.
 */

public class MenuBgAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public MenuBgAdapter(List<String> list) {
        super(R.layout.item_menu_bg_view, list);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
    }

}

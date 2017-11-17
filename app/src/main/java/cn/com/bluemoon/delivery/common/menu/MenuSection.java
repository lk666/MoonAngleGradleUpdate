package cn.com.bluemoon.delivery.common.menu;

import com.chad.library.adapter.base.entity.SectionEntity;

import cn.com.bluemoon.delivery.app.api.model.UserRight;

/**
 * 菜单实体类
 * Created by bm on 2017/11/16.
 */

public class MenuSection extends SectionEntity<UserRight> {

    public MenuSection(boolean isHeader, String header) {
        super(isHeader, header);
    }

    public MenuSection(UserRight userRight) {
        super(userRight);
    }
}

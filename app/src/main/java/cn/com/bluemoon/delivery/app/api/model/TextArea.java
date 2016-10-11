package cn.com.bluemoon.delivery.app.api.model;

import cn.com.bluemoon.lib.view.selectordialog.ISecectedItem;

/**
 * Created by bm on 2016/10/11.
 */
public class TextArea implements ISecectedItem {

    private String text;

    public TextArea(String text) {
        this.text = text;
    }

    @Override
    public String getShowText() {
        return text;
    }
}

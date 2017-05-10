package cn.com.bluemoon.delivery.app.api.model.wash.enterprise;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * 8.05企业收衣获取合作品类商品
 */
public class ResultGetCooperationList extends ResultBase {
    /**
     * 商品信息
     */
    public List<GoodsInfoListBean> goodsInfoList;
    /**
     * 所有洗衣商品
     */
    public List<WashListBean> washList;

    public static class GoodsInfoListBean implements Serializable {
        /**
         * 商品图片地址
         */
        public String imgPath;
        /**
         * 商品编码
         */
        public String washCode;
        /**
         * 商品名称
         */
        public String washName;

        /**
         * 选择衣物类型时使用
         */
        @JSONField(serialize = false)
        public boolean isSelected = false;
    }

    public static class WashListBean implements Serializable {

        /**
         * 一级分类编码
         */
        public String oneLevelCode;
        /**
         * 一级分类名称
         */
        public String oneLevelName;
        /**
         * 二级分类商品
         */
        public List<GoodsInfoListBean> twoLevelList;

    }
}

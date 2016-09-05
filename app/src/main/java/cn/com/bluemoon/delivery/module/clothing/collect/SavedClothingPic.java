package cn.com.bluemoon.delivery.module.clothing.collect;

/**
 * 修改衣物时的初始衣物照片实体类
 * Created by lk on 2016/6/20.
 */
public class SavedClothingPic extends ClothingPic {

    public SavedClothingPic(ClothingPic clothingPic) {
        setImgId(clothingPic.getImgId());
        setImgPath(clothingPic.getImgPath());
    }
}

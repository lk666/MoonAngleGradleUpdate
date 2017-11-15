package cn.com.bluemoon.delivery.app.api.model.scan;

import cn.com.bluemoon.cardocr.lib.bean.IdCardInfo;

/**
 * Created by bm on 2017/11/10.
 */

public class ResultIDCard extends IdCardInfo {

    public boolean isSuccess;
    public String responesMsg;
    //正反面标识，0为正面，1为反面
    public int identifySide;

    public ResultIDCard(IdCardInfo idCardInfo, boolean isFront) {
        if (idCardInfo == null) return;
        setName(idCardInfo.getName());
        setAddress(idCardInfo.getAddress());
        setAuthority(idCardInfo.getAuthority());
        setBirth(idCardInfo.getBirth());
        setId(idCardInfo.getId());
        setNation(idCardInfo.getNation());
        setImageUrl(idCardInfo.getImageUrl());
        setSex(idCardInfo.getSex());
        setValidDate(idCardInfo.getValidDate());
        identifySide = isFront ? 0 : 1;
        isSuccess = true;
    }
}

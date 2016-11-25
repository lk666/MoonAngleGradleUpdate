package com.bluemoon.umengshare;

import com.umeng.socialize.bean.SHARE_MEDIA;

/**
 * Created by allenli on 2016/11/25.
 */
public interface ShareCallBack {
     void boardClickCallBack(SHARE_MEDIA platform, String platformString, ShareModel shareModel);
     void shareSuccess(SHARE_MEDIA platform, String platformString, ShareModel shareModel);
     void shareCancel(SHARE_MEDIA platform, String platformString, ShareModel shareModel);
     void shareError(SHARE_MEDIA platform, String platformString, ShareModel shareModel, String errorMsg);
}

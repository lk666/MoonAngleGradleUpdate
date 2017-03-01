package cn.com.bluemoon.delivery.module.base.interf;

/**
 * Created by bm on 2017/3/1.
 */

public interface BMDownLoadListener {

    void onDownStart(long downloadId, String url, String path);

    void onLoading(long downloadId, String url);

    void onDownFinish(long downloadId, String url, boolean isSuccess);

}

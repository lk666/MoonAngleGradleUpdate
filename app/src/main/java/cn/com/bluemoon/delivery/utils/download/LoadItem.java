package cn.com.bluemoon.delivery.utils.download;

/**
 * Created by bm on 2017/3/1.
 */

public class LoadItem {

    //下载id
    private long id;
    //下载网址
    private String url;
    //存放文件路径
    private String filePath;

    public LoadItem(String url,long id,String filePath){
        this.url = url;
        this.id = id;
        this.filePath = filePath;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getId() {

        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}

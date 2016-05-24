package cn.com.bluemoon.delivery.app.api.model.knowledge;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by allenli on 2016/5/20.
 */
public class ResultFavorites extends ResultBase {
    private List<FavoriteItem> list;
    private long timestamp;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public List<FavoriteItem> getList() {
        return list;
    }

    public void setList(List<FavoriteItem> list) {
        this.list = list;
    }
}

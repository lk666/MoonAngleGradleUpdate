package cn.com.bluemoon.delivery.app.api.model.team;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by bm on 2016/6/23.
 */
public class ResultRelationDetail extends ResultBase{
    RelationDetail relationDetail;

    public RelationDetail getRelationDetail() {
        return relationDetail;
    }

    public void setRelationDetail(RelationDetail relationDetail) {
        this.relationDetail = relationDetail;
    }
}

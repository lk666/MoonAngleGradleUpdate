package cn.com.bluemoon.delivery.app.api.model.personalinfo;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * 获取兴趣爱好和特长
 * Created by liangjiangli on 2017/6/15.
 */

public class ResultGetInterstInfo extends ResultBase{

    /**
     * interest 技术爱好/特长 String
     * otherHobby 其他爱好 String
     * performExperience 表演经历 String
     * specialty 业余爱好/特长 String
     */

    public String interest;
    public String otherHobby;
    public String performExperience;
    public String specialty;
}

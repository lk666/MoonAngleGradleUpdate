package cn.com.bluemoon.delivery.module.card;

import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.Animation;

import org.kymjs.kjframe.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import cn.com.bluemoon.delivery.AppContext;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.card.PunchCard;
import cn.com.bluemoon.delivery.app.api.model.card.PunchCardType;
import cn.com.bluemoon.delivery.app.api.model.card.WorkTask;
import cn.com.bluemoon.delivery.app.api.model.card.Workplace;
import cn.com.bluemoon.delivery.utils.AccelerateInterpolator;
import cn.com.bluemoon.lib.tagview.Tag;
import cn.com.bluemoon.lib.tagview.TagListView;

/**
 * Created by bm on 2016/3/30.
 */
public class CardUtils {

    public static String getAddress(PunchCard punchCard){
        if(punchCard==null){
            return "";
        }
        StringBuffer strBuffer = new StringBuffer();
        if (!StringUtils.isEmpty(punchCard.getProvinceName()))
            strBuffer.append(punchCard.getProvinceName());
        if (!StringUtils.isEmpty(punchCard.getCityName()))
            strBuffer.append(punchCard.getCityName());
        if (!StringUtils.isEmpty(punchCard.getCountyName()))
            strBuffer.append(punchCard.getCountyName());
        if (!StringUtils.isEmpty(punchCard.getTownName()))
            strBuffer.append(punchCard.getTownName());
        if (!StringUtils.isEmpty(punchCard.getVillageName()))
            strBuffer.append(punchCard.getVillageName());
        if (!StringUtils.isEmpty(punchCard.getAddress()))
            strBuffer.append(punchCard.getAddress());
        return strBuffer.toString();
    }

    public static String getCharge(PunchCard punchCard){
        if(PunchCardType.other.toString().equals(punchCard.getPunchCardType())){
            return AppContext.string(R.string.card_no_code_workplace);
        }
        StringBuffer strBuffer = new StringBuffer();
        if(punchCard!=null){
            if (!StringUtils.isEmpty(punchCard.getAttendanceCode()))
                strBuffer.append(punchCard.getAttendanceCode());
            if (!StringUtils.isEmpty(punchCard.getAttendanceName()))
                strBuffer.append("-").append(punchCard.getAttendanceName());
            if (!StringUtils.isEmpty(punchCard.getPrincipalName())
                    ||!StringUtils.isEmpty(punchCard.getPrincipalMobile()))
                strBuffer.append("\n");
            if (!StringUtils.isEmpty(punchCard.getPrincipalName()))
                strBuffer.append(punchCard.getPrincipalName());
            if (!StringUtils.isEmpty(punchCard.getPrincipalMobile()))
                strBuffer.append(" ").append(punchCard.getPrincipalMobile());
        }
        return strBuffer.toString();
    }

    public static String getChargeNoCode(PunchCard punchCard){
        if(PunchCardType.other.toString().equals(punchCard.getPunchCardType())){
            return AppContext.string(R.string.card_no_code_workplace);
        }
        StringBuffer strBuffer = new StringBuffer();
        if(punchCard!=null){
            if (!StringUtils.isEmpty(punchCard.getAttendanceName()))
                strBuffer.append(punchCard.getAttendanceName());
            if (!StringUtils.isEmpty(punchCard.getPrincipalName())
                    ||!StringUtils.isEmpty(punchCard.getPrincipalMobile()))
                strBuffer.append("\n");
            if (!StringUtils.isEmpty(punchCard.getPrincipalName()))
                strBuffer.append(punchCard.getPrincipalName());
            if (!StringUtils.isEmpty(punchCard.getPrincipalMobile()))
                strBuffer.append(" ").append(punchCard.getPrincipalMobile());
        }
        return strBuffer.toString();
    }

    public static String getChargeNoPhone(PunchCard punchCard){
        if(PunchCardType.other.toString().equals(punchCard.getPunchCardType())){
            return AppContext.string(R.string.card_no_code_workplace);
        }
        StringBuffer strBuffer = new StringBuffer();
        if(punchCard!=null){
            if (!StringUtils.isEmpty(punchCard.getAttendanceCode()))
                strBuffer.append(punchCard.getAttendanceCode());
            if (!StringUtils.isEmpty(punchCard.getAttendanceName()))
                strBuffer.append("-").append(punchCard.getAttendanceName());
        }
        return strBuffer.toString();
    }

    public static String getWorkPlaceAddress(Workplace workplace){
        if(workplace==null){
            return "";
        }
        StringBuffer strBuffer = new StringBuffer();
        if (!StringUtils.isEmpty(workplace.getProvinceName()))
            strBuffer.append(workplace.getProvinceName());
        if (!StringUtils.isEmpty(workplace.getCityName()))
            strBuffer.append(workplace.getCityName());
        if (!StringUtils.isEmpty(workplace.getCountyName()))
            strBuffer.append(workplace.getCountyName());
        if (!StringUtils.isEmpty(workplace.getAddress()))
            strBuffer.append(workplace.getAddress());
        return strBuffer.toString();
    }

    public static String getWorkPlaceAddress(PunchCard punchCard){
        if(punchCard==null){
            return "";
        }
        StringBuffer strBuffer = new StringBuffer();
        if (!StringUtils.isEmpty(punchCard.getProvinceName()))
            strBuffer.append(punchCard.getProvinceName());
        if (!StringUtils.isEmpty(punchCard.getCityName()))
            strBuffer.append(punchCard.getCityName());
        if (!StringUtils.isEmpty(punchCard.getCountyName()))
            strBuffer.append(punchCard.getCountyName());
        return strBuffer.toString();
    }

    public static String getWorkTaskString(List<Tag> tags){
        String str =null;
        if(tags!=null){
            for (int i=0;i<tags.size();i++){
                if(StringUtils.isEmpty(str)){
                    str = tags.get(i).getKey();
                }else{
                    str += ","+tags.get(i).getKey();
                }
            }
        }
        return str;
    }
    // 动画实际执行
    public static ObjectAnimator startPropertyAnim(View view) {
        // 第二个参数"rotation"表明要执行旋转
        // 0f -> 360f，从旋转360度，也可以是负值，负值即为逆时针旋转，正值是顺时针旋转。
        ObjectAnimator anim = ObjectAnimator.ofFloat(view, "rotation", 0f, 359f);

        // 动画的持续时间，执行多久？
        anim.setDuration(1500);
        anim.setRepeatMode(ObjectAnimator.RESTART);
        anim.setRepeatCount(Animation.INFINITE);
        anim.setInterpolator(new AccelerateInterpolator());
        // 正式开始启动执行动画
        anim.start();
        return anim;
    }

    // 动画实际执行
    public static void stopPropertyAnim(ObjectAnimator anim) {
        if (null != anim && anim.isStarted()) {
            // 正式开始启动执行动画
            anim.cancel();
            anim = null;
        }
    }

    /**
     * 设置工作任务
     * @param workTasks
     * @param tagListView
     */
    public static void setTags(List<WorkTask> workTasks, TagListView tagListView, boolean isWorkOff) {
        List<Tag> list = new ArrayList<>();
        if (workTasks != null) {
            if (!isWorkOff) {
                if (workTasks.size() > 0) {
                    tagListView.setVisibility(View.VISIBLE);
                } else {
                    tagListView.setVisibility(View.GONE);
                }
            }
            for (int i = 0; i < workTasks.size(); i++) {
                Tag tag = new Tag();
                tag.setId(i);
                tag.setKey(workTasks.get(i).getTaskCode());
                tag.setChecked(workTasks.get(i).isSelected);
                tag.setTitle(workTasks.get(i).getTaskName());
                list.add(tag);
            }
        }
        tagListView.setTags(list);
    }
}

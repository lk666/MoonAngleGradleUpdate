package cn.com.bluemoon.delivery.card;

import org.kymjs.kjframe.utils.StringUtils;

import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.AppContext;
import cn.com.bluemoon.delivery.app.api.model.card.PunchCard;
import cn.com.bluemoon.delivery.app.api.model.card.PunchCardType;
import cn.com.bluemoon.delivery.app.api.model.card.Workplace;
import cn.com.bluemoon.lib.tagview.Tag;

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

    public static String getWorkPlaceItem(Workplace workplace){
        if(workplace==null){
            return "";
        }
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append(workplace.getWorkplaceCode());
        if(!StringUtils.isEmpty(workplace.getWorkplaceName()))
            strBuffer.append("-").append(workplace.getWorkplaceName());
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

}

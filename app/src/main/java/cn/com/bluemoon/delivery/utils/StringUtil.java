package cn.com.bluemoon.delivery.utils;

import cn.com.bluemoon.lib.utils.LibStringUtil;

public class StringUtil extends LibStringUtil {

    public static String getCheckVersionDescription(String desc) {
        StringBuffer strBuff = new StringBuffer();
        String[] descs = desc.split(";");
        for (String description : descs) {
            strBuff.append(description);
            strBuff.append("\n");
        }
        return strBuff.toString();
    }

    public static boolean isEmptyString(String str) {
        return str == null || "".equals(str.trim()) || "null".equals(str) || "(null)".equals(str);
    }

    /**
     * 用“-”符号连接多个字符串，忽略空字符串
     *
     * @param params
     * @return
     */
    public static String getStringParams(String... params) {
        return getStringParamsByFormat("-", params);
    }
}
  

package cn.com.bluemoon.delivery.utils;

import java.text.DecimalFormat;

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

    public static String formatPriceByFen(long price) {
        return String.format("%.2f", (double) price / 100);
    }

    public static String formatPriceByFen(int price) {
        return String.format("%.2f", (double) price / 100);
    }

    public static String formatBoxesNum(double boxes) {
        return formatByPoint(boxes,1);
    }

    public static String formatDoubleMoney(long money) {
        StringBuilder strBuff = new StringBuilder( String.valueOf(money));
        int length = strBuff.toString().length();
        if (length == 1) {
            strBuff.insert(0, "0.0");
        } else if (length == 2){
            strBuff.insert(0, "0.");
        } else if (length > 8) {
            strBuff.insert(length - 8, ",");
            strBuff.insert(length - 4, ",");
            strBuff.insert(length, ".");
        } else if (length > 5) {
            strBuff.insert(length - 5, ",");
            strBuff.insert(length - 1, ".");
        } else {
            strBuff.insert(length - 2, ".");
        }
        return strBuff.toString();
    }

    public static String formatIntMoney(long money) {
        StringBuilder strBuff = new StringBuilder( String.valueOf(money));
        int length = strBuff.toString().length();
        if (length > 6) {
            strBuff.insert(length - 6, ",");
            strBuff.insert(length - 2, ",");
        } else if (length > 3) {
            strBuff.insert(length - 3, ",");
        }
        return strBuff.toString();
    }

    /**
     * 3000返回30.00, 123456789返回1,234,567.89
     *
     * @param priceFen 分
     */
    public static String getPriceFormat(long priceFen) {
        return new DecimalFormat(",##0.00").format(priceFen / 100.0);
    }
}
  

package cn.com.bluemoon.delivery.utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

/**
 * ClassName:StringUtils <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:   TODO ADD REASON. <br/>
 * Date:     2015/8/18 11:00:45 <br/>
 *
 * @author allenli
 * @see
 * @since JDK 1.6
 */
public class StringUtil {

    private final static Pattern emailer = Pattern
            .compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");

    private final static Pattern IMG_URL = Pattern
            .compile(".*?(gif|jpeg|png|jpg|bmp)");

    private final static Pattern URL = Pattern
            .compile("^(https|http)://.*?$(net|com|.com.cn|org|me|)");

    private final static ThreadLocal<SimpleDateFormat> dateFormater = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

    private final static ThreadLocal<SimpleDateFormat> dateFormater2 = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };

    /**
     * 判断是否是邮箱
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        if (email == null || email.trim().length() == 0)
            return false;
        return emailer.matcher(email).matches();
    }

    /**
     * 判断是否是图片链接
     *
     * @param url
     * @return
     */
    public static boolean isImgUrl(String url) {
        if (url == null || url.trim().length() == 0)
            return false;
        return IMG_URL.matcher(url).matches();
    }

    /**
     * 判断是否是网址
     *
     * @param str
     * @return
     */
    public static boolean isUrl(String str) {
        if (str == null || str.trim().length() == 0)
            return false;
        return URL.matcher(str).matches();
    }

    public static String getCheckVersionDescription(String desc) {
        StringBuffer strBuff = new StringBuffer();
        String[] descs = desc.split(";");
        for (String description : descs) {
            strBuff.append(description);
            strBuff.append("\n");
        }
        return strBuff.toString();
    }

    public static boolean isEmpty(CharSequence input) {
        if(input != null && !"".equals(input)) {
            for(int i = 0; i < input.length(); ++i) {
                char c = input.charAt(i);
                if(c != 32 && c != 9 && c != 13 && c != 10) {
                    return false;
                }
            }

            return true;
        } else {
            return true;
        }
    }

    public static boolean isEmpty(CharSequence... strs) {
        CharSequence[] var4 = strs;
        int var3 = strs.length;

        for(int var2 = 0; var2 < var3; ++var2) {
            CharSequence str = var4[var2];
            if(isEmpty(str)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isEmptyString(String str) {
        return str == null || "".equals(str.trim()) || "null".equals(str) || "(null)".equals(str);
    }

    public static String formatPrice(String price) {
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(Double.valueOf(price));
    }

    public static String formatPrice(double price) {
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(price);
    }

    public static String formatArea(double area) {
        DecimalFormat df = new DecimalFormat("0.0");
        return df.format(area);
    }

    public static String formatPriceByFen(long price) {
        return String.format("%.2f", (double) price / 100);
    }

    public static String formatPriceByFen(int price) {
        return String.format("%.2f", (double) price / 100);
    }

    public static String formatBoxesNum(double boxes) {
        return String.format("%.1f", boxes);
    }

    /**
     * 转化为int
     *
     * @param str
     * @param defValue
     * @return
     */
    public static int toInt(String str, int defValue) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
        }
        return defValue;
    }

    /**
     * 转化为int
     *
     * @param obj
     * @return
     */
    public static int toInt(Object obj) {
        if (obj == null)
            return 0;
        return toInt(obj.toString(), 0);
    }

    /**
     * 转化为long
     *
     * @param obj
     * @return
     */
    public static long toLong(String obj) {
        try {
            return Long.parseLong(obj);
        } catch (Exception e) {
        }
        return 0;
    }

    /**
     * 转化为boolean
     *
     * @param b
     * @return
     */
    public static boolean toBoolean(String b) {
        try {
            return Boolean.parseBoolean(b);
        } catch (Exception e) {
        }
        return false;
    }

    public static String getString(String s) {
        return s == null ? "" : s;
    }


}
  

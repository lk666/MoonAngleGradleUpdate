package cn.com.bluemoon.delivery.utils;  

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Pattern;

import cn.com.bluemoon.delivery.entity.ItemListBean;

// TODO: lk 2016/6/22 各种乱码
/**  
 * ClassName:StringUtils <br/>  
 * Function: TODO ADD FUNCTION. <br/>  
 * Reason:   TODO ADD REASON. <br/>  
 * Date:     2015/8/18 11:00:45 <br/>
 * @author   allenli  
 * @version    
 * @since    JDK 1.6  
 * @see        
 */
public class StringUtil {
	
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
		return String.format("%.2f",(double)price/100);
	}

	public static String formatPriceByFen(int price) {
		return String.format("%.2f",(double)price/100);
	}

	public static String formatBoxesNum(double boxes) {
          return String.format("%.1f",boxes);
	}

	
	// 2015/10/10 19:00
	public static String toDateString(String sdate) {
		String year = sdate.substring(0, 4);
		String month = sdate.substring(4, 6);
		String day = sdate.substring(6, 8);
		String hour = sdate.substring(8, 10);
		String minute = sdate.substring(10, 12);
        return year+"/"+month+"/"+day+" "+hour+":"+minute;
     }
	
	public static String getResultMessage(ItemListBean result) {
		if (result!=null && result.itemList != null && result.itemList.get(0)!=null) {
			if (result.itemList.get(0).getMessage() == null) {
				return result.itemList.get(0).getDispatchId() + "\n" + result.itemList.get(0).getMsg();
			} else {
				return result.itemList.get(0).getDispatchId() + "\n" + result.itemList.get(0).getMessage();
			}
		} else {
			
			return "error";
		}
	}
	
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
	     * ���ַ���תλ��������
	     * 
	     * @param sdate
	     * @return
	     */
	    public static Date toDate(String sdate) {
	        return toDate(sdate, dateFormater.get());
	    }

	    public static Date toDate(String sdate, SimpleDateFormat dateFormater) {
	        try {
	            return dateFormater.parse(sdate);
	        } catch (ParseException e) {
	            return null;
	        }
	    }

	    public static String getDateString(Date date) {
	        return dateFormater.get().format(date);
	    }

	    /**
	     * ���Ѻõķ�ʽ��ʾʱ��
	     * 
	     * @param sdate
	     * @return
	     */
	    public static String friendly_time(String sdate) {
	        Date time = null;

	        if (TimeZoneUtil.isInEasternEightZones())
	            time = toDate(sdate);
	        else
	            time = TimeZoneUtil.transformTime(toDate(sdate),
	                    TimeZone.getTimeZone("GMT+08"), TimeZone.getDefault());

	        if (time == null) {
	            return "Unknown";
	        }
	        String ftime = "";
	        Calendar cal = Calendar.getInstance();

	        // �ж��Ƿ���ͬһ��
	        String curDate = dateFormater2.get().format(cal.getTime());
	        String paramDate = dateFormater2.get().format(time);
	        if (curDate.equals(paramDate)) {
	            int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
	            if (hour == 0)
	                ftime = Math.max(
	                        (cal.getTimeInMillis() - time.getTime()) / 60000, 1)
	                        + "����ǰ";
	            else
	                ftime = hour + "Сʱǰ";
	            return ftime;
	        }

	        long lt = time.getTime() / 86400000;
	        long ct = cal.getTimeInMillis() / 86400000;
	        int days = (int) (ct - lt);
	        if (days == 0) {
	            int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
	            if (hour == 0)
	                ftime = Math.max(
	                        (cal.getTimeInMillis() - time.getTime()) / 60000, 1)
	                        + "����ǰ";
	            else
	                ftime = hour + "Сʱǰ";
	        } else if (days == 1) {
	            ftime = "����";
	        } else if (days == 2) {
	            ftime = "ǰ�� ";
	        } else if (days > 2 && days < 31) {
	            ftime = days + "��ǰ";
	        } else if (days >= 31 && days <= 2 * 31) {
	            ftime = "һ����ǰ";
	        } else if (days > 2 * 31 && days <= 3 * 31) {
	            ftime = "2����ǰ";
	        } else if (days > 3 * 31 && days <= 4 * 31) {
	            ftime = "3����ǰ";
	        } else {
	            ftime = dateFormater2.get().format(time);
	        }
	        return ftime;
	    }

	    public static String friendly_time2(String sdate) {
	        String res = "";
	        if (isEmpty(sdate))
	            return "";

	        String[] weekDays = { "������", "����һ", "���ڶ�", "������", "������", "������", "������" };
	        String currentData = StringUtil.getDataTime("MM-dd");
	        int currentDay = toInt(currentData.substring(3));
	        int currentMoth = toInt(currentData.substring(0, 2));

	        int sMoth = toInt(sdate.substring(5, 7));
	        int sDay = toInt(sdate.substring(8, 10));
	        int sYear = toInt(sdate.substring(0, 4));
	        Date dt = new Date(sYear, sMoth - 1, sDay - 1);

	        if (sDay == currentDay && sMoth == currentMoth) {
	            res = "���� / " + weekDays[getWeekOfDate(new Date())];
	        } else if (sDay == currentDay + 1 && sMoth == currentMoth) {
	            res = "���� / " + weekDays[(getWeekOfDate(new Date()) + 6) % 7];
	        } else {
	            if (sMoth < 10) {
	                res = "0";
	            }
	            res += sMoth + "/";
	            if (sDay < 10) {
	                res += "0";
	            }
	            res += sDay + " / " + weekDays[getWeekOfDate(dt)];
	        }

	        return res;
	    }

	    /**
	     * ��ȡ��ǰ���������ڼ�<br>
	     * 
	     * @param dt
	     * @return ��ǰ���������ڼ�
	     */
	    public static int getWeekOfDate(Date dt) {
	        Calendar cal = Calendar.getInstance();
	        cal.setTime(dt);
	        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
	        if (w < 0)
	            w = 0;
	        return w;
	    }

	    /**
	     * �жϸ����ַ���ʱ���Ƿ�Ϊ����
	     * 
	     * @param sdate
	     * @return boolean
	     */
	    public static boolean isToday(String sdate) {
	        boolean b = false;
	        Date time = toDate(sdate);
	        Date today = new Date();
	        if (time != null) {
	            String nowDate = dateFormater2.get().format(today);
	            String timeDate = dateFormater2.get().format(time);
	            if (nowDate.equals(timeDate)) {
	                b = true;
	            }
	        }
	        return b;
	    }

	    /**
	     * ����long���͵Ľ��������
	     * 
	     * @return
	     */
	    public static long getToday() {
	        Calendar cal = Calendar.getInstance();
	        String curDate = dateFormater2.get().format(cal.getTime());
	        curDate = curDate.replace("-", "");
	        return Long.parseLong(curDate);
	    }

	    public static String getCurTimeStr() {
	        Calendar cal = Calendar.getInstance();
	        String curDate = dateFormater.get().format(cal.getTime());
	        return curDate;
	    }

	    /***
	     * ��������ʱ�����ص��ǵ���s
	     * 
	     * @author ���� 2015-2-9 ����4:50:06
	     * 
	     * @return long
	     * @param dete1
	     * @param date2
	     * @return
	     */
	    public static long calDateDifferent(String dete1, String date2) {

	        long diff = 0;

	        Date d1 = null;
	        Date d2 = null;

	        try {
	            d1 = dateFormater.get().parse(dete1);
	            d2 = dateFormater.get().parse(date2);

	            // ����ms
	            diff = d2.getTime() - d1.getTime();

	        } catch (Exception e) {
	            e.printStackTrace();
	        }

	        return diff / 1000;
	    }

	    /**
	     * �жϸ����ַ����Ƿ�հ״��� �հ״���ָ�ɿո��Ʊ�����س��������з���ɵ��ַ��� �������ַ���Ϊnull����ַ���������true
	     * 
	     * @param input
	     * @return boolean
	     */
	    public static boolean isEmpty(String input) {
	        if (input == null || "".equals(input))
	            return true;

	        for (int i = 0; i < input.length(); i++) {
	            char c = input.charAt(i);
	            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
	                return false;
	            }
	        }
	        return true;
	    }

	    /**
	     * �ж��ǲ���һ���Ϸ��ĵ����ʼ���ַ
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
	     * �ж�һ��url�Ƿ�ΪͼƬurl
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
	     * �ж��Ƿ�Ϊһ���Ϸ���url��ַ
	     * 
	     * @param str
	     * @return
	     */
	    public static boolean isUrl(String str) {
	        if (str == null || str.trim().length() == 0)
	            return false;
	        return URL.matcher(str).matches();
	    }

	    /**
	     * �ַ���ת����
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
	     * ����ת����
	     * 
	     * @param obj
	     * @return ת���쳣���� 0
	     */
	    public static int toInt(Object obj) {
	        if (obj == null)
	            return 0;
	        return toInt(obj.toString(), 0);
	    }

	    /**
	     * ����ת����
	     * 
	     * @param obj
	     * @return ת���쳣���� 0
	     */
	    public static long toLong(String obj) {
	        try {
	            return Long.parseLong(obj);
	        } catch (Exception e) {
	        }
	        return 0;
	    }

	    /**
	     * �ַ���ת����ֵ
	     * 
	     * @param b
	     * @return ת���쳣���� false
	     */
	    public static boolean toBool(String b) {
	        try {
	            return Boolean.parseBoolean(b);
	        } catch (Exception e) {
	        }
	        return false;
	    }

	    public static String getString(String s) {
	        return s == null ? "" : s;
	    }

	    /**
	     * ��һ��InputStream��ת�����ַ���
	     * 
	     * @param is
	     * @return
	     */
	    public static String toConvertString(InputStream is) {
	        StringBuffer res = new StringBuffer();
	        InputStreamReader isr = new InputStreamReader(is);
	        BufferedReader read = new BufferedReader(isr);
	        try {
	            String line;
	            line = read.readLine();
	            while (line != null) {
	                res.append(line + "<br>");
	                line = read.readLine();
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            try {
	                if (null != isr) {
	                    isr.close();
	                    isr.close();
	                }
	                if (null != read) {
	                    read.close();
	                    read = null;
	                }
	                if (null != is) {
	                    is.close();
	                    is = null;
	                }
	            } catch (IOException e) {
	            }
	        }
	        return res.toString();
	    }

	    /***
	     * ��ȡ�ַ���
	     * 
	     * @param start
	     *            �����￪ʼ��0����
	     * @param num
	     *            ��ȡ���ٸ�
	     * @param str
	     *            ��ȡ���ַ���
	     * @return
	     */
	    public static String getSubString(int start, int num, String str) {
	        if (str == null) {
	            return "";
	        }
	        int leng = str.length();
	        if (start < 0) {
	            start = 0;
	        }
	        if (start > leng) {
	            start = leng;
	        }
	        if (num < 0) {
	            num = 1;
	        }
	        int end = start + num;
	        if (end > leng) {
	            end = leng;
	        }
	        return str.substring(start, end);
	    }

	    /**
	     * ��ȡ��ǰʱ��Ϊÿ��ڼ���
	     * 
	     * @return
	     */
	    public static int getWeekOfYear() {
	        return getWeekOfYear(new Date());
	    }

	    /**
	     * ��ȡ��ǰʱ��Ϊÿ��ڼ���
	     * 
	     * @param date
	     * @return
	     */
	    public static int getWeekOfYear(Date date) {
	        Calendar c = Calendar.getInstance();
	        c.setFirstDayOfWeek(Calendar.MONDAY);
	        c.setTime(date);
	        int week = c.get(Calendar.WEEK_OF_YEAR) - 1;
	        week = week == 0 ? 52 : week;
	        return week > 0 ? week : 1;
	    }

	    public static int[] getCurrentDate() {
	        int[] dateBundle = new int[3];
	        String[] temp = getDataTime("yyyy-MM-dd").split("-");

	        for (int i = 0; i < 3; i++) {
	            try {
	                dateBundle[i] = Integer.parseInt(temp[i]);
	            } catch (Exception e) {
	                dateBundle[i] = 0;
	            }
	        }
	        return dateBundle;
	    }

	    /**
	     * ���ص�ǰϵͳʱ��
	     */
	    public static String getDataTime(String format) {
	        SimpleDateFormat df = new SimpleDateFormat(format);
	        return df.format(new Date());
	    }

}
  

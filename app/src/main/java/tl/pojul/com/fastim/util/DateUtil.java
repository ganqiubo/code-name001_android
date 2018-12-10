package tl.pojul.com.fastim.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    public static String getConversationDate(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date1 = format.parse(date);
            Date date2 = format.parse(getFormatDate());
            int days = (int) ((date1.getTime() - date2.getTime()) / (1000 * 3600 * 24));
            if (days <= 0) {
                return date.split(" ")[1];
            } else if (days == 1) {
                return "昨天";
            } else if (days == 2) {
                return "前天";
            } else {
                return (days + "天前");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getFormatDate() {
        Date ss = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(ss.getTime());
    }

    public static String getFormatRoughDate() {
        Date ss = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(ss.getTime());
    }

    public static String transformToRoughDate(String date) {
        try {
            return date.split(" ")[0];
        } catch (Exception e) {
            return "";
        }
    }

    public static boolean isDiffDay(String date1, String date2) {
        try {
            String roughDate1 = date1.split(" ")[0];
            String roughDate2 = date2.split(" ")[0];
            if (!roughDate1.equals(roughDate2)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public static String getHeadway(long timeMilli) {
        long dsTimeMilli = System.currentTimeMillis() - timeMilli;
        long l;
        if (dsTimeMilli <= 20 * 1000) {
            return "刚刚";
        } else if (dsTimeMilli <= 60 * 1000) {
            return "1分钟前";
        } else if (dsTimeMilli <= 30 * 60 * 1000) {
            int mins = (int) (dsTimeMilli / (60 * 1000)) + 1;
            return (mins + "分钟前");
        } else if (dsTimeMilli <= 24 * 60 * 60 * 1000) {
            int hours = (int) (dsTimeMilli / (60 * 60 * 1000)) + 1;
            return (hours + "小时前");
        } else if (dsTimeMilli <= (31L * 24L * 60L * 60L * 1000L)) {
            int days = (int) (dsTimeMilli / (24 * 60 * 60 * 1000L)) + 1;
            return (days + "天前");
        } else if (dsTimeMilli <= (12L * 31L * 24L * 60L * 60L * 1000L)) {
            int months = (int) (dsTimeMilli / (31L * 24L * 60L * 60L * 1000L)) + 1;
            return (months + "个月前");
        } else if (dsTimeMilli <= (1000L * 12L * 31L * 24L * 60L * 60L * 1000L)) {
            int years = (int) (dsTimeMilli / (12L * 31L * 24L * 60L * 60L * 1000L)) + 1;
            return (years + "年前");
        } else {
            return ("千年以前");
        }
    }

    /**
     * 转换时间日期格式字串为long型
     *
     * @param time 格式为：yyyy-MM-dd HH:mm:ss的时间日期类型
     */
    public static Long convertTimeToLong(String time) {
        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            date = sdf.parse(time);
            return date.getTime();
        } catch (Exception e) {
            return System.currentTimeMillis();
        }
    }

    /**
     * 比较两个日期的大小，日期格式为yyyy-MM-dd
     *
     * @param date
     * @return true <br/>false
     */
    public static boolean isDateOverdue(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dt1 = null;
        Date dt2 = null;
        try {
            dt1 = sdf.parse(date);
            dt2 = sdf.parse(getFormatDate());
        } catch (Exception e) {
            return true;
        }
        if (dt1.getTime() <= dt2.getTime()) {
            return true;
        } else {
            return false;
        }
    }

    public static String getLeftTime(long time){
        long dsTimeMilli = time - System.currentTimeMillis();
        if(dsTimeMilli <= 0){
            return "0天0小时";
        }
        int leftDays = (int) (dsTimeMilli / (24 * 60 * 60 * 1000L));
        int leftHours = (int) ((dsTimeMilli % (24 * 60 * 60 * 1000L)) / (60 * 60 * 1000)) + 1;
        if(leftDays <= 0){
            return leftHours + "小时";
        }else{
            return leftDays + "天" + leftHours + "小时";
        }
    }

}

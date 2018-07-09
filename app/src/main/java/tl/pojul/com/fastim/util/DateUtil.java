package tl.pojul.com.fastim.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    public static String getConversationDate(String date){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date1 = format.parse(date);
            Date date2 = format.parse(getFormatDate());
            int days = (int) ((date1.getTime() - date2.getTime()) / (1000*3600*24));
            if(days <= 0){
                return date.split(" ")[1];
            }else if(days == 1){
                return "昨天";
            }else if(days == 2){
                return "前天";
            }else{
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
        try{
            return date.split(" ")[0];
        }catch (Exception e){return "";}
    }

    public static boolean isDiffDay(String date1, String date2){
        try{
            String roughDate1 = date1.split(" ")[0];
            String roughDate2 = date2.split(" ")[0];
            if(!roughDate1.equals(roughDate2)){
                return true;
            }else{
                return false;
            }
        }catch (Exception e){return  false;}
    }

}

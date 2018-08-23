package tl.pojul.com.fastim.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumberUtil {

    public static String getNumStr(int num){
        float numFloat;
        if(num < 1000){
            return (num + "");
        }else if(num < 10000){
            numFloat = (float) (num * 0.001);
            return (new BigDecimal((numFloat + "")).setScale(1, RoundingMode.HALF_UP).toString() + "千");
        }else{
            numFloat = (float) (num * 0.0001);
            return (new BigDecimal((numFloat + "")).setScale(1, RoundingMode.HALF_UP).toString() + "万");
        }
    }

}

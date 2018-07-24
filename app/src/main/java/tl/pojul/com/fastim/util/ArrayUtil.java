package tl.pojul.com.fastim.util;

import java.util.List;

public class ArrayUtil {

    public static int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    public static String toCommaSplitStr(List<String> strs){
        StringBuilder sb = new StringBuilder();
        sb.append("");
        for(int i = 0; i < strs.size(); i++){
            if(i > 0){
                sb.append(",");
            }
            sb.append(strs.get(i));
        }
        return sb.toString();
    }

}

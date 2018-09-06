package tl.pojul.com.fastim.util;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomUtil {

    public static int getRandomRange(int min, int max){
        int random = min + ((int) (new Random().nextFloat() * (max - min)));
        return random;
    }

    /**
     * @param raws
     * @param size
     * */
    public static List<Integer> randomArrays(List<Integer> raws, int size){
        List<Integer> randoms = new ArrayList<>();
        if(raws.size() <= 0 || size <= 0){
            return randoms;
        }
        if(raws.size() >= size){
            return  raws.subList(0, raws.size());
        }
        for (int i = 0; i < size; i++) {
            int posi = i % raws.size();
            randoms.add(raws.get(posi));
        }
        return randoms;
    }

}

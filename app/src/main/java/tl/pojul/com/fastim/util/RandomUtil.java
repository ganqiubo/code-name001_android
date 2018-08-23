package tl.pojul.com.fastim.util;

import java.util.Random;

public class RandomUtil {

    public static int getRandomRange(int min, int max){
        int random = min + ((int) (new Random().nextFloat() * (max - min)));
        return random;
    }

}

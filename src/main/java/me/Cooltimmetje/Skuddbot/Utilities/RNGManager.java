package me.Cooltimmetje.Skuddbot.Utilities;

import java.util.Random;

/**
 * Managing class for RNG.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.0
 * @since 2.0
 */
public class RNGManager {

    private Random random;

    public RNGManager(){
        random = new Random();
    }

    public RNGManager(long seed){
        random = new Random(seed);
    }

    public int integer(int min, int max) {
        return random.nextInt((max - min) + 1) + min;
    }


}

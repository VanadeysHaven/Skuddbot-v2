package me.Cooltimmetje.Skuddbot.Utilities;

/**
 * Some useful utilities I can use throughout the code.
 *
 * @author Tim (Cooltimmetje)
 * @since ALPHA-2.0
 * @version ALPHA-2.0
 */
public class MiscUtils {

    public static boolean isInt(String str){
        try {
            int num = Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}

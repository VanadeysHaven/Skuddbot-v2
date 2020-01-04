package me.Cooltimmetje.Skuddbot.Utilities;

import java.util.Random;

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

    public static boolean isBoolean(String str){
        return str.equals("true") || str.equals("false");
    }

    public static boolean isDouble(String str) {
        try {
            double num = Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e){
            return false;
        }
    }

    public static boolean isLong(String str) {
        try{
            long num = Long.parseLong(str);
            return true;
        } catch (NumberFormatException e){
            return false;
        }
    }

    public static String randomString(int len){
        final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random rnd = new Random(); //TODO: Random manager

        StringBuilder sb = new StringBuilder( len );
        for( int i = 0; i < len; i++ )
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }
}

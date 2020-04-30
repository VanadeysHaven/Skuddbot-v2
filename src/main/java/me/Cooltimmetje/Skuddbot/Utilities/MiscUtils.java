package me.Cooltimmetje.Skuddbot.Utilities;

import java.util.Random;

/**
 * Some useful utilities I can use throughout the code.
 *
 * @author Tim (Cooltimmetje)
 * @since ALPHA-2.2
 * @version ALPHA-2.0
 */
public class MiscUtils {

    private static Random rnd = new Random(); //TODO: Random manager

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

        StringBuilder sb = new StringBuilder( len );
        for( int i = 0; i < len; i++ )
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

    public static String flipText(String input){
        String normal = "abcdefghijklmnopqrstuvwxyz_,;.?!'()[]{}";
        String split  = "ɐqɔpǝɟbɥıɾʞlɯuodbɹsʇnʌʍxʎz‾'؛˙¿¡,)(][}{";
//maj
        normal += "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        split  += "∀qϽᗡƎℲƃHIſʞ˥WNOԀὉᴚS⊥∩ΛMXʎZ";
//number
        normal += "0123456789";
        split  += "0ƖᄅƐㄣϛ9ㄥ86";

        char letter;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i< input.length(); i++) {
            letter = input.charAt(i);

            int a = normal.indexOf(letter);
            sb.append((a != -1) ? split.charAt(a) : letter);
        }
        return sb.reverse().toString();
    }

    public static String stripEveryone(String input){
        return input.replace("@everyone", "@\u200Beveryone").replace("@here", "@\u200Bhere");
    }

    public static String enumify(String input){
        return input.toUpperCase().replace("-", "_");
    }

    public static String unEnumify(String input){
        return input.toLowerCase().replace("_", "-");
    }

}

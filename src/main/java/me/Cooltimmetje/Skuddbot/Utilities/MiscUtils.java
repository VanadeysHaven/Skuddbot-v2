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

    public static String glueStrings(String prefix, String glue, String lastGlue, String suffix, int limit, String otherString, String[] strings){
        if(strings.length == 0)
            return prefix + suffix;
        if(strings.length == 1)
            return prefix + strings[0] + suffix;

        StringBuilder sb = new StringBuilder();

        for(int i=0; i < strings.length && i < limit; i++)
            if(i == strings.length-1)
                sb.append(lastGlue).append(strings[i]);
            else
                sb.append(glue).append(strings[i]);

        if(limit < strings.length)
            if(strings.length - limit == 1)
                sb.append(lastGlue).append(strings[strings.length - 1]);
            else
                sb.append(lastGlue).append(strings.length - limit).append(" ").append(otherString);

            return prefix + sb.toString().substring(glue.length()) + suffix;
    }

}

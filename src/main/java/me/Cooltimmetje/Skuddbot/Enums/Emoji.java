package me.Cooltimmetje.Skuddbot.Enums;

import lombok.Getter;

/**
 * Emoji's for easy recalling throughout the code.
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.1
 * @since ALPHA-2.0
 */
@Getter
public enum Emoji {

    WHITE_CHECK_MARK        ("✅", "white_check_mark"),
    ARROW_UP                ("⬆", "arrow_up"),
    WARNING                 ("⚠", "warning"),
    X                       ("❌", "x"),
    HOURGLASS_FLOWING_SAND  ("⏳", "hourglass_flowing_sand"),
    CROSSED_SWORDS          ("⚔", "crossed_swords"),
    EYES                    ("\uD83D\uDC40", "eyes"),
    MAILBOX_WITH_MAIL       ("\uD83D\uDCEC", "mailbox_with_mail"),
    NOTEPAD_SPIRAL          ("\uD83D\uDDD2", "spiral_note_pad"),
    SPADES                  ("♠", "spades"),
    CLUBS                   ("♣", "clubs"),
    DIAMONDS                ("♦", "diamonds"),
    HEARTS                  ("♥", "hearts"),
    QUESTION                ("❓", "question"),
    TWO                     ("2⃣", "two"),
    THREE                   ("3⃣", "three"),
    FOUR                    ("4⃣", "four"),
    FIVE                    ("5⃣", "five"),
    SIX                     ("6⃣", "six"),
    SEVEN                   ("7⃣", "seven"),
    EIGHT                   ("8⃣", "eight"),
    NINE                    ("9⃣", "nine"),
    TEN                     ("\uD83D\uDD1F", "keycap_ten"),
    A                       ("\uD83C\uDDE6", "regional_indicator_symbol_a"),
    D                       ("\uD83C\uDDE9", "regional_indicator_symbol_d"),
    H                       ("\uD83C\uDDED", "regional_indicator_symbol_h"),
    J                       ("\uD83C\uDDEF", "regional_indicator_symbol_j"),
    K                       ("\uD83C\uDDF0", "regional_indicator_symbol_k"),
    Q                       ("\uD83C\uDDF6", "regional_indicator_symbol_q"),
    S                       ("\uD83C\uDDF8", "regional_indicator_symbol_s"),
    BACON                   ("\uD83E\uDD53", "bacon"),
    DOG                     ("\uD83D\uDC36", "dog"),
    CAT                     ("\uD83D\uDC31", "cat"),
    CAKE                    ("\uD83C\uDF70", "cake"),
    HEART                   ("❤️", "heart"),
    BLACK_JOKER             ("\uD83C\uDCCF", "black_joker"),
    SOON                    ("\uD83D\uDD1C", "soon"),
    MONEYBAG                ("\uD83D\uDCB0", "moneybag");

    private String unicode;
    private String alias;

    Emoji(String unicode, String alias){
        this.unicode = unicode;
        this.alias = alias;
    }

    public String getString(){
        return ":" + alias + ":";
    }

    public static Emoji getByUnicode(String unicodeEmoji){
        for(Emoji emoji : Emoji.values()){
            if(emoji.getUnicode().equals(unicodeEmoji)){
                return emoji;
            }
        }
        return null;
    }

}
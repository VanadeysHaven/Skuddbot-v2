package me.VanadeysHaven.Skuddbot.Enums;

import lombok.Getter;

/**
 * Emoji's for easy recalling throughout the code.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.3.1
 * @since 2.0
 */
@Getter
public enum Emoji {

    WHITE_CHECK_MARK        ("✅", "white_check_mark"),
    ARROW_UP                ("⬆", "arrow_up"),
    ARROW_LEFT              ("⬅️", "arrow_left"),
    ARROW_RIGHT             ("➡️", "arrow_right"),
    ARROWS_CC               ("\uD83D\uDD04", "arrows_counterclockwise"),
    ARROWS_LR               ("↔️", "left_right_arrow"),
    ARROW_LEFT_HOOK         ("↩️", "leftwards_arrow_with_hook"),
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
    B                       ("\uD83C\uDDE7", "regional_indicator_symbol_b"),
    D                       ("\uD83C\uDDE9", "regional_indicator_symbol_d"),
    G                       ("\uD83C\uDDEC", "regional_indicator_symbol_g"),
    H                       ("\uD83C\uDDED", "regional_indicator_symbol_h"),
    J                       ("\uD83C\uDDEF", "regional_indicator_symbol_j"),
    K                       ("\uD83C\uDDF0", "regional_indicator_symbol_k"),
    P                       ("\uD83C\uDDF5", "regional_indicator_symbol_p"),
    Q                       ("\uD83C\uDDF6", "regional_indicator_symbol_q"),
    S                       ("\uD83C\uDDF8", "regional_indicator_symbol_s"),
    BACON                   ("\uD83E\uDD53", "bacon"),
    DOG                     ("\uD83D\uDC36", "dog"),
    CAT                     ("\uD83D\uDC31", "cat"),
    CAKE                    ("\uD83C\uDF70", "cake"),
    HEART                   ("❤️", "heart"),
    BLACK_JOKER             ("\uD83C\uDCCF", "black_joker"),
    SOON                    ("\uD83D\uDD1C", "soon"),
    MONEYBAG                ("\uD83D\uDCB0", "moneybag"),
    COIN                    ("\uD83E\uDE99", "coin"),
    WASTEBASKET             ("🗑️", "wastebasket"),
    GAME_DIE                ("\uD83C\uDFB2", "game_die"),
    GIFT                    ("\uD83C\uDF81", "gift"),
    DAGGER                  ("\uD83D\uDDE1", "dagger"),
    PRINTER                 ("\uD83D\uDDA8", "printer"),
    HAMSTER                 ("\uD83D\uDC39", "hamster"),
    RABBIT                  ("\uD83D\uDC30", "rabbit"),
    SNAKE                   ("\uD83D\uDC0D", "snake"),
    SEAL                    ("\uD83E\uDDAD", "seal"),
    OWL                     ("\uD83E\uDD89", "owl"),
    BAT                     ("\uD83E\uDD87", "bat"),
    PANDA                   ("\uD83D\uDC3C", "panda_face"),
    FLAG_NL                 ("\uD83C\uDDF3\uD83C\uDDF1", "flag_nl"),
    CROWN                   ("\uD83D\uDC51", "crown"),
    OTTER                   ("\uD83E\uDDA6", "otter");

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
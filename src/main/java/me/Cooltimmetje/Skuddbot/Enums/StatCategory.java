package me.Cooltimmetje.Skuddbot.Enums;

import lombok.Getter;

/**
 * <class discription>
 *
 * @author Tim (Cooltimmetje)
 * @since ALPHA-2.0
 * @version ALPHA-2.0
 */
@Getter
public enum StatCategory {

    NO_CATEGORY     (""               ),
    CHALLENGE       ("Challenge"      ),
    FREE_FOR_ALL    ("Free For All"   ),
    BLACKJACK       ("Blackjack"      ),
    TEAM_DEATHMATCH ("Team Deathmatch");

    private String name;

    StatCategory(String name){
        this.name = name;
    }


}

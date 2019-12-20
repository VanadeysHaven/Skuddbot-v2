package me.Cooltimmetje.Skuddbot.Enums.UserStats;

import lombok.Getter;

/**
 * The categories for stats.
 *
 * @author Tim (Cooltimmetje)
 * @since ALPHA-2.0
 * @version ALPHA-2.0
 */
@Getter
public enum UserStatCategory {

    NO_CATEGORY     (""               ),
    CHALLENGE       ("Challenge"      ),
    FREE_FOR_ALL    ("Free For All"   ),
    BLACKJACK       ("Blackjack"      ),
    TEAM_DEATHMATCH ("Team Deathmatch");

    private String name;

    UserStatCategory(String name){
        this.name = name;
    }


}

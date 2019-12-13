package me.Cooltimmetje.Skuddbot.Profiles;

import lombok.Getter;

/**
 * This class represents a user and their data and statistics.
 *
 * @author Tim (Cooltimmetje)
 * @since ALPHA-2.0
 * @version ALPHA-2.0
 */
public class SkuddUser {

    @Getter private Identifier id;

    public SkuddUser(Identifier id){
        this.id = id;
    }
}

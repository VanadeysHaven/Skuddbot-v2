package me.Cooltimmetje.Skuddbot.Profiles.Users;

import lombok.Getter;
import lombok.Setter;

/**
 * This class represents a identifier for a user. It combines multiple identifiers into one.
 *
 * @author Tim (Cooltimmetje)
 * @since ALPHA-2.0
 * @version ALPHA-2.0
 */
public class Identifier {

    @Getter private int id;
    @Getter private long serverId;
    @Getter @Setter private long userId;
    @Getter @Setter private String twitchUsername;

    public Identifier(int id, long serverId, long userId){
        this.id = id;
        this.serverId = serverId;
        this.userId = userId;
    }

    public Identifier(int id, long serverId, String twitchUsername){
        this.id = id;
        this.serverId = serverId;
        this.twitchUsername = twitchUsername;
    }

    public Identifier(int id, long serverId, long userId, String twitchUsername){
        this.id = id;
        this.serverId = serverId;
        this.userId = userId;
        this.twitchUsername = twitchUsername;
    }

}

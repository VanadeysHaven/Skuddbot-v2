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

    @Getter private long serverId;
    @Getter @Setter private long userId;
    @Getter @Setter private String twitchUsername;

    public Identifier(long serverId, long userId){
        this.serverId = serverId;
        this.userId = userId;
        twitchUsername = null;
    }

    public Identifier(long serverId, String twitchUsername){
        this.serverId = serverId;
        this.twitchUsername = twitchUsername;
        userId = -1;
    }

    public Identifier(long serverId, long userId, String twitchUsername){
        this.serverId = serverId;
        this.userId = userId;
        this.twitchUsername = twitchUsername;
    }

    @Override
    public boolean equals(Object obj){
        if(!(obj instanceof Identifier)) return false;
        Identifier id = (Identifier) obj;
        if(serverId != id.getServerId()) return false;
        if(userId != -1 && id.getUserId() != -1) if(userId != id.getUserId()) return false;
        if(twitchUsername != null && id.getTwitchUsername() != null) return twitchUsername.equals(id.getTwitchUsername());

        return true;
    }

}

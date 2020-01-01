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
    @Getter @Setter private long discordId;
    @Getter @Setter private String twitchUsername;

    public Identifier(long serverId, long discordId){
        this.serverId = serverId;
        this.discordId = discordId;
        twitchUsername = null;
    }

    public Identifier(long serverId, String twitchUsername){
        this.serverId = serverId;
        this.twitchUsername = twitchUsername;
        discordId = -1;
    }

    public Identifier(long serverId, long discordId, String twitchUsername){
        this.serverId = serverId;
        this.discordId = discordId;
        this.twitchUsername = twitchUsername;
    }

    @Override
    public boolean equals(Object obj){
        if(!(obj instanceof Identifier)) return false;
        Identifier id = (Identifier) obj;
        if(serverId != id.getServerId()) return false;
        if(discordId != -1 && id.getDiscordId() != -1) if(discordId != id.getDiscordId()) return false;
        if(twitchUsername != null && id.getTwitchUsername() != null) return twitchUsername.equals(id.getTwitchUsername());

        return true;
    }

}

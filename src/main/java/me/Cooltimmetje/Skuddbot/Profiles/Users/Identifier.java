package me.Cooltimmetje.Skuddbot.Profiles.Users;

import lombok.Getter;
import lombok.Setter;
import me.Cooltimmetje.Skuddbot.Database.QueryExecutor;
import me.Cooltimmetje.Skuddbot.Enums.Query;

import java.sql.ResultSet;
import java.sql.SQLException;

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

    public int getId(){
        QueryExecutor qe = null;
        int id = -1;
        try{
            qe = new QueryExecutor(Query.SELECT_USER_ID).setLong(1, serverId).setLong(2, discordId);
            ResultSet rs = qe.executeQuery();
            while(rs.next()){
                id = rs.getInt(1);
            }
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            assert qe != null;
            qe.close();
        }

        return id;
    }

    public void save(){
        int userId = getId();
        boolean exists = userId == -1;

        if(exists) return;
        QueryExecutor qe = null;
        try {
            qe = new QueryExecutor(Query.SAVE_USER_ID).setLong(1, serverId).setLong(2, discordId);
            qe.execute();
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            assert qe != null;
            qe.close();
        }
    }

}

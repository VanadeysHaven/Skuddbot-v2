package me.VanadeysHaven.Skuddbot.Profiles.Users;

import lombok.Getter;
import me.VanadeysHaven.Skuddbot.Main;
import me.VanadeysHaven.Skuddbot.Profiles.ProfileManager;
import me.VanadeysHaven.Skuddbot.Utilities.UserUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

/**
 * Represents a member of a server.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.4
 * @since 2.2
 */
public class ServerMember {

    @Getter private Identifier id;
    private Guild server;
    private User user;

    public ServerMember(Identifier id){
        this.id = id;
    }

    public ServerMember(long serverId, long userId){
        this(new Identifier(serverId, userId));
    }

    public Guild getServer() {
        if(server == null)
            server = Main.getSkuddbot().getApi().getGuildById(id.getServerId());

        return server;
    }

    public User getUser(){
        if (user == null)
            user = Main.getSkuddbot().getApi().retrieveUserById(id.getDiscordId()).complete();

        return user;
    }

    public String getDisplayName(){
        return UserUtils.getDisplayName(getServer(), getUser());
    }

    public String getGameLogName(){
        return "$" + id.getServerId() + "-" + id.getDiscordId();
    }

    public SkuddUser asSkuddUser(){
        return ProfileManager.getInstance().getUser(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServerMember member = (ServerMember) o;
        return id.equals(member.getId());
    }

}

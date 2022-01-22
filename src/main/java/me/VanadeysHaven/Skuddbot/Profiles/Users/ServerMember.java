package me.VanadeysHaven.Skuddbot.Profiles.Users;

import lombok.Getter;
import me.VanadeysHaven.Skuddbot.Main;
import me.VanadeysHaven.Skuddbot.Profiles.ProfileManager;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

/**
 * Represents a member of a server.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.2.1
 * @since 2.2
 */
public class ServerMember {

    @Getter private Identifier id;
    private Server server;
    private User user;

    public ServerMember(Identifier id){
        this.id = id;
    }

    public ServerMember(long serverId, long userId){
        this(new Identifier(serverId, userId));
    }

    public Server getServer() {
        if(server == null)
            server = Main.getSkuddbot().getApi().getServerById(id.getServerId()).orElse(null);

        return server;
    }

    public User getUser(){
        if (user == null)
            user = Main.getSkuddbot().getApi().getUserById(id.getDiscordId()).join();

        return user;
    }

    public String getDisplayName(){
        return getUser().getDisplayName(getServer());
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

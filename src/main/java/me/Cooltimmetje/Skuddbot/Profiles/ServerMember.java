package me.Cooltimmetje.Skuddbot.Profiles;

import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Identifier;
import me.Cooltimmetje.Skuddbot.Profiles.Users.SkuddUser;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

/**
 * Represents a member of a server.
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.2
 * @since ALPHA-2.2
 */
public class ServerMember {

    private Identifier id;
    private Server server;
    private User user;

    public ServerMember(Identifier id){
        this.id = id;
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

    public SkuddUser asSkuddUser(){
        return ProfileManager.getInstance().getUser(id);
    }
}

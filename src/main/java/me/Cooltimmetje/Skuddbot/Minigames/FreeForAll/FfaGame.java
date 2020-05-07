package me.Cooltimmetje.Skuddbot.Minigames.FreeForAll;

import me.Cooltimmetje.Skuddbot.Profiles.ServerMember;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;

import java.util.ArrayList;

/**
 * Represents a game of Free for All
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.2
 * @since ALPHA-2.2
 */
public class FfaGame {

    private ArrayList<FfaPlayer> entrants;
    private TextChannel channel;
    private ServerMember host;
    private Message message;


    public FfaGame(TextChannel channel, ServerMember host){
        entrants = new ArrayList<>();
        this.channel = channel;
        this.host = host;
    }

    public void enterGame(ServerMember member){
        enterGame(member, 0);
    }

    public void enterGame(ServerMember member, int bet){
        entrants.add(new FfaPlayer(member, bet));
    }

}

package me.Cooltimmetje.Skuddbot.Minigames.Blackjack;

import me.Cooltimmetje.Skuddbot.Commands.Managers.Command;
import me.Cooltimmetje.Skuddbot.Enums.Emoji;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Identifier;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;

import java.util.ArrayList;

/**
 * Command used for invoking the blackjack game.
 *
 * @author Tim (Cooltimmetje)
 * @version 2.2.1
 * @since 2.0
 */
public class BlackjackCommand extends Command {

    private static ArrayList<BlackjackGameManager> managers;

    public BlackjackCommand() {
        super(new String[]{"blackjack", "bj", "21", "deal"}, "Play a game of blackjack against the dealer.", Location.SERVER);
        managers = new ArrayList<>();
    }
    
    @Override
    public void run(Message message, String content) {
        Server server = message.getServer().orElse(null); assert server != null;
        BlackjackGameManager manager = getManager(server.getId());
        Identifier id = new Identifier(server.getId(), message.getAuthor().getId());

        if(manager.isOnCooldown(id)){
            MessagesUtils.addReaction(message, Emoji.HOURGLASS_FLOWING_SAND, "You are currently on cooldown, to prevent gambling addictions, you must wait 1 minute between games.");
            return;
        }
        if(manager.hasGameActive(id)){
            MessagesUtils.addReaction(message, Emoji.X, "You already have a game of Blackjack in progress.");
            return;
        }

        manager.createGame(id, message.getChannel());
    }

    private static BlackjackGameManager getManager(long serverId){
        for(BlackjackGameManager manager : managers)
            if(manager.getServerId() == serverId) return manager;

        BlackjackGameManager newManager = new BlackjackGameManager(serverId);
        managers.add(newManager);
        return newManager;
    }

    public static void cleanUp(Identifier id){
        getManager(id.getServerId()).cleanUp(id);
    }

}

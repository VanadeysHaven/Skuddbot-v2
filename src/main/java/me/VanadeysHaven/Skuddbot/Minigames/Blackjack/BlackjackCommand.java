package me.VanadeysHaven.Skuddbot.Minigames.Blackjack;

import me.VanadeysHaven.Skuddbot.Commands.Managers.Command;
import me.VanadeysHaven.Skuddbot.Enums.Emoji;
import me.VanadeysHaven.Skuddbot.Exceptions.InsufficientBalanceException;
import me.VanadeysHaven.Skuddbot.Exceptions.InvalidBetException;
import me.VanadeysHaven.Skuddbot.Profiles.Users.Currencies.Cashier;
import me.VanadeysHaven.Skuddbot.Profiles.Users.Currencies.Currency;
import me.VanadeysHaven.Skuddbot.Profiles.Users.Identifier;
import me.VanadeysHaven.Skuddbot.Profiles.Users.SkuddUser;
import me.VanadeysHaven.Skuddbot.Utilities.MessagesUtils;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;

import java.util.ArrayList;

/**
 * Command used for invoking the blackjack game.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.3
 * @since 2.0
 */
public class BlackjackCommand extends Command {

    private static ArrayList<BlackjackGameManager> managers = new ArrayList<>();

    public BlackjackCommand() {
        super(new String[]{"blackjack", "bj", "21", "deal"}, "Play a game of blackjack against the dealer.", "https://wiki.skuddbot.xyz/minigames/blackjack", Location.SERVER);
    }

    @Override
    public void run(Message message, String content) {
        String[] args = content.split(" ");
        Server server = message.getServer().orElse(null); assert server != null;
        BlackjackGameManager manager = getManager(server.getId());
        Identifier id = new Identifier(server.getId(), message.getAuthor().getId());
        SkuddUser su = pm.getUser(id);

        if(manager.isOnCooldown(id)){
            MessagesUtils.addReaction(message, Emoji.HOURGLASS_FLOWING_SAND, "You are currently on cooldown, to prevent gambling addictions you must wait 1 minute between games.");
            return;
        }
        if(manager.hasGameActive(id)){
            MessagesUtils.addReaction(message, Emoji.X, "You already have a game of Blackjack in progress.");
            return;
        }

        Cashier cashier = su.getCurrencies().getCashier(Currency.SKUDDBUX);
        int bet = -1;
        try {
            if (args.length > 1) {
                bet = cashier.placeBet(args[1]);
            } else {
                bet = cashier.placeBet("");
            }
        } catch (InvalidBetException | InsufficientBalanceException e) {
            MessagesUtils.addReaction(message, Emoji.X, e.getMessage());
        }

        if(bet <= 0){
            MessagesUtils.addReaction(message, Emoji.X, "You must place a bet.");
            return;
        }

        manager.startNewGame(su.asMember(), bet, message.getChannel());
    }

    private BlackjackGameManager getManager(long serverId){
        for(BlackjackGameManager manager : managers)
            if(manager.getServerId() == serverId)
                return manager;

        BlackjackGameManager newManager = new BlackjackGameManager(serverId);
        managers.add(newManager);
        return newManager;
    }

}

//            if((args[1].equalsIgnoreCase("double") || args[1].equalsIgnoreCase("split")) && su.getPermissions().hasPermission(PermissionLevel.BOT_ADMIN)) {
//                manager.startNewGame(su.asMember(), message.getChannel(), args[1].toLowerCase()); //Just for testing purposes, it's NOT rigged.
//                return;
//            }
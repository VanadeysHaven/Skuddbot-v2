package me.VanadeysHaven.Skuddbot.Minigames.Challenge;

import me.VanadeysHaven.Skuddbot.Commands.Managers.Command;
import me.VanadeysHaven.Skuddbot.Commands.Managers.CommandRequest;
import me.VanadeysHaven.Skuddbot.Enums.Emoji;
import me.VanadeysHaven.Skuddbot.Exceptions.InsufficientBalanceException;
import me.VanadeysHaven.Skuddbot.Exceptions.InvalidBetException;
import me.VanadeysHaven.Skuddbot.Main;
import me.VanadeysHaven.Skuddbot.Profiles.Users.Currencies.Cashier;
import me.VanadeysHaven.Skuddbot.Profiles.Users.Currencies.Currency;
import me.VanadeysHaven.Skuddbot.Profiles.Users.SkuddUser;
import me.VanadeysHaven.Skuddbot.Utilities.MessagesUtils;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.util.ArrayList;

/**
 * Command for controlling challenges.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.3.23
 * @since 2.1
 */
public class ChallengeCommand extends Command {

    private static ArrayList<ChallengeGameManager> managers;

    public ChallengeCommand(){
        super(new String[]{"challenge", "duel", "fight", "1v1"}, "Fight someone!", "https://wiki.skuddbot.xyz/minigames/challenge", Location.SERVER);
        managers = new ArrayList<>();
    }

    @Override
    public void run(CommandRequest request) { //TODO: make this better
        String[] args = request.getArgs();
        Server server = request.getServer();
        User author = request.getUser();
        ChallengeGameManager manager = getManager(server.getId());
        Message message = request.getMessage();

        if(manager.isOnCooldown(author.getId())){
            MessagesUtils.addReaction(message, Emoji.HOURGLASS_FLOWING_SAND, "You are still wounded from the last fight! You need to wait 1 minute between fights!");
            return;
        }
        if(args.length < 2) {
            MessagesUtils.addReaction(message, Emoji.X, "Not enough arguments! - Usage: `!challenge <mention/open/cancel> [decline/bet]`");
            return;
        }
        if(!message.getMentionedUsers().isEmpty()){
            User mentionedUser = message.getMentionedUsers().get(0);
            if(mentionedUser.getId() == Main.getSkuddbot().getApi().getYourself().getId()){
                MessagesUtils.addReaction(message, Emoji.X, "You can't challenge me!");
                return;
            }
            if(mentionedUser.getId() == message.getAuthor().getId()){
                MessagesUtils.addReaction(message, Emoji.X, "You can't challenge yourself.");
                return;
            }

            SkuddUser su = pm.getUser(server.getId(), author.getId());
            Cashier cashier = su.getCurrencies().getCashier(Currency.SKUDDBUX);
            int placedBet = -1;
            try {
                if(args.length > 2){
                    if(args[2].equalsIgnoreCase("decline")){
                        manager.processDecline(author, message.getMentionedUsers().get(0), message);
                        return;
                    } else {
                        placedBet = cashier.placeBet(args[2]);
                    }
                } else {
                    placedBet = cashier.placeBet("");
                }
            } catch (InvalidBetException | InsufficientBalanceException e) {
                MessagesUtils.addReaction(message, Emoji.X, e.getMessage());
            }

            manager.processAccept(author, message.getMentionedUsers().get(0), message, placedBet);
        } else if(args[1].equalsIgnoreCase("open")) {
            SkuddUser su = pm.getUser(server.getId(), author.getId());
            Cashier cashier = su.getCurrencies().getCashier(Currency.SKUDDBUX);
            int placedBet = -1;
            try {
                if (args.length > 2) {
                    placedBet = cashier.placeBet(args[2]);
                } else {
                    placedBet = cashier.placeBet("");
                }
            } catch (InvalidBetException | InsufficientBalanceException e) {
                MessagesUtils.addReaction(message, Emoji.X, e.getMessage());
                return;
            }

            manager.processAccept(author, message, placedBet);
        } else if(args[1].equalsIgnoreCase("cancel")) {
            if(manager.hasOutstandingGame(author)) {
                manager.cancelGame(author);
                MessagesUtils.addReaction(message, Emoji.WHITE_CHECK_MARK, "Challenge cancelled, bet refunded.");
            } else
                MessagesUtils.addReaction(message, Emoji.X, "You have no outstanding challenge. Start one with `!challenge <mention/open> [bet]`");
        } else {
            MessagesUtils.addReaction(message, Emoji.X, "Invalid arguments. - Usage: `!challenge <mention/open/cancel> [decline/bet]`");
        }
    }

    private static ChallengeGameManager getManager(long serverId){
        for(ChallengeGameManager manager : managers)
            if(serverId == manager.getServerId())
                return manager;

        ChallengeGameManager manager = new ChallengeGameManager(serverId);
        managers.add(manager);
        return manager;
    }

}

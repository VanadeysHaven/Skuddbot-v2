package me.Cooltimmetje.Skuddbot.Minigames.Challenge;

import me.Cooltimmetje.Skuddbot.Commands.Managers.Command;
import me.Cooltimmetje.Skuddbot.Enums.Emoji;
import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Currencies.Currency;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Settings.UserSetting;
import me.Cooltimmetje.Skuddbot.Profiles.Users.SkuddUser;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.util.ArrayList;

/**
 * Command for controlling challenges.
 *
 * @author Tim (Cooltimmetje)
 * @version 2.2.1
 * @since 2.1
 */
public class ChallengeCommand extends Command {

    private static ArrayList<ChallengeGameManager> managers;

    public ChallengeCommand(){
        super(new String[]{"challenge", "duel", "fight", "1v1"}, "Fight someone!", Location.SERVER);
        managers = new ArrayList<>();
    }

    @Override
    public void run(Message message, String content) {
        String[] args = content.split(" ");
        Server server = message.getServer().orElse(null); assert server != null;
        User author = message.getAuthor().asUser().orElse(null); assert author != null;
        ChallengeGameManager manager = getManager(server.getId());
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
            int placedBet = su.getSettings().getInt(UserSetting.DEFAULT_BET);
            if(args.length > 2){
                if(args[2].equalsIgnoreCase("decline")){
                    manager.processDecline(author, message.getMentionedUsers().get(0), message);
                    return;
                } else if(args[2].equalsIgnoreCase("all")) {
                    placedBet = su.getCurrencies().getInt(Currency.SKUDDBUX);
                } else if(MiscUtils.isInt(args[2])) {
                    placedBet = Integer.parseInt(args[2]);
                }
            }

            if(!su.getCurrencies().hasEnoughBalance(Currency.SKUDDBUX, placedBet)){
                MessagesUtils.addReaction(message, Emoji.X, "You do not have enough Skuddbux to make this bet: " + placedBet);
                return;
            }

            manager.processAccept(author, message.getMentionedUsers().get(0), message, placedBet);
        } else if(args[1].equalsIgnoreCase("open")) {
            SkuddUser su = pm.getUser(server.getId(), author.getId());
            int placedBet = su.getSettings().getInt(UserSetting.DEFAULT_BET);
            if(args.length > 2) {
                if (args[2].equalsIgnoreCase("all"))
                    placedBet = su.getCurrencies().getInt(Currency.SKUDDBUX);
                else if (MiscUtils.isInt(args[2]))
                    placedBet = Integer.parseInt(args[2]);
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

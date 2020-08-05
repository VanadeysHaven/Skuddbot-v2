package me.Cooltimmetje.Skuddbot.Minigames.FreeForAll;

import me.Cooltimmetje.Skuddbot.Commands.Managers.Command;
import me.Cooltimmetje.Skuddbot.Enums.Emoji;
import me.Cooltimmetje.Skuddbot.Profiles.ServerMember;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Currencies.Currency;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Settings.UserSetting;
import me.Cooltimmetje.Skuddbot.Profiles.Users.SkuddUser;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.util.ArrayList;

/**
 * The command for Free for All
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.2.1
 * @since ALPHA-2.2
 */
public class FfaCommand extends Command {

    private static final ArrayList<FfaGameManager> managers = new ArrayList<>();

    public FfaCommand() {
        super(new String[]{"freeforall", "ffa"}, "Play a game of Free for All!");
    }

    @Override
    public void run(Message message, String content) {
        String[] args = content.split(" ");
        Server server = message.getServer().orElse(null); assert server != null;
        User user = message.getUserAuthor().orElse(null); assert user != null;
        TextChannel channel = message.getChannel();
        SkuddUser su = pm.getUser(server.getId(), user.getId());
        ServerMember member = su.asMember();
        FfaGameManager manager = getManager(server.getId());
        if(manager.isOnCooldown(user.getId())){
            MessagesUtils.addReaction(message, Emoji.HOURGLASS_FLOWING_SAND, "You are still wounded from the last fight, you must wait 5 minutes between games.");
            return;
        }

        int bet = 0;
        if(args.length > 1){
            if(args[1].equalsIgnoreCase("bet")){
                bet = su.getSettings().getInt(UserSetting.DEFAULT_BET);
            } else if (args[1].equalsIgnoreCase("all")) {
                bet = su.getCurrencies().getInt(Currency.SKUDDBUX);
            } else if (MiscUtils.isLong(args[1])){
                bet = Integer.parseInt(args[1]);
            } else if (args[1].equalsIgnoreCase("leave")){
                if(getManager(server.getId()).isInGame(member)){
                    getManager(server.getId()).leaveGame(member);
                    MessagesUtils.addReaction(message, Emoji.WHITE_CHECK_MARK, "Game left!");
                } else {
                    MessagesUtils.addReaction(message, Emoji.X, "You are not in a game of free for all, or there's no game active.");
                }
                return;
            } else {
                MessagesUtils.addReaction(message, Emoji.X, "Invalid usage: `!ffa [bet/all/betAmount/leave]`");
                return;
            }
        }

        if(bet != 0 && !su.getCurrencies().hasEnoughBalance(Currency.SKUDDBUX, bet)){
            MessagesUtils.addReaction(message, Emoji.X, "You do not have enough Skuddbux to make this bet: " + bet);
            return;
        }

        if(manager.isInGame(member)){
            MessagesUtils.addReaction(message, Emoji.X, "You are already entered into this game.");
            return;
        }

        message.delete();
        manager.enterGame(channel, member, bet);
    }

    private FfaGameManager getManager(long serverId){
        for(FfaGameManager manager : managers)
            if(manager.getServerId() == serverId)
                return manager;

        FfaGameManager manager = new FfaGameManager(serverId);
        managers.add(manager);
        return manager;
    }

    public static void runReminders() {
        for (FfaGameManager manager : managers)
            manager.runReminder();
    }


}

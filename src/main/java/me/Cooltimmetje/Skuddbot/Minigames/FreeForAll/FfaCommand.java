package me.Cooltimmetje.Skuddbot.Minigames.FreeForAll;

import me.Cooltimmetje.Skuddbot.Commands.Managers.Command;
import me.Cooltimmetje.Skuddbot.Enums.Emoji;
import me.Cooltimmetje.Skuddbot.Exceptions.InsufficientBalanceException;
import me.Cooltimmetje.Skuddbot.Exceptions.InvalidBetException;
import me.Cooltimmetje.Skuddbot.Profiles.ServerMember;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Currencies.Cashier;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Currencies.Currency;
import me.Cooltimmetje.Skuddbot.Profiles.Users.SkuddUser;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.util.ArrayList;

/**
 * The command for Free for All
 *
 * @author Tim (Cooltimmetje)
 * @version 2.3
 * @since 2.2
 */
public class FfaCommand extends Command {

    private static final ArrayList<FfaGameManager> managers = new ArrayList<>();

    public FfaCommand() {
        super(new String[]{"freeforall", "ffa"}, "Play a game of Free for All!", "https://wiki.skuddbot.xyz/minigames/free-for-all");
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

        int bet = -1;
        FfaCashier cashier = new FfaCashier(su.getCurrencies().getCashier(Currency.SKUDDBUX));
        try {
            if(args.length > 1){
                if (args[1].equalsIgnoreCase("leave")){
                    if(getManager(server.getId()).isInGame(member)){
                        getManager(server.getId()).leaveGame(member);
                        MessagesUtils.addReaction(message, Emoji.WHITE_CHECK_MARK, "Game left!");
                    } else {
                        MessagesUtils.addReaction(message, Emoji.X, "You are not in a game of free for all, or there's no game active.");
                    }
                    return;
                } else {
                    bet = cashier.placeBet(args[1]);
                }
            } else {
                bet = cashier.placeBet("");
            }
        } catch (InvalidBetException | InsufficientBalanceException e) {
            MessagesUtils.addReaction(message, Emoji.X, e.getMessage());
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

    private class FfaCashier extends Cashier {

        public FfaCashier(Cashier cashier) {
            super(cashier.getUser(), cashier.getCurrency());
        }

        @Override
        protected int formatBet(String betStr) throws InvalidBetException {
            betStr = betStr.toLowerCase();

            if(betStr.equals("match")) return formatMatch();

            return super.formatBet(betStr);
        }

        private int formatMatch() {
            return getManager(getUser().getId().getServerId()).getCurrentHighestBet();
        }

    }

}

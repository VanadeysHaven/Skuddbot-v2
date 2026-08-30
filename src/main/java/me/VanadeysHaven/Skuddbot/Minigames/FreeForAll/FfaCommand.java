package me.VanadeysHaven.Skuddbot.Minigames.FreeForAll;

import me.VanadeysHaven.Skuddbot.Commands.Managers.Command;
import me.VanadeysHaven.Skuddbot.Commands.Managers.CommandRequest;
import me.VanadeysHaven.Skuddbot.Enums.Emoji;
import me.VanadeysHaven.Skuddbot.Exceptions.InsufficientBalanceException;
import me.VanadeysHaven.Skuddbot.Exceptions.InvalidBetException;
import me.VanadeysHaven.Skuddbot.Profiles.Users.Currencies.Cashier;
import me.VanadeysHaven.Skuddbot.Profiles.Users.Currencies.Currency;
import me.VanadeysHaven.Skuddbot.Profiles.Users.ServerMember;
import me.VanadeysHaven.Skuddbot.Profiles.Users.SkuddUser;
import me.VanadeysHaven.Skuddbot.Utilities.MessagesUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

import java.util.ArrayList;

/**
 * The command for Free for All
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.4
 * @since 2.2
 */
public final class FfaCommand extends Command {

    private static final ArrayList<FfaGameManager> managers = new ArrayList<>();

    public FfaCommand() {
        super(new String[]{"freeforall", "ffa"}, "Play a game of Free for All!", "https://wiki.skuddbot.xyz/minigames/free-for-all");
    }

    @Override
    public void run(CommandRequest request) {
        String[] args = request.getArgs();
        Guild server = request.getGuild();
        User user = request.getUser();
        MessageChannel channel = request.getChannel();
        SkuddUser su = pm.getUser(server.getIdLong(), user.getIdLong());
        ServerMember member = su.asMember();
        FfaGameManager manager = getManager(server.getIdLong());
        Message message = request.getMessage();

        if(manager.isOnCooldown(user.getIdLong())){
            MessagesUtils.addReaction(message, Emoji.HOURGLASS_FLOWING_SAND, "You are still wounded from the last fight, you must wait 5 minutes between games.");
            return;
        }

        int bounty = -1;
        FfaCashier cashier = new FfaCashier(su.getCurrencies().getCashier(Currency.SKUDDBUX));
        try {
            if(args.length > 1){
                if (args[1].equalsIgnoreCase("leave")){
                    if(getManager(server.getIdLong()).isInGame(member)){
                        getManager(server.getIdLong()).leaveGame(member);
                        MessagesUtils.addReaction(message, Emoji.WHITE_CHECK_MARK, "Game left!");
                    } else {
                        MessagesUtils.addReaction(message, Emoji.X, "You are not in a game of free for all, or there's no game active.");
                    }
                    return;
                } else {
                    bounty = cashier.placeBet(args[1]);
                }
            } else {
                bounty = cashier.placeBet("");
            }
        } catch (InvalidBetException | InsufficientBalanceException e) {
            MessagesUtils.addReaction(message, Emoji.X, e.getMessage());
        }

        if(manager.isInGame(member)){
            MessagesUtils.addReaction(message, Emoji.X, "You are already entered into this game.");
            return;
        }

        message.delete().queue();
        manager.enterGame(channel, member, bounty);
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
            return getManager(getUser().getId().getServerId()).getCurrentHighestBounty();
        }

    }

}

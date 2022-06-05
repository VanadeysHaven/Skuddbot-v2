package me.VanadeysHaven.Skuddbot.Minigames.DoubleOrNothing;

import me.VanadeysHaven.Skuddbot.Commands.Managers.Command;
import me.VanadeysHaven.Skuddbot.Commands.Managers.CommandRequest;
import me.VanadeysHaven.Skuddbot.Enums.Emoji;
import me.VanadeysHaven.Skuddbot.Exceptions.InsufficientBalanceException;
import me.VanadeysHaven.Skuddbot.Exceptions.InvalidBetException;
import me.VanadeysHaven.Skuddbot.Profiles.Users.Currencies.Cashier;
import me.VanadeysHaven.Skuddbot.Profiles.Users.Currencies.Currency;
import me.VanadeysHaven.Skuddbot.Profiles.Users.SkuddUser;
import me.VanadeysHaven.Skuddbot.Utilities.MessagesUtils;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.util.ArrayList;

/**
 * Command for starting a game of double or nothing.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.3.23
 * @since 2.1.1
 */
public class DonCommand extends Command {

    private final ArrayList<DonGameManager> managers;

    public DonCommand() {
        super(new String[]{"doubleornothing", "don"}, "Play a game of double or nothing.", "https://wiki.skuddbot.xyz/minigames/double-or-nothing");
        managers = new ArrayList<>();
    }

    @Override
    public void run(CommandRequest request) {
        String[] args = request.getArgs();
        Server server = request.getServer();
        User user = request.getUser();
        SkuddUser su = pm.getUser(server.getId(), user.getId());
        DonGameManager manager = getManager(server.getId());
        Message message = request.getMessage();

        if (manager.isOnCooldown(user.getId())) {
            MessagesUtils.addReaction(message, Emoji.HOURGLASS_FLOWING_SAND, "You must wait 1 minute between games.");
            return;
        }
        if(manager.hasGameInProgress(user.getId())){
            MessagesUtils.addReaction(message, Emoji.X, "You still have a game in progress, please finish that first.");
            return;
        }

        int bet = -1;
        Cashier cashier = su.getCurrencies().getCashier(Currency.SKUDDBUX);
        try {
            if (args.length >= 2) {
                bet = cashier.placeBet(args[1]);
            } else {
                bet = cashier.placeBet("");
            }
        } catch (InvalidBetException | InsufficientBalanceException e) {
            MessagesUtils.addReaction(message, Emoji.X, e.getMessage());
        }

        if(bet <= 0){
            MessagesUtils.addReaction(message, Emoji.X, "You must place a bet to play!");
            return;
        }

        manager.startGame(user, bet, message.getChannel());
    }

    private DonGameManager getManager(long serverId){
        for(DonGameManager manager : managers)
            if(manager.getServerId() == serverId)
                return manager;

        DonGameManager manager = new DonGameManager(serverId);
        managers.add(manager);
        return manager;
    }



}

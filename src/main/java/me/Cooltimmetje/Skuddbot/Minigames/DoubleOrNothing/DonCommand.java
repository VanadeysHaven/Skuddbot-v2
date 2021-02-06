package me.Cooltimmetje.Skuddbot.Minigames.DoubleOrNothing;

import me.Cooltimmetje.Skuddbot.Commands.Managers.Command;
import me.Cooltimmetje.Skuddbot.Enums.Emoji;
import me.Cooltimmetje.Skuddbot.Exceptions.InsufficientBalanceException;
import me.Cooltimmetje.Skuddbot.Exceptions.InvalidBetException;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Currencies.Cashier;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Currencies.Currency;
import me.Cooltimmetje.Skuddbot.Profiles.Users.SkuddUser;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.util.ArrayList;

/**
 * Command for starting a game of double or nothing.
 *
 * @author Tim (Cooltimmetje)
 * @version 2.3
 * @since 2.1.1
 */
public class DonCommand extends Command {

    private final ArrayList<DonGameManager> managers;

    public DonCommand() {
        super(new String[]{"doubleornothing", "don"}, "Play a game of double or nothing.", "https://wiki.skuddbot.xyz/minigames/double-or-nothing");
        managers = new ArrayList<>();
    }

    @Override
    public void run(Message message, String content) {
        String[] args = content.split(" ");
        Server server = message.getServer().orElse(null); assert server != null;
        User user = message.getAuthor().asUser().orElse(null); assert user != null;
        SkuddUser su = pm.getUser(server.getId(), user.getId());
        DonGameManager manager = getManager(server.getId());

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

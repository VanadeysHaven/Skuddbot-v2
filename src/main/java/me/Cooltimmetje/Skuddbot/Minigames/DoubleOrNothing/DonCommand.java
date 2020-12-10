package me.Cooltimmetje.Skuddbot.Minigames.DoubleOrNothing;

import me.Cooltimmetje.Skuddbot.Commands.Managers.Command;
import me.Cooltimmetje.Skuddbot.Enums.Emoji;
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
 * Command for starting a game of double or nothing.
 *
 * @author Tim (Cooltimmetje)
 * @version 2.2.1
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

        int bet = su.getSettings().getInt(UserSetting.DEFAULT_BET);
        if(args.length >= 2){
            if(args[1].equalsIgnoreCase("all")){
                bet = su.getCurrencies().getInt(Currency.SKUDDBUX);
            } else if (!MiscUtils.isInt(args[1])){
                MessagesUtils.addReaction(message, Emoji.X, "`" + args[1] + "` is not a valid number.");
                return;
            } else {
                bet = Integer.parseInt(args[1]);
            }
        }
        if(!su.getCurrencies().hasEnoughBalance(Currency.SKUDDBUX, bet)){
            MessagesUtils.addReaction(message, Emoji.X, "You do not have enough Skuddbux to make this bet: " + bet);
            return;
        }
        if(bet <= 0){
            MessagesUtils.addReaction(message, Emoji.X, "Your bet must be higher than 0!");
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

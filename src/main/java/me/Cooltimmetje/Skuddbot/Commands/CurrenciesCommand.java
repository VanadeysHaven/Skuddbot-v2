package me.Cooltimmetje.Skuddbot.Commands;

import me.Cooltimmetje.Skuddbot.Commands.Managers.Command;
import me.Cooltimmetje.Skuddbot.Enums.Emoji;
import me.Cooltimmetje.Skuddbot.Enums.PermissionLevel;
import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Currencies.Currency;
import me.Cooltimmetje.Skuddbot.Profiles.Users.PermissionManager;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Settings.UserSetting;
import me.Cooltimmetje.Skuddbot.Profiles.Users.SkuddUser;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.util.concurrent.ExecutionException;

/**
 * Command used to see and edit user currencies.
 *
 * @author Tim (Cooltimmetje)
 * @version 2.2.1
 * @since 2.1.1
 */
public class CurrenciesCommand extends Command {

    public CurrenciesCommand() {
        super(new String[]{"currency", "currencies", "wallet", "skuddbux", "bux", "balance", "bal"}, "Shows your current currency balance.", "https://wiki.skuddbot.xyz/features/currencies#view-and-edit",Location.SERVER);
    }

    @Override
    public void run(Message message, String content) {
        Server server = message.getServer().orElse(null); assert server != null;
        User author = message.getUserAuthor().orElse(null); assert author != null;
        User user = author;
        String[] args = content.split(" ");
        if(!message.getMentionedUsers().isEmpty())
            user = message.getMentionedUsers().get(0);

        if(args.length >= 2 && MiscUtils.isLong(args[1]))
            try {
                user = Main.getSkuddbot().getApi().getUserById(Long.parseLong(args[1])).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

        PermissionManager authorPerms = pm.getUser(server.getId(), author.getId()).getPermissions();
        SkuddUser su = pm.getUser(server.getId(), user.getId());
        if(args.length >= 5)
            if(authorPerms.hasPermission(PermissionLevel.SERVER_ADMIN)){
                editValue(message, content, su, user, server);
                return;
            }

        if(user.getId() != author.getId() && su.getSettings().getBoolean(UserSetting.PROFILE_PRIVATE) && !authorPerms.hasPermission(PermissionLevel.SERVER_ADMIN)){
            MessagesUtils.addReaction(message, Emoji.X, "This user has set their currencies to private.");
            return;
        }

        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor("Currencies for: " + user.getDisplayName(server), null, user.getAvatar());
        eb.setTitle("__Server:__ " + server.getName());

        for(Currency currency : Currency.values())
            if(currency.isShowWhenZero() || su.getCurrencies().getInt(currency) > 0)
                eb.addInlineField("__" + currency.getName() + ":__", su.getCurrencies().getString(currency));

        message.getChannel().sendMessage(eb);
    }

    private void editValue(Message message, String content, SkuddUser su, User user, Server server) {
        String[] args = content.split(" ");
        Currency currency;
        try {
            currency = Currency.valueOf(args[2].toUpperCase().replace("-", "_"));
        } catch (IllegalArgumentException e){
            MessagesUtils.addReaction(message, Emoji.X, "`" + args[2] + "` is not a currency.");
            return;
        }
        if(!currency.isCanBeEdited()){
            MessagesUtils.addReaction(message, Emoji.X, "`" + currency + "` cannot be edited.");
            return;
        }
        if(!MiscUtils.isInt(args[4])){
            MessagesUtils.addReaction(message, Emoji.X, "`" + args[4] + "` is not an number.");
            return;
        }
        int mutationAmount = Integer.parseInt(args[4]);

        try {
            switch (args[3].toLowerCase()) {
                case "add":
                    su.getCurrencies().incrementInt(currency, mutationAmount);
                    MessagesUtils.addReaction(message, Emoji.WHITE_CHECK_MARK, "Added `" + mutationAmount + "` to currency `" + currency + "` for user `" + user.getDisplayName(server) + "`");
                    break;
                case "remove":
                    su.getCurrencies().incrementInt(currency, mutationAmount * -1);
                    MessagesUtils.addReaction(message, Emoji.WHITE_CHECK_MARK, "Removed `" + mutationAmount + "` from currency `" + currency + "` for user `" + user.getDisplayName(server) + "`");
                    break;
                case "set":
                    su.getCurrencies().setInt(currency, mutationAmount);
                    MessagesUtils.addReaction(message, Emoji.WHITE_CHECK_MARK, "Set currency `" + currency + "` to `" + mutationAmount + "` for user `" + user.getDisplayName(server) + "`");
                    break;
                default:
                    MessagesUtils.addReaction(message, Emoji.X, "`" + args[3] + "` is not a valid operation.");
                    break;
            }
        } catch (IllegalArgumentException e){
            MessagesUtils.addReaction(message, Emoji.X, e.getMessage());
        }
    }
}

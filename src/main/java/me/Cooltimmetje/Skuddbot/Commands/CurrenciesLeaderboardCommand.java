package me.Cooltimmetje.Skuddbot.Commands;

import me.Cooltimmetje.Skuddbot.Commands.Managers.Command;
import me.Cooltimmetje.Skuddbot.Enums.Emoji;
import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.Profiles.Server.SkuddServer;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Currencies.Currency;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Identifier;
import me.Cooltimmetje.Skuddbot.Profiles.Users.SkuddUser;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import me.Cooltimmetje.Skuddbot.Utilities.TableUtilities.TableArrayGenerator;
import me.Cooltimmetje.Skuddbot.Utilities.TableUtilities.TableDrawer;
import me.Cooltimmetje.Skuddbot.Utilities.TableUtilities.TableRow;
import org.apache.commons.lang3.StringUtils;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;

import java.util.LinkedHashMap;

/**
 * Command for currency leaderboards.
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.1.1
 * @since ALPHA-2.1.1
 */
public class CurrenciesLeaderboardCommand extends Command {

    private static final int LEADERBOARD_LIMIT = 10;

    public CurrenciesLeaderboardCommand() {
        super(new String[]{"currencyleaderboard", "clb", "currencylb"}, "Used to view the leaderboards of currencies.");
    }

    @Override
    public void run(Message message, String content) {
        long startTime = System.currentTimeMillis();
        Server server = message.getServer().orElse(null); assert server != null;
        SkuddServer ss = sm.getServer(server.getId());
        String[] args = content.split(" ");
        Currency currency ;
        if(args.length < 2){
            currency = Currency.SKUDDBUX;
        } else {
            if (args[1].equalsIgnoreCase("list")) {
                MessagesUtils.sendPlain(message.getChannel(), "Available currencies: \n\n" + Currency.formatCurrencies());
                return;
            } else {
                try {
                    currency = Currency.valueOf(args[1].toUpperCase().replace("-", "_"));
                } catch (IllegalArgumentException e){
                    MessagesUtils.addReaction(message, Emoji.X, "`" + args[1] + "` is not a available stat.");
                    return;
                }
            }
        }

        if(!currency.isHasLeaderboard()){
            MessagesUtils.addReaction(message, Emoji.X, "The currency `" + currency + "` has no leaderboard.");
        }

        LinkedHashMap<Identifier,Integer> sortedMap = ss.getTopCurrencies(LEADERBOARD_LIMIT, currency);
        int i = 0;
        int lastValue = -1;

        TableRow topRow = new TableRow("Pos", "Name", StringUtils.capitalize(currency.getSuffix()));
        TableArrayGenerator tag = new TableArrayGenerator(topRow);

        for(Identifier id : sortedMap.keySet()){
            TableRow tr = new TableRow();
            String name = getName(id, server);
            SkuddUser su = pm.getUser(id);
            if(lastValue == sortedMap.get(id)){
                tr.add(" ");
            } else {
                i++;
                tr.add(i);
            }
            tr.add(name);
            tr.add(su.getCurrencies().getString(currency));
            tag.addRow(tr);

            lastValue = sortedMap.get(id);
        }

        String sb = "**" + currency.getName() + " leaderboard** | **" + server.getName() + "**\n```\n" +
                new TableDrawer(tag).drawTable() +
                "```" + "\n" + "Generated in `" + (System.currentTimeMillis() - startTime) + "ms`";
        MessagesUtils.sendPlain(message.getChannel(), sb.trim());
    }

    private String getName(Identifier id, Server server){
        if(id.getDiscordId() != -1){
            return Main.getSkuddbot().getApi().getUserById(id.getDiscordId()).join().getDisplayName(server);
        } else {
            return id.getTwitchUsername();
        }
    }
}

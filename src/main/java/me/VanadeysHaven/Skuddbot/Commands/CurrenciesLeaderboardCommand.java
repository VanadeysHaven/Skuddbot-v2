package me.VanadeysHaven.Skuddbot.Commands;

import me.VanadeysHaven.Skuddbot.Commands.Managers.Command;
import me.VanadeysHaven.Skuddbot.Commands.Managers.CommandRequest;
import me.VanadeysHaven.Skuddbot.Enums.Emoji;
import me.VanadeysHaven.Skuddbot.Main;
import me.VanadeysHaven.Skuddbot.Profiles.Server.SkuddServer;
import me.VanadeysHaven.Skuddbot.Profiles.Users.Currencies.Currency;
import me.VanadeysHaven.Skuddbot.Profiles.Users.Identifier;
import me.VanadeysHaven.Skuddbot.Utilities.MessagesUtils;
import me.VanadeysHaven.Skuddbot.Utilities.TableUtilities.TableArrayGenerator;
import me.VanadeysHaven.Skuddbot.Utilities.TableUtilities.TableDrawer;
import me.VanadeysHaven.Skuddbot.Utilities.TableUtilities.TableRow;
import org.apache.commons.lang3.StringUtils;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;

import java.util.LinkedHashMap;

/**
 * Command for currency leaderboards.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.3.26
 * @since 2.1.1
 */
public class CurrenciesLeaderboardCommand extends Command {

    private static final int LEADERBOARD_LIMIT = 10;

    public CurrenciesLeaderboardCommand() {
        super(new String[]{"currencyleaderboard", "clb", "currencylb"}, "Used to view the leaderboards of currencies.", "https://wiki.skuddbot.xyz/features/currencies#leaderboards");
    }

    @Override
    public void run(CommandRequest request) {
        long startTime = System.currentTimeMillis();
        Server server = request.getServer();
        SkuddServer ss = sm.getServer(server.getId());
        String[] args = request.getArgs();
        Message message = request.getMessage();

        Currency currency ;
        if(args.length < 2){
            currency = Currency.SKUDDBUX;
        } else {
            if (args[1].equalsIgnoreCase("list")) {
                MessagesUtils.sendPlain(request.getChannel(), "Available currencies: \n\n" + Currency.formatCurrencies());
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
        int leaderValue = -1;
        int lastValue = -1;

        TableRow topRow = new TableRow("Pos", "Name", StringUtils.capitalize(currency.getSuffix()), "Gap to leader", "Gap to next");
        TableArrayGenerator tag = new TableArrayGenerator(topRow);

        for(Identifier id : sortedMap.keySet()){
            TableRow tr = new TableRow();
            String name = getName(id, server);
            int currentValue = sortedMap.get(id);
            if(lastValue == currentValue){
                tr.add(" ");
            } else {
                i++;
                tr.add(i);
            }
            tr.add(name);
            tr.add(currentValue);

            if(leaderValue == -1) {
                tr.add(" ");
                tr.add(" ");
            } else {
                int gapToLeader = currentValue - leaderValue;
                int gapToNext = currentValue - lastValue;
                tr.add(gapToLeader);
                if(gapToLeader != gapToNext)
                    tr.add(currentValue - lastValue);
                else
                    tr.add(" ");
            }
            tag.addRow(tr);

            lastValue = currentValue;
            if(leaderValue == -1)
                leaderValue = lastValue;
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

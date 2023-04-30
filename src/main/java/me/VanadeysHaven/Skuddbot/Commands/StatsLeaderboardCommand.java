package me.VanadeysHaven.Skuddbot.Commands;

import me.VanadeysHaven.Skuddbot.Commands.Managers.Command;
import me.VanadeysHaven.Skuddbot.Commands.Managers.CommandRequest;
import me.VanadeysHaven.Skuddbot.Enums.Emoji;
import me.VanadeysHaven.Skuddbot.Main;
import me.VanadeysHaven.Skuddbot.Profiles.Server.ServerSetting;
import me.VanadeysHaven.Skuddbot.Profiles.Server.SkuddServer;
import me.VanadeysHaven.Skuddbot.Profiles.Users.Identifier;
import me.VanadeysHaven.Skuddbot.Profiles.Users.SkuddUser;
import me.VanadeysHaven.Skuddbot.Profiles.Users.Stats.Stat;
import me.VanadeysHaven.Skuddbot.Utilities.MessagesUtils;
import me.VanadeysHaven.Skuddbot.Utilities.TableUtilities.TableArrayGenerator;
import me.VanadeysHaven.Skuddbot.Utilities.TableUtilities.TableDrawer;
import me.VanadeysHaven.Skuddbot.Utilities.TableUtilities.TableRow;
import org.apache.commons.lang3.StringUtils;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;

import java.util.LinkedHashMap;

/**
 * Command for stat leaderboards.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.3.26
 * @since 2.0
 */
public class StatsLeaderboardCommand extends Command {

    private static final int LEADERBOARD_LIMIT = 10;

    public StatsLeaderboardCommand(){
        super(new String[]{"statleaderboard", "slb", "statlb"}, "Used to view the leaderboards of stats.", "https://wiki.skuddbot.xyz/features/stats#leaderboards");
    }

    @Override
    public void run(CommandRequest request) {
        long startTime = System.currentTimeMillis();
        Server server = request.getServer();
        SkuddServer ss = sm.getServer(server.getId());
        String commandPrefix = ss.getSettings().getString(ServerSetting.COMMAND_PREFIX).replace("_", " ");
        String[] args = request.getArgs();
        Message message = request.getMessage();
        if(args.length < 2){
            MessagesUtils.addReaction(message, Emoji.X, "You need to specify which stat leaderboard you want to view. Use `" + commandPrefix + " list` to view all available stats.");
            return;
        }
        if(args[1].equalsIgnoreCase("list")){
            MessagesUtils.sendPlain(request.getChannel(), "Available stats: \n\n" + Stat.formatStats());
            return;
        }
        Stat stat;
        try {
            stat = Stat.valueOf(args[1].toUpperCase().replace("-", "_"));
        } catch (IllegalArgumentException e){
            MessagesUtils.addReaction(message, Emoji.X, "`" + args[1] + "` is not a available stat.");
            return;
        }

        if(!stat.isHasLeaderboard()) {
            MessagesUtils.addReaction(message, Emoji.X, "This stat does not have a leaderboard.");
            return;
        }

        LinkedHashMap<Identifier,Integer> sortedMap = ss.getTopStats(LEADERBOARD_LIMIT, stat);
        int i = 0;
        int lastValue = -1;
        int leaderValue = -1;
        TableRow topRow = new TableRow("Pos", "Name");
        if(stat == Stat.EXPERIENCE)
            topRow.add("Level");
        else
            topRow.add(StringUtils.capitalize(stat.getSuffix()));
        topRow.add("Gap to leader");
        topRow.add("Gap to next");

        TableArrayGenerator tag = new TableArrayGenerator(topRow);
        for(Identifier id : sortedMap.keySet()){
            TableRow tr = new TableRow();
            String name = getName(id, server);
            SkuddUser su = pm.getUser(id);
            int currentValue = sortedMap.get(id);
            if(lastValue == currentValue){
                tr.add(" ");
            } else {
                i++;
                tr.add(i);
            }
            tr.add(name);
            if(stat == Stat.EXPERIENCE)
                tr.add(su.getStats().formatLevel());
            else
                tr.add(su.getStats().getString(stat));

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
                leaderValue = currentValue;
        }

        String sb = "**" + stat.getCategory().getName() + ": " + stat.getName() + " leaderboard** | **" + server.getName() + "**\n```\n" +
                new TableDrawer(tag).drawTable() +
                "```" + "\n" + "Generated in `" + (System.currentTimeMillis() - startTime) + "ms`";
        MessagesUtils.sendPlain(request.getChannel(), sb.trim());
    }

    private String getName(Identifier id, Server server){
        if(id.getDiscordId() != -1){
            return Main.getSkuddbot().getApi().getUserById(id.getDiscordId()).join().getDisplayName(server);
        } else {
            return id.getTwitchUsername();
        }
    }

}

package me.Cooltimmetje.Skuddbot.Commands;


import com.sun.xml.internal.ws.util.StringUtils;
import me.Cooltimmetje.Skuddbot.Database.QueryExecutor;
import me.Cooltimmetje.Skuddbot.Enums.Emoji;
import me.Cooltimmetje.Skuddbot.Enums.Query;
import me.Cooltimmetje.Skuddbot.Enums.ServerSetting;
import me.Cooltimmetje.Skuddbot.Enums.Stat;
import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Identifier;
import me.Cooltimmetje.Skuddbot.Profiles.Users.SkuddUser;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import me.Cooltimmetje.Skuddbot.Utilities.TableUtilities.TableArrayGenerator;
import me.Cooltimmetje.Skuddbot.Utilities.TableUtilities.TableDrawer;
import me.Cooltimmetje.Skuddbot.Utilities.TableUtilities.TableRow;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Command for stat leaderboards.
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.0
 * @since ALPHA-2.0
 */
public class LeaderboardCommand extends Command {

    private static final Logger logger = LoggerFactory.getLogger(LeaderboardCommand.class);

    public LeaderboardCommand(){
        super(new String[]{"leaderboard", "lb", "statlb"}, "Used to view the leaderboards of stats.");
    }

    @Override
    public void run(Message message, String content) {
        long startTime = System.currentTimeMillis();
        Server server = message.getServer().orElse(null); assert server != null;
        String commandPrefix = sm.getServer(server.getId()).getSettings().getString(ServerSetting.COMMAND_PREFIX).replace("_", " ");
        String[] args = content.split(" ");
        if(args.length < 2){
            MessagesUtils.addReaction(message, Emoji.X, "You need to specify which stat leaderboard you want to view. Use `" + commandPrefix + " list` to view all available stats.");
            return;
        }
        if(args[1].equalsIgnoreCase("list")){
            message.getChannel().sendMessage("Available stats: \n\n" + Stat.formatStats());
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

        HashMap<Identifier, Integer> statValues = new HashMap<>();
        QueryExecutor qe = null;
        try {
            qe = new QueryExecutor(Query.SELECT_ALL_STAT_VALUES).setLong(1, server.getId()).setString(2, stat.getDbReference());
            ResultSet rs = qe.executeQuery();
            while (rs.next()){
                statValues.put(new Identifier(server.getId(), rs.getLong("discord_id"), rs.getString("twitch_username")), rs.getInt("stat_value"));
            }
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            assert qe != null;
            qe.close();
        }

        LinkedHashMap<Identifier,Integer> sortedMap = sortMap(statValues);
        int i = 0;
        int lastValue = -1;
        TableRow topRow = new TableRow("Pos", "Name");
        if(stat == Stat.EXPERIENCE) {
            topRow.add("Level");
        } else {
            topRow.add(StringUtils.capitalize(stat.getSuffix()));
        }
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
            if(stat == Stat.EXPERIENCE) {
                tr.add(su.getStats().formatLevel());
            } else {
                tr.add(su.getStats().getString(stat));
            }
            tag.addRow(tr);

            lastValue = sortedMap.get(id);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("**").append(stat.getCategory().getName()).append(": ").append(stat.getName()).append(" leaderboard** | **").append(server.getName()).append("**\n```\n");
        sb.append(new TableDrawer(tag).drawTable());
        sb.append("```").append("\n").append("Generated in `").append(System.currentTimeMillis() - startTime).append("ms`");
        message.getChannel().sendMessage(sb.toString().trim());
    }

    private LinkedHashMap<Identifier, Integer> sortMap(HashMap<Identifier,Integer> unsortedMap){
        List<Identifier> mapKeys = new ArrayList<>(unsortedMap.keySet());
        List<Integer> mapValues = new ArrayList<>(unsortedMap.values());
        mapValues.sort(Collections.reverseOrder());

        LinkedHashMap<Identifier,Integer> sortedMap = new LinkedHashMap<>();
        for(int mapValue : mapValues){
            Iterator<Identifier> keyIt = mapKeys.iterator();

            while(keyIt.hasNext()){
                Identifier key = keyIt.next();
                int comp = unsortedMap.get(key);

                if(comp == mapValue){
                    keyIt.remove();
                    sortedMap.put(key, mapValue);
                    break;
                }
            }

            if(sortedMap.size() == 10){
                return sortedMap;
            }
        }

        return sortedMap;
    }

    private String getName(Identifier id, Server server){
        if(id.getDiscordId() != -1){
            return Main.getSkuddbot().getApi().getUserById(id.getDiscordId()).join().getDisplayName(server);
        } else {
            return id.getTwitchUsername();
        }
    }

}

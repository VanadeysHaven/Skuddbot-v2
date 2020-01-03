package me.Cooltimmetje.Skuddbot.Commands;

import me.Cooltimmetje.Skuddbot.Enums.Emoji;
import me.Cooltimmetje.Skuddbot.Enums.UserSetting;
import me.Cooltimmetje.Skuddbot.Enums.UserStats.UserStat;
import me.Cooltimmetje.Skuddbot.Enums.UserStats.UserStatCategory;
import me.Cooltimmetje.Skuddbot.Profiles.ProfileManager;
import me.Cooltimmetje.Skuddbot.Profiles.Users.SkuddUser;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

/**
 * Used to view and edit stats.
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.0
 * @since ALPHA-2.0
 */
public class StatsCommand extends Command {

    private ProfileManager pm = new ProfileManager();

    public StatsCommand(){
        super(new String[]{"stats"}, "View stats from users with this command.");
    }

    @Override
    public void run(Message message, String content) {
        MessageAuthor author = message.getAuthor(); assert author != null;
        User user = message.getAuthor().asUser().orElse(null); assert user != null;
        if(!message.getMentionedUsers().isEmpty())
            user = message.getMentionedUsers().get(0);
        Server server = message.getServer().orElse(null); assert server != null;
        SkuddUser su = pm.getUser(server.getId(), user.getId());
        if(user.getId() != author.getId() && su.getSettings().getBoolean(UserSetting.STATS_PRIVATE)) {
            MessagesUtils.addReaction(message, Emoji.X, "This user has set their stats to private.");
            return;
        }

        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor("Stats for: " + user.getDisplayName(server), null, user.getAvatar());
        eb.setTitle("__Server:__ " + server.getName());

        for(UserStatCategory category : UserStatCategory.values()){
            if(category != UserStatCategory.NO_CATEGORY) eb.addField("\u200B", "__" + category.getName() + ":__");
            for(UserStat stat : UserStat.values()){
                if(stat.getCategory() != category) continue;
                eb.addInlineField("__" + stat.getName() + ":__", su.getStats().getString(stat) + " " + stat.getSuffix());
            }
        }

        message.getChannel().sendMessage(eb);
    }
}
